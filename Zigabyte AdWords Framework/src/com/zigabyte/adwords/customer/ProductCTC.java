package com.zigabyte.adwords.customer;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.zigabyte.adwords.core.PersistentFactory;
import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;
import com.zigabyte.adwords.customer.AdwordsCTC.AreaInfo;
import com.zigabyte.adwords.plugin.AbstractProduct;

class ProductCTC extends AbstractProduct {
	
	final private String address;
	final private String area;

	private PGroup group;
	
	final private List<PKeyWord> productKeywords;
	final private List<PAdv> productAds;
	
	private final String uri = "?q={Keyword:err}+Chicago+Illinois&aw=1";

	ProductCTC(String address, String area) throws SQLException {
		
		// data cleanup
		address = address.replaceAll(" UNIT.*$", "");
		
		if(area.trim().length() == 0) {
			throw new IllegalArgumentException(address + ": " + area);
		}
		if(address.length() > PAdv.MAX_TITLE_LEN) {
			throw new IllegalArgumentException(address);
		}
		
		this.address = address;
		this.area = area;
		this.productKeywords = new ArrayList<PKeyWord>();
		this.productAds = new ArrayList<PAdv>();
	}		

	public PGroup getGroup() {
		if(group == null) { 
			throw new NullPointerException();
		}
		return group;
	}

	public List<PKeyWord> getKeywords() {
		return productKeywords;
	}

	void advertiseBuilding(int min_all, int max_all, int min_1br, int min_2br) throws SQLException {

		this.group = PersistentFactory.getGroup(area + " Buildings");

		NumberFormat nf = new DecimalFormat("$###,###");
		PKeyWord kw = PersistentFactory.getKeyword(this.group, this.address, nf.format(min_all), nf.format(max_all));
		productKeywords.add(kw);
		
		String adTitle;
		String adKey;

		if(this.address.length() + 7 > PAdv.MAX_TITLE_LEN) {
			adTitle = "{Keyword:err}";
			adKey = this.area + ":Long:Address";
		}
		else {
			adTitle = "{Keyword:err} Condos";
			adKey = this.area + ":Short:Address";
		}

		productAds.add(PersistentFactory.getAdv(
				adKey + ":1", adTitle,
				"For Sale in " + this.area,
				"Condos starting from {param1}",
				"http://www.chicagotopcondos.com",
				"http://www.chicagotopcondos.com/" + uri,
				this.group));

		productAds.add(PersistentFactory.getAdv(
				adKey + ":2", adTitle,
				"Condos from {param1}",
				"View listings online now for free.",
				"http://www.chicagotopcondos.com",
				"http://www.chicagotopcondos.com/" + uri,
				this.group));

		productAds.add(PersistentFactory.getAdv(
				adKey + ":3", adTitle,
				"Condos at {param1} to {param2}", // 35 char
				"On sale now. Great time to buy.",
				"http://www.chicagotopcondos.com",
				"http://www.chicagotopcondos.com/" + uri,
				this.group));
	}

	public void advertiseArea(AreaInfo areaInfo) throws SQLException {
		// this is used when advertising single units - due to IDX agreement, promote the area instead.
		
		this.group = PersistentFactory.getGroup(area + " Units");

		NumberFormat nf = new DecimalFormat("$###,###");
		PKeyWord kw = PersistentFactory.getKeyword(this.group, this.address, nf.format(areaInfo.min_all), nf.format(areaInfo.max_all));
		productKeywords.add(kw);
		
		String adTitle;
		String adKey;

		if(this.area.length() + 7 > PAdv.MAX_TITLE_LEN) {
			adTitle = this.area; //"{Keyword:err}" => address
			adKey = this.area + ":Long:Area";
		}
		else {
			adTitle = this.area + " Condos";
			adKey = this.area + ":Short:Area";
		}

		productAds.add(PersistentFactory.getAdv(
				adKey + ":1", adTitle,
				"Near {Keyword:err}",
				"Condos from {param1} and up",
				"http://www.chicagotopcondos.com",
				"http://www.chicagotopcondos.com/" + uri,
				this.group));

// not applicable to single-unit (param1 and param2 are identical)
//		
//		productAds.add(AdWordsDb.getAdv(
//				adKey + ":2", adTitle,
//				"Condos from {param1} to {param2}",
//				"View listings online now for free.",
//				"http://www.chicagotopcondos.com",
//				"http://www.chicagotopcondos.com/" + uri,
//				this.group));

		productAds.add(PersistentFactory.getAdv(
				adKey + ":3", adTitle,
				"Condos for sale in your area.",
				"Near {Keyword:err}",
				"http://www.chicagotopcondos.com",
				"http://www.chicagotopcondos.com/" + uri,
				this.group));
	}

	@Override
	public List<PAdv> getAdvs() throws Exception {
		return this.productAds;
	}
}
