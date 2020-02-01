package com.zigabyte.adwords.plugin;

public class StringPair {
	private String x;
	private String y;

	public StringPair(String x, String y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object other) {
	    // Not strictly necessary, but often a good optimization
	    if (this == other)
	      return true;

	    if (!(other instanceof StringPair))
	      return false;

	    StringPair p = (StringPair) other;
	    return this.x.equals(p.x) && this.y.equals(p.y);
	  }

	@Override
	public int hashCode() { return 0; }

	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}


}
