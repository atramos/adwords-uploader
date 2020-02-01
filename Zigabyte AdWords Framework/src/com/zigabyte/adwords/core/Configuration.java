package com.zigabyte.adwords.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

public class Configuration {
	private Properties properties;
	boolean useSandbox;

	public Configuration(String strProperties) throws ConfigurationException {

		//		 Read properties file.
		properties = new Properties();

		try {
		    properties.load(new FileInputStream(strProperties));
		} catch (IOException e) {
			System.out.println();
			throw new ConfigurationException("Couldn't load properties: " + strProperties,e);
		}
	}

	public void save() throws Exception {
		String FORMAT = "yyyy-MM-dd HH:mm:ss";

	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT);
	    String now =  sdf.format(cal.getTime());

		String strProperties = (useSandbox ? "sandbox.properties" : "live.properties");

		FileOutputStream out = new FileOutputStream(strProperties);
		properties.store(out, "-- Changed on " + now + " --");
		out.close();
	}

	public void setCampaignId(Long id) {
		properties.setProperty("campaignId", id.toString());
	}

	public Long getCampaignId() {
		return Long.parseLong(properties.getProperty("campaignId"));
	}

	public boolean getUseSandbox() {
		return properties.getProperty("useSandbox").equals("true");
	}

	public String getEmail() {
		return properties.getProperty("email");
	}

	public String getPassword() {
		return properties.getProperty("password");
	}

	public String getClientId() {
		return properties.getProperty("clientId");
	}

	public String getUserAgent() {
		return properties.getProperty("useragent");
	}

	public String getDeveloperToken() {
		return properties.getProperty("developerToken");
	}

	public String getCustomerDbClass() {
		return properties.getProperty("customerDbPlugin");
	}

	public String getCustomerDatabase() {
		return properties.getProperty("customerDatabase");
	}

	public String getCustomerDbPassword() {
		return properties.getProperty("customerDbpwd");
	}

	public String getCustomerDbUser() {
		return properties.getProperty("customerDbuser");
	}

	public String getAdwordsDatabase() {
		return properties.getProperty("adwordsDatabase");
	}

	public String getAdwordsDbPassword() {
		return properties.getProperty("adwordsDbpwd");
	}

	public String getAdwordsDbUser() {
		return properties.getProperty("adwordsDbuser");
	}

	public String getCustomerDbHost() {
		return properties.getProperty("dbHostname");
	}

	public boolean getIsRecoveryMode() {
		return properties.getProperty("recoveryMode").equals("true");
	}
}
