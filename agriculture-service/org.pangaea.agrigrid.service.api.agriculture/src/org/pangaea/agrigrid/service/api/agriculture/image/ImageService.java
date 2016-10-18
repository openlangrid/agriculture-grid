package org.pangaea.agrigrid.service.api.agriculture.image;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;

/**
 * <#if locale="ja">
 * 画像の検索を行うサービス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public interface ImageService {
	/**
	 * <#if locale="ja">
	 * 全てのタグの一覧を取得する。
	 * @param language 取得する言語
	 * @return カテゴリ一覧
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	Category[] listAllCategories(String language)
	throws ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 各言語でのカテゴリ名を取得する。
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
	 * 画像を検索する。
	 * 画像のタイトルキャプチャに対して検索する文字列と、画像に付加されているタグを指定できる。
	 * @param text 検索するテキスト
	 * @param textLanguage テキストの言語
	 * @param matchingMethod マッチング方法(COMPLETE - 完全一致, PARTIAL - 部分一致, PREFIX - 前方一致, SUFFIX - 後方一致) 
	 * @param categoryIds カテゴリID。AND検索
	 * @param orders 並び順
	 * @return 検索結果
	 * @throws InvalidParameterException 不正なパラメータが渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	ImageEntry[] searchImages(
			String text, String textLanguage, String matchingMethod
			, String[] categoryIds, Order[] orders)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * サムネイル画像(JPEG)を取得する。
	 * @param imageId 画像ID
	 * @return サムネイル
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	byte[] getThumbnail(String imageId)
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
