package com.zaumal.common.uc.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="SYS_MENU_INFO")
@NamedQuery(name="SysMenuInfo.findAll", query="SELECT s FROM SysMenuInfo s")
public class SysMenuInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public SysMenuInfo() {
	}

}