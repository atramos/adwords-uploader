package com.zigabyte.adwords.plugin;

import java.util.List;

import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;

public interface ICustomerDb extends IDb {

	List<AbstractProduct> getProducts() throws Exception;

//	Group getGroupFromProduct(IProduct product) throws Exception;
//
//	List<KeyWord> getRelatedKeywords(IProduct product) throws Exception;
//
//	List<Adv> getRelatedAdvs(IProduct product) throws Exception;
}
