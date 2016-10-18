package org.pangaea.agrigrid.service.api.agriculture.video;

import java.util.Calendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.pangaea.agrigrid.service.api.agriculture.Caption;
import org.pangaea.agrigrid.service.api.agriculture.Category;

/**
 * <#if locale="ja">
 * ビデオ情報を格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class VideoEntry {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public VideoEntry(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param videoId 映像ID
	 * @param fileName ファイル名
	 * @param url ダウンロードURL
	 * @param captions キャプション情報(全言語分)
	 * @param subtitleLanguages 字幕の対応言語
	 * @param copyright 著作権情報
	 * @param license ライセンス情報
	 * @param categories カテゴリ情報
	 * @param createdAt 作成日時
	 * @param updatedAt 更新日時
	 * <#elseif locale="en">
	 * </#if>
	 */
	public VideoEntry(String videoId, String fileName, String url,
			Caption[] captions, String[] subtitleLanguages
			, String copyright, String license
			, Category[] categories, Calendar createdAt, Calendar updatedAt)
	{
		this.videoId = videoId;
		this.fileName = fileName;
		this.url = url;
		this.captions = captions;
		this.subtitleLanguages = subtitleLanguages;
		this.copyright = copyright;
		this.license = license;
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

	public String getVideoId() {
		return videoId;
	}
	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Caption[] getCaptions() {
		return captions;
	}
	public void setCaptions(Caption[] captions) {
		this.captions = captions;
	}
	public String[] getSubtitleLanguages() {
		return subtitleLanguages;
	}
	public void setSubtitleLanguages(String[] subtitleLanguages) {
		this.subtitleLanguages = subtitleLanguages;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
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
	private String videoId;
	private String fileName;
	private String url;
	private Caption[] captions;
	private String[] subtitleLanguages;
	private String copyright;
	private String license;
	private Category[] categories;
	private Calendar createdAt;
	private Calendar updatedAt;
}
