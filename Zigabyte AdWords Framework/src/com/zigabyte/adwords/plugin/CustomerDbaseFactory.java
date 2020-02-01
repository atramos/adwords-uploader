package com.zigabyte.adwords.plugin;


import java.util.List;

import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.Configuration;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;

public class CustomerDbaseFactory {
	static ICustomerDb customerDb;

	public static void initDB(Configuration configuration) throws Exception {
		customerDb = (ICustomerDb) Db.initDB(configuration.getCustomerDbClass(),
				configuration.getCustomerDbHost(),
				configuration.getCustomerDatabase(),
				configuration.getCustomerDbUser(),
				configuration.getCustomerDbPassword());
	}

	public static ICustomerDb getDb() {
		return customerDb;
	}

	public static List<PKeyWord> getRelatedKeywords(AbstractProduct product) throws Exception {
		return product.getKeywords();
	}

	public static List<PAdv> getRelatedAdvs(AbstractProduct product) throws Exception {
		return product.getAdvs();
	}

	public static PGroup getGroupFromProduct(AbstractProduct product) throws Exception {
		return product.getGroup();
	}
}
