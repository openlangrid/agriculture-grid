package org.pangaea.agrigrid.service.agriculture.dao.entity;

import java.util.Calendar;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pangaea.agrigrid.service.agriculture.dao.DaoContext;
import org.pangaea.agrigrid.service.agriculture.dao.DaoFactory;

public class AdjacencyPairTest {
	@Before
	public void setUp() throws Exception{
		factory = DaoFactory.createInstance();
		context = factory.getContext();
		session = context.getSession();
		session.beginTransaction();
		for(Object o : session.createCriteria(AdjacencyPair.class).list()){
			session.delete(o);
		}
	}

	@After
	public void tearDown() throws Exception{
		session.getTransaction().commit();
	}

	@Test
	public void test_list() throws Exception{
		session.createCriteria(AdjacencyPair.class).list();
	}

	@Test
	public void test_add() throws Exception{
		AdjacencyPair p = new AdjacencyPair();
		Calendar now = Calendar.getInstance();
		p.setCreatedAt(now);
		p.setUpdatedAt(now);
		session.persist(p);
		
		AdjacencyPairFirstTurn ft = new AdjacencyPairFirstTurn("en", "hello");
		ft.setAdjacencyPairId(p.getAdjacencyPairId());
		p.getFirstTurns().put("en", ft);

		{	AdjacencyPairSecondTurnGroup sg = new AdjacencyPairSecondTurnGroup();
			sg.setAdjacencyPairId(p.getAdjacencyPairId());
			session.persist(sg);
			AdjacencyPairSecondTurn st = new AdjacencyPairSecondTurn("en", "world");
			st.setGroupId(sg.getGroupId());
			sg.getSecondTurns().put("en", st);
			p.getSecondTurnGroup().add(sg);
		}
		{	AdjacencyPairSecondTurnGroup sg = new AdjacencyPairSecondTurnGroup();
			sg.setAdjacencyPairId(p.getAdjacencyPairId());
			session.persist(sg);
			AdjacencyPairSecondTurn st = new AdjacencyPairSecondTurn("en", "universe");
			st.setGroupId(sg.getGroupId());
			sg.getSecondTurns().put("en", st);
			p.getSecondTurnGroup().add(sg);
		}
	}

	private DaoFactory factory;
	private DaoContext context;
	private Session session;
}
