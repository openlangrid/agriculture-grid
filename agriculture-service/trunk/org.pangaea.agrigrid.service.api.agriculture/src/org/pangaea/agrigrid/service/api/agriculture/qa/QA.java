package org.pangaea.agrigrid.service.api.agriculture.qa;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <#if locale="ja">
 * Q&A情報のうち、言語、質問文、応答文のみを格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class QA implements Serializable{
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public QA(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param language 言語
	 * @param question 質問文
	 * @param answers 応答文
	 * <#elseif locale="en">
	 * </#if>
	 */
	public QA(String language, String question, String[] answers){
		this.language = language;
		this.question = question;
		this.answers = answers;
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

	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String[] getAnswers() {
		return answers;
	}
	public void setAnswers(String[] answers) {
		this.answers = answers;
	}

	private String language;
	private String question;
	private String[] answers;

	private static final long serialVersionUID = 297558080045900866L;
}
