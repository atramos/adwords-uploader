package com.zigabyte.adwords.core;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.api.adwords.lib.AdWordsService;
import com.google.api.adwords.lib.AdWordsServiceLogger;
import com.google.api.adwords.lib.AdWordsUser;
import com.google.api.adwords.v200909.cm.AdError;
import com.google.api.adwords.v200909.cm.AdGroup;
import com.google.api.adwords.v200909.cm.AdGroupAd;
import com.google.api.adwords.v200909.cm.AdGroupAdOperation;
import com.google.api.adwords.v200909.cm.AdGroupAdStatus;
import com.google.api.adwords.v200909.cm.AdGroupCriterion;
import com.google.api.adwords.v200909.cm.AdGroupCriterionOperation;
import com.google.api.adwords.v200909.cm.AdGroupOperation;
import com.google.api.adwords.v200909.cm.AdGroupPage;
import com.google.api.adwords.v200909.cm.AdGroupSelector;
import com.google.api.adwords.v200909.cm.AdGroupServiceError;
import com.google.api.adwords.v200909.cm.AdGroupServiceErrorReason;
import com.google.api.adwords.v200909.cm.AdGroupServiceInterface;
import com.google.api.adwords.v200909.cm.AdGroupStatus;
import com.google.api.adwords.v200909.cm.AdParam;
import com.google.api.adwords.v200909.cm.AdParamOperation;
import com.google.api.adwords.v200909.cm.AdParamServiceInterface;
import com.google.api.adwords.v200909.cm.ApiError;
import com.google.api.adwords.v200909.cm.ApiException;
import com.google.api.adwords.v200909.cm.BasicJobStatus;
import com.google.api.adwords.v200909.cm.BatchFailureResult;
import com.google.api.adwords.v200909.cm.Bid;
import com.google.api.adwords.v200909.cm.BiddableAdGroupCriterion;
import com.google.api.adwords.v200909.cm.Budget;
import com.google.api.adwords.v200909.cm.BudgetBudgetDeliveryMethod;
import com.google.api.adwords.v200909.cm.BudgetBudgetPeriod;
import com.google.api.adwords.v200909.cm.BulkMutateJob;
import com.google.api.adwords.v200909.cm.BulkMutateJobSelector;
import com.google.api.adwords.v200909.cm.BulkMutateJobServiceInterface;
import com.google.api.adwords.v200909.cm.BulkMutateRequest;
import com.google.api.adwords.v200909.cm.Campaign;
import com.google.api.adwords.v200909.cm.CampaignOperation;
import com.google.api.adwords.v200909.cm.CampaignReturnValue;
import com.google.api.adwords.v200909.cm.CampaignServiceInterface;
import com.google.api.adwords.v200909.cm.CampaignStatus;
import com.google.api.adwords.v200909.cm.EntityCountLimitExceeded;
import com.google.api.adwords.v200909.cm.EntityId;
import com.google.api.adwords.v200909.cm.EntityIdType;
import com.google.api.adwords.v200909.cm.FailureResult;
import com.google.api.adwords.v200909.cm.JobOperation;
import com.google.api.adwords.v200909.cm.Keyword;
import com.google.api.adwords.v200909.cm.KeywordMatchType;
import com.google.api.adwords.v200909.cm.ManualCPC;
import com.google.api.adwords.v200909.cm.ManualCPCAdGroupBids;
import com.google.api.adwords.v200909.cm.Money;
import com.google.api.adwords.v200909.cm.Operand;
import com.google.api.adwords.v200909.cm.Operation;
import com.google.api.adwords.v200909.cm.OperationResult;
import com.google.api.adwords.v200909.cm.OperationStream;
import com.google.api.adwords.v200909.cm.Operator;
import com.google.api.adwords.v200909.cm.PolicyViolationError;
import com.google.api.adwords.v200909.cm.RequiredError;
import com.google.api.adwords.v200909.cm.ReturnValueResult;
import com.google.api.adwords.v200909.cm.TextAd;
import com.zigabyte.adwords.plugin.AbstractProduct;
import com.zigabyte.adwords.plugin.CustomerDbaseFactory;
import com.zigabyte.adwords.plugin.Tuple;


public class AdWordsApiClient {
	
	private static final double BASERATE_AD_PARAM_OP = 0.1;
	private static final double BASERATE_BULK_JOB = 1;
	private static final double BULKRATE_KEYWORD_ADD = 7.5;
	private static final double BULKRATE_GROUP_ADD = 0.5;
	private static final double BULKRATE_AD_UPDATE = 0.5;
	private static final double BULKRATE_AD_ADD = 20;
	
	private static final int AD_PARAM_BATCH_SIZE = 500;
	private static final int CAMPAIGN_BUDGET_USD = 10;
	
	private static double creditTracker = 0;
	
	AdWordsUser user;
	Long campaignId;

	Map<String,PGroup> groupsToCreate;
	Map<String,PKeyWord> keywordsToCreate;
	Map<String,PAdv> adsToCreate;
	Map<String,PKeyWord> pricesToUpdate;
	private boolean recoveryMode;

	public AdWordsApiClient(Long campaignId, String email, String password, String clientId, String userAgent,
		      String developerToken, boolean useSandbox, boolean recoveryMode) {
		// Log SOAP XML request and response.
		AdWordsServiceLogger.log();

		// Get AdWordsUser from "~/adwords.properties".
		user = new AdWordsUser(email, password, clientId, userAgent,
			      developerToken, useSandbox);

		this.campaignId = campaignId;

		groupsToCreate = new HashMap<String,PGroup>();
		keywordsToCreate = new HashMap<String,PKeyWord>();
		adsToCreate = new HashMap<String,PAdv>();
		pricesToUpdate = new HashMap<String,PKeyWord>();
		this.recoveryMode = recoveryMode;
	}

	public AdWordsApiClient(Configuration configuration) {
		this(
				configuration.getCampaignId(),
				configuration.getEmail(),
				configuration.getPassword(),
				configuration.getClientId(),
				configuration.getUserAgent(),
				configuration.getDeveloperToken(),
				configuration.getUseSandbox(),
				configuration.getIsRecoveryMode()
		);
	}

	private void addKeywordToCreate(PKeyWord k) {
		keywordsToCreate.put(k.getString(),k);
	}

	private void addAdvsToCreate(PAdv adv) {
		adsToCreate.put(adv.getNameKey(), adv);
	}

	private void addGroupToCreate(PGroup g) {
		groupsToCreate.put(g.getGroupName(),g);
	}

	private static long createAndBeginJob(
			BulkMutateJobServiceInterface bulkMutateJobService, EntityId scopingEntityId,
			Operation[] operations) throws IOException {
		// Initialize the bulk mutate job id.
		Long jobId = null;

		// Create operation stream.
		OperationStream opStream = new OperationStream();
		opStream.setScopingEntityId(scopingEntityId);
		opStream.setOperations(operations);

		// Create bulk mutate request part.
		BulkMutateRequest part = new BulkMutateRequest();
		part.setPartIndex(0);
		part.setOperationStreams(new OperationStream[] {opStream});

		// Create bulk mutate job.
		BulkMutateJob job = new BulkMutateJob();
		job.setId(jobId);
		job.setNumRequestParts(1);

		// Set the part request in the job.
		job.setRequest(part);

		// Create the operation to contain the job.
		JobOperation operation = new JobOperation();
		operation.setOperand(job);

		operation.setOperator(Operator.ADD);

		// Add/set the job. The job will not start until all parts are added.
		job = bulkMutateJobService.mutate(operation);


		// Store job id.
		jobId = job.getId();

		return jobId;
	}

	public AdGroup[] listGroups() throws Exception {
		AdGroupServiceInterface ags = 
			user.getService(AdWordsService.V200909.ADGROUP_SERVICE);
		
		AdGroupSelector selector = new AdGroupSelector();
		selector.setCampaignId(campaignId);
		AdGroupPage page = ags.get(selector);
		
		// http://code.google.com/apis/adwords/docs/reference/v200909/AdGroupService.AdGroupPage.html
		
		return page.getEntries();
	}
	
	public void createGroups() throws Exception {
		
		if(recoveryMode) {
			for(AdGroup ag: listGroups()) {
				// force existence in database
				PGroup pg = PersistentFactory.getGroup(ag.getName());
				pg.setId(ag.getId());
				
				if(groupsToCreate.get(ag.getName()) != null) {
					groupsToCreate.remove(ag.getName());
					System.out.println("Already exists remotely: " + ag.getName());
				}
			}
		}
			
		System.out.format("API Creating %1$d groups\n", groupsToCreate.size());

		// Get the BulkMutateJobService.
		BulkMutateJobServiceInterface bulkMutateJobService =
			user.getService(AdWordsService.V200909.BULK_MUTATE_JOB_SERVICE);

		// Set scope of jobs.
		EntityId scopingEntityId = new EntityId(EntityIdType.CAMPAIGN_ID, campaignId);

		// Create the operations for AddGroup
		AdGroupOperation[] operations = getAddGroupsOperation();

		if (operations.length > 0) { // invoke creation API

			// Create and begin job.
			long jobId =
				createAndBeginJob(bulkMutateJobService, scopingEntityId, operations);

			// Mointor and retrieve results from job.
			OperationResult[] operationResults =
				retrieveResultsFromJob(bulkMutateJobService, jobId);

			creditTracker += BASERATE_BULK_JOB + operations.length * BULKRATE_GROUP_ADD;

			operationsLoop: for(int i=0; i< operationResults.length; i++) {

				OperationResult opres = operationResults[i];

				if (opres instanceof ReturnValueResult) {
					// Get the result by the get*Method for the type of return value
					// through reflection.
					Operand operand = ((ReturnValueResult) opres).getReturnValue();
					AdGroup ag = operand.getAdGroup();
					PGroup g = PersistentFactory.getGroup(ag.getName());
					g.setId(ag.getId());
				}
				else if(opres instanceof FailureResult) {
					FailureResult fr = (FailureResult) opres;
					ApiError[] errs = fr.getCause().getErrors();
					StringBuffer err = new StringBuffer();

					for(ApiError ae : errs) {
						if(ae instanceof AdGroupServiceError) {

							AdGroupServiceError ase = (AdGroupServiceError) ae;

							if(ase.getReason() == AdGroupServiceErrorReason.DUPLICATE_ADGROUP_NAME) {
								// FIXME: quick hack... may cause null IDs to seep into database.
								continue operationsLoop;
							}

							err.append(ase.getApiErrorType());
							err.append(" // FieldPath=");
							err.append(ase.getFieldPath());
							err.append(" // Trigger=");
							err.append(ase.getTrigger());
							err.append(" // Reason=");
							err.append(ase.getReason().toString());
						}
						else {
							err.append("\nProblem: ");
							err.append(ae.toString());						
						}
					}
					throw new Exception(opres.getClass() + ": " + err.toString());
				}
				else {

					throw new Exception("Error on Group creation: " 
							+ operations[i].getOperand().getName()
							+ " : " + opres.getClass());
				}
			}
		}
		
		PGroup.flushGroups();
    }


	public void createKeywords() throws Exception {
		if (keywordsToCreate.isEmpty()) {
			System.out.println("No keywords to Create");
			return;
		}

		System.out.format("Creating %1$d keywords\n", keywordsToCreate.size());

		// Get the BulkMutateJobService.
		BulkMutateJobServiceInterface bulkMutateJobService =
			user.getService(AdWordsService.V200909.BULK_MUTATE_JOB_SERVICE);

		// Set scope of jobs.
		EntityId scopingEntityId = new EntityId(EntityIdType.CAMPAIGN_ID, campaignId);

		// Create the operations.
		AdGroupCriterionOperation[] operations = getAddGroupsCriterionOperation();

		// nothing to do!
		if (operations.length == 0) return;

		creditTracker += BASERATE_BULK_JOB + operations.length * BULKRATE_KEYWORD_ADD;
		
		// Create and begin job.
		long jobId =
			createAndBeginJob(bulkMutateJobService, scopingEntityId, operations);

		// Mointor and retrieve results from job.
		OperationResult[] operationResults =
			retrieveResultsFromJob(bulkMutateJobService, jobId);

		for(int i=0; i< operationResults.length; i++) {
			OperationResult opRes = operationResults[i];
			if (opRes instanceof ReturnValueResult) {
				// Get the result by the get*Method for the type of return value
				// through reflection.
				Operand operand = ((ReturnValueResult) opRes).getReturnValue();
				AdGroupCriterion agc = operand.getAdGroupCriterion();

				// update successful Keywords' ID into database
				Keyword gk = (Keyword) agc.getCriterion();
				PKeyWord k = keywordsToCreate.get(gk.getText());
				k.setId(gk.getId());
				if(!k.inDb()) {
					PersistentFactory.insertKeyword(k);
				}
			} 
			else if(opRes instanceof FailureResult){
				FailureResult fr = (FailureResult) opRes;
				ApiError[] errs = fr.getCause().getErrors();
				StringBuffer err = new StringBuffer();
				
				for(ApiError ae : errs) {
					err.append(ae.toString() + ": ");
					if(ae instanceof RequiredError) {
						RequiredError re = (RequiredError)ae;
						err.append(re.getApiErrorType()
								+ " //Field=" + re.getFieldPath()
								+ " //Trigger=" + re.getTrigger()
								+ " //TypeDesc=" + re.getTypeDesc()
								+ " //Reason.value" + re.getReason().getValue()
								+ "\n");
					}
				}

				throw new Exception("Error on Keyword creation: "
						+ fr.getOperationResultType()
						+ " // " + fr.toString() + " // " + err);
			}
			else if(opRes instanceof BatchFailureResult){
				BatchFailureResult fr = (BatchFailureResult) opRes;
				System.err.println("Error on Keyword creation: "
						+ fr.getOperationResultType()
						+ " // " + fr.toString());
			}
			else {
				throw new Exception(opRes.toString());
			}
		}
    }

	public void createAds() throws Exception {
		if (adsToCreate.isEmpty()) {
			System.out.println("No ads to Create");
			return;
		}
		
		System.out.format("Creating and/or Updating %1$d ads\n", adsToCreate.size());

		// Get the BulkMutateJobService.
		BulkMutateJobServiceInterface bulkMutateJobService =
			user.getService(AdWordsService.V200909.BULK_MUTATE_JOB_SERVICE);

		// Set scope of jobs.
		EntityId scopingEntityId = new EntityId(EntityIdType.CAMPAIGN_ID, campaignId);

		// Create the operations.
		List<Tuple<AdGroupAdOperation, PAdv>> operations = getAddAdsOperation();

		// nothing to do!
		if (operations.size() == 0) return;
		
		AdGroupAdOperation[] ops = new AdGroupAdOperation[operations.size()];
		for(int i=0; i < operations.size(); ++i) {
			ops[i] = operations.get(i).a;
		}

		// Create and begin job.
		long jobId =
			createAndBeginJob(bulkMutateJobService, scopingEntityId, ops);

		// Mointor and retrieve results from job.
		OperationResult[] operationResults =
			retrieveResultsFromJob(bulkMutateJobService, jobId);
		
		creditTracker += BASERATE_BULK_JOB;

		for(int i=0; i< operationResults.length; i++) {
			if (operationResults[i] instanceof ReturnValueResult) {
				// Get the result by the get*Method for the type of return value
				// through reflection.
				Operand operand = ((ReturnValueResult) operationResults[i]).getReturnValue();
				AdGroupAd agd = operand.getAdGroupAd();

				TextAd tad = (TextAd) agd.getAd();
				//Adv adv = adsToCreate.get(tad.getHeadline() + tad.getDescription1() + tad.getDescription2());
				PAdv adv = operations.get(i).b;

				adv.setId(tad.getId());
				
				if (!adv.inDb()) {
					PersistentFactory.insertAd(adv);
					creditTracker += BULKRATE_AD_ADD;
				} else {
					PersistentFactory.update(adv);
					creditTracker += BULKRATE_AD_UPDATE;
				}
			} else {
				FailureResult fr = (FailureResult) operationResults[i];
				ApiError[] errs = fr.getCause().getErrors();
				StringBuffer err = new StringBuffer();
				
				for(ApiError ae : errs) {
					err.append("\nProblem with ad: ");
					err.append(operations.get(i).b.toString());
					err.append(" => ");
							
					if(ae instanceof AdError) {
						AdError e = (AdError) ae;
						err.append(e.getApiErrorType() + ":" + e.getFieldPath() + ":" + e.getReason().getValue() + " ");						
					}
					else if(ae instanceof EntityCountLimitExceeded) {
						EntityCountLimitExceeded ex = (EntityCountLimitExceeded)ae;
						err.append("Count Limit Exceeded: " + ex.getFieldPath()
								+ " id=" + ex.getEnclosingId()
								+ " limit=" + ex.getLimit());
					}
					else if(ae instanceof PolicyViolationError) {
						PolicyViolationError pve = (PolicyViolationError) ae;
						err.append("Policy Violation: "
								+ pve.getExternalPolicyName() + " // "
								+ pve.getExternalPolicyDescription() + " // "
								+ pve.getExternalPolicyUrl() + "\n");
					}
					else {
						err.append(ae.toString());
					}
				}
				
				throw new Exception("Error on Ad creation: " + err);
			}
		}
    }

	private static OperationResult[] retrieveResultsFromJob(
			BulkMutateJobServiceInterface bulkMutateJobService, long jobId)
	throws InterruptedException, IOException {

		OperationResult[] operationResults;
		BulkMutateJob job = null;

		// Create selector.
		BulkMutateJobSelector selector = new BulkMutateJobSelector();
		selector.setJobIds(new long[] {jobId});

		// Loop while waiting for the job to complete.
		do {
			BulkMutateJob[] jobs = bulkMutateJobService.get(selector);
			job = jobs[0];

			System.out.println("Bulk mutate job with id \"" + job.getId() + "\" has status \""
					+ job.getStatus() + "\".");

			if (job.getStatus().equals(BasicJobStatus.PENDING)
					|| job.getStatus().equals(BasicJobStatus.PROCESSING)) {
				Thread.sleep(10000);
			}
		} while(job.getStatus().equals(BasicJobStatus.PENDING)
				|| job.getStatus().equals(BasicJobStatus.PROCESSING));

		if (job.getStatus() == BasicJobStatus.FAILED) {
			throw new ApiException("Job failed.", null, null);
		}

		int i = 0;
		selector.setResultPartIndex(i);
		BulkMutateJob jobWithResult = bulkMutateJobService.get(selector)[0];

		System.out.println("Bulk mutate result with job id \"" + job.getId()
				+ "\" and part number \"" + i + "\" was retrieved.");

		operationResults = jobWithResult.getResult().getOperationStreamResults()[0].getOperationResults();

		return operationResults;
	}

	private List<Tuple<AdGroupAdOperation,PAdv>> getAddAdsOperation() {

		List<Tuple<AdGroupAdOperation,PAdv>> operations = new LinkedList<Tuple<AdGroupAdOperation,PAdv>>();

		for(PAdv theAd: adsToCreate.values()) {
			boolean isUpdate = theAd.inDb();
			
			// Create text ad.
			TextAd textAd = new TextAd();
			textAd.setHeadline(theAd.getHeadline());
			textAd.setDescription1(theAd.getDescription1());
			textAd.setDescription2(theAd.getDescription2());
			textAd.setDisplayUrl(theAd.getDisplayUrl());
			textAd.setUrl(theAd.getUrl());
			if (isUpdate) {
				// is an update
				textAd.setId(theAd.getId());
			}

			// Create ad group ad.
			AdGroupAd textAdGroupAd = new AdGroupAd();
			textAdGroupAd.setAdGroupId(theAd.getGroup().getId());
			textAdGroupAd.setAd(textAd);
			if (isUpdate) {
				textAdGroupAd.setStatus(AdGroupAdStatus.PAUSED);
			}

			// Create operations.
			AdGroupAdOperation textAdGroupAdOperation = new AdGroupAdOperation();
			textAdGroupAdOperation.setOperand(textAdGroupAd);
			textAdGroupAdOperation.setOperator(isUpdate ? Operator.SET : Operator.ADD);

	        operations.add(new Tuple<AdGroupAdOperation,PAdv>(textAdGroupAdOperation, theAd));
		}

        return operations;
	}

	private AdGroupCriterionOperation[] getAddGroupsCriterionOperation() {
		List<AdGroupCriterionOperation> l = new LinkedList<AdGroupCriterionOperation>();

		for(PKeyWord k: keywordsToCreate.values()) {
			
		      // Create keyword.
		      Keyword keyword = new Keyword();
		      keyword.setText(k.getString());
		      keyword.setMatchType(KeywordMatchType.BROAD);

		      // Create biddable ad group criterion.
		      BiddableAdGroupCriterion keywordBiddableAdGroupCriterion = new BiddableAdGroupCriterion();
		      keywordBiddableAdGroupCriterion.setAdGroupId(k.getG().getId());
		      keywordBiddableAdGroupCriterion.setCriterion(keyword);
		      
		      // Create operations.
		      AdGroupCriterionOperation keywordAdGroupCriterionOperation = new AdGroupCriterionOperation();
		      keywordAdGroupCriterionOperation.setOperand(keywordBiddableAdGroupCriterion);
		      keywordAdGroupCriterionOperation.setOperator(Operator.ADD);

		      l.add(keywordAdGroupCriterionOperation);
		}

		AdGroupCriterionOperation[] operations = new AdGroupCriterionOperation[l.size()];
        int count = 0;
        for(AdGroupCriterionOperation o: l) {
        	operations[count++] = o;
        }

        return operations;
	}

	private AdGroupOperation[] getAddGroupsOperation() {
		List<AdGroupOperation> l = new LinkedList<AdGroupOperation>();

		for(PGroup g: groupsToCreate.values()) {
	        
	        if(g.inDb()) {
	        	// in the present implementation, the AdGroup settings are
	        	// static. So there is nothing to update.
	        	continue;
	        }
			
	        // Create ad group.
	        AdGroup adGroup = new AdGroup();
	        adGroup.setName(g.getGroupName());
	        adGroup.setStatus(AdGroupStatus.ENABLED);
	        adGroup.setCampaignId(campaignId.longValue());

	        // Create ad group bid.
	        ManualCPCAdGroupBids adGroupBids = new ManualCPCAdGroupBids();
	        adGroupBids.setKeywordMaxCpc(new Bid(new Money(null, 
	        		(long)(g.getMaxCpcUSD() * 1000000L))));
	        adGroup.setBids(adGroupBids);

	        // Create operations.
	        AdGroupOperation operation = new AdGroupOperation();
	        operation.setOperand(adGroup);
	        operation.setOperator(Operator.ADD);

	        l.add(operation);
		}

		AdGroupOperation[] operations = new AdGroupOperation[l.size()];
        int count = 0;
        for(AdGroupOperation o: l) {
        	operations[count++] = o;
        }

        return operations;
	}

	private void addPriceToUpdate(PKeyWord k) {
		pricesToUpdate.put(k.getString(),k);
	}

	public void updatePrices() throws Exception {
		Map<Long,PKeyWord> keyWordsById = new HashMap<Long,PKeyWord>();

		for(PKeyWord k: pricesToUpdate.values()) {
			// save a copy
			keyWordsById.put(k.getId(), k);
		}

		// check whether there're prices to update
		if (pricesToUpdate.isEmpty()) {
			System.out.println("No prices to Update");
			return;
		}

		System.out.format("Updating %1$d prices\n", pricesToUpdate.size());

		// Get the AdParamService.
		// Current API doesn't suppor Bulk Job AdParams
		// operations
		AdParamServiceInterface adParamService =
			user.getService(AdWordsService.V200909.AD_PARAM_SERVICE);

		// Create the operations.
		List<AdParamOperation> operationsQueue = Arrays.asList(getAdParamOperations());
		
		creditTracker += Math.ceil(operationsQueue.size() * BASERATE_AD_PARAM_OP);
		
		while(operationsQueue.size() > 0) {
			
			int batchEnd = Math.min(operationsQueue.size(), AD_PARAM_BATCH_SIZE);
			AdParamOperation[] operations = operationsQueue.subList(0, batchEnd).toArray(new AdParamOperation[0]);
			operationsQueue = operationsQueue.subList(batchEnd, operationsQueue.size());

			System.out.format("Submitting %1$d operations\n", operations.length);

			// Set ad parameters.
			AdParam[] adParams = adParamService.mutate(operations);

			// Display ad parameters.
			if (adParams != null) {
				for (AdParam adParam : adParams) {
					// getKeyById
					PKeyWord k = keyWordsById.get(adParam.getCriterionId());
					PersistentFactory.updatePrice(k);
				}
			}
			
		}
	}

	private AdParamOperation[] getAdParamOperations() {
		List<AdParamOperation> l = new LinkedList<AdParamOperation>();

		for(PKeyWord k: pricesToUpdate.values()) {
			if (k.updatePrice1()) {
				// Create ad params.
				AdParam adParam1 = new AdParam();
				adParam1.setAdGroupId(k.getG().getId());
				adParam1.setCriterionId(k.getId());
				adParam1.setInsertionText(k.getPriceParam1());
				adParam1.setParamIndex(1);

				// Create operations.
				AdParamOperation operation = new AdParamOperation();
				operation.setOperand(adParam1);
				operation.setOperator(Operator.SET);

		        l.add(operation);
			}

			if (k.updatePrice2()) {
				AdParam adParam2 = new AdParam();
				adParam2.setAdGroupId(k.getG().getId());
				adParam2.setCriterionId(k.getId());
				adParam2.setInsertionText(k.getPriceParam2());
				adParam2.setParamIndex(2);

				// Create operations.
				AdParamOperation operation2 = new AdParamOperation();
				operation2.setOperand(adParam2);
				operation2.setOperator(Operator.SET);

		        l.add(operation2);
			}
		}

		AdParamOperation[] operations = new AdParamOperation[l.size()];
        int count = 0;
        for(AdParamOperation o: l) {
        	operations[count++] = o;
        }

        return operations;
	}

	public CampaignReturnValue createCampaign(String string) throws Exception, RemoteException {
	      // Get the CampaignService.
	      CampaignServiceInterface campaignService =
	          user.getService(AdWordsService.V200909.CAMPAIGN_SERVICE);

	      // Create campaign.
	      Campaign campaign = new Campaign();
	      campaign.setName(string);
	      campaign.setStatus(CampaignStatus.PAUSED);
	      campaign.setBiddingStrategy(new ManualCPC());

	      // Create budget.
	      Budget budget = new Budget();
	      budget.setPeriod(BudgetBudgetPeriod.DAILY);
	      budget.setAmount(new Money(null, CAMPAIGN_BUDGET_USD * 1000000L));
	      budget.setDeliveryMethod(BudgetBudgetDeliveryMethod.STANDARD);
	      campaign.setBudget(budget);

	      // Create operations.
	      CampaignOperation operation = new CampaignOperation();
	      operation.setOperand(campaign);
	      operation.setOperator(Operator.ADD);

	      CampaignOperation[] operations = new CampaignOperation[] {operation};

	      // Add campaign.
	      CampaignReturnValue result = campaignService.mutate(operations);

	      return result;
	}

	public void synchronize() throws Exception {
		
		List<AbstractProduct> products = CustomerDbaseFactory.getDb().getProducts();

		for(AbstractProduct product : products) {

			List<PAdv> relatedAdvs = CustomerDbaseFactory.getRelatedAdvs(product);
			for(PAdv adv : relatedAdvs) {
	
				PGroup g = adv.getGroup();
				if (!g.inDb()) {
					this.addGroupToCreate(g);
				}
	
				if (!adv.inDb() || adv.update()) {
					this.addAdvsToCreate(adv);
				}
			}
	
			List<PKeyWord> relatedKeywords = CustomerDbaseFactory.getRelatedKeywords(product);
			for(PKeyWord k : relatedKeywords) {
	
				PGroup g = k.getG();
				if (!g.inDb()) {
					this.addGroupToCreate(g);
				}
	
				if (!k.inDb()) {
					this.addKeywordToCreate(k);
				}
	
				if (k.updatePrice()) {
					this.addPriceToUpdate(k);
				}
			}
		}


		try {
			this.createGroups();
			this.createKeywords();
			this.createAds();
			this.updatePrices();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Total API credits used: " + creditTracker);
		System.out.println("Total currency........: $" + creditTracker * 0.25/1000);
		
	}
}
