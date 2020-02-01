package com.zigabyte.adwords.plugin;

import java.util.List;

public abstract class AbstractCustomerDb extends Db implements ICustomerDb {

	public abstract List<AbstractProduct> getProducts() throws Exception;

}
