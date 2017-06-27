package com.zaumal.core.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

@SuppressWarnings({ "incomplete-switch", "unchecked", "rawtypes" })
public class DynamicSpecifications {
	public static <T> Specification<T> bySearchFilter(final Collection<SearchFilter> andFilters, final Class<T> clazz) {
		return new Specification<T>() {
			@Override
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

				Predicate returnPredicate = null;
				List<Predicate> orPredicateList = new ArrayList<Predicate>();
				boolean flag = false;


				if (!CollectionUtils.isEmpty(andFilters)) {

					List<Predicate> andPredicates = new ArrayList<Predicate>();
					for (SearchFilter filter : andFilters) {
						// nested path translate, 如Task的名为"user.name"的filedName,
						// 转换为Task.user.name属性
						String[] names = StringUtils.split(filter.fieldName, ".");
						Path expression = root.get(names[0]);
						for (int i = 1; i < names.length; i++) {
							expression = expression.get(names[i]);
						}

						// logic operator
						switch (filter.operator) {
						case EQ:
							andPredicates.add(builder.equal(expression, filter.value));
							break;
						case LIKE:
							andPredicates.add(builder.like(expression, "%" + filter.value + "%"));
							break;
						case GT:
							andPredicates.add(builder.greaterThan(expression, (Comparable) filter.value));
							break;
						case GE:
							andPredicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case LT:
							andPredicates.add(builder.lessThan(expression, (Comparable) filter.value));
							break;
						case GTE:
							andPredicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case LTE:
							andPredicates.add(builder.lessThanOrEqualTo(expression, (Comparable) filter.value));
							break;
						case NEQ:
							andPredicates.add(builder.notEqual(expression, (Comparable) filter.value));
							break;
						}
					}

					for (Predicate orPredicate : orPredicateList) {
						andPredicates.add(orPredicate);
					}

					// 将所有条件用 and 联合起来
					if (andPredicates.size() > 0) {
						flag = true;
						returnPredicate = builder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
					} else {
						for (Predicate orPredicate : orPredicateList) {
							andPredicates.add(orPredicate);
						}
						returnPredicate = builder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
					}
				} else {
					List<Predicate> andPredicates = new ArrayList<Predicate>();
					for (Predicate orPredicate : orPredicateList) {
						andPredicates.add(orPredicate);
					}
					returnPredicate = builder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
				}

				if (flag) {
					return returnPredicate;
				} else {
					return builder.conjunction();
				}

			}
		};
	}
}
