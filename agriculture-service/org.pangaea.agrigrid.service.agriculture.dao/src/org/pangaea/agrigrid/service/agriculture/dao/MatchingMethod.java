/*
 * $Id: MatchingMethod.java 14307 2011-01-20 07:45:28Z Takao Nakaguchi $
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

/**
 * <#if locale="ja">
 * 検索方法。
 * <#elseif locale="en">
 * </#if>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14307 $
 */
public enum MatchingMethod {
	/**
	 * <#if locale="ja">
	 * 完全一致。
	 * <#elseif locale="en">
	 * </#if>
	 */
	COMPLETE

	/**
	 * <#if locale="ja">
	 * 前方一致。
	 * <#elseif locale="en">
	 * </#if>
	 */
	, PREFIX

	/**
	 * <#if locale="ja">
	 * 後方一致。
	 * <#elseif locale="en">
	 * </#if>
	 */
	, SUFFIX

	/**
	 * <#if locale="ja">
	 * 部分一致。
	 * <#elseif locale="en">
	 * </#if>
	 */
	, PARTIAL

	/**
	 * <#if locale="ja">
	 * 言語パス一致。
	 * 開始言語、終了言語は固定、中間言語は出現順のみ固定でマッチングが行われる。
	 * <br>
	 * <ul>
	 * <li>((ja en))は((ja ko en))にマッチ</li>
	 * <li>((ja ko en))は((ja zh ko en))および((ja ko zh en))にマッチ</li>
	 * </ul>
	 * <#elseif locale="en">
	 * </#if>
	 */
	, LANGUAGEPATH

	/**
	 * <#if locale="ja">
	 * 包含。対象が文字列の集合の場合に、値がそれに含まれればマッチしたと見なす。
	 * <#elseif locale="en">
	 * </#if>
	 */
	, IN
	;
}
