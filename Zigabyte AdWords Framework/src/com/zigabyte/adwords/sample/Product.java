package com.zigabyte.adwords.sample;

import java.util.List;

import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;
import com.zigabyte.adwords.plugin.AbstractProduct;

public class Product extends AbstractProduct {
	String name;
	String category;
	double normal_price;
	double promo_price;

	public Product(String name, String category, double normal_price, double promo_price) {
		this.name = name;
		this.category = category;
		this.normal_price = normal_price;
		this.promo_price = promo_price;
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public double getNormal_price() {
		return normal_price;
	}

	public double getPromo_price() {
		return promo_price;
	}

	@Override
	public PGroup getGroup() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PKeyWord> getKeywords() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PAdv> getAdvs() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
