package com.zigabyte.adwords.plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.zigabyte.adwords.core.PersistentFactory;
import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;
import com.zigabyte.adwords.sample.Product;

public abstract class SimplifiedCustomerDb extends AbstractCustomerDb implements ICustomerDb {
	Set<StringPair> s;

	public SimplifiedCustomerDb() {
		s = new HashSet<StringPair>();
	}

	public PGroup getGroupFromProduct(AbstractProduct product) throws SQLException {
		PGroup g = null;
		String query = getGroupFromProductQuery(product);

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery(query);

			if (rs.next()) {
				String strGroup = getGroupStringFromRs(rs);
				g = PersistentFactory.getGroup(strGroup);
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

		return g;
	}

	protected abstract String getGroupStringFromRs(ResultSet rs) throws SQLException;

	protected abstract String getGroupFromProductQuery(AbstractProduct product);

	public List<String> getRawRelatedKeywords(AbstractProduct product) throws Exception {

		String query = getRelatedKeywordsQuery(product);

		Statement stmt = null;
		ResultSet rs = null;

		List<String> keywords = new LinkedList<String>();

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery(query);

			while (rs.next()) {
				String strKeyword = getKeywordStringFromRs(rs);
				keywords.add(strKeyword);
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

		return keywords;
	}

	protected abstract String getKeywordStringFromRs(ResultSet rs) throws Exception;

	protected abstract String getRelatedKeywordsQuery(AbstractProduct product);

	public List<PAdv> getRawRelatedAdvs(AbstractProduct product) throws Exception {

		String query = getRelatedAdvsQuery(product);

		Statement stmt = null;
		ResultSet rs = null;

		List<PAdv> advs = new LinkedList<PAdv>();

		try {
			stmt = conn.createStatement();

			rs = stmt.executeQuery(query);

			while (rs.next()) {
				PAdv adv = getRawAdvFromRs(rs,getGroupFromProduct(product));
				advs.add(adv);
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

		return advs;
	}

	protected abstract PAdv getRawAdvFromRs(ResultSet rs, PGroup g) throws Exception;

	protected abstract String getRelatedAdvsQuery(AbstractProduct product);

	public List<PKeyWord> getRelatedKeywords(AbstractProduct product) throws Exception {
		List<PKeyWord> keywords = new LinkedList<PKeyWord>();

		List<String> l = getRawRelatedKeywords(product);
		Iterator<String> it = l.iterator();

		while (it.hasNext()) {
			String strKeyword = it.next();

			PGroup g = getGroupFromProduct(product);

			Product p = (Product) product;
			String strStdPrice = Double.toString(p.getNormal_price());
			String strPromoPrice = Double.toString(p.getPromo_price());

			PKeyWord k = PersistentFactory.getKeyword(g,strKeyword, strStdPrice, strPromoPrice);
			keywords.add(k);
		}

		return keywords;
	}

	public List<PAdv> getRelatedAdvs(AbstractProduct product) throws Exception {
		List<PAdv> advs = new LinkedList<PAdv>();

		List<PAdv> l = getRawRelatedAdvs(product);
		Iterator<PAdv> it = l.iterator();

		while (it.hasNext()) {
			PAdv a = it.next();

			String strA = a.getNameKey();
			String strG = a.getGroup().getGroupName();
			StringPair p = new StringPair(strA, strG);

			if (!s.contains(p)) {
				advs.add(a);
				s.add(p);
			} else {
				System.out.println("Discarding repetition of adv " + strA + " in group " + strG);
			}
		}

		return advs;
	}

	@Override
	public List<AbstractProduct> getProducts() throws Exception {
	
		String query = getProductsQuery();
	
		Statement stmt = null;
		ResultSet rs = null;
	
	
		List<AbstractProduct> products = new LinkedList<AbstractProduct>();
	
		try {
			stmt = conn.createStatement();
	
			rs = stmt.executeQuery(query);
	
			while (rs.next()) {
				AbstractProduct product = createProductFromRs(rs);
				products.add(product);
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
	
		return products;
	}

	protected abstract String getProductsQuery();

	protected abstract AbstractProduct createProductFromRs(ResultSet rs)
			throws Exception;
}
