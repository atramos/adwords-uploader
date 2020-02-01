package com.zigabyte.adwords.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

final public class PGroup {
	
	final private Map<String,PAdv> adsHash = new HashMap<String,PAdv>();
	final private Map<String,PKeyWord> kwHash = new HashMap<String,PKeyWord>();
	
	final private String groupName;
	Long idGroup;
	boolean dirty = false;

	private PGroup(Long idGroup, String name) {
		
		if(name.trim().length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.groupName = name;
		this.idGroup = idGroup;
	}
	
	private PGroup(String name) {
		this(null, name);
	}

	public String getGroupName() {
		return groupName;
	}

	/*package*/ Long getId() {
		if(idGroup == null) {
			throw new IllegalStateException("Group not initialized: " + this.toString());
		}
		return idGroup;
	}

	public double getMaxCpcUSD() {
		return 0.10;
	}

	public boolean inDb() {
		return !dirty && idGroup != null;
	}

	/*package*/ void setId(Long idGroup) {
		if(!idGroup.equals(this.idGroup)) {
			this.dirty = true;
		}
		this.idGroup = idGroup;
	}

	@Override
	public String toString() {
		return super.toString() + "::[" + groupName + "][" + idGroup + "]," + 
			"inDb=" + inDb();
	}

	/*package*/ boolean contains(String ad_name_key) {
		return adsHash.containsKey(ad_name_key);
	}
	
	/*package*/ PAdv getAdv(String ad_name_key) {
		return adsHash.get(ad_name_key);
	}
	
	/*package*/ void addAdv(PAdv adv) {
		// perform advanced validations to avoid mysterious API errors
		if(adsHash.size() >= 50) {
			throw new IndexOutOfBoundsException("Too many Ads in Group: " 
					+ groupName + "\n" + adsHash.toString());
		}
		adv.validateLengths(new ArrayList<PKeyWord>(kwHash.values()));
		adsHash.put(adv.getNameKey(), adv);
	}
	/*package*/ void addKeyword(PKeyWord kw) {
		// perform advanced validations to avoid mysterious API errors
		for(PAdv ad : adsHash.values()) {
			ad.validateLengths(kw);
		}
		kwHash.put(kw.getString(), kw);
	}

	private static Map<String,PGroup> groupsHash = new HashMap<String,PGroup>();

	public static PGroup getInstance(String strGroup, Connection conn, Long campaignId) throws SQLException {
		if (groupsHash.containsKey(strGroup)) {
			return groupsHash.get(strGroup);
		}

		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		PGroup g;

		try {
			rs = stmt.executeQuery("SELECT * FROM groups WHERE str_group = '" + strGroup +"' AND campaign_id='" + campaignId + "'");

			if (rs.next()) {
				g = new PGroup(new Long(rs.getLong("id_group")),strGroup);
				System.out.println("Group read from database: " + g.toString());
			}
			else {
				g = new PGroup(strGroup);
				System.out.println("Group not in database: " + g.toString());
			}
		}
		finally {
			stmt.close();
		}

		groupsHash.put(strGroup, g);
		return g;
	}

	public static void flushGroups() throws SQLException {
		
		for(PGroup g : groupsHash.values()) {
			if(g.idGroup == null) {
				throw new NullPointerException();
			}
			if(!g.inDb()) {
				System.out.println("Writing to database: " + g.toString());
				PersistentFactory.insertGroup(g);
			}
		}
	}
	
}
