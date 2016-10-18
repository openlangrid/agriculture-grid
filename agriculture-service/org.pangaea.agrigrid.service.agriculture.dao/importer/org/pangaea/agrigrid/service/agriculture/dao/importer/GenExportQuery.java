package org.pangaea.agrigrid.service.agriculture.dao.importer;

public class GenExportQuery {
	public static void main(String[] args) throws Exception{
		String[] lang = {"en", "ja", "vi"};
		String[] qn = {"1", "2", "3"};
		String[] an = {"1", "2", "3", "4", "5"};
		StringBuilder b = new StringBuilder();
		b.append(q1);
		b.append("\n");
		for(String l : lang){
			for(String n : qn){
				b.append(String.format(q2_fmt, l, n));
				b.append("\n");
			}
		}
		b.append(q3);
		b.append("\n");
		for(String l : lang){
			b.append(String.format(q4_fmt, l));
			b.append("\n");
			for(String n : an){
				b.append(String.format(q5_fmt, l, n));
				b.append("\n");
			}
			b.append(String.format(q6_fmt, l));
			b.append("\n");
		}
		b.append(q7);
		b.append("\n");
		for(String l : lang){
			for(String n : qn){
				b.append(String.format(q8_fmt, l, n));
				b.append("\n");
			}
		}
		b.append(q9);
		b.append("\n");
		for(String l : lang){
			b.append(String.format(q10_fmt, l));
			b.append("\n");
			for(String n : an){
				b.append(String.format(q11_fmt, l, n));
				b.append("\n");
			}
			b.append(String.format(q12_fmt, l));
			b.append("\n");
		}
		b.append(q13);
		b.append("\n");
		System.out.println(b.toString());
	}

	private static String q1 = "SELECT q.id as qid";
	//                                q_en_1.expression as q_en_1
	private static String q2_fmt = ", q_%s_%s.expression as q_%1$s_%2$s";
	private static String q3 = ", qa.id as aid";
	//                                a_en_head.expression as a_en_head
	private static String q4_fmt = ", a_%s_head.expression as a_%1$s_head";
	//                                a_en_temp1.expression as a_en_temp_1
	private static String q5_fmt = ", a_%s_temp%s.expression as a_%1$s_temp_%2$s";
	private static String q6_fmt = ", a_%s_foot.expression as a_%1$s_foot";
	private static String q7 = " FROM `question_messages` q";
	//                           left join `question_messages_i18n` q_en_1 on (q_en_1.id=q.id and q_en_1.language_code='en' and q_en_1.sentence_id=1)
	private static String q8_fmt = " left join `question_messages_i18n` q_%s_%s on (q_%1$s_%2$s.id=q.id and q_%1$s_%2$s.language_code='%1$s' and q_%1$s_%2$s.sentence_id=%2$s)";
	private static String q9 = ", `answer_messages` qa";
	private static String q10_fmt = " left join `answer_snippets_i18n` a_%s_head on(qa.id=a_%1$s_head.id and a_%1$s_head.type='HEAD' and a_%1$s_head.language_code='%1$s')";
	private static String q11_fmt = " left join `answer_snippets_i18n` a_%s_temp%s on(qa.id=a_%1$s_temp%2$s.id and a_%1$s_temp%2$s.type='TEMPLATE' and a_%1$s_temp%2$s.language_code='%1$s' and a_%1$s_temp%2$s.sentence_id=%2$s)";
	private static String q12_fmt = " left join `answer_snippets_i18n` a_%s_foot on(qa.id=a_%1$s_foot.id and a_%1$s_foot.type='FOOT' and a_%1$s_foot.language_code='%1$s')";
	private static String q13 = " WHERE q.thread_id=qa.thread_id order by q.id";
}
