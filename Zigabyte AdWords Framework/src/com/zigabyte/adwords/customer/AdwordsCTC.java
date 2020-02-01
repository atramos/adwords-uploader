package com.zigabyte.adwords.customer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.zigabyte.adwords.plugin.AbstractProduct;
import com.zigabyte.adwords.plugin.Db;
import com.zigabyte.adwords.plugin.ICustomerDb;

public class AdwordsCTC extends Db implements ICustomerDb {

	 public InputStream findFileInClasspath(String fileName) throws IOException
	 {
	         URL url = getClass().getClassLoader().getResource(fileName);
	         if (url == null){
	             throw new FileNotFoundException(fileName);
	         }
	         return url.openStream();
	     }

	@Override
	public List<AbstractProduct> getProducts() throws Exception {

		String tableSetup =
			IOUtils.toString(findFileInClasspath("adwords_config/work_query.sql"));

		int limitSQL = Integer.parseInt(System.getProperty("limitSQL", "-1"));
		if(limitSQL > 0) {
			tableSetup += " limit " + limitSQL;
		}
		
		List<AbstractProduct> products = new LinkedList<AbstractProduct>();

		Statement stmt = conn.createStatement();
		stmt.executeUpdate(tableSetup);
		ResultSet rs = stmt.executeQuery("SELECT * FROM T1 WHERE N >= 2");
		while (rs.next()) {
			ProductCTC product = new ProductCTC(rs.getString("ADDRESS"), rs.getString("AREA"));
			
			product.advertiseBuilding(rs.getInt("MIN_ALL"), rs.getInt("MAX_ALL"),
									  rs.getInt("MIN_1BR"), rs.getInt("MIN_2BR"));
			
			products.add(product);
		}
		rs.close();
		
		Map<String, AreaInfo> areas = getAreas(stmt);
		
		rs = stmt.executeQuery("SELECT ADDRESS, AREA FROM T1 WHERE N = 1");
		while (rs.next()) {
			String area = rs.getString("AREA");
			if(areas.containsKey(area)) {
				ProductCTC product =  new ProductCTC(rs.getString("ADDRESS"), area);
				product.advertiseArea(areas.get(area));
				products.add(product);				
			}
		}
		rs.close();

		stmt.close();
		return products;
	}

	private Map<String, AreaInfo> getAreas(Statement stmt) throws SQLException,
			IOException {

		Map<String, AreaInfo> areas = new HashMap<String,AreaInfo>();
		
		ResultSet rs = stmt.executeQuery(IOUtils.toString(
				findFileInClasspath("adwords_config/2nd_query.sql")));
		
		while(rs.next()) {
			AreaInfo ai = new AreaInfo();
			String name = rs.getString("AREA");
			ai.n = rs.getInt("N_AREA");
			ai.min_all = rs.getInt("MIN_ALL");
			ai.max_all = rs.getInt("MAX_ALL");
			ai.min_1br = rs.getInt("MIN_1BR");
			ai.min_2br = rs.getInt("MIN_2BR");
			areas.put(name, ai);
		}
		rs.close();
		return areas;
	}

	class AreaInfo {
		int n, min_all, max_all, min_1br, min_2br;
	}

}
