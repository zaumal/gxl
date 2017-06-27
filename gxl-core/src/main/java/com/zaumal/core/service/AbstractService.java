package com.zaumal.core.service;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import com.zaumal.core.Exception.ServiceException;
import com.zaumal.core.bean.ConditionBean;
import com.zaumal.core.bean.DynamicSpecifications;
import com.zaumal.core.bean.SearchFilter;
import com.zaumal.core.repository.GenericRepository;
import com.zaumal.core.utils.SysProperties;


public abstract class AbstractService<T, ID extends Serializable> {
	@Autowired
	private EntityManagerFactory entityManagerFactory;
	
	public final static String SPLIT_DELIMITER = "##";

	abstract protected GenericRepository<T, ID> getDao();

	abstract protected Class<T> getTemplateClass();

	protected boolean isValidObject(T object) {
		return true;
	}

	protected void throwIfnotValidObject(T object) throws ServiceException {
		try {
			if (!isValidObject(object)) {
				assert false;
			}
		} catch (Exception e) {
			throw new ServiceException("You have used a invalid object as query! Try to set its valid flag, or turn off this check (by passing doValidCheck=false) if it is what you intent to do.");
		}
	}

	public T findOne(ID id) {
		return getDao().findOne(id);
	}

	public T getByKey(ID id, boolean invalidObject) {
		T obj = getDao().findOne(id);
		
		if (null != obj) {
			if(!invalidObject && !isValidObject(obj)){
				return null;
			}
			return obj;
		}
		return null;
	}
	
	public List<T> getList(T object) {
		return getList(object, null, "LIKE");
	}

	public List<T> getList(T object, ConditionBean condition) {
		return getList(object, condition, "LIKE");
	}

	public List<T> getList(T object, String queryFlag) {
		return getList(object, null, queryFlag, true);
	}
	
	public List<T> getList(T object, ConditionBean condition, String queryFlag) {
		return getList(object, condition, queryFlag, true);
	}
	
	public List<T> getList(T object, ConditionBean condition, String queryFlag, boolean doValidCheck) throws ServiceException {
		if (doValidCheck) {
			throwIfnotValidObject(object);
		}

		Specification<T> specifications = getSpecification(object, queryFlag);

		if (null != condition) {
			if ("desc".equals(condition.getOrderBy())) {
				return getDao().findAll(specifications, new Sort(Direction.DESC, new String[] { condition.getColumnName() }));
			} else if ("asc".equals(condition.getOrderBy())) {
				return getDao().findAll(specifications, new Sort(Direction.ASC, new String[] { condition.getColumnName() }));
			}
		}

		return getDao().findAll(specifications);
	}
	
	public Page<T> getListForPage(T object, ConditionBean sort, Integer pageNo, Integer pageSize) throws ServiceException {
		return getListForPage(object, sort, pageNo, pageSize, "LIKE");
	}

	public Page<T> getListForPage(T object, ConditionBean sort, Integer pageNo, Integer pageSize, String queryFlag) {
		return getListForPage(object, sort, pageNo, pageSize, queryFlag, true);
	}
	
	public Page<T> getListForPage(T object, ConditionBean sort, Integer pageNo, Integer pageSize, boolean doValidCheck) throws ServiceException {
		return getListForPage(object, sort, pageNo, pageSize, "LIKE", doValidCheck);
	}

	public Page<T> getListForPage(List<SearchFilter> filters, ConditionBean sort, Integer pageNo, Integer pageSize) {
		Specification<T> specifications = DynamicSpecifications.bySearchFilter(filters, getTemplateClass());
		return getListForPage(specifications, sort, pageNo, pageSize);
	}
	
	public Page<T> getListForPage(T object, ConditionBean sort, Integer pageNo, Integer pageSize, String queryFlag, boolean doValidCheck) throws ServiceException {
		if (doValidCheck) {
			throwIfnotValidObject(object);
		}
		
		Specification<T> specifications = getSpecification(object, queryFlag);
		
		return getListForPage(specifications, sort, pageNo, pageSize);
	}

	public Page<T> getListForPage(Specification<T> specifications, ConditionBean sort, Integer pageNo, Integer pageSize) {
		if (sort != null) {
			if ("desc".equals(sort.getOrderBy())) {
				return getDao().findAll(specifications, new PageRequest(pageNo - 1, pageSize, new Sort(Direction.DESC, new String[] { sort.getColumnName() })));
			} else if ("asc".equals(sort.getOrderBy())) {
				return getDao().findAll(specifications, new PageRequest(pageNo - 1, pageSize, new Sort(Direction.ASC, new String[] { sort.getColumnName() })));
			} else {
				return getDao().findAll(specifications, new PageRequest(pageNo - 1, pageSize, new Sort(Direction.ASC, new String[] { sort.getColumnName() })));
			}
		}
		return getDao().findAll(specifications, new PageRequest(pageNo - 1, pageSize));
	}
	
	private Specification<T> getSpecification(T object, String queryFlag) {
		List<SearchFilter> filters = new ArrayList<SearchFilter>();
		addFilterParams(filters,object, queryFlag);
		return  DynamicSpecifications.bySearchFilter(filters, getTemplateClass());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SearchFilter> addFilterParams(List<SearchFilter> filters,T object, String operaFlag) throws ServiceException {
		try {
			Metamodel metaModel = entityManagerFactory.createEntityManager().getMetamodel();
			EntityType<T> entityType = metaModel.entity((Class<T>) object.getClass());

			String idName = null;
			// Handle Id field
			ID id = (ID) entityManagerFactory.getPersistenceUnitUtil().getIdentifier(object);
			if (id != null) { // do only if the id field is set
				Attribute idAttribute = entityType.getId(id.getClass());
				if (idAttribute.getJavaType().getName().startsWith(SysProperties.PACKAGE_START_WITH)) { // is
																								// a
																								// embedded
																								// class
					idName = idAttribute.getName(); // Add idName to
					// exception
					EmbeddableType<ID> idEmbeddedType = metaModel.embeddable((Class<ID>) id.getClass());
					filters = addAttributesFilterParams(filters, id, operaFlag, idEmbeddedType, "", idName + ".");
				}
			}
			// Handle All fields (but idName if handled above)
			addAttributesFilterParams(filters, object, operaFlag, entityType, idName, "");
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return filters;
	}
	
	@SuppressWarnings("rawtypes")
	protected <T2> List<SearchFilter> addAttributesFilterParams(List<SearchFilter> filters, T2 object, String operaFlag, ManagedType<T2> managedType, String excludeIdName, String prefix)
			throws ServiceException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		for (Attribute attribute : managedType.getAttributes()) {
			if (attribute.getName().equals(excludeIdName)) {
				continue;
			}
			SearchFilter filter = handleField(object, attribute, operaFlag, prefix);
			if (filter != null) {
				filters.add(filter);
			}
		}
		return filters;
	}
	
	@SuppressWarnings("rawtypes")
	protected <T2> SearchFilter handleField(T2 object,Attribute attribute, String operaFlag, String prefix) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String paramName = attribute.getName();
		Method getter = (Method) attribute.getJavaMember();
		Class returnType = attribute.getJavaType();

		Object paramValue = getter.invoke(object);
		if (paramValue != null) {
			if (java.lang.Integer.class.equals(returnType) || java.lang.Double.class.equals(returnType) || java.util.Date.class.equals(returnType) || java.lang.Long.class.equals(returnType)
					|| returnType.isEnum()) {
				operaFlag = "EQ";
			}
			if (operaFlag == "IN") {
				// Hacks to break the String field into list
				return new SearchFilter(prefix + paramName, SearchFilter.Operator.valueOf(operaFlag), Arrays.asList(StringUtils.split((String) paramValue, SPLIT_DELIMITER)));
			} else {
				return new SearchFilter(prefix + paramName, SearchFilter.Operator.valueOf(operaFlag), paramValue);
			}
		}
		return null;
	}
}