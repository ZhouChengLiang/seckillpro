package org.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

@Service
public class SeckillServiceImpl implements SeckillService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SeckillDao seckillDao;
	
	@Autowired
	private SuccessKilledDao successKilledDao;
	
	@Autowired
	private RedisDao redisDao;

	// MD5盐值
	private final String salt = "qazwsxedc123";

	@Override
	public List<Seckill> getSeckillList() {
		return seckillDao.queryAll(0, 4);
	}

	@Override
	public Seckill getById(Long seckillId) {
		return seckillDao.queryById(seckillId);
	}

	@Override
	public Exposer exportSeckillUrl(Long seckillId) {
		//优化点 缓存优化
		Seckill seckill = redisDao.getSeckill(seckillId);
		if(seckill == null){
			//访问数据库
			seckill = getById(seckillId);
			if (seckill == null) {
				return new Exposer(false, seckillId);
			}else{
				redisDao.putSeckill(seckill);
			}
		}
		Date startTime = seckill.getStartTime();
		Date endTime = seckill.getEndTime();
		Date nowTime = new Date();
		if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
			return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
		}
		// 转化特定字符串的过程
		String md5 = getMD5(seckillId);
		return new Exposer(true, md5, seckillId);
	}

	private String getMD5(Long seckillId) {
		String base = seckillId + " " + salt;
		String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
		return md5;
	}

	@Override
	@Transactional
	/**
	 * 使用注解控制事务方法的优点 明确标注事务方法的编程风格 保证事务方法的执行时间尽可能短
	 * 不要穿插其他网络操作的RPC/Http请求或者剥离到事务方法外 不是所有的方法都需要事务 如只有一条修改操作,只读操作不需要事务控制
	 */
	public SeckillExecution executeSeckill(Long seckillId, Long userPhone, String md5)
			throws SeckillException, SeckillCloseException, RepeatKillException {
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			logger.error(SeckillStatEnum.DATA_REWRITE.getStateInfo());
			throw new SeckillException(SeckillStatEnum.DATA_REWRITE.getStateInfo());
		}
		// 执行秒杀逻辑 减库存,记录购买行为
		Date killTime = new Date();
		// 记录购买行为
		int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
		if (insertCount <= 0) {
			logger.error(SeckillStatEnum.REPEAT_KILL.getStateInfo());
			throw new RepeatKillException(SeckillStatEnum.REPEAT_KILL.getStateInfo());
		} else {
			//减库存,热点商品竞争
			int updateCount = seckillDao.reduceNumber(seckillId, killTime);
			if (updateCount <= 0) {
				logger.error(SeckillStatEnum.END.getStateInfo());
				throw new SeckillCloseException(SeckillStatEnum.END.getStateInfo());
			} else {
				// 秒杀成功
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
			}
		}
	}

	@Override
	public SeckillExecution executeSeckillProcedure(Long seckillId, Long userPhone, String md5){
		if (md5 == null || !md5.equals(getMD5(seckillId))) {
			logger.error(SeckillStatEnum.DATA_REWRITE.getStateInfo());
			return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
		}
		Date killTime = new Date();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("seckillId", seckillId);
		map.put("phone", userPhone);
		map.put("killTime", killTime);
		map.put("result", null);
		//执行存储过程
		try {
			seckillDao.killByProcedure(map);
			//获取result
			int result = MapUtils.getInteger(map, "result", -2);
			if(result == 1){
				SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
				return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
			}else{
				return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
		}
	}

}
