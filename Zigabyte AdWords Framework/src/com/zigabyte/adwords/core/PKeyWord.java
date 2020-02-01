package com.zigabyte.adwords.core;

final public class PKeyWord {
	
	final private String str_keyword;
	final private PGroup g;
	final private String std_price;
	final private String promo_price;

	private Long id_keyword;
	boolean updatePrice1;
	boolean updatePrice2;

	public String getPriceParam2() {
		return promo_price;
	}

	public String getPriceParam1() {
		return std_price;
	}

	public boolean updatePrice() {
		return updatePrice1() || updatePrice2();
	}

	public boolean updatePrice1() {
		return updatePrice1;
	}

	public boolean updatePrice2() {
		return updatePrice2;
	}

	/*package*/ PKeyWord (Long id_keyword, String str_keyword, String std_price, boolean updatePrice1, String promo_price, boolean updatePrice2, PGroup g) {
		this.id_keyword = id_keyword;
		this.str_keyword = str_keyword;
		this.std_price = std_price;
		this.promo_price = promo_price;
		this.updatePrice1 = updatePrice1;
		this.updatePrice2 = updatePrice2;
		this.g = g;
		g.addKeyword(this);
	}

	/*package*/ PKeyWord (String str_keyword,  String strStdPrice, String strPromoPrice, PGroup g) {
		this(null, str_keyword, strStdPrice, true, strPromoPrice, true, g);
	}

	public PGroup getG() {
		return g;
	}

	/*package*/ Long getId() {
		return id_keyword;
	}

	public String getString() {
		return str_keyword;
	}
	
	@Override
	public String toString() {
		return str_keyword;
	}

	/*package*/ boolean inDb() {
		return g.inDb() && getId() != null;
	}

	/*package*/ void setId(Long id) {
		this.id_keyword = id;
	}
}
