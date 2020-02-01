package com.zigabyte.adwords.sample;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.zigabyte.adwords.core.PersistentFactory;
import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.plugin.SimplifiedCustomerDb;
import com.zigabyte.adwords.plugin.AbstractProduct;

public class MyDb extends SimplifiedCustomerDb {

	@Override
	protected AbstractProduct createProductFromRs(ResultSet rs) throws Exception {
		return new Product(
				rs.getString("name"),
				rs.getString("category"),
				rs.getDouble("Normal price"),
				rs.getDouble("Promo price"));
	}

	@Override
	protected String getProductsQuery() {
		return "SELECT * from products";
	}

	@Override
	protected String getKeywordStringFromRs(ResultSet rs) throws Exception {
		return rs.getString("keyword");
	}

	private static String URL = "http://www.mysite.com";


	@Override
	protected PAdv getRawAdvFromRs(ResultSet rs, PGroup g) throws Exception {
		return PersistentFactory.getAdv(
				rs.getString("adv"),
				rs.getString("adv"),
				rs.getString("description1"),
				rs.getString("description2"),
				URL,
				URL,
				g);
	}

	@Override
	protected String getRelatedAdvsQuery(AbstractProduct product) {
		return "SELECT * FROM products_advs WHERE product_name = '" + ((Product) product).getName() + "'";
	}

	@Override
	protected String getRelatedKeywordsQuery(AbstractProduct product) {
		return "SELECT * FROM products_keywords WHERE product_name = '" + ((Product) product).getName() + "'";
	}

	@Override
	protected String getGroupFromProductQuery(AbstractProduct product) {
		return "SELECT * FROM category_group WHERE category = '" + ((Product) product).getCategory() + "'";
	}

	@Override
	protected String getGroupStringFromRs(ResultSet rs) throws SQLException {
		return rs.getString("group");
	}


}
