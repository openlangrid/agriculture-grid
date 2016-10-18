package org.pangaea.agrigrid.service.api.agriculture;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <#if locale="ja">
 * キャプション情報を格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class Caption {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Caption(){
	}	

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param language 言語
	 * @param text テキスト
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Caption(String language, String text){
		this.language = language;
		this.text = text;
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

	public String getLanguage(){
		return language;
	}
	public void setLanguage(String language){
		this.language = language;
	}
	public String getText(){
		return text;
	}
	public void setText(String text){
		this.text = text;
	}
	private String language;
	private String text;
}
