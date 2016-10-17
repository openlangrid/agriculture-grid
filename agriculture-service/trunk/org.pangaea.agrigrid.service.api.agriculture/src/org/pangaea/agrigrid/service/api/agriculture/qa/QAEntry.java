package org.pangaea.agrigrid.service.api.agriculture.qa;

import java.util.Calendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.pangaea.agrigrid.service.api.agriculture.Category;

/**
 * <#if locale="ja">
 * Q&A情報格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class QAEntry {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public QAEntry(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param qaId Q&A ID
	 * @param language 言語
	 * @param question 質問文
	 * @param answers 応答文
	 * @param categories カテゴリ
	 * @param createdAt 作成日時
	 * @param updatedAt 更新日時
	 * <#elseif locale="en">
	 * </#if>
	 */
	public QAEntry(
			String qaId, String language, String question, String[] answers
			, Category[] categories, Calendar createdAt, Calendar updatedAt) {
		this.qaId = qaId;
		this.language = language;
		this.question = question;
		this.answers = answers;
		this.categories = categories;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
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

	public String getQaId() {
		return qaId;
	}
	public void setQaId(String qaId) {
		this.qaId = qaId;
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
	public Category[] getCategories() {
		return categories;
	}
	public void setCategories(Category[] categories) {
		this.categories = categories;
	}
	public Calendar getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Calendar createdAt) {
		this.createdAt = createdAt;
	}
	public Calendar getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Calendar updatedAt) {
		this.updatedAt = updatedAt;
	}
	private String qaId;
	private String language;
	private String question;
	private String[] answers;
	private Category[] categories;
	private Calendar createdAt;
	private Calendar updatedAt;
}
