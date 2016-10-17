package org.pangaea.agrigrid.service.agriculture.dao.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;
import org.pangaea.agrigrid.service.agriculture.dao.EntityNotFoundException;
import org.pangaea.agrigrid.service.agriculture.dao.HibernateAdjacencyPairDao;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

import au.com.bytecode.opencsv.CSVReader;

public class QAImporter {
	public static void main(String[] args) throws Exception{
		InputStream is = QAImporter.class.getResourceAsStream("question_messages.csv");
		try{
/*
			new QAImporter().clearQAs();
			System.out.println("qas cleared.");
/*/
			System.out.println(
					new QAImporter().importQAs(is)
					+ " qas imported."
					);
//*/
		} finally{
			is.close();
		}
	}

	public void clearQAs() throws IOException, EntityNotFoundException{
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.beginTransaction();
		try{
			HibernateAdjacencyPairDao dao = f.getAdjacencyPairDao();
			dao.clear();
		} finally{
			c.commit();
		}
	}

	public int importQAs(InputStream is) throws IOException, EntityNotFoundException{
		int count = 0;
		DaoFactory f = DaoFactory.createInstance();
		DaoContext c = f.getContext();
		c.beginTransaction();
		try{
			HibernateAdjacencyPairDao dao = f.getAdjacencyPairDao();
			dao.clear();
			CSVReader r = new CSVReader(new InputStreamReader(is, "UTF-8"));
			r.readNext();
			List<Category> categories = new ArrayList<Category>();
			for(String[] values; (values = r.readNext()) != null;){
				if(values.length == 0) continue;
				if(values[0].length() == 0) continue;

				// [0] "qid"
				// [1-3] ,"q_en_1","q_en_2","q_en_3"
				// [4-6] ,"q_ja_1","q_ja_2","q_ja_3"
				// [7-9] ,"q_vi_1","q_vi_2","q_vi_3"
				// [10] ,"aid"
				// [11-17] ,"a_en_head","a_en_temp_1","a_en_temp_2","a_en_temp_3","a_en_temp_4","a_en_temp_5","a_en_foot"
				// [18-24] ,"a_ja_head","a_ja_temp_1","a_ja_temp_2","a_ja_temp_3","a_ja_temp_4","a_ja_temp_5","a_ja_foot"
				// [25-31] ,"a_vi_head","a_vi_temp_1","a_vi_temp_2","a_vi_temp_3","a_vi_temp_4","a_vi_temp_5","a_vi_foot"
				for(int i = 0; i < values.length; i++){
					String s = values[0];
					if(s == null){
						values[0] = "";
					} else{
						values[0] = s.trim();
					}
				}

				// first turns
				List<AdjacencyPairFirstTurn> firstTurns = new ArrayList<AdjacencyPairFirstTurn>();
				{
					StringBuilder enBuf = new StringBuilder();
					StringBuilder jaBuf = new StringBuilder();
					StringBuilder viBuf = new StringBuilder();
					int index = 1;
					for(StringBuilder b : new StringBuilder[]{enBuf, jaBuf, viBuf}){
						for(int i = 0; i < 3; i++){
							b.append(values[index]);
							index++;
						}
					}
					if(enBuf.length() > 0) firstTurns.add(new AdjacencyPairFirstTurn("en", enBuf.toString()));
					if(jaBuf.length() > 0) firstTurns.add(new AdjacencyPairFirstTurn("ja", jaBuf.toString()));
					if(viBuf.length() > 0) firstTurns.add(new AdjacencyPairFirstTurn("vi", viBuf.toString()));
				}

				List<List<AdjacencyPairSecondTurn>> secondTurns = new ArrayList<List<AdjacencyPairSecondTurn>>();
				{
					List<AdjacencyPairSecondTurn> st = new ArrayList<AdjacencyPairSecondTurn>();
					StringBuilder enBuf = new StringBuilder();
					StringBuilder jaBuf = new StringBuilder();
					StringBuilder viBuf = new StringBuilder();
					int index = 11;
					for(StringBuilder b : new StringBuilder[]{enBuf, jaBuf, viBuf}){
						for(int i = 0; i < 7; i++){
							b.append(values[index]);
							index++;
						}
					}
					if(enBuf.length() > 0) st.add(new AdjacencyPairSecondTurn("en", enBuf.toString()));
					if(jaBuf.length() > 0) st.add(new AdjacencyPairSecondTurn("ja", jaBuf.toString()));
					if(viBuf.length() > 0) st.add(new AdjacencyPairSecondTurn("vi", viBuf.toString()));
					secondTurns.add(st);
				}

				AdjacencyPair p = dao.addAdjacencyPair(firstTurns, secondTurns, categories);
				System.out.println(p.getAdjacencyPairId());
				count++;
			}
		} finally{
			c.commit();
		}

		return count;
	}
}
