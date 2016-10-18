package org.pangaea.agrigrid.service.api.agriculture.video;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <#if locale="ja">
 * 字幕情報を格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class Subtitle {
	/**
	 * <#if locale="ja">
	 * コンストラクタ
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Subtitle() {
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param startMillis 開始時間(ミリ秒)
	 * @param endMillis 終了時間(ミリ秒)
	 * @param text テキスト
	 * <#elseif locale="en">
	 * </#if>
	 */
	public Subtitle(long startMillis, long endMillis, String text) {
		super();
		this.endMillis = endMillis;
		this.startMillis = startMillis;
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

	public long getStartMillis() {
		return startMillis;
	}
	public void setStartMillis(long startMillis) {
		this.startMillis = startMillis;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public long getEndMillis() {
		return endMillis;
	}
	public void setEndMillis(long endMillis) {
		this.endMillis = endMillis;
	}

	private long endMillis;
	private long startMillis;
	private String text;
}
