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

/**
 * <#if locale="ja">
 * ソート方向を示す。
 * <#elseif locale="en">
 * </#if>
 * @author $Author: Takao Nakaguchi $
 * @version $Revision: 14307 $
 */
public enum OrderDirection {
	/**
	 * <#if locale="ja">
	 * 昇順。
	 * <#elseif locale="en">
	 * </#if>
	 */
	ASCENDANT,

	/**
	 * <#if locale="ja">
	 * 降順
	 * <#elseif locale="en">
	 * </#if>
	 */
	DESCENDANT
}
