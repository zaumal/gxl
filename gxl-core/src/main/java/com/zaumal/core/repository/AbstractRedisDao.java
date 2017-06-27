package com.zaumal.core.repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.CollectionUtils;

public abstract class AbstractRedisDao<K, V>{ 
	@Autowired
    protected RedisTemplate<K, V> redisTemplate;  
    /** 
     * 设置redisTemplate 
     * @param redisTemplate the redisTemplate to set 
     */ 
	protected void setRedisTemplate(RedisTemplate<K, V> redisTemplate) {  
        this.redisTemplate = redisTemplate;  
    }  
	protected RedisTemplate<K, V> getRedisTemplate() {
		return redisTemplate;
	}

	/** 
     * 获取 RedisSerializer 
     * <br>------------------------------<br> 
     */  
    protected RedisSerializer<String> getRedisSerializer() {  
        return redisTemplate.getStringSerializer();
    }  
    
    public boolean add(final K k,final V v) {  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            public Boolean doInRedis(RedisConnection connection)  
                    throws DataAccessException {  
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key  = serializer.serialize(k.toString());
                byte[] value = objToByte(v);
                return connection.setNX(key, value); 
            }  
        });  
        return result;  
    }  
      
    /** 
     * 批量新增 使用pipeline方式   
     *<br>------------------------------<br> 
     *@param list 
     *@return 
     */  
    public boolean add(final K k,final List<V> list) {
    	boolean result = false;
        if(!CollectionUtils.isEmpty(list)){
        	result = redisTemplate.execute(new RedisCallback<Boolean>() {  
                public Boolean doInRedis(RedisConnection connection)  
                        throws DataAccessException {  
                    RedisSerializer<String> serializer = getRedisSerializer();
                    byte[] key  = serializer.serialize(k.toString());
                    for (V v : list) {  
                    	byte[] value = objToByte(v);  
                        connection.setNX(key, value);  
                    }  
                    return true;  
                }  
            }, false, true); 
        }
        return result;  
    }  
      
    /**  
     * 删除 
     * <br>------------------------------<br> 
     * @param key 
     */  
    public void delete(K key) {  
        List<K> list = new ArrayList<K>();  
        list.add(key);  
        delete(list);  
    }  
  
    /** 
     * 删除多个 
     * <br>------------------------------<br> 
     * @param keys 
     */  
    public void delete(List<K> keys) {  
        redisTemplate.delete(keys);  
    }  
  
    /** 
     * 修改  
     * <br>------------------------------<br> 
     * @param user 
     * @return  
     */  
    public boolean update(final K k,final V v) {  
        if (get(k) == null) {  
            throw new NullPointerException("数据行不存在, key = " + k.toString());  
        }  
        boolean result = redisTemplate.execute(new RedisCallback<Boolean>() {  
            public Boolean doInRedis(RedisConnection connection)  
                    throws DataAccessException {  
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key  = serializer.serialize(k.toString());
                byte[] value = objToByte(v);
                connection.set(key, value);  
                return true;  
            }  
        });  
        return result;  
    }  
  
    public Set<String> getKeys(final K k) {  
    	Set<String> result = redisTemplate.execute(new RedisCallback<Set<String>>() {  
            public Set<String> doInRedis(RedisConnection connection)  
                    throws DataAccessException {  
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key  = serializer.serialize(k.toString());
                Set<byte[]> value = connection.keys(key);  
                if (value == null) {  
                    return null;  
                }
                Set<String> keys = new HashSet<String>();
                for(byte[] e : value){
                	String k1 = serializer.deserialize(e);
                	keys.add(k1);
                }
                return keys;
            }  
        });  
        return result;  
    } 
    
    /**  
     * 通过key获取 
     * <br>------------------------------<br> 
     * @param keyId 
     * @return 
     */  
    public V get(final K k) {  
        V result = redisTemplate.execute(new RedisCallback<V>() {  
            public V doInRedis(RedisConnection connection)  
                    throws DataAccessException {  
                RedisSerializer<String> serializer = getRedisSerializer();  
                byte[] key  = serializer.serialize(k.toString());
                byte[] value = connection.get(key);  
                if (value == null) {  
                    return null;  
                }  
                return byteToObj(value);
            }  
        });  
        return result;  
    } 
    
    /**
     * 把对象转化为byte[]
     */
    private byte[] objToByte(V v){
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        byte[] bytes = null;
        try {
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(v);
            bytes = bo.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
    
    // 
    /**
     * 把byte[]还原为对象
     */
    @SuppressWarnings("unchecked")
	private V byteToObj(byte[] bytes){
        ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
        ObjectInputStream in;
        V v = null;
        try {
            in = new ObjectInputStream(bi);
            v = (V) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }
}  
