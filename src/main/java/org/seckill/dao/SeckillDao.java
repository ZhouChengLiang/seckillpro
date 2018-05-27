package org.seckill.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.Seckill;

public interface SeckillDao {
	
	/**
	 * �����
	 * @param seckillId
	 * @param killTime
	 * @return ���Ӱ������>1 ��ʾ���µļ�¼����
	 */
	int reduceNumber(@Param("seckillId") Long seckillId,@Param("killTime") Date killTime);
	
	/**
	 * ����
	 * @param seckillId
	 * @return
	 */
	Seckill queryById(Long seckillId);
	
	List<Seckill> queryAll(@Param("offset") Integer offset,@Param("limit") Integer limit);
}	
