package org.pangaea.agrigrid.service.api.agriculture.video;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;

/**
 * <#if locale="ja">
 * 映像を検索するサービス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public interface VideoService {
	/**
	 * <#if locale="ja">
	 * 全てのタグの一覧を取得する。
	 * @param languages 取得する言語
	 * @return カテゴリ一覧
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	Category[] listAllCategories(String language)
	throws ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 指定のカテゴリ名を取得する。
	 * @param categoryId カテゴリID
	 * @param languages 取得する言語
	 * @return カテゴリ名
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	String[] getCategoryNames(String categoryId, String[] languages)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * ビデオを検索する。
	 * @param text 検索するテキスト(キャプションと字幕から検索される)
	 * @param textLanguage テキストの言語
	 * @param matchingMethod マッチング方法(COMPLETE - 完全一致, PARTIAL - 部分一致, PREFIX - 前方一致, SUFFIX - 後方一致) 
	 * @param cateogryIds カテゴリID。AND検索
	 * @param orders 並び順
	 * @return 検索結果
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	VideoEntry[] searchVideos(
			String text, String textLanguage, String matchingMethod
			, String[] categoryIds, Order[] orders)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * サムネイル画像(JPEG)を取得する。
	 * @param videoId 映像ID
	 * @return サムネイル
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	byte[] getThumbnail(String videoId)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 字幕の一覧を取得する。
	 * @param videoId 映像のID
	 * @param language 字幕の言語
	 * @return 字幕一覧
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	Subtitle[] getSubtitles(String videoId, String language)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 対応する言語を取得する。
	 * @return 対応言語
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	String[] getSupportedLanguages()
	throws ProcessFailedException;
}
