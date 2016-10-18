/*
 * $Id:FileTransInfoDao.java 4384 2007-04-03 08:56:48Z nakaguchi $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2005-2008 NICT Language Grid Project.
 *
 * This program is free software: you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation, either version 2.1 of the License, or (at 
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser 
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.pangaea.agrigrid.service.agriculture.dao;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <#if locale="ja">
 * ソート順を格納する。
 * <#elseif locale="en">
 * </#if>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14307 $
 */
public class Order implements Serializable {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Order(){}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param fieldName ソートするフィールド
	 * @param direction ソート方向("ASCENDANT" or "DESCENDANT")
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Order(String fieldName, OrderDirection direction) {
		this.fieldName = fieldName;
		this.direction = direction;
	}

	@Override
	public boolean equals(Object value){
		return EqualsBuilder.reflectionEquals(this, value);
	}

	@Override
	public int hashCode(){
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString(){
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * <#if locale="ja">
	 * fieldを返す。
	 * @return field
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * <#if locale="ja">
	 * fieldを設定する。
	 * @param field field
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setFieldName(String field) {
		this.fieldName = field;
	}

	/**
	 * <#if locale="ja">
	 * ソート方向を返す。
	 * @return ソート方向
	 * <#elseif locale="en">
	 * </#if>
	 */
	public OrderDirection getDirection() {
		return direction;
	}

	/**
	 * <#if locale="ja">
	 * ソート方向を設定する。
	 * @param direction ソート方向
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setDirection(OrderDirection direction) {
		this.direction = direction;
	}

	private String fieldName;
	private OrderDirection direction;

	private static final long serialVersionUID = -2250489893834214481L;
}
