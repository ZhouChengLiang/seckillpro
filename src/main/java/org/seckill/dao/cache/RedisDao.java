package org.seckill.dao.cache;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDao {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final JedisPool jedisPool;
	private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);
	
	
	public RedisDao(GenericObjectPoolConfig poolConfig,String host,int port,int timeout,String password){
		jedisPool = new JedisPool(poolConfig,host, port,timeout,password);
	}
	
	public Seckill getSeckill(Long seckillId){
		//redis 操作逻辑
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:"+seckillId;
				//并没有实现内部序列化操作
				// get --> byte[] -->反序列化 -->Object(Seckill)
				//采用自定义序列化
				//protostuff :pojo
				byte[] bytes = jedis.get(key.getBytes());
				if(bytes != null){
					//空对象
					Seckill seckill = schema.newMessage();
					ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
					//seckill 被反ProtostuffIOUtil序列化
					return seckill;
				}
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public String putSeckill(Seckill seckill){
		// set Object ->序列化 ->byte[]
		try {
			Jedis jedis = jedisPool.getResource();
			try {
				String key = "seckill:"+seckill.getSeckillId();
				byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
				//超时缓存
				String result = jedis.setex(key.getBytes(), 60*60, bytes);
				return result;
			} finally {
				jedis.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
}
