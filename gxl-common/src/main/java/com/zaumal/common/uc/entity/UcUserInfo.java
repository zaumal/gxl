package com.zaumal.common.uc.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="UC_USER_INFO")
@NamedQuery(name="UcUserInfo.findAll", query="SELECT u FROM UcUserInfo u")
public class UcUserInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public UcUserInfo() {
	}

}