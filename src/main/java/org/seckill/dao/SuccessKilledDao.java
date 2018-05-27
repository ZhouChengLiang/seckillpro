package org.seckill.dao;

import org.apache.ibatis.annotations.Param;
import org.seckill.entity.SuccessKilled;

public interface SuccessKilledDao {
	
	/**
	 * ���빺����ϸ �ɹ����ظ�
	 * @param seckillId
	 * @param userPhone
	 * @return ��ʾ����Ľ��������
	 */
	int insertSuccessKilled(@Param("seckillId") Long seckillId,@Param("userPhone") Long userPhone);
	
	/**
	 * ��ѯSuccessKilled ��Я����Ʒ����
	 * @param seckillId
	 * @return
	 */
	SuccessKilled queryByIdWithSeckill(@Param("seckillId") Long seckillId,@Param("userPhone") Long userPhone);
}
