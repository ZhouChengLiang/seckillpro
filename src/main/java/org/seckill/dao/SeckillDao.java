package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * 减库存
	 * @param seckillId
	 * @param killTime
	 * @return 如果影响行数>1 表示更新的记录行数
	 */
	int reduceNumber(@Param("seckillId") Long seckillId,@Param("killTime") Date killTime);
	
	/**
	 * 根据
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(Long seckillId);
	
	List<Seckill> queryAll(@Param("offset") Integer offset,@Param("limit") Integer limit);
}	
