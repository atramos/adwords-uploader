package com.zigabyte.adwords.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.google.api.adwords.v200909.cm.AdGroup;
import com.mysql.jdbc.exceptions.jdbc4.MySQLDataException;
import com.zigabyte.adwords.plugin.Db;

public class PersistentFactory extends Db {
	
	private static PersistentFactory awd;
	private static Long campaignId;
	
	public static void initDB(Configuration configuration) throws Exception {
		awd = (PersistentFactory) Db.initDB(new PersistentFactory(),
				configuration.getCustomerDbHost(),
				configuration.getAdwordsDatabase(),
				configuration.getAdwordsDbUser(),
				configuration.getAdwordsDbPassword());
		
		campaignId = configuration.getCampaignId();
	}

	public static PGroup getGroup(String strGroup) throws SQLException {
		return PGroup.getInstance(strGroup, awd.conn, campaignId);
	}

	/*package*/ static void insertGroup(PGroup g) throws SQLException {
		Statement stmt = awd.conn.createStatement();
		try {
			String query = "INSERT INTO groups (str_group,id_group,campaign_id) VALUES ('" + g.getGroupName() +"'," + g.getId() + "," + campaignId + ")";
			log(query);
			stmt.execute(query);
		}
		finally {
			stmt.close();
		}
	}

	/*package*/ static void insertAd(PAdv a) throws SQLException {
		Statement stmt = null;

		try {
			stmt = awd.conn.createStatement();
			String query = 
				"INSERT INTO ads (str_group, name_key, str_ad, id_ad,description1, description2, displayUrl, url, campaign_id) VALUES ('"
					+ a.getGroup().getGroupName()
					+ "','"
					+ a.getNameKey()
					+ "','"
					+ a.getHeadline()
					+ "'," + a.getId() + ",'"
					+ a.getDescription1()
					+ "' "
					+ ",'"
					+ a.getDescription2()
					+ "' "
					+ ",'"
					+ a.getDisplayUrl()
					+ "' " + ",'" + a.getUrl() + "', " + campaignId + ")";
			log(query);
			stmt.execute(query);
		}
		finally {
//			it is a good idea to release
//			resources in a finally{} block
//			in reverse-order of their creation
//			if they are no-longer needed
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
	}

	private final static boolean debug = System.getProperty("sqldebug") != null;
	
	private static void log(String query) {
		if(debug) System.out.println(query);
	}

	/*package*/ static void insertKeyword(PKeyWord k) throws SQLException {
		Statement stmt = awd.conn.createStatement();

		String query = "INSERT INTO keywords (str_group,str_keyword, id_keyword, str_price, str_promo, campaign_id) VALUES ('"
			+ k.getG().getGroupName()
			+ "','"
			+ k.getString()
			+ "',"
			+ k.getId()
			+ ",'"
			+ k.getPriceParam1()
			+ "','"
			+ k.getPriceParam2() + "'," + campaignId + ")";

		try {
			log(query);
			stmt.execute(query);
		}
		finally {
			stmt.close();
		}
	}

	public PersistentFactory() {
	}


	public static PKeyWord getKeyword(PGroup g, String strKeyword, String strStdPrice, String strPromoPrice) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;

		PKeyWord k = new PKeyWord(strKeyword, strStdPrice, strPromoPrice, g);

		try {
			stmt = awd.conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM keywords WHERE str_group = '" + k.getG().getGroupName() +"' AND str_keyword = '" + k.getString() + "' AND campaign_id='" + campaignId + "'");

			if (rs.next()) {
				String regular = rs.getString("str_price").trim();
				String promo = rs.getString("str_promo").trim();
				k = new PKeyWord(new Long(rs.getLong("id_keyword")),strKeyword, strStdPrice, !strStdPrice.trim().equals(regular),strPromoPrice, !strPromoPrice.trim().equals(promo), g);
			}
		}
		finally {
//			it is a good idea to release
//			resources in a finally{} block
//			in reverse-order of their creation
//			if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}

		return k;
	}

	public static PAdv getAdv(String name_key, String ad_title,  String description1, 
			String description2, String displayUrl, String url, PGroup theGroup) throws SQLException {

		if(theGroup.contains(name_key)) {
			return theGroup.getAdv(name_key);
		}
		
		Statement stmt = null;
		ResultSet rs = null;

		try {
			AdBean adBean = new AdBean(name_key, ad_title, description1,
					description2, displayUrl, url);
			
			PAdv dbAd;

			stmt = awd.conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM ads WHERE str_group = '"
					+ theGroup.getGroupName() + "' AND name_key = '"
					+ adBean.getNameKey() + "' AND campaign_id='" + campaignId + "'");

			if (rs.next()) {
				dbAd = new PAdv(
						new Long(rs.getLong("id_ad")),
						rs.getString("name_key"),
						rs.getString("str_ad"),
						rs.getString("description1"),
						rs.getString("description2"),
						rs.getString("displayUrl"),
						rs.getString("url"),
						theGroup);

				if (!dbAd.equals(adBean)) {
					dbAd.setUpdate(true);
					dbAd.setHeadline(adBean.getHeadline());
					dbAd.setDescription1(adBean.getDescription1());
					dbAd.setDescription2(adBean.getDescription2());
					dbAd.setDisplayUrl(adBean.getDisplayUrl());
					dbAd.setUrl(adBean.getUrl());
				}
			}
			else {				
				dbAd = new PAdv(null, adBean, theGroup);
			}
			
			theGroup.addAdv(dbAd);
			return dbAd;
		}
		finally {
			//			it is a good idea to release
			//			resources in a finally{} block
			//			in reverse-order of their creation
			//			if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) { } // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
	}
	
	/*package*/ static void updatePrice(PKeyWord k) throws SQLException {
		Statement stmt = null;

		try {
			stmt = awd.conn.createStatement();
			String query = "UPDATE keywords set str_price = '"
					+ k.getPriceParam1() + "',str_promo = '" + k.getPriceParam2()
					+ "' WHERE id_keyword = " + k.getId().toString()
					+ " AND campaign_id = " + campaignId;
			log(query);
			stmt.execute(query);
		}
		finally {
//			it is a good idea to release
//			resources in a finally{} block
//			in reverse-order of their creation
//			if they are no-longer needed
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
	}

	/*package*/ static void update(PAdv adv) throws SQLException {
		Statement stmt = null;

		try {
			stmt = awd.conn.createStatement();
			String query = "UPDATE ads set " +
				"description1 = '" + adv.getDescription1() + "'," +
				"description2 = '" + adv.getDescription2() + "'," +
				"displayUrl = '" + adv.getDisplayUrl() + "'," +
				"url = '" + adv.getUrl() + "' WHERE id_ad = " +
					adv.getId().toString()
				+ " AND campaign_id = " + campaignId;

			log(query);
			stmt.execute(query);
		}
		finally {
//			it is a good idea to release
//			resources in a finally{} block
//			in reverse-order of their creation
//			if they are no-longer needed
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) { } // ignore
				stmt = null;
			}
		}
	}
}
