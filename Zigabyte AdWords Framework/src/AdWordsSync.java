import com.zigabyte.adwords.core.AdWordsApiClient;
import com.zigabyte.adwords.core.PersistentFactory;
import com.zigabyte.adwords.core.Configuration;
import com.zigabyte.adwords.core.ConfigurationException;
import com.zigabyte.adwords.plugin.CustomerDbaseFactory;


public class AdWordsSync {
	private static void usage() {
		String strUsage = "Usage:\n\tjava AdWordsSync [propertiesfile]";
		System.out.println(strUsage);
		System.exit(0);
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			usage();
		}

		Configuration configuration = null;
		try {
			configuration = new Configuration(args[0]);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}

		AdWordsApiClient aw = new AdWordsApiClient(configuration);

		try {
			CustomerDbaseFactory.initDB(configuration);
			PersistentFactory.initDB(configuration);
			aw.synchronize();
			
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}
	}
}
