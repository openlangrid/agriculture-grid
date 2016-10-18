/*
 * $Id: Category.java 14068 2010-11-11 11:21:43Z Takao Nakaguchi $
 *
 * This is a program for Language Grid Core Node. This combines multiple language resources and provides composite language services.
 * Copyright (C) 2009 NICT Language Grid Project.
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
package org.pangaea.agrigrid.service.api.agriculture;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <#if locale="ja">
 * カテゴリを格納する。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14068 $
 */
public class Category
implements Serializable {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * Constructor.
	 * </#if>
	 */
	public Category() {
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param categoryId カテゴリID
	 * @param categoryName カテゴリ名
	 * <#elseif locale="en">
	 * Constructor.
	 * </#if>
	 */
	public Category(String categoryId, String categoryName) {
		this.categoryId = categoryId;
		this.categoryName = categoryName;
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
	 * カテゴリIDを取得する。
	 * @return カテゴリID
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getCategoryId() {
		return categoryId;
	}

	/**
	 * <#if locale="ja">
	 * カテゴリIDを設定する。
	 * @param categoryId カテゴリID
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	/**
	 * <#if locale="ja">
	 * カテゴリ名を取得する。
	 * @return カテゴリ名
	 * <#elseif locale="en">
	 * </#if>
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * <#if locale="ja">
	 * カテゴリ名を設定する。
	 * @param categoryName カテゴリ名
	 * <#elseif locale="en">
	 * </#if>
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	private String categoryId;
	private String categoryName;

	private static final long serialVersionUID = 7346247746830311842L;
}
