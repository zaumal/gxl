package com.zaumal.common.uc.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="SYS_CODE_INFO")
@NamedQuery(name="SysCodeInfo.findAll", query="SELECT s FROM SysCodeInfo s")
public class SysCodeInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public SysCodeInfo() {
	}

}