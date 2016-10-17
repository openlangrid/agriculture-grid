package org.pangaea.agrigrid.service.api.agriculture.qa;

import jp.go.nict.langrid.service_1_2.InvalidParameterException;
import jp.go.nict.langrid.service_1_2.ProcessFailedException;

/**
 * <#if locale="ja">
 * Q&A管理サービス。
 * <#elseif locale="en">
 * </#if>
 * @author Takao Nakaguchi
 */
public interface QAManagementService extends QAService {
	/**
	 * <#if locale="ja">
	 * Q&Aを追加する。
	 * @param qas 各言語分のQ&A情報
	 * @param categoryIds カテゴリID
	 * @return 追加されたQ&AのID
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	String addQA(QA[] qas, String[] categoryIds)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * Q&Aを削除する。
	 * @param qaId 削除するQ&AのID
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	void deleteQA(String qaId)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * Q&Aを取得する。
	 * @param qaId 取得するQ&AのID
	 * @return Q&A情報。全言語分が返される。
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	QA[] getQA(String qaId)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * Q&A情報を設定する。既存の情報は破棄される。
	 * @param qaId 設定するQ&AのID
	 * @param entries Q&A情報。内部のqaIdは無視される。
	 * @return Q&A情報。全言語分が返される。
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	void setQA(String qaId, QA[] qas)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * Q&AのカテゴリIDを取得する。
	 * @param qaId カテゴリIDを取得するQ&AのID
	 * @return カテゴリID一覧
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	String[] getQACategoryIds(String qaId)
	throws InvalidParameterException, ProcessFailedException;

	/**
	 * <#if locale="ja">
	 * Q&AのカテゴリIDを設定する。既存のカテゴリ情報は破棄される。
	 * @param qaId Q&A ID
	 * @param categoryIds 設定するカテゴリID
	 * @throws InvalidParameterException 不正な引数が渡された
	 * @throws ProcessFailedException 処理に失敗した
	 * <#elseif locale="en">
	 * </#if>
	 */
	void setQACategoryIds(String qaId, String[] categoryIds)
	throws InvalidParameterException, ProcessFailedException;
}
