package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 配置spring 和 junit整合 junit 启动时加载springIOC容器
 * @author Administrator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledDaoTest {
	
	
	@Autowired private SuccessKilledDao successKilledDao;
	
	@Test
	public void testInsertSuccessKilled(){
		Long seckillId = 1000L;
		Long userPhone = 15268594665L;
		int updateCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
		System.out.println("updateCount>>>"+updateCount);
	}
	
	@Test
	public void testQueryByIdWithSeckill(){
		Long seckillId = 1000L;
		Long userPhone = 15268594665L;
		System.out.println(successKilledDao.queryByIdWithSeckill(seckillId,userPhone));
	}
}
