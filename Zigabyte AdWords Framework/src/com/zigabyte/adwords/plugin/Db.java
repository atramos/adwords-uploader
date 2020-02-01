package com.zigabyte.adwords.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Db implements IDb {
	public static Db createDbObject(String aClass) throws Exception {
		Class<?> [] classParm = null;
		Object [] objectParm = null;

		Class<?> cl = Class.forName(aClass);
		java.lang.reflect.Constructor<?> co = cl.getConstructor(classParm);
		return (Db) co.newInstance(objectParm);
	}

	protected Connection conn;

	public static Db initDB(String aClass, String host, 
			String database, String user, String password) throws Exception {

		Db aDatabase = createDbObject(aClass);	// force instance creation
		return initDB(aDatabase, host, database, user, password);
	}

	protected static Db initDB(Db aDatabase, String host, String database,
			String user, String password) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		aDatabase.conn = null;

		String url = "jdbc:mysql://" + host + "/" + database + "?"
		+ "user=" + user + "&password=" + password;

		Class.forName("com.mysql.jdbc.Driver").newInstance();

		aDatabase.conn = DriverManager.getConnection(url);

		return aDatabase;
	}
}
