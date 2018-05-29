package org.seckill.service;

import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
public interface SeckillService{
	
	/**
	 * ��ѯ������ɱ��¼
	 * @return
	 */
	List<Seckill> getSeckillList();
	
	/**
	 * ��ѯ������ɱ��¼
	 * @param seckillId
	 * @return
	 */
	Seckill getById(Long seckillId);
	
	/**
	 * ��ɱ����ʱ�����ɱ�ӿڵĵ�ַ
	 * �������ϵͳʱ�����ɱʱ��
	 * @param seckillId
	 */
	Exposer exportSeckillUrl(Long seckillId);
	
	
	/**
	 * ִ����ɱ����
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 */
	SeckillExecution executeSeckill(Long seckillId,Long userPhone,String md5) throws SeckillException,SeckillCloseException,RepeatKillException ;
	
	/**
	 * ִ����ɱ����  �洢����
	 * @param seckillId
	 * @param userPhone
	 * @param md5
	 * @return
	 * @throws SeckillException
	 * @throws SeckillCloseException
	 * @throws RepeatKillException
	 */
	SeckillExecution executeSeckillProcedure(Long seckillId,Long userPhone,String md5);
}
