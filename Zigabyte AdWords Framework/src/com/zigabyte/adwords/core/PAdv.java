package com.zigabyte.adwords.core;

import java.util.List;
import java.util.regex.Pattern;

final public class PAdv extends AdBean {

	public static final int MAX_DESCR_LEN = 35;
	public static final int MAX_TITLE_LEN = 25;
	
	final private PGroup g;
	private Long id_ad;
	
	private boolean update;

	/*package*/ PAdv (Long db_id, AdBean b, PGroup g) {
		this(db_id, b.getNameKey(), b.getHeadline(), b.getDescription1(), 
				b.getDescription2(), b.getDisplayUrl(), b.getUrl(), g);
	}
	
	/*package*/ PAdv (Long db_id, String name_key, String headline, String description1, String description2, String displayUrl, String url, PGroup g) {
		
		super(name_key, headline, description1, description2, displayUrl, url);
		
		if(headline.replaceAll("\\{[^\\}]*\\}", "").length() > MAX_TITLE_LEN) {
			throw new IllegalArgumentException(">" + MAX_TITLE_LEN + ": " + headline);
		}
		if(description1.replaceAll("\\{[^\\}]*\\}", "").length() > MAX_DESCR_LEN) {
			throw new IllegalArgumentException(">" + MAX_DESCR_LEN +": " + description1);
		}
		if(description2.replaceAll("\\{[^\\}]*\\}", "").length() > MAX_DESCR_LEN) {
			throw new IllegalArgumentException(">" + MAX_DESCR_LEN+ ": " + description2);
		}

		this.id_ad = db_id;
		this.g = g;

		update = false;
	}
	
	private String resolve(String text, PKeyWord kw) {
		return text
				.replaceAll("\\{KeyWord.*?\\}",
						kw.getString())
				.replaceAll("\\{param1\\}", 
						kw.getPriceParam1().replace('$','#'))
				.replaceAll("\\{param2\\}", 
						kw.getPriceParam2().replace('$','#'));
	}
	
	private void validate(String text, PKeyWord kw, int length) {
		String res = resolve(text, kw);
		if (res.length() > length) {
			throw new IllegalArgumentException("Length >" + length
					+ ": [" + this.getHeadline() + "] with keyword ["
					+ kw.getString() + "] => [" + res + "]");
		}
	}
	
	/*package*/ void validateLengths(PKeyWord kw) {
		validate(this.getHeadline(), kw, MAX_TITLE_LEN);
		validate(this.getDescription1(), kw, MAX_DESCR_LEN);
		validate(this.getDescription2(), kw, MAX_DESCR_LEN);
	}
	
	/*package*/ void validateLengths(List<PKeyWord> keywordList) {
		
		for (PKeyWord kw : keywordList) {
			validateLengths(kw);
		}
	}

	/*package*/ void setUpdate(boolean update) {
		this.update = update;
	}

	/*package*/ boolean update() {
		return this.update;
	}

	public PGroup getGroup() {
		return g;
	}

	public Long getId() {
		return id_ad;
	}

	public boolean inDb() {
		return g.inDb() && getId() != null;
	}

	/*package*/ void setId(Long id) {
		this.id_ad = id;
	}

	public String toString() {
		return 
			  "{ Headline = " + this.getHeadline() 
			+ ", Description1 = " + this.getDescription1()
			+ ", Description2 = " + this.getDescription2()
			+ ", URL = " + this.getUrl()
			+ ", DisplayURL = " + this.getDisplayUrl()
			+ ", inDb = " + this.inDb()
			+ " }";
	}


}