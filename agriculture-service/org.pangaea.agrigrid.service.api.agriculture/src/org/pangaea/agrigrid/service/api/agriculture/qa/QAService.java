package org.pangaea.agrigrid.service.api.agriculture.qa;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

import org.pangaea.agrigrid.service.api.agriculture.Category;
import org.pangaea.agrigrid.service.api.agriculture.Order;

/**
 * <#if locale="ja">
 * Q&A検索、管理サービス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public interface QAService {
	/**
	 * <#if locale="ja">
	 * カテゴリーの一覧を取得する。
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
	 * Q&Aを検索する。
	 * @param text 検索するテキスト(質問文及び応答文が対象)
	 * @param textLang テキストの言語
	 * @param matchingMethod マッチング方法(COMPLETE - 完全一致, PARTIAL - 部分一致, PREFIX - 前方一致, SUFFIX - 後方一致) 
	 * @param categoryIds カテゴリID。AND指定される
	 * @param orders 並び順
	 * @return 検索結果
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	QAEntry[] searchQAs(String text, String textLang, String matchingMethod
			, String[] categoryIds, Order[] orders)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * 指定された言語分のQ&A情報を取得する。
	 * @param qaId Q&A ID
	 * @param languages 言語
	 * @return 取得結果
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	QAEntry[] getQAs(String qaId, String[] languages)
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
