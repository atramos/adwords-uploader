package com.zigabyte.adwords.core;

class AdBean {

	private String description1;
	private String description2;
	private String displayUrl;
	private String headline;
	final private String name_key;
	private String url;

	/*package*/ AdBean (String name_key, String headline,  String description1, String description2, String displayUrl, String url) {

		this.name_key = name_key;
		this.headline = headline;
		this.description1 = description1;
		this.description2 = description2;
		this.displayUrl = displayUrl;
		this.url = url;
	}

	public String getDescription1() {
		return description1;
	}

	public String getDescription2() {
		return description2;
	}

	public String getDisplayUrl() {
		return displayUrl;
	}

	public String getHeadline() {
		return headline;
	}

	public String getNameKey() {
		return name_key;
	}

	public String getUrl() {
		return url;
	}

	/*package*/ void setDescription1(String description1) {
		this.description1 = description1;
	}

	/*package*/ void setDescription2(String description2) {
		this.description2 = description2;
	}

	/*package*/ void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	/*package*/ void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Key[" + name_key + "]";
	}

	/*package*/ boolean equals(AdBean that) {
		return this.headline.equals(that.headline)
			&& this.description1.equals(that.description1)
			&& this.description2.equals(that.description2)
			&& this.displayUrl.equals(that.displayUrl)
			&& this.url.equals(that.url);
	}

}
