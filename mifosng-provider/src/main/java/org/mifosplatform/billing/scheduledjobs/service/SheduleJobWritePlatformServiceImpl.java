package org.mifosplatform.billing.scheduledjobs.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
import org.mifosplatform.billing.action.service.ActionDetailsReadPlatformService;
import org.mifosplatform.billing.action.service.ActiondetailsWritePlatformService;
import org.mifosplatform.billing.billingmaster.api.BillingMasterApiResourse;
import org.mifosplatform.billing.billingorder.service.InvoiceClient;
import org.mifosplatform.billing.contract.service.ContractPeriodReadPlatformService;
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
import org.mifosplatform.billing.scheduledjobs.data.EventActionData;
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

public class SheduleJobWritePlatformServiceImpl implements SheduleJobWritePlatformService {
	

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
	private final ContractPeriodReadPlatformService contractPeriodReadPlatformService;
	private final ScheduleJob scheduleJob;
	private final EntitlementWritePlatformService entitlementWritePlatformService;
	private final ActionDetailsReadPlatformService actionDetailsReadPlatformService;
	private final ActiondetailsWritePlatformService  actiondetailsWritePlatformService;
	private String ReceiveMessage;

	private File file=null;
	
	@Autowired
	public SheduleJobWritePlatformServiceImpl(final InvoiceClient invoiceClient,final SheduleJobReadPlatformService sheduleJobReadPlatformService,
			final ScheduledJobRepository scheduledJobRepository,final BillingMasterApiResourse billingMasterApiResourse,final FromJsonHelper fromApiJsonHelper,
			final ProcessRequestRepository processRequestRepository,final OrderWritePlatformService orderWritePlatformService,final ScheduleJob scheduleJob,
			final OrderReadPlatformService orderReadPlatformService,final BillingMessageDataWritePlatformService billingMessageDataWritePlatformService,
			final PrepareRequestReadplatformService prepareRequestReadplatformService,final ProcessRequestReadplatformService processRequestReadplatformService,
			final ProcessRequestWriteplatformService processRequestWriteplatformService,final ScheduledJobDetailRepository scheduledJobDetailRepository,
			final BillingMesssageReadPlatformService billingMesssageReadPlatformService,final MessagePlatformEmailService messagePlatformEmailService,
			final EntitlementReadPlatformService entitlementReadPlatformService,final EntitlementWritePlatformService entitlementWritePlatformService,
			final ContractPeriodReadPlatformService contractPeriodReadPlatformService,final ActionDetailsReadPlatformService actionDetailsReadPlatformService,
			final ActiondetailsWritePlatformService actiondetailsWritePlatformService) {

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
		this.contractPeriodReadPlatformService=contractPeriodReadPlatformService;
		this.actionDetailsReadPlatformService=actionDetailsReadPlatformService;
		this.actiondetailsWritePlatformService=actiondetailsWritePlatformService;
		this.scheduleJob=scheduleJob;
	
		
	}

	// @Transactional
	@Override
	@CronTarget(jobName = JobName.INVOICE)
	public void processInvoice() {
	try
	{

		JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.INVOICE.toString());
		Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processInvoice");
		if(data!=null){
			 String path=FileUtils.generateLogFileDirectory()+ JobName.INVOICE.toString() + File.separator +"Invoice_"+new Date().toString().replace(":", "_").trim()+".log";
			 FileHandler fileHandler = new FileHandler(path);
			 logger.addHandler(fileHandler);
	         SimpleFormatter formatter = new SimpleFormatter();  
	         fileHandler.setFormatter(formatter);  
         FileUtils.BILLING_JOB_PATH=path.trim();
		     List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
					    
		    	         if(sheduleDatas.isEmpty()){
				 				logger.info("ScheduleJobData Empty \r\n");
				 		 }
		    	    	 for (ScheduleJobData scheduleJobData : sheduleDatas) {
		    	    		 /*
		    	 		    	    		 file = new File(FileUtils.MIFOSX_BASE_DIR + File.separator + ThreadLocalContextUtil.getTenant().getName().replaceAll(" ", "").trim()
						                + File.separator + "SheduleLogFile"+ File.separator +"ScheduleLog-"+new Date().toString().replace(" ", "-").trim()+".log");
						        
						        FileUtils.BILLING_JOB_INVOICE_PATH=file.getAbsolutePath();*/

		    	    		logger.info("ScheduleJobData id= "+scheduleJobData.getId()+" ,BatchName= "+scheduleJobData.getBatchName()+
		    	    				" ,query="+scheduleJobData.getQuery()+"\r\n");

		    	 			List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
		    	 			if(clientIds.isEmpty()){
		    	 				logger.info("Invoicing clients are not found \r\n");
		    	 			}
		    	 			else{
		    	 				logger.info("Invoicing the clients..... \r\n");
		    	 			}
		    	 			// Get the Client Ids
		    	 			for (Long clientId : clientIds) {
		    	 				try {
		    	 					
		    	 					if(data.isDynamic().equalsIgnoreCase("Y")){
		    	 						
		    	 						BigDecimal amount=this.invoiceClient.invoicingSingleClient(clientId,new LocalDate());				
										logger.info("ClientId: "+clientId+"\tAmount: "+amount.toString()+"\r\n");
										
		    	 					}else{
		    	 						BigDecimal amount=this.invoiceClient.invoicingSingleClient(clientId,data.getProcessDate());
										logger.info("ClientId: "+clientId+"\tAmount: "+amount.toString()+"\r\n");									
		    	 					}

		    	 					

		    	 				} catch (Exception dve) {
		    	 					
		    	 					handleCodeDataIntegrityIssues(null, dve);
		    	 				}
		    	 			}
		    	 		}
		    	    	 logger.info("Invoices are Generated....."+ThreadLocalContextUtil.getTenant().getTenantIdentifier()+"\r\n");
		    	    	 fileHandler.close();
		    	    	 
		    	 		System.out.println("Invoices are Generated....."+ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		    	
		    }
	
	}catch(Exception exception)
	{
		exception.printStackTrace();
	} 
	}

	private void handleCodeDataIntegrityIssues(Object object, Exception dve) {

	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.REQUESTOR)
	public void processRequest() {

		try {

			System.out.println("Processing Request Details.......");
			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processRequest");
			List<PrepareRequestData> data = this.prepareRequestReadplatformService.retrieveDataForProcessing();
			
			if(!data.isEmpty()){
				
				String path=FileUtils.generateLogFileDirectory()+JobName.REQUESTOR.toString()+ File.separator +"Requester-"+new Date().toString().replace(":", "_").trim()+".log";
				FileHandler fileHandler = new FileHandler(path);
				 logger.addHandler(fileHandler);
		         SimpleFormatter formatter = new SimpleFormatter();  
		         fileHandler.setFormatter(formatter);  
			     FileUtils.BILLING_JOB_PATH=path.trim();
			     
			    logger.info("Processing Request Details.......");
				for (PrepareRequestData requestData : data) {
					logger.info("Prepare Request id="+requestData.getRequestId()+" ,clientId="+requestData.getClientId()+" ,orderId="
					+requestData.getOrderId()+" ,HardwareId="+requestData.getHardwareId()+" ,planName="+requestData.getPlanName()+
					" ,Provisiong system="+requestData.getProvisioningSystem()+"\r\n");
					this.prepareRequestReadplatformService.processingClientDetails(requestData);
				}
				logger.info(" Requestor Job is Completed...."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+"\r\n");
				fileHandler.close();
				
			}
			
			System.out.println(" Requestor Job is Completed...."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
			
		} catch (DataIntegrityViolationException exception) {

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.RESPONSOR)
	public void processResponse() {

		try {
			System.out.println("Processing Response Details.......");
			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processResponse");
			List<ProcessingDetailsData> processingDetails = this.processRequestReadplatformService.retrieveProcessingDetails();
			if(!processingDetails.isEmpty()){
				String path=FileUtils.generateLogFileDirectory()+ JobName.RESPONSOR.toString()+ File.separator +"Responser-"+new Date().toString().replace(":", "_").trim()+".log";
				FileHandler fileHandler = new FileHandler(path);
				 logger.addHandler(fileHandler);
		         SimpleFormatter formatter = new SimpleFormatter();  
		         fileHandler.setFormatter(formatter);  

			     FileUtils.BILLING_JOB_PATH=path.trim();
			     
			    logger.info("Processing Response Details.......");
				for (ProcessingDetailsData detailsData : processingDetails) {
	                logger.info("Process Response id="+detailsData.getId()+" ,orderId="+detailsData.getOrderId()+" ,Provisiong System="
	                		+detailsData.getProvisionigSystem()+" ,RequestType="+detailsData.getRequestType()+"\r\n");
					this.processRequestWriteplatformService.notifyProcessingDetails(detailsData);
				}
				logger.info("Responsor Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+" \r\n");
				fileHandler.close();
				
			}
			System.out.println("Responsor Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());

		} catch (DataIntegrityViolationException exception) {

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.SIMULATOR)
	public void processSimulator() {

		try {
			System.out.println("Processing Simulator Details.......");

			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processSimulator");
			List<ProcessingDetailsData> processingDetails = this.processRequestReadplatformService.retrieveUnProcessingDetails();
			if(!processingDetails.isEmpty()){
				String path=FileUtils.generateLogFileDirectory()+JobName.SIMULATOR.toString()+ File.separator +"Simulator-"+new Date().toString().replace(":", "_").trim()+".log";
				FileHandler fileHandler = new FileHandler(path);
				 logger.addHandler(fileHandler);
		         SimpleFormatter formatter = new SimpleFormatter();  
		         fileHandler.setFormatter(formatter);  

			     FileUtils.BILLING_JOB_PATH=path.trim();
			     
			    logger.info("Processing Simulator Details.......");
				for (ProcessingDetailsData detailsData : processingDetails) {
	                
					logger.info("simulator Process Request id="+detailsData.getId()+" ,orderId="+detailsData.getOrderId()+" ,Provisiong System="
	                		+detailsData.getProvisionigSystem()+" ,RequestType="+detailsData.getRequestType()+"\r\n");
					ProcessRequest processRequest = this.processRequestRepository.findOne(detailsData.getId());
					processRequest.setProcessStatus();
					this.processRequestRepository.save(processRequest);
	
				}
				logger.info("Simulator Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+" \r\n");
				fileHandler.close();

			}
			System.out.println("Simulator Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (DataIntegrityViolationException exception) {

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Override
	@CronTarget(jobName = JobName.GENERATE_STATMENT)
	public void generateStatment() {

		try {
			System.out.println("Processing statement Details.......");
			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.generateStatment");
			JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.GENERATE_STATMENT.toString());
		    if(data!=null){
		    	String path=FileUtils.generateLogFileDirectory()+ JobName.GENERATE_STATMENT.toString() + File.separator +"statement-"+new Date().toString().replace(":", "_").trim()+".log";
		    	FileHandler fileHandler = new FileHandler(path);
				 logger.addHandler(fileHandler);
		         SimpleFormatter formatter = new SimpleFormatter();  
		         fileHandler.setFormatter(formatter);  

			     FileUtils.BILLING_JOB_PATH=path.trim();
			     
		        logger.info("Processing statement Details....... \r\n");
		    	List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
		    	if(sheduleDatas.isEmpty()){
	 				logger.info("ScheduleJobData Empty \r\n");
	 		    }
		    	for(ScheduleJobData scheduleJobData:sheduleDatas)
				{
		    		logger.info("ScheduleJobData id= "+scheduleJobData.getId()+" ,BatchName= "+scheduleJobData.getBatchName()+
    	    				" ,query="+scheduleJobData.getQuery()+"\r\n");
		    		
					List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
					if(clientIds.isEmpty()){
    	 				logger.info("no records are available for statement generation \r\n");
    	 			}
    	 			else{
    	 				logger.info("generate Statements for the clients..... \r\n");
    	 			}
					 for(Long clientId:clientIds)
					 {
						    logger.info("processing clientId: "+clientId);
						    JSONObject jsonobject = new JSONObject();
						
							DateTimeFormatter formatter1 = DateTimeFormat.forPattern("dd MMMM yyyy");
							String formattedDate ;
							if(data.isDynamic().equalsIgnoreCase("Y")){
								formattedDate = formatter1.print(new LocalDate());	
							}else{
								formattedDate = formatter1.print(data.getDueDate());
							}
							

							// System.out.println(formattedDate);
							jsonobject.put("dueDate",formattedDate);
							jsonobject.put("locale", "en");
							jsonobject.put("dateFormat", "dd MMMM YYYY");
							jsonobject.put("message", data.getPromotionalMessage());
							logger.info("sending jsonData for Statement Generation is: "+jsonobject.toString()+" . \r\n");
							this.billingMasterApiResourse.retrieveBillingProducts(clientId,	jsonobject.toString());
					 }

				}
		    	logger.info("statement Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+" . \r\n");
		    	fileHandler.close();
				
			}
			System.out.println("statement Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	
	@Transactional
	@Override
	@CronTarget(jobName = JobName.MESSAGE_MERGE)
	public void processingMessages() 
	  {
		try 
		{
		 JobParameterData data=this.sheduleJobReadPlatformService.getJobParameters(JobName.MESSAGE_MERGE.toString());
		 Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processingMessages");
          if(data!=null){
        	  String path=FileUtils.generateLogFileDirectory()+ JobName.MESSAGE_MERGE.toString() + File.separator +"Messanger-"+new Date().toString().replace(":", "_").trim()+".log";
        	  FileHandler fileHandler = new FileHandler(path);
 			 logger.addHandler(fileHandler);
 	         SimpleFormatter formatter = new SimpleFormatter();  
 	         fileHandler.setFormatter(formatter);  

 		     FileUtils.BILLING_JOB_PATH=path.trim();
 		     
		      logger.info("Processing the Messanger....... \r\n");
		      
		    List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobDetails(data.getBatchName());
		   
		    if(sheduleDatas.isEmpty()){
 				logger.info("ScheduleJobData Empty \r\n");
 		    }
		    for (ScheduleJobData scheduleJobData : sheduleDatas) {
		    	   logger.info("ScheduleJobData id= "+scheduleJobData.getId()+" ,BatchName= "+scheduleJobData.getBatchName()+
	    				" ,query="+scheduleJobData.getQuery()+"\r\n");
		    	   logger.info("Selected Message Template Name is :" +data.getDefaultValue()+" \r\n");
				   Long messageId = this.sheduleJobReadPlatformService.getMessageId(data.getDefaultValue());
				   logger.info("Selected Message Template id is :" +messageId+" \r\n");
					if(messageId!=null){
					  logger.info("generating the message....... \r\n");
					  this.billingMessageDataWritePlatformService.createMessageData(messageId,scheduleJobData.getQuery());
					}
				}	   
		    logger.info("Messanger Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+" . \r\n");
	    	fileHandler.close();
			

	     }
          System.out.println("Messanger Job is Completed..."
					+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());

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
			 Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processingAutoExipryOrders");
            if(data!=null){
            	
	            String path=FileUtils.generateLogFileDirectory()+ JobName.AUTO_EXIPIRY.toString() + File.separator +"AutoExipiry-"+new Date().toString().replace(":", "_").trim()+".log";
	            FileHandler fileHandler = new FileHandler(path);
				 logger.addHandler(fileHandler);
		         SimpleFormatter formatter = new SimpleFormatter();  
		         fileHandler.setFormatter(formatter);  

			     FileUtils.BILLING_JOB_PATH=path.trim();
			     
			    logger.info("Processing Auto Exipiry Details....... \r\n");
			      
				List<ScheduleJobData> sheduleDatas = this.sheduleJobReadPlatformService.retrieveSheduleJobParameterDetails(data.getBatchName());
				LocalDate exipirydate=null;
				
				if(sheduleDatas.isEmpty()){
	 				logger.info("ScheduleJobData Empty \r\n");
	 		    }

				
				if(data.isDynamic().equalsIgnoreCase("Y")){
					exipirydate=new LocalDate();
				}else{
					exipirydate=data.getExipiryDate();
				}
				for (ScheduleJobData scheduleJobData : sheduleDatas) 
				{
					logger.info("ScheduleJobData id= "+scheduleJobData.getId()+" ,BatchName= "+scheduleJobData.getBatchName()+
    	    				" ,query="+scheduleJobData.getQuery()+"\r\n");
					List<Long> clientIds = this.sheduleJobReadPlatformService.getClientIds(scheduleJobData.getQuery());
					
					if(clientIds.isEmpty()){
    	 				logger.info("no records are available for Auto Expiry \r\n");
    	 			}
					
					for(Long clientId:clientIds)
					  {
						logger.info("processing client id :"+clientId+"\r\n");
						List<OrderData> orderDatas = this.orderReadPlatformService.retrieveClientOrderDetails(clientId);
						if(orderDatas.isEmpty()){
							logger.info("No Orders are Found for :"+clientId+"\r\n");
						}					    
		      			for (OrderData orderData : orderDatas) 
		      			  {
		      				logger.info("OrderData id="+orderData.getId()+" ,ClientId="+orderData.getClientId()+" ,Status="+orderData.getStatus()
		      						+" ,PlanCode="+orderData.getPlan_code()+" ,ServiceCode="+orderData.getService_code()+" ,Price="+
		      						orderData.getPrice()+" ,OrderEndDate="+orderData.getEndDate()+"\r\n");
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
									logger.info("sending json data for Disconnecting the Order is : "+jsonobject.toString()+"\r\n");
									final JsonElement parsedCommand = this.fromApiJsonHelper.parse(jsonobject.toString());
				
									final JsonCommand command = JsonCommand.from(jsonobject.toString(),parsedCommand,this.fromApiJsonHelper,"DissconnectOrder",clientId, null,
											null,clientId, null, null, null,null, null, null);
									this.orderWritePlatformService.disconnectOrder(command,	orderData.getId());
								 }
		      				 }
		      			  }
						}
					}
		    	logger.info("Auto Exipiry Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier()+" . \r\n");
		    	fileHandler.close();

		}
            
            System.out.println("Auto Exipiry Job is Completed..."+ ThreadLocalContextUtil.getTenant().getTenantIdentifier());
		

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
			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processNotify");
			List<BillingMessageDataForProcessing> billingMessageDataForProcessings=this.billingMesssageReadPlatformService.retrieveMessageDataForProcessing();
    	    if(!billingMessageDataForProcessings.isEmpty()){
    	    	String path=FileUtils.generateLogFileDirectory()+JobName.PUSH_NOTIFICATION.toString() + File.separator +"PushNotification-"+new Date().toString().replace(":", "_").trim()+".log";
    	    	FileHandler	fileHandler = new FileHandler(path);
   			 logger.addHandler(fileHandler);
   	         SimpleFormatter formatter = new SimpleFormatter();  
   	         fileHandler.setFormatter(formatter);  

   		     FileUtils.BILLING_JOB_PATH=path.trim();
   		     
			    logger.info("Processing Notify Details....... \r\n");
    	    	
				for(BillingMessageDataForProcessing emailDetail : billingMessageDataForProcessings){
					logger.info("BillingMessageData id="+emailDetail.getId()+" ,MessageTo="+emailDetail.getMessageTo()+" ,MessageType="
							+emailDetail.getMessageType()+" ,MessageFrom="+emailDetail.getMessageFrom()+" ,Message="
							+emailDetail.getBody()+"\r\n");
	    	    	if(emailDetail.getMessageType()=='E'){
	    	    		 String Result=this.messagePlatformEmailService.sendToUserEmail(emailDetail);
	    	    		 logger.info(emailDetail.getId()+"-"+Result+" ... \r\n");
	    	    	}
	    	    	else if(emailDetail.getMessageType()=='M'){
	    	    		String message = this.sheduleJobReadPlatformService.retrieveMessageData(emailDetail.getId());
	    	    		String Result=this.messagePlatformEmailService.sendToUserMobile(message,emailDetail.getId());
	    	    		 logger.info(emailDetail.getId()+"-"+Result+" ..\r\n");
	    	    	}
	    	    	else{
	    	    		 logger.info("Message Type Unknown ..\r\n");
	    	    	}		                        
	           }
				logger.info("Notify Job is Completed.... \r\n");
				fileHandler.close();
				
    	    }
			System.out.println("Notify Job is Completed...");
		} catch (DataIntegrityViolationException exception) {

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.Middleware)
	public void processMiddleware() {

		try {

			System.out.println("Processing Middleware Details.......");
			Logger logger = Logger.getLogger("SheduleJobWritePlatformServiceImpl.processMiddleware");
			JobParameterData data = this.sheduleJobReadPlatformService.getJobParameters(JobName.Middleware.toString());
			if(data!=null){
				String credentials = data.getUsername().trim() + ":"+ data.getPassword().trim();
				byte[] encoded = Base64.encodeBase64(credentials.getBytes());
				HttpClient httpClient = new DefaultHttpClient();
	
				List<EntitlementsData> entitlementDataForProcessings = this.entitlementReadPlatformService
						.getProcessingData(new Long(100));
	            if(!entitlementDataForProcessings.isEmpty()){
	            	String path=FileUtils.generateLogFileDirectory()+ JobName.Middleware.toString() + File.separator +"middleware-"+new Date().toString().replace(":", "_").trim()+".log";
	             FileHandler	fileHandler = new FileHandler(path);
	   			 logger.addHandler(fileHandler);
	   	         SimpleFormatter formatter = new SimpleFormatter();  
	   	         fileHandler.setFormatter(formatter);  

	   		     FileUtils.BILLING_JOB_PATH=path.trim();
	   		     
				    logger.info("Processing Middleware Details....... \r\n");
				    logger.info("Staker Server Details.....");
				    logger.info("UserName of Staker:"+data.getUsername());
				    logger.info("password of Staker: **************");
				    logger.info("url of staker:"+data.getUrl());
				  
				for (EntitlementsData entitlementsData : entitlementDataForProcessings) {
					logger.info("EntitlementsData id="+entitlementsData.getId()+" ,clientId="+entitlementsData.getClientId()+" ,HardwareId="
							+entitlementsData.getHardwareId()+" ,RequestType="+entitlementsData.getRequestType()+"\r\n");
					Long clientId = entitlementsData.getClientId();
					ClientEntitlementData clientdata = this.entitlementReadPlatformService.getClientData(clientId);
					ReceiveMessage = "Success";
					if(entitlementsData.getRequestType().equalsIgnoreCase("ACTIVATION")){
					    String status="";
						String query = "login= " + clientdata.getEmailId()+ "&password=0000&full_name="+ clientdata.getFullName()
								+ "&account_number="+ clientId + "&tariff_plan=1&status=1&&stb_mac="+ entitlementsData.getHardwareId();
						logger.info("data Sending to Stalker Server is: "+query+" \r\n");
						StringEntity se = new StringEntity(query.trim());
						logger.info("Url for Activation request:"+data.getUrl() + "accounts/" +"\r\n");
						HttpPost postRequest = new HttpPost(data.getUrl() + "accounts/");
						postRequest.setHeader("Authorization", "Basic " + new String(encoded));
						postRequest.setEntity(se);
						HttpResponse response = httpClient.execute(postRequest);
						if (response.getStatusLine().getStatusCode() != 200) {
							System.out.println("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode());
							logger.info("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode()+" \r\n");
							return;
						}
						BufferedReader br1 = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
						String output;
						while ((output = br1.readLine()) != null) {
							System.out.println(output);
							logger.info("Output From Staker : "+ output+" \r\n");
							final JsonElement ele = fromApiJsonHelper.parse(output);
							 status = fromApiJsonHelper.extractStringNamed("status", ele);
							 logger.info("status of the output is : "+ status+" \r\n");
							if (status.equalsIgnoreCase("ERROR")) {
								final String error = fromApiJsonHelper.extractStringNamed("error", ele);
								logger.info("error of the output is : "+ error+" \r\n");
								ReceiveMessage = "failure :" + error;
							}
						}
						
	                    if(!(status.equalsIgnoreCase("ERROR") || status.equalsIgnoreCase(""))){
	                    logger.info("Url for account_subscription request:"+data.getUrl() + "account_subscription/"+ clientId +"\r\n");
						String query1 = data.getUrl() + "account_subscription/"+ clientId;
						String queryData = "subscribed[]="+ entitlementsData.getProduct();
						logger.info("data Sending to Stalker Server is: "+queryData+" \r\n");
						StringEntity se1 = new StringEntity(queryData.trim());
	
						HttpPut putRequest = new HttpPut(query1.trim());
						putRequest.setHeader("Authorization", "Basic " + new String(encoded));
						putRequest.setEntity(se1);
						HttpResponse response1 = httpClient.execute(putRequest);
						if (response1.getStatusLine().getStatusCode() != 200) {
							System.out.println("Failed : HTTP error code : " + response1.getStatusLine().getStatusCode());
							logger.info("Failed : HTTP error code : "+ response.getStatusLine().getStatusCode()+" \r\n");
							return;
						}
						BufferedReader br2 = new BufferedReader(new InputStreamReader((response1.getEntity().getContent())));
	
						String output2;
						while ((output2 = br2.readLine()) != null) {
							System.out.println(output2);
							logger.info("Output From Staker : "+ output2+" \r\n");
							final JsonElement ele = fromApiJsonHelper.parse(output2);
							final String status1 = fromApiJsonHelper.extractStringNamed("status", ele);
							 logger.info("status of the output is : "+ status1+" \r\n");
							if (status1.equalsIgnoreCase("ERROR")) {
								final String error = fromApiJsonHelper.extractStringNamed("error", ele);
								logger.info("error of the output is : "+ error+" \r\n");
								ReceiveMessage = "failure :" + error;
							}
						}
	                   }
					  
					}else if(entitlementsData.getRequestType().equalsIgnoreCase("DISCONNECTION")){
						String query = "status= " + new Long(0);
						logger.info("data Sending to Stalker Server is: "+query+" \r\n");
						StringEntity se = new StringEntity(query.trim());					
						String url=""+data.getUrl() + "accounts/123";
						logger.info("Url for DISCONNECTION request:"+ url +"\r\n");
						HttpPut putrequest = new HttpPut(url.trim());
						putrequest.setEntity(se);
						putrequest.setHeader("Authorization", "Basic " + new String(encoded));
						
						
						HttpResponse putresponse = httpClient.execute(putrequest);
						if (putresponse.getStatusLine().getStatusCode() != 200) {
							System.out.println("Failed : HTTP error code : "+ putresponse.getStatusLine().getStatusCode());
							logger.info("Failed : HTTP error code : "+ putresponse.getStatusLine().getStatusCode()+" \r\n");
							return;
						}
						BufferedReader br = new BufferedReader(
								new InputStreamReader((putresponse.getEntity().getContent())));
						String output="";
						while ((output = br.readLine()) != null) {
							System.out.println(output);
							logger.info("Output From Staker : "+ output+" \r\n");
							final JsonElement ele = fromApiJsonHelper.parse(output);
							final String status = fromApiJsonHelper.extractStringNamed("status", ele);
							 logger.info("status of the output is : "+ status+" \r\n");
							if (status.equalsIgnoreCase("ERROR")) {
								final String error = fromApiJsonHelper.extractStringNamed("error", ele);
								logger.info("error of the output is : "+ error+" \r\n");
								ReceiveMessage = "failure :" + error;
							}
						}
					}
						JsonObject object = new JsonObject();
						object.addProperty("serviceId",entitlementsData.getServiceId());
						object.addProperty("receivedStatus", new Long(1));
						
						object.addProperty("receiveMessage", ReceiveMessage);
						String entityName = "ENTITLEMENT";
						logger.info("sending json data to EntitlementApi is:"+object.toString()+"\r\n");
						final JsonElement element1 = fromApiJsonHelper.parse(object.toString());
						JsonCommand comm = new JsonCommand(null, object.toString(),element1, fromApiJsonHelper, entityName,
								entitlementsData.getId(), null, null, null, null,null, null, null, null, null);
						CommandProcessingResult result = this.entitlementWritePlatformService.create(comm);
						System.out.println(result);
						logger.info("Result From the EntitlementApi is:"+result+"\r\n");
	
					}
				    logger.info("Middleware Job is Completed...");
				    fileHandler.close();
				    
	            }
					httpClient.getConnectionManager().shutdown();
					System.out.println("Middleware Job is Completed...");
			}
		} catch (DataIntegrityViolationException exception) {

		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	@Transactional
	@Override
	@CronTarget(jobName = JobName.EVENT_ACTION_PROCESSOR)
	public void eventActionProcessor() {
		
	System.out.println("Processing Event Actions.....");
		
      //Retrieve Event Actions
	List<EventActionData> actionDatas=this.actionDetailsReadPlatformService.retrieveAllActionsForProccessing();
	
	for(EventActionData eventActionData:actionDatas){
		
		System.out.println(eventActionData.getId());
		this.actiondetailsWritePlatformService.ProcessEventActions(eventActionData);
	}
		
	System.out.println("Event Actions are Proccesed....");
	}
}

	
