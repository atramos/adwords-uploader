package com.zigabyte.adwords.plugin;

import java.util.List;

import com.zigabyte.adwords.core.PAdv;
import com.zigabyte.adwords.core.PGroup;
import com.zigabyte.adwords.core.PKeyWord;

public abstract class AbstractProduct {

	public abstract PGroup getGroup() throws Exception;

	public abstract List<PKeyWord> getKeywords() throws Exception;
	
	public abstract List<PAdv> getAdvs() throws Exception;
	
}
