package org.pangaea.agrigrid.service.api.agriculture.image;

import java.util.Calendar;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.pangaea.agrigrid.service.api.agriculture.Caption;
import org.pangaea.agrigrid.service.api.agriculture.Category;

/**
 * <#if locale="ja">
 * 画像情報を格納するクラス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public class ImageEntry {
	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ImageEntry(){
	}

	/**
	 * <#if locale="ja">
	 * コンストラクタ。
	 * @param imageId イメージのID
	 * @param fileName ファイル名
	 * @param url ダウンロードURL
	 * @param captions 画像のタイトルキャプション(全言語分)
	 * @param copyright 著作権情報
	 * @param license ライセンス情報
	 * @param tags タグ
	 * @param createdAt 作成日時
	 * @param updatedAt 更新日時
	 * <#elseif locale="en">
	 * </#if>
	 */
	public ImageEntry(String imageId, String fileName, String url,
			Caption[] captions, String copyright, String license,
			Category[] categories, Calendar createdAt, Calendar updatedAt)
	{
		this.imageId = imageId;
		this.fileName = fileName;
		this.url = url;
		this.captions = captions;
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

	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
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

	private String imageId;
	private String fileName;
	private String url;
	private Caption[] captions;
	private String copyright;
	private String license;
	private Category[] categories;
	private Calendar createdAt;
	private Calendar updatedAt;
}
