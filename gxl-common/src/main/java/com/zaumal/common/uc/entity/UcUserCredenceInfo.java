package com.zaumal.common.uc.entity;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="UC_USER_CREDENCE_INFO")
@NamedQuery(name="UcUserCredenceInfo.findAll", query="SELECT u FROM UcUserCredenceInfo u")
public class UcUserCredenceInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public UcUserCredenceInfo() {
	}

}