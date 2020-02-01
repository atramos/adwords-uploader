import java.io.IOException;

import javax.xml.rpc.ServiceException;

import com.google.api.adwords.lib.AdWordsService;
import com.google.api.adwords.lib.AdWordsUser;
import com.google.api.adwords.v200909.cm.AdGroup;
import com.google.api.adwords.v200909.cm.AdGroupAd;
import com.google.api.adwords.v200909.cm.AdGroupAdOperation;
import com.google.api.adwords.v200909.cm.AdGroupAdReturnValue;
import com.google.api.adwords.v200909.cm.AdGroupAdServiceInterface;
import com.google.api.adwords.v200909.cm.AdGroupCriterion;
import com.google.api.adwords.v200909.cm.AdGroupCriterionOperation;
import com.google.api.adwords.v200909.cm.AdGroupCriterionReturnValue;
import com.google.api.adwords.v200909.cm.AdGroupCriterionServiceInterface;
import com.google.api.adwords.v200909.cm.AdGroupOperation;
import com.google.api.adwords.v200909.cm.AdGroupReturnValue;
import com.google.api.adwords.v200909.cm.AdGroupServiceInterface;
import com.google.api.adwords.v200909.cm.AdGroupStatus;
import com.google.api.adwords.v200909.cm.Bid;
import com.google.api.adwords.v200909.cm.BiddableAdGroupCriterion;
import com.google.api.adwords.v200909.cm.Keyword;
import com.google.api.adwords.v200909.cm.KeywordMatchType;
import com.google.api.adwords.v200909.cm.ManualCPCAdGroupBids;
import com.google.api.adwords.v200909.cm.Money;
import com.google.api.adwords.v200909.cm.Operator;
import com.google.api.adwords.v200909.cm.TextAd;


public class AdWordsTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	static Long addAd(Long adGroupId
			,String headline
			,String description1
			,String description2
			,String displayUrl
			,String url
			,boolean debug
			) throws Exception {
		Long addId = null;
	
		// Get the AdGroupAdService.
		AdGroupAdServiceInterface adGroupAdService =
			user.getService(AdWordsService.V200909.ADGROUP_AD_SERVICE);
	
		// Create text ad.
		TextAd textAd = new TextAd();
		textAd.setHeadline(headline);
		textAd.setDescription1(description1);
		textAd.setDescription2(description2);
		textAd.setDisplayUrl(displayUrl);
		textAd.setUrl(url);
	
		// Create ad group ad.
		AdGroupAd textAdGroupAd = new AdGroupAd();
		textAdGroupAd.setAdGroupId(adGroupId);
		textAdGroupAd.setAd(textAd);
	
		// Create operations.
		AdGroupAdOperation textAdGroupAdOperation = new AdGroupAdOperation();
		textAdGroupAdOperation.setOperand(textAdGroupAd);
		textAdGroupAdOperation.setOperator(Operator.ADD);
	
		AdGroupAdOperation[] operations =
			new AdGroupAdOperation[] {textAdGroupAdOperation};
	
		// Add ads.
		AdGroupAdReturnValue result = adGroupAdService.mutate(operations);
	
		// Display ads.
		if (result != null && result.getValue() != null) {
			for (AdGroupAd adGroupAdResult : result.getValue()) {
				if (debug)
					System.out.println("Ad with id  \"" + adGroupAdResult.getAd().getId() + "\""
						+ " and type \"" + adGroupAdResult.getAd().getAdType() + "\" was added.");
	
				addId = adGroupAdResult.getAd().getId();
			}
		} else {
			throw new Exception("No ads were added.");
		}
	
		return addId;
	}

	static Long addGroup(long campaignId, String name, boolean debug) throws IOException, ServiceException {
	    // Get the AdGroupService.
	    AdGroupServiceInterface adGroupService =
	        user.getService(AdWordsService.V200909.ADGROUP_SERVICE);
	
	    // Create ad group.
	    AdGroup adGroup = new AdGroup();
	    adGroup.setName(name);
	    adGroup.setStatus(AdGroupStatus.ENABLED);
	    adGroup.setCampaignId(campaignId);
	
	    // Create ad group bid.
	    ManualCPCAdGroupBids adGroupBids = new ManualCPCAdGroupBids();
	    adGroupBids.setKeywordMaxCpc(new Bid(new Money(null, 10000000L)));
	    adGroup.setBids(adGroupBids);
	
	
	    // Create operations.
	    AdGroupOperation operation = new AdGroupOperation();
	    operation.setOperand(adGroup);
	    operation.setOperator(Operator.ADD);
	
	    AdGroupOperation[] operations = new AdGroupOperation[]{operation};
	
	    // Add ad group.
	    AdGroupReturnValue result = adGroupService.mutate(operations);
	
	    Long adGroupId = null;
	
	    // Display new ad groups.
	    if (result != null && result.getValue() != null) {
	      for (AdGroup adGroupResult : result.getValue()) {
	    	  if (debug)
	    		  System.out.println("Ad group with name \""
	    				  + adGroupResult.getName() + "\" and id \""
	    				  + adGroupResult.getId() + "\" was added.");
	
	        adGroupId = adGroupResult.getId();
	      }
	    }
	
	    return adGroupId;
	}

	public static Long addKeyword(Long adGroupId, String text, boolean debug) throws Exception {
	      Long keywordId = null;
	
		  // Get the AdGroupCriterionService.
	      AdGroupCriterionServiceInterface adGroupCriterionService =
	          user.getService(AdWordsService.V200909.ADGROUP_CRITERION_SERVICE);
	
	      // Create keyword.
	      Keyword keyword = new Keyword();
	      keyword.setText(text);
	      keyword.setMatchType(KeywordMatchType.BROAD);
	
	      // Create biddable ad group criterion.
	      BiddableAdGroupCriterion keywordBiddableAdGroupCriterion = new BiddableAdGroupCriterion();
	      keywordBiddableAdGroupCriterion.setAdGroupId(adGroupId);
	      keywordBiddableAdGroupCriterion.setCriterion(keyword);
	
	      // Create operations.
	      AdGroupCriterionOperation keywordAdGroupCriterionOperation = new AdGroupCriterionOperation();
	      keywordAdGroupCriterionOperation.setOperand(keywordBiddableAdGroupCriterion);
	      keywordAdGroupCriterionOperation.setOperator(Operator.ADD);
	
	      AdGroupCriterionOperation[] operations =
	          new AdGroupCriterionOperation[] {keywordAdGroupCriterionOperation};
	
	      // Add ad group criteria.
	      AdGroupCriterionReturnValue result = adGroupCriterionService.mutate(operations);
	
	      // Display ad group criteria.
	      if (result != null && result.getValue() != null) {
	        for (AdGroupCriterion adGroupCriterionResult : result.getValue()) {
	        	if (debug)
	        		System.out.println("Ad group criterion with ad group id \""
	        				+ adGroupCriterionResult.getAdGroupId() + "\", criterion id \""
	        				+ adGroupCriterionResult.getCriterion().getId() + "\", and type \""
	        				+ adGroupCriterionResult.getCriterion().getCriterionType() + "\" was added.");
	
	          keywordId = adGroupCriterionResult.getCriterion().getId();
	        }
	 	}
	
	    return keywordId;
	}

	private static AdWordsUser user;

}
