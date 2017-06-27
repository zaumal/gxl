package com.zaumal.core.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
	public List<T> jpaQuery(String jpaSQL);
	
	public Integer jpaUpdate(String jpaSQL);
	
	public List<Object[]> nativeQueryObj(String nativeSQL);
	
	public List<T> nativeQueryT(String nativeSQL, Class<T> tClass);
	
	public List<Map<String, Object>> nativeQueryMap(String nativeSQL);
	
	public Integer nativeUpdate(String nativeSQL);
	
	public Map<String,Object> nativeQueryPage(String nativeSQL, Integer pageNo, Integer pageSize, String database);

	public void clear();

	public void detach(Object entity);

	public T merge(T entity);

	public void close();

	public BigDecimal getsequenceNext(String sequenceName);
}