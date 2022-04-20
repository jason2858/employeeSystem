package com.yesee.gov.website.model;
//default package
//Generated 2018/7/19 �U�� 03:30:01 by Hibernate Tools 4.3.1.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
* TbGroupFunction generated by hbm2java
*/
@Entity
@Table(name = "tb_group_function", catalog = "yesee")
public class TbGroupFunction implements java.io.Serializable {

	private TbGroupFunctionId id;

	public TbGroupFunction() {
	}

	public TbGroupFunction(TbGroupFunctionId id) {
		this.id = id;
	}

	@EmbeddedId

	@AttributeOverrides({
			@AttributeOverride(name = "groupId", column = @Column(name = "group_id", nullable = false, length = 10)),
			@AttributeOverride(name = "funcId", column = @Column(name = "func_id", nullable = false, length = 10)) })
	public TbGroupFunctionId getId() {
		return this.id;
	}

	public void setId(TbGroupFunctionId id) {
		this.id = id;
	}

}