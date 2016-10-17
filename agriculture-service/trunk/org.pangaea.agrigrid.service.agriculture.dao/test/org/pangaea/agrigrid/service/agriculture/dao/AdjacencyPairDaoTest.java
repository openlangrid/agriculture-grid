package org.pangaea.agrigrid.service.agriculture.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPair;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairFirstTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.AdjacencyPairSecondTurn;
import org.pangaea.agrigrid.service.agriculture.dao.entity.Category;

public class AdjacencyPairDaoTest {
	@Before
	public void setUp() throws Exception{
		context = DaoFactory.createInstance().getContext();
		context.beginTransaction();
		dao = new HibernateAdjacencyPairDao(context);
		dao.clear();
	}

	@After
	public void tearDown() throws Exception{
		context.commit();
	}

	@Test
	public void test_add() throws Exception{
		add();

		Assert.assertEquals(1, dao.getTotalCount());
	}

	@Test
	public void test_add_delete() throws Exception{
		AdjacencyPair p = add();

		dao.delete(p.getAdjacencyPairId());

		Assert.assertEquals(0, dao.getTotalCount());
	}

	@Test
	public void test_add_search_1() throws Exception{
		add();

		List<AdjacencyPair> ret = dao.searchAdjacencyPairs(
				"hello", "en", MatchingMethod.COMPLETE, new String[]{}, new Order[]{});
		Assert.assertEquals(1, ret.size());

	}

	@Test
	public void test_add_search_secondTurns() throws Exception{
		add();

		List<AdjacencyPair> ret = dao.searchAdjacencyPairs(
				"world", "en", MatchingMethod.COMPLETE, new String[]{}, new Order[]{});
		Assert.assertEquals(1, ret.size());
	}

	@Test
	public void test_add_search_secondTurns_2() throws Exception{
		add();

		List<AdjacencyPair> ret = dao.searchAdjacencyPairs(
				"w", "en", MatchingMethod.COMPLETE, new String[]{}, new Order[]{});
		Assert.assertEquals(0, ret.size());
	}

	@Test
	public void test_add_get() throws Exception{
		long id = add().getAdjacencyPairId();

		AdjacencyPair ret = dao.get(id);
		Assert.assertEquals("hello", ret.getFirstTurns().get("en").getText());
	}

	private AdjacencyPair add(){
		List<AdjacencyPairFirstTurn> firstTurns = new ArrayList<AdjacencyPairFirstTurn>();
		firstTurns.add(new AdjacencyPairFirstTurn("en", "hello"));
		firstTurns.add(new AdjacencyPairFirstTurn("ja", "こんにちは"));
		List<List<AdjacencyPairSecondTurn>> secondTurns = new ArrayList<List<AdjacencyPairSecondTurn>>();
		secondTurns.add(Arrays.asList(
				new AdjacencyPairSecondTurn("en", "world")
				, new AdjacencyPairSecondTurn("ja", "世界")
				));
		secondTurns.add(Arrays.asList(
				new AdjacencyPairSecondTurn("en", "universe")
				, new AdjacencyPairSecondTurn("ja", "宇宙")
				));
		List<Category> categories = new ArrayList<Category>();
		categories.add(new Category("aec001001001"){{
			getTexts().put("en", "category1");
			getTexts().put("ja", "カテゴリ1");
			}});
		return dao.addAdjacencyPair(firstTurns, secondTurns, categories);
	}

	private DaoContext context;
	private HibernateAdjacencyPairDao dao;
}
