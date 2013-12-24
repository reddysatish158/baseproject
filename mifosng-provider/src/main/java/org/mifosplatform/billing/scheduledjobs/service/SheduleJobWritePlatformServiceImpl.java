package org.mifosplatform.billing.scheduledjobs.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifosplatform.billing.billingmaster.api.BillingMasterApiResourse;
import org.mifosplatform.billing.billingorder.service.InvoiceClient;
import org.mifosplatform.billing.entitlements.data.ClientEntitlementData;
import org.mifosplatform.billing.entitlements.data.EntitlementsData;
import org.mifosplatform.billing.entitlements.service.EntitlementReadPlatformService;
import org.mifosplatform.billing.entitlements.service.EntitlementWritePlatformService;
import org.mifosplatform.billing.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.billing.message.service.BillingMessageDataWritePlatformService;
import org.mifosplatform.billing.message.service.BillingMesssageReadPlatformService;
import org.mifosplatform.billing.message.service.MessagePlatformEmailService;
import org.mifosplatform.billing.order.data.OrderData;
import org.mifosplatform.billing.order.service.OrderReadPlatformService;
import org.mifosplatform.billing.order.service.OrderWritePlatformService;
import org.mifosplatform.billing.plan.domain.StatusTypeEnum;
import org.mifosplatform.billing.preparerequest.data.PrepareRequestData;
import org.mifosplatform.billing.preparerequest.service.PrepareRequestReadplatformService;
import org.mifosplatform.billing.processrequest.data.ProcessingDetailsData;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.billing.processrequest.service.ProcessRequestReadplatformService;
import org.mifosplatform.billing.processscheduledjobs.service.SheduleJobReadPlatformService;
import org.mifosplatform.billing.processscheduledjobs.service.SheduleJobWritePlatformService;
import org.mifosplatform.billing.scheduledjobs.ProcessRequestWriteplatformService;
import org.mifosplatform.billing.scheduledjobs.data.JobParameterData;
import org.mifosplatform.billing.scheduledjobs.data.ScheduleJobData;
import org.mifosplatform.billing.scheduledjobs.domain.ScheduledJobRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.annotation.CronTarget;
import org.mifosplatform.infrastructure.jobs.domain.ScheduledJobDetailRepository;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class SheduleJobWritePlatformServiceImpl implements
		SheduleJobWritePlatformService {

	private final SheduleJobReadPlatformService sheduleJobReadPlatformService;
	private final InvoiceClient invoiceClient;
	private final ScheduledJobRepository scheduledJobRepository;
	private final BillingMasterApiResourse billingMasterApiResourse;
	private final OrderWritePlatformService orderWritePlatformService;
	private final FromJsonHelper fromApiJsonHelper;
	private final OrderReadPlatformService orderReadPlatformService;
	private final BillingMessageDataWritePlatformService billingMessageDataWritePlatformService;
	private final PrepareRequestReadplatformService prepareRequestReadplatformService;
	private final ProcessRequestReadplatformService processRequestReadplatformService;
	private final ProcessRequestWriteplatformService processRequestWriteplatformService;
	private final ProcessRequestRepository processRequestRepository;
	private final ScheduledJobDetailRepository scheduledJobDetailRepository;
	private final BillingMesssageReadPlatformService billingMesssageReadPlatformService;
	private final MessagePlatformEmailService messagePlatformEmailService;
	private final EntitlementReadPlatformService entitlementReadPlatformService;

	private final EntitlementWritePlatformService entitlementWritePlatformService;
	private String ReceiveMessage;
	private File file=null;
	@Autowired
	public SheduleJobWritePlatformServiceImpl(
			final InvoiceClient invoiceClient,
			final SheduleJobReadPlatformService sheduleJobReadPlatformService,
			final ScheduledJobRepository scheduledJobRepository,
			final BillingMasterApiResourse billingMasterApiResourse,
			final ProcessRequestRepository processRequestRepository,
			final OrderWritePlatformService orderWritePlatformService,
			final FromJsonHelper fromApiJsonHelper,
			final OrderReadPlatformService orderReadPlatformService,
			final BillingMessageDataWritePlatformService billingMessageDataWritePlatformService,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,
			final ProcessRequestReadplatformService processRequestReadplatformService,
			final ProcessRequestWriteplatformService processRequestWriteplatformService,
			final ScheduledJobDetailRepository scheduledJobDetailRepository,
			final BillingMesssageReadPlatformService billingMesssageReadPlatformService,
			final MessagePlatformEmailService messagePlatformEmailService,
			final EntitlementReadPlatformService entitlementReadPlatformService,
			final EntitlementWritePlatformService entitlementWritePlatformService) {
		this.sheduleJobReadPlatformService = sheduleJobReadPlatformService;
		this.invoiceClient = invoiceClient;
		this.scheduledJobRepository = scheduledJobRepository;
		this.billingMasterApiResourse = billingMasterApiResourse;
		this.orderWritePlatformService = orderWritePlatformService;
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.orderReadPlatformService = orderReadPlatformService;
		this.billingMessageDataWritePlatformService = billingMessageDataWritePlatformService;
		this.prepareRequestReadplatformService = prepareRequestReadplatformService;
		this.processRequestReadplatformService = processRequestReadplatformService;
		this.processRequestWriteplatformService = processRequestWriteplatformService;
		this.processRequestRepository = processRequestRepository;
		this.scheduledJobDetailRepository = scheduledJobDetailRepository;
		this.billingMesssageReadPlatformService = billingMesssageReadPlatformService;
		this.messagePlatformEmailService = messagePlatformEmailService;
		this.entitlementReadPlatformService = entitlementReadPlatformService;
		this.entitlementWritePlatformService = entitlementWritePlatformService;
	}

	// @Transactional
	@Override
	@CronTarget(jobName = JobName.INVOICE)
	public void processInvoice() {


	try
	{
		
		JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.INVOICE.toString());
		
		if(data!=null){
			
		    	List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
		    	    	 
		    	    	 for (ScheduleJobData scheduleJobData : sheduleDatas) {
		    	    		 
		    	    		 file = new File(FileUtils.MIFOSX_BASE_DIR + File.separator + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim()
						                + File.separator + "SheduleLogFile"+ File.separator +"ScheduleLog-"+new Date().toString().replace(" ", "-").trim()+".log");
						        
						        FileUtils.BILLING_JOB_INVOICE_PATH=file.getAbsolutePath();
						        
						        if(!file.exists()){
						        	try {
										file.createNewFile();
									} catch (IOException e) {
										try {
											FileWriter fw = new FileWriter(file);
											fw.append(e.toString());
											fw.close();
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										e.printStackTrace();
									}
						        }


		    	 			List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
		    	 			
		    	 			// Get the Client Ids
		    	 			for (Long clientId : clientIds) {
		    	 				try {
		    	 					
		    	 					if(data.isDynamic().equalsIgnoreCase("Y")){
		    	 						
		    	 						BigDecimal amount=this.invoiceClient.invoicingSingleClient(clientId,new LocalDate());
		    	 						FileWriter fw = new FileWriter(file);
										fw.append("ClientId: "+clientId+"\tAmount: "+amount.toString());
										fw.close();
		    	 					}else{
		    	 						BigDecimal amount=this.invoiceClient.invoicingSingleClient(clientId,data.getProcessDate());
		    	 						FileWriter fw = new FileWriter(file);
										fw.append("ClientId: "+clientId+"\tAmount: "+amount.toString());
										fw.close();
		    	 					}

		    	 					

		    	 				} catch (Exception dve) {
		    	 					
		    	 					handleCodeDataIntegrityIssues(null, dve);
		    	 				}
		    	 			}
		    	 			/*ScheduleJobs scheduleJob = this.scheduledJobRepository
		    	 					.findOne(scheduleJobData.getId());
		    	 			scheduleJob.setStatus('Y');
		    	 			this.scheduledJobRepository.save(scheduleJob);*/
		    	 		}

		    	 		System.out.println("Invoices are Generated....."+ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		    	
		    }
	
	}catch(DataIntegrityViolationException exception)
	{
		exception.printStackTrace();
	}
	

	}

	private void handleCodeDataIntegrityIssues(Object object, Exception dve) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.REQUESTOR)
	public void processRequest() {

		try {

			System.out.println("Processing Request Details.......");
			List<PrepareRequestData> data = this.prepareRequestReadplatformService.retrieveDataForProcessing();
			for (PrepareRequestData requestData : data) {

				this.prepareRequestReadplatformService.processingClientDetails(requestData);
			}
			System.out.println(" Requestor Job is Completed...."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {

		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.RESPONSOR)
	public void processResponse() {

		try {
			System.out.println("Processing Response Details.......");

			List<ProcessingDetailsData> processingDetails = this.processRequestReadplatformService.retrieveProcessingDetails();

			for (ProcessingDetailsData detailsData : processingDetails) {

				this.processRequestWriteplatformService.notifyProcessingDetails(detailsData);
			}
			System.out.println("Responsor Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());

		} catch (DataIntegrityViolationException exception) {

		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.SIMULATOR)
	public void processSimulator() {

		try {
			System.out.println("Processing Simulator Details.......");

			List<ProcessingDetailsData> processingDetails = this.processRequestReadplatformService
					.retrieveUnProcessingDetails();

			for (ProcessingDetailsData detailsData : processingDetails) {

				ProcessRequest processRequest = this.processRequestRepository
						.findOne(detailsData.getId());

				processRequest.setProcessStatus();
				this.processRequestRepository.save(processRequest);

			}
			System.out.println("Simulator Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {

		}
	}

	@Override
	@CronTarget(jobName = JobName.GENERATE_STATMENT)
	public void generateStatment() {

		try {
			System.out.println("Processing statement Details.......");

			JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.GENERATE_STATMENT.toString());
		    if(data!=null){
		    	 
		    	 List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
		    	 
		    	for(ScheduleJobData scheduleJobData:sheduleDatas)
				{
					List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
					  
					 for(Long clientId:clientIds)
					 {
						
						 JSONObject jsonobject = new JSONObject();
						
							DateTimeFormatter formatter = DateTimeFormat.forPattern("dd MMMM yyyy");
							String formattedDate ;
							if(data.isDynamic().equalsIgnoreCase("Y")){
								formattedDate = formatter.print(new LocalDate());	
							}else{
								formattedDate = formatter.print(data.getDueDate());
							}
							

							// System.out.println(formattedDate);
							jsonobject.put("dueDate",formattedDate);
							jsonobject.put("locale", "en");
							jsonobject.put("dateFormat", "dd MMMM YYYY");
							jsonobject.put("message", data.getPromotionalMessage());
							this.billingMasterApiResourse.retrieveBillingProducts(clientId,	jsonobject.toString());
					 }

				}

			}
			System.out.println("statement Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (Exception exception) {

		}
	}

	/*@Transactional
	@Override
	@CronTarget(jobName = JobName.MESSANGER)
	public void processingMessages() {
		try {
			System.out.println("Processing Message Details.......");
			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.MESSANGER.toString());
            
			if (data != null) {

				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService
						.retrieveSheduleJobParameterDetails(data.getBatchName());

				for (ScheduleJobData scheduleJobData : sheduleDatas) {

					Long messageId = this.sheduleJobReadPlatformService
							.getMessageId(data.getMessageTempalate());

					this.billingMessageDataWritePlatformService
							.createMessageData(messageId,
									scheduleJobData.getQuery(),null);

				}
			}

			System.out.println("Messanger job is completed"
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		}

		catch (Exception dve) {
			handleCodeDataIntegrityIssues(null, dve);
		}
	}*/
	
	@Transactional
	@Override
	@CronTarget(jobName = JobName.MESSAGE_MERGE)
	public void processingMessages() 
	  {
		try 
		{
			JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.MESSAGE_MERGE.toString());
	         
          if(data!=null){
      			
		    List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobDetails(data.getSendEmail());
		
		    for (ScheduleJobData scheduleJobData : sheduleDatas) {

					Long messageId = this.sheduleJobReadPlatformService.getMessageId(data.getEmailMessageTemplateName());
					
					if(messageId!=null){
						
					  this.billingMessageDataWritePlatformService.createMessageData(messageId,scheduleJobData.getQuery(),null);
					
					}
				}
		
		   List<ScheduleJobData> sheduleData = this.sheduleJobReadPlatformService.retrieveSheduleJobDetails(data.getSendMessage());
		
		   for (ScheduleJobData scheduleJobData : sheduleData) {
			
			Long messageId = this.sheduleJobReadPlatformService.getMessageId(data.getSendMessageTemplateName());
			
			if(messageId!=null){
				
			   this.billingMessageDataWritePlatformService.createMessageData(messageId,scheduleJobData.getQuery(),null);
			
			}
			
		  }
		   
		   List<ScheduleJobData> osdData = this.sheduleJobReadPlatformService.retrieveSheduleJobDetails(data.getOsdMessage());
		   

		   for (ScheduleJobData scheduleJobData : osdData) {
			
			Long messageId = this.sheduleJobReadPlatformService.getMessageId(data.getOSDMessageTemplate());
			
			if(messageId!=null){
				
			   this.billingMessageDataWritePlatformService.createMessageData(messageId,scheduleJobData.getQuery(),"OSDMessage");
			
			}
			
		  }
		   
	     }
    	   
		}
		
		catch (Exception dve) 
		{
			handleCodeDataIntegrityIssues(null, dve);
		}
	  }

	@Transactional
	@Override
	@CronTarget(jobName = JobName.AUTO_EXIPIRY)
	public void processingAutoExipryOrders() {
		try {
			System.out.println("Processing Auto Exipiry Details.......");

			
			JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.AUTO_EXIPIRY.toString());
         
                 if(data!=null){
                	 
			List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
			LocalDate exipirydate=null;
			if(data.isDynamic().equalsIgnoreCase("Y")){
				exipirydate=new LocalDate();
			}else{
				exipirydate=data.getExipiryDate();
			}
			for (ScheduleJobData scheduleJobData : sheduleDatas) 
			{
				List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
				
				for(Long clientId:clientIds)
				{
					
				List<OrderData> orderDatas = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);
				
      			for (OrderData orderData : orderDatas) 
      			  {
      				
      				if(!(orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.DISCONNECTED.toString()) || orderData.getStatus().equalsIgnoreCase(StatusTypeEnum.PENDING.toString())))
      				 {
      					
				    if (orderData.getEndDate().equals(exipirydate) || exipirydate.isAfter(orderData.getEndDate()))
				     {

				    	  SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
				  		//System.out.println(dateFormat.format(localDate.toDate()));
					JSONObject jsonobject = new JSONObject();
					jsonobject.put("disconnectReason","Date Expired");
					jsonobject.put("disconnectionDate",dateFormat.format(orderData.getEndDate().toDate()));
					jsonobject.put("dateFormat","dd MMMM yyyy");
					jsonobject.put("locale","en");
					final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());

					final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
							null,clientId, null, null, null,null, null, null);
					this.orderWritePlatformService.disconnectOrder(command,	orderData.getId());
				     }
				}
			}
				}
				}
		}
		
		   


		

		} catch (Exception dve) {
			handleCodeDataIntegrityIssues(null, dve);

		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.PUSH_NOTIFICATION)
	public void processNotify() {
		try {
			System.out.println("Processing Notify Details.......");
			List<BillingMessageDataForProcessing> billingMessageDataForProcessings=this.billingMesssageReadPlatformService.retrieveMessageDataForProcessing();
    	    for(BillingMessageDataForProcessing emailDetail : billingMessageDataForProcessings){
    	    	if(emailDetail.getMessageType()=='E'){
    	    		 this.messagePlatformEmailService.sendToUserEmail(emailDetail);
    	    	}
    	    	else if(emailDetail.getMessageType()=='M'){
    	    		String message = this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
    	    		this.messagePlatformEmailService.sendToUserMobile(message,emailDetail.getId());
    	    	}
    	    	else{
    	    		return;
    	    	}		                        
           }
			System.out.println("Notify Job is Completed...");
		} catch (DataIntegrityViolationException exception) {

		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.Middleware)
	public void processMiddleware() {
		// TODO Auto-generated method stub
		try {

			System.out.println("Processing Middleware Details.......");

			JobParameterData data = this.sheduleJobReadPlatformService
					.getJobParameters(JobName.Middleware.toString());
			String credentials = data.getUsername().trim() + ":"
					+ data.getPassword().trim();
			byte[] encoded = Base64.encodeBase64(credentials.getBytes());
			HttpClient httpClient = new DefaultHttpClient();

			List<EntitlementsData> entitlementDataForProcessings = this.entitlementReadPlatformService
					.getProcessingData(new Long(100));

			for (EntitlementsData entitlementsData : entitlementDataForProcessings) {
				Long clientId = entitlementsData.getClientId();
				ClientEntitlementData clientdata = this.entitlementReadPlatformService.getClientData(clientId);
				ReceiveMessage = "Success";
				if(entitlementsData.getRequestType().equalsIgnoreCase("ACTIVATION")){
				    String status="";
					String query = "login= " + clientdata.getEmailId()+ "&password=0000&full_name="+ clientdata.getFullName()
							+ "&account_number="+ clientId + "&tariff_plan=1&status=1&&stb_mac="+ entitlementsData.getHardwareId();
					StringEntity se = new StringEntity(query.trim());
					HttpPost postRequest = new HttpPost(data.getUrl() + "accounts/");
					postRequest.setHeader("Authorization", "Basic " + new String(encoded));
					postRequest.setEntity(se);
					HttpResponse response = httpClient.execute(postRequest);
					if (response.getStatusLine().getStatusCode() != 200) {
						System.out.println("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
						return;
					}
					BufferedReader br1 = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
					String output;
					while ((output = br1.readLine()) != null) {
						System.out.println(output);
						final JsonElement ele = fromApiJsonHelper.parse(output);
						 status = fromApiJsonHelper.extractStringNamed("status", ele);
						if (status.equalsIgnoreCase("ERROR")) {
							final String error = fromApiJsonHelper.extractStringNamed("error", ele);
							ReceiveMessage = "failure :" + error;
						}
					}
					
                    if(!(status.equalsIgnoreCase("ERROR") || status.equalsIgnoreCase(""))){
					String query1 = data.getUrl() + "account_subscription/"+ clientId;
					String queryData = "subscribed[]="+ entitlementsData.getProduct();
					StringEntity se1 = new StringEntity(queryData.trim());

					HttpPut putRequest = new HttpPut(query1.trim());
					putRequest.setHeader("Authorization", "Basic " + new String(encoded));
					putRequest.setEntity(se1);
					HttpResponse response1 = httpClient.execute(putRequest);
					if (response1.getStatusLine().getStatusCode() != 200) {
						System.out.println("Failed : HTTP error code : " + response1.getStatusLine().getStatusCode());
						return;
					}
					BufferedReader br2 = new BufferedReader(new InputStreamReader((response1.getEntity().getContent())));

					String output2;
					while ((output2 = br2.readLine()) != null) {
						System.out.println(output2);
						final JsonElement ele = fromApiJsonHelper.parse(output2);
						final String status1 = fromApiJsonHelper.extractStringNamed("status", ele);
						if (status1.equalsIgnoreCase("ERROR")) {
							final String error = fromApiJsonHelper.extractStringNamed("error", ele);
							ReceiveMessage = "failure :" + error;
						}
					}
                   }
				  
				}else if(entitlementsData.getRequestType().equalsIgnoreCase("DISCONNECTION")){
					String query = "status= " + new Long(0);
					StringEntity se = new StringEntity(query.trim());
					String url=""+data.getUrl() + "accounts/123";
					HttpPut putrequest = new HttpPut(url.trim());
					putrequest.setHeader("Authorization", "Basic " + new String(encoded));
					putrequest.setEntity(se);
					HttpResponse putresponse = httpClient.execute(putrequest);
					if (putresponse.getStatusLine().getStatusCode() != 200) {
						System.out.println("Failed : HTTP error code : "+ putresponse.getStatusLine().getStatusCode());
						return;
					}
					BufferedReader br = new BufferedReader(
							new InputStreamReader((putresponse.getEntity().getContent())));
					String output="";
					while ((output = br.readLine()) != null) {
						System.out.println(output);
						final JsonElement ele = fromApiJsonHelper.parse(output);
						final String status = fromApiJsonHelper.extractStringNamed("status", ele);
						if (status.equalsIgnoreCase("ERROR")) {
							final String error = fromApiJsonHelper.extractStringNamed("error", ele);
							ReceiveMessage = "failure :" + error;
						}
					}
				}
					JsonObject object = new JsonObject();
					object.addProperty("serviceId",entitlementsData.getServiceId());
					object.addProperty("receivedStatus", new Long(1));
					
					object.addProperty("receiveMessage", ReceiveMessage);
					String entityName = "ENTITLEMENT";
					final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
					JsonCommand comm = new JsonCommand(null, object.toString(),element1, fromApiJsonHelper, entityName,
							entitlementsData.getId(), null, null, null, null,null, null, null, null, null);
					CommandProcessingResult result = this.entitlementWritePlatformService.create(comm);
					System.out.println(result);

				}
				httpClient.getConnectionManager().shutdown();
				System.out.println("Middleware Job is Completed...");
			
		} catch (DataIntegrityViolationException exception) {

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

	
