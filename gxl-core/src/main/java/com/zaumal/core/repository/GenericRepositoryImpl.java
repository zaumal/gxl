package com.zaumal.core.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public class GenericRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID> {
	private final EntityManager entityManager;

	public GenericRepositoryImpl(Class<T> domainClass, EntityManager em) {
		super(domainClass, em);
		entityManager = em;
	}

	public GenericRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation, final EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> jpaQuery(String jpaSQL) {
		return (List<T>)entityManager.createQuery(jpaSQL).getResultList();
	}
	@Override
	public Integer jpaUpdate(String jpaSQL) {
		return entityManager.createQuery(jpaSQL).executeUpdate();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> nativeQueryObj(String nativeSQL) {
		return (List<Object[]>)entityManager.createNativeQuery(nativeSQL).getResultList();
	}
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<T> nativeQueryT(String nativeSQL, Class<T> tClass) {
		Query query = entityManager.createNativeQuery(nativeSQL);
		query.unwrap(NativeQuery.class).addEntity(tClass);
		return query.getResultList();
	}
	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<Map<String, Object>> nativeQueryMap(String nativeSQL) {
		Query query = entityManager.createNativeQuery(nativeSQL);
		query.unwrap(NativeQuery.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		return query.getResultList();
	}
	@Override
	public Integer nativeUpdate(String nativeSQL) {
		return entityManager.createNativeQuery(nativeSQL).executeUpdate();
	}
	@Override
	public Map<String, Object> nativeQueryPage(String nativeSQL,
			Integer pageNo, Integer pageSize, String database) {
		Map<String, Object> result = new HashMap<String, Object>();

		String countSql = "select count(*) as totalRecord from (" + nativeSQL + ") sqlTempTable";

		//默认ORACLE
		String sql = "select * from (select pageTempTable1.*, rownum as seq from (" + nativeSQL + ") pageTempTable1 where rownum <= " + String.valueOf((pageNo + 1) * pageSize)
				+ ") pageTempTable2 where seq > " + String.valueOf(pageNo * pageSize);
		if ("MYSQL".equals(database.toUpperCase())) {
			sql = nativeSQL + " limit " + String.valueOf(pageNo * pageSize) + ", " + String.valueOf(pageSize);
		} else {
			sql = "select * from (select pageTempTable1.*, rownum as seq from (" + nativeSQL + ") pageTempTable1 where rownum <= " + String.valueOf((pageNo + 1) * pageSize)
					+ ") pageTempTable2 where seq > " + String.valueOf(pageNo * pageSize);
		}

		result.put("totalRecord", entityManager.createNativeQuery(countSql).getResultList().get(0));
		result.put("contentList", entityManager.createNativeQuery(sql).getResultList());

		return result;
	}
	@Override
	public void clear() {
		entityManager.clear();
	}
	@Override
	public void close() {
		entityManager.close();
	}
	@Override
	public void detach(Object entity) {
		entityManager.detach(entity);
	}
	@Override
	public T merge(T entity) {
		return entityManager.merge(entity);
	}
	@Override
	public BigDecimal getsequenceNext(String sequenceName) {
		final String sql = " select " + sequenceName + ".nextval from dual ";
		Query query = entityManager.createNativeQuery(sql);
		return (BigDecimal) query.getResultList().get(0);
	}
}