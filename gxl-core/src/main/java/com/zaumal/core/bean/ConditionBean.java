package com.zaumal.core.bean;

public class ConditionBean {
	private String columnName;// 列名字
	private String operation;// 操作类型：<,>,<=,>=,=,like
	private Object value;// 查询条件值
	private String orderBy;

	public ConditionBean(String columnName, String operation, Object value) {
		this.columnName = columnName;
		this.operation = operation;
		this.value = value;
	}

	public ConditionBean(String columnName, String operation, Object value, String orderBy) {
		this.columnName = columnName;
		this.operation = operation;
		this.value = value;
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}