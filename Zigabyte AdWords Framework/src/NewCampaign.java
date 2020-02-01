import com.google.api.adwords.v200909.cm.Campaign;
import com.google.api.adwords.v200909.cm.CampaignReturnValue;
import com.zigabyte.adwords.core.AdWordsApiClient;
import com.zigabyte.adwords.core.Configuration;
import com.zigabyte.adwords.core.ConfigurationException;

public class NewCampaign {
	public static void main(String[] args) {
		Configuration configuration = null;
		try {
			configuration = new Configuration(args[0]);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(0);
		}

		AdWordsApiClient aw = new AdWordsApiClient(configuration);

		try {
			CampaignReturnValue result = aw.createCampaign(
					"Campaign " + System.currentTimeMillis());

			if (result != null && result.getValue() != null) {
				for (Campaign campaignResult : result.getValue()) {
					Long id = campaignResult.getId();

					System.out.println("Campaign with name \"" + campaignResult.getName() + "\" and id \""
							+ id + "\" was added.");

					// Update properties
					configuration.setCampaignId(id);
					configuration.save();
				}
			} else {
				System.out.println("No campaigns were added.");
			}
		} catch (Exception e) {
			System.out.println("Error creating group - see stack trace below");
			e.printStackTrace();
		}
	}

}