package org.mifosplatform.billing.uploadstatus.service;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifosplatform.billing.adjustment.api.AdjustmentApiResource;
import org.mifosplatform.billing.adjustment.data.AdjustmentData;
import org.mifosplatform.billing.adjustment.service.AdjustmentReadPlatformService;
import org.mifosplatform.billing.importfile.data.MRNErrorData;
import org.mifosplatform.billing.inventory.api.InventoryItemDetailsApiResource;
import org.mifosplatform.billing.inventory.command.ItemDetailsCommand;
import org.mifosplatform.billing.inventory.data.InventoryItemDetailsData;
import org.mifosplatform.billing.inventory.exception.OrderQuantityExceedsException;
import org.mifosplatform.billing.inventory.service.InventoryItemDetailsWritePlatformService;
import org.mifosplatform.billing.order.exceptions.NoGrnIdFoundException;
import org.mifosplatform.billing.payments.api.PaymentsApiResource;
import org.mifosplatform.billing.paymode.data.McodeData;
import org.mifosplatform.billing.paymode.service.PaymodeReadPlatformService;
import org.mifosplatform.billing.uploadstatus.command.UploadStatusCommand;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatus;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusCommandValidator;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusEnum;
import org.mifosplatform.billing.uploadstatus.domain.UploadStatusRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.ApiParameterError;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.InvalidJsonException;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.exception.UnsupportedParameterException;
import org.mifosplatform.infrastructure.core.serialization.ApiRequestJsonSerializationSettings;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.exception.ClientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


@Service
public class UploadStatusWritePlatformServiceImp implements UploadStatusWritePlatformService{

	private PlatformSecurityContext context;
	private UploadStatusRepository uploadStatusRepository;
	private final Gson gsonConverter;
	private final Set<String> RESPONSE_DATA_ITEM_DETAILS_PARAMETERS = new HashSet<String>(Arrays.asList("id", "itemMasterId", "serialNumber", "grnId","provisioningSerialNumber", "quality", "status","warranty", "remarks"));
	private int rownumber;
	private String filePath;
	private int countno;
	private Long processRecords=(long) 0;
	private Long unprocessRecords=new Long(0);
	private Long totalRecords=new Long(0);
	private String processStatus=null;
	private String errormessage=null;
	private String uploadStatusValue="UPLOADSTATUS";
	private ApiRequestJsonSerializationSettings serSettings;
	private Long orderIdValue;
	private String resultStatus="";
	//public int rowupdateno=0;
	public List<AdjustmentData> adjustmentDataList;
	 public Collection<McodeData> paymodeDataList;
	
	 final private static String MEDIAASSETS_RESOURCE_TYPE = "ASSESTS";
	 final private static String EPG_RESOURCE_TYPE = "EPGPROGRAMGUIDE";
	 final private static String MRN_RESOURCE_TYPE = "MRNDETAILS";
	 private String uploadProcess=null;
	
	 private final AdjustmentReadPlatformService adjustmentReadPlatformService;
	 private final PaymodeReadPlatformService paymodeReadPlatformService;
	 private PaymentsApiResource paymentsApiResource;
	 private AdjustmentApiResource adjustmentApiResource;
	
	private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	
	@Autowired
	private InventoryItemDetailsWritePlatformService inventoryItemDetailsWritePlatformService;
	@Autowired
	private  DefaultToApiJsonSerializer<ItemDetailsCommand> toApiJsonSerializer;	
	
	private InventoryItemDetailsApiResource inventoryItemDetailsApiResource;
	
	@Autowired
	public UploadStatusWritePlatformServiceImp(final PlatformSecurityContext context,UploadStatusRepository uploadStatusRepository,DefaultToApiJsonSerializer<InventoryItemDetailsData> toApiJsonSerializer,final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService, final InventoryItemDetailsApiResource inventoryItemDetailsApiResource,
			AdjustmentReadPlatformService adjustmentReadPlatformService,PaymodeReadPlatformService paymodeReadPlatformService,PaymentsApiResource paymentsApiResource,AdjustmentApiResource adjustmentApiResource) {
		this.context=context;
		this.uploadStatusRepository=uploadStatusRepository;
		this.commandsSourceWritePlatformService=commandsSourceWritePlatformService;
		this.gsonConverter=new Gson();
		this.inventoryItemDetailsApiResource = inventoryItemDetailsApiResource;
		this.adjustmentReadPlatformService=adjustmentReadPlatformService;
		this.paymodeReadPlatformService=paymodeReadPlatformService;
		this.paymentsApiResource=paymentsApiResource;
		this.adjustmentApiResource=adjustmentApiResource;
	}
	
	//@Transactional
	
	@Override
	public CommandProcessingResult updateUploadStatus(Long orderId,int countno, ApiRequestJsonSerializationSettings settings) {
		//processRecords=(long)0;
		processStatus=null;
		serSettings=settings;
		errormessage="";
		
		
		UploadStatus uploadStatus = this.uploadStatusRepository.findOne(orderId);
		uploadProcess=uploadStatus.getUploadProcess();
		LocalDate currentDate = new LocalDate();
		currentDate.toDate();
    
		filePath=uploadStatus.getUploadFilePath();
		String fileLocation = uploadStatus.getUploadFilePath();	
		

		if(uploadProcess.equalsIgnoreCase("Hardware Items") && new File(fileLocation).getName().contains(".csv")){
			//ArrayList<ItemDetailsCSVData> CSVData = new ArrayList<ItemDetailsCSVData>();
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			BufferedReader csvFileBufferedReader = null;
			String line = null;
			String splitLineRegX = ",";
			int initialindex=8,i=1;
			Long processRecordCount=0L;
			Long totalRecordCount=0L;
			JSONObject jsonObject = new JSONObject();
			UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
			try{
				csvFileBufferedReader = new BufferedReader(new FileReader(filePath));
				line = csvFileBufferedReader.readLine();
				while((line = csvFileBufferedReader.readLine()) != null){
					try{
					String[] currentLineData = line.split(splitLineRegX);
					
					if(currentLineData.length>=8){
						jsonObject.put("itemMasterId",currentLineData[0]);
						jsonObject.put("serialNumber",currentLineData[1]);
						jsonObject.put("grnId",currentLineData[2]);
						jsonObject.put("provisioningSerialNumber",currentLineData[3]);
						jsonObject.put("quality", currentLineData[4]);
						jsonObject.put("status",currentLineData[5]);
						jsonObject.put("warranty", currentLineData[6]);
						jsonObject.put("remarks", currentLineData[7]);
						jsonObject.put("locale", "en");
						//jsonObject.put("clientId", 1);
						//jsonObject.put("officeId", 1);
						totalRecordCount++;
						final CommandWrapper commandRequest = new CommandWrapperBuilder().createInventoryItem(null).withJson(jsonObject.toString().toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
						 if(result!=null){
						    	//Long rsId = result.resourceId();
						    	processRecordCount++;
						    	errorData.add(new MRNErrorData((long)i, "Success."));
						 }
					}else{
						errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
					}
					
					}catch(OrderQuantityExceedsException e){
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
					}catch(NoGrnIdFoundException e){
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
					}catch (PlatformApiDataValidationException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getErrors().get(0).getDefaultUserMessage()));
						
					}catch (PlatformDataIntegrityException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
						
					}catch (NullPointerException e) {
						errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
						
					}catch (IllegalStateException e) {
						errorData.add(new MRNErrorData((long)i,e.getMessage()));
						
					}catch (Exception e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						
					}
					i++;
				}
				
				uploadStatusForMrn.setProcessRecords(processRecordCount);
				uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
				uploadStatusForMrn.setTotalRecords(totalRecordCount);
				writeCSVData(fileLocation, errorData,uploadStatusForMrn);
				processRecordCount=0L;totalRecordCount=0L;
				uploadStatusForMrn=null;
				
			}catch (FileNotFoundException e) {
				throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found", "file.not.found");					
			}catch (Exception e) {
				errorData.add(new MRNErrorData((long)i, "Error: "+e.getCause().getLocalizedMessage()));
				
			}finally{
				if(csvFileBufferedReader!=null){
					try{
						csvFileBufferedReader.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			
			writeToFile(fileLocation,errorData);
			
		}else if(uploadProcess.equalsIgnoreCase("Mrn") && new File(fileLocation).getName().contains(".csv")){
			
		ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
		BufferedReader csvFileBufferedReader = null;
		String line = null;
		String splitLineRegX = ",";
		int initialindex=8,i=1;
		Long processRecordCount=0L;
		Long totalRecordCount=0L;
		JSONObject jsonObject = new JSONObject();
		UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
		try{
			csvFileBufferedReader = new BufferedReader(new FileReader(filePath));
			line = csvFileBufferedReader.readLine();
			while((line = csvFileBufferedReader.readLine()) != null){
				try{
				String[] currentLineData = line.split(splitLineRegX);
				
				if(currentLineData.length>=2){
					
					jsonObject.put("mrnId",currentLineData[0]);
					jsonObject.put("serialNumber",currentLineData[1]);
					jsonObject.put("locale","en");
					totalRecordCount++;
					context.authenticatedUser().validateHasReadPermission(MRN_RESOURCE_TYPE);
					final CommandWrapper commandRequest = new CommandWrapperBuilder().moveMRN().withJson(jsonObject.toString().toString()).build();
					final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
					 if(result!=null){
					    	//Long rsId = result.resourceId();
					    	processRecordCount++;
					    	errorData.add(new MRNErrorData((long)i, "Success."));
					 }
				}else{
					errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
				}
				
				}catch(OrderQuantityExceedsException e){
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
				}catch(NoGrnIdFoundException e){
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
				}catch (PlatformApiDataValidationException e) {
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getErrors().get(0).getDefaultUserMessage()));
					
				}catch (PlatformDataIntegrityException e) {
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
					
				}catch (NullPointerException e) {
					errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
					
				}catch (IllegalStateException e) {
					errorData.add(new MRNErrorData((long)i,e.getMessage()));
					
				}catch (Exception e) {
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
					
				}
				i++;
			}
			
			uploadStatusForMrn.setProcessRecords(processRecordCount);
			uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
			uploadStatusForMrn.setTotalRecords(totalRecordCount);
			writeCSVData(fileLocation, errorData,uploadStatusForMrn);
			processRecordCount=0L;totalRecordCount=0L;
			uploadStatusForMrn=null;
			
		}catch (FileNotFoundException e) {
			throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found", "file.not.found");					
		}catch (Exception e) {
			errorData.add(new MRNErrorData((long)i, "Error: "+e.getCause().getLocalizedMessage()));
			
		}finally{
			if(csvFileBufferedReader!=null){
				try{
					csvFileBufferedReader.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		
		writeToFile(fileLocation,errorData);
		
		}else if(uploadProcess.equalsIgnoreCase("Mrn")){
			Integer cellNumber = 2;
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			Workbook wb = null;
			Long processRecordCount=0L;
			Long totalRecordCount=0L;
			UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
			try {
				wb = WorkbookFactory.create(new File(fileLocation));
				Sheet sheet = wb.getSheetAt(0);	
				int numberOfRows = sheet.getPhysicalNumberOfRows();
				System.out.println("Number of rows : "+numberOfRows);	
				
				for (int i = 1; i < numberOfRows; i++) {

						Row row = sheet.getRow(i);
						JSONObject jsonObject = new JSONObject();
					 
					 try {
		
						 if(row.getCell(1).getStringCellValue().equalsIgnoreCase("EOF"))
							 throw new EOFException();
						
						jsonObject.put("mrnId",row.getCell(0).getNumericCellValue());
						jsonObject.put("serialNumber",row.getCell(1).getStringCellValue());
						jsonObject.put("locale","en");
						totalRecordCount++;
						context.authenticatedUser().validateHasReadPermission(MRN_RESOURCE_TYPE);
						final CommandWrapper commandRequest = new CommandWrapperBuilder().moveMRN().withJson(jsonObject.toString().toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
					    if(result!=null){
					    	//Long rsId = result.resourceId();
					    	processRecordCount++;
					    	errorData.add(new MRNErrorData((long)i, "Success"));
					    	/*
					    	Long rsId = result.resourceId();
					    	if(rsId != null ){
						    	if(rsId>1){
						    		//errorData.add(new MRNErrorData((long)i, row.getCell(2).getStringCellValue(), (long)row.getCell(1).getNumericCellValue(), row.getCell(0).getDateCellValue()));
						    		errorData.add(new MRNErrorData((long)i, result.getTransactionId()));
		 		
						    	}else{
						    		errorData.add(new MRNErrorData((long)i, result.getTransactionId()));
						    	}
						    }
					    */}
						
						//System.out.println(i);
					}catch (PlatformApiDataValidationException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getDefaultUserMessage()));
						
					}catch (PlatformDataIntegrityException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
						
					}catch (NullPointerException e) {
						errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
						
					}catch (EOFException e){
						errorData.add(new MRNErrorData((long)i, "Completed: End Of Record"));
						
						break;
					}catch (IllegalStateException e) {
						errorData.add(new MRNErrorData((long)i,e.getMessage()));
						
					}catch (Exception e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						
					}
				}
				uploadStatusForMrn.setProcessRecords(processRecordCount);
				uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
				uploadStatusForMrn.setTotalRecords(totalRecordCount);
				writeXLSXFileMediaEpgMrn(fileLocation, errorData,uploadStatusForMrn,cellNumber);
				processRecordCount=0L;totalRecordCount=0L;
				uploadStatusForMrn=null;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.getStackTrace();
			}
			
		}else if(uploadProcess.equalsIgnoreCase("Epg") && new File(fileLocation).getName().contains(".csv")){
			
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			BufferedReader csvFileBufferedReader = null;
			String line = null;
			String splitLineRegX = ",";
			int initialindex=8,i=1;
			Long processRecordCount=0L;
			Long totalRecordCount=0L;
			JSONObject jsonObject = new JSONObject();
			UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
			try{
				csvFileBufferedReader = new BufferedReader(new FileReader(filePath));
				line = csvFileBufferedReader.readLine();
				while((line = csvFileBufferedReader.readLine()) != null){
					try{
					String[] currentLineData = line.split(splitLineRegX);
					
					if(currentLineData.length>=11){
						jsonObject.put("channelName",currentLineData[0]);
						jsonObject.put("channelIcon",currentLineData[1]);
						jsonObject.put("programDate",new SimpleDateFormat("dd/MM/yyyy").parse(currentLineData[2]));
						jsonObject.put("startTime",currentLineData[3]);
						jsonObject.put("stopTime",currentLineData[4]);
						jsonObject.put("programTitle",currentLineData[5]);
						jsonObject.put("programDescription",currentLineData[6]);
						jsonObject.put("type",currentLineData[7]);
						jsonObject.put("genre",currentLineData[8]);
						jsonObject.put("locale",currentLineData[9]);
						jsonObject.put("dateFormat",currentLineData[10]);
						jsonObject.put("locale","en");
						totalRecordCount++;
						context.authenticatedUser().validateHasReadPermission(EPG_RESOURCE_TYPE);
						final CommandWrapper commandRequest = new CommandWrapperBuilder().createEpgXsls(0L).withJson(jsonObject.toString().toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
						 if(result!=null){
						    	//Long rsId = result.resourceId();
						    	processRecordCount++;
						    	errorData.add(new MRNErrorData((long)i, "Success."));
						 }
					}else{
						errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
					}
					
					}catch(OrderQuantityExceedsException e){
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
					}catch(NoGrnIdFoundException e){
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
					}catch (PlatformApiDataValidationException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getErrors().get(0).getDefaultUserMessage()));
						
					}catch (PlatformDataIntegrityException e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
						
					}catch (NullPointerException e) {
						errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
						
					}catch (IllegalStateException e) {
						errorData.add(new MRNErrorData((long)i,e.getMessage()));
						
					}catch (Exception e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						
					}
					i++;
				}
				
				uploadStatusForMrn.setProcessRecords(processRecordCount);
				uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
				uploadStatusForMrn.setTotalRecords(totalRecordCount);
				writeCSVData(fileLocation, errorData,uploadStatusForMrn);
				processRecordCount=0L;totalRecordCount=0L;
				uploadStatusForMrn=null;
				
			}catch (FileNotFoundException e) {
				throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found", "file.not.found");					
			}catch (Exception e) {
				errorData.add(new MRNErrorData((long)i, "Error: "+e.getCause().getLocalizedMessage()));
				
			}finally{
				if(csvFileBufferedReader!=null){
					try{
						csvFileBufferedReader.close();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
			
			
			writeToFile(fileLocation,errorData);
			
			
			}else if(uploadProcess.equalsIgnoreCase("Adjustments") && new File(fileLocation).getName().contains(".csv")){
			
				adjustmentDataList=this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
				ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
				BufferedReader csvFileBufferedReader = null;
				String line = null;
				String splitLineRegX = ",";
				int initialindex=8,i=1;
				Long processRecordCount=0L;
				Long totalRecordCount=0L;
				JSONObject jsonObject = new JSONObject();
				UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
				try{
					csvFileBufferedReader = new BufferedReader(new FileReader(filePath));
					line = csvFileBufferedReader.readLine();
					while((line = csvFileBufferedReader.readLine()) != null){
						try{
						String[] currentLineData = line.split(splitLineRegX);
						
						
						if(adjustmentDataList.size()>0)
				        {
				         for(AdjustmentData adjustmentData:adjustmentDataList)
				         {
				         if( adjustmentData.getAdjustment_code().equalsIgnoreCase(currentLineData[2].toString()));
				          {          
				        	  if(currentLineData.length>=6){
				        		  jsonObject.put("adjustment_date", currentLineData[1]);
					        	  jsonObject.put("adjustment_code", adjustmentData.getId());
					        	  jsonObject.put("adjustment_type",currentLineData[3]);
					        	  jsonObject.put("amount_paid",currentLineData[4]);
					        	  jsonObject.put("Remarks",currentLineData[5]);
					        	  jsonObject.put("locale", "en");
					        	  jsonObject.put("dateFormat","dd MMMM yyyy");
					        	  totalRecordCount++;
					        	  final CommandWrapper commandRequest = new CommandWrapperBuilder().createAdjustment(Long.valueOf(currentLineData[0])).withJson(jsonObject.toString().toString()).build();
					              final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
					              if(result!=null){
								    	//Long rsId = result.resourceId();
								    	processRecordCount++;
								    	errorData.add(new MRNErrorData((long)i, "Success."));
					              }
					        
								}else{
									errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
								}
				        	}
				         }
				        }
						
						
						if(currentLineData.length>=8){
							
						}else{
							errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
						}
						
						}catch (DataIntegrityViolationException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getLocalizedMessage()));
						}catch (PlatformApiDataValidationException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getErrors().get(0).getDefaultUserMessage()));
						}catch (PlatformDataIntegrityException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
						}catch (NullPointerException e) {
							errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
						}catch (IllegalStateException e) {
							errorData.add(new MRNErrorData((long)i,e.getMessage()));
						}catch (ClientNotFoundException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));
						}catch (Exception e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						}
						i++;
					}
					
					uploadStatusForMrn.setProcessRecords(processRecordCount);
					uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
					uploadStatusForMrn.setTotalRecords(totalRecordCount);
					writeCSVData(fileLocation, errorData,uploadStatusForMrn);
					processRecordCount=0L;totalRecordCount=0L;
					uploadStatusForMrn=null;
					
				}catch (FileNotFoundException e) {
					throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found", "file.not.found");					
				}catch (Exception e) {
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getCause().getLocalizedMessage()));
				}finally{
					if(csvFileBufferedReader!=null){
						try{
							csvFileBufferedReader.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
				
				writeToFile(fileLocation,errorData);
				
			}else if(uploadProcess.equalsIgnoreCase("Payments") && new File(fileLocation).getName().contains(".csv")){
			
				adjustmentDataList=this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
				ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
				BufferedReader csvFileBufferedReader = null;
				String line = null;
				String splitLineRegX = ",";
				int initialindex=8,i=1;
				Long processRecordCount=0L;
				Long totalRecordCount=0L;
				JSONObject jsonObject = new JSONObject();
				UploadStatus uploadStatusForMrn = this.uploadStatusRepository.findOne(orderId);
				try{
					csvFileBufferedReader = new BufferedReader(new FileReader(filePath));
					line = csvFileBufferedReader.readLine();
					while((line = csvFileBufferedReader.readLine()) != null){
						try{
						String[] currentLineData = line.split(splitLineRegX);
											
						if(currentLineData.length>=5){
							paymodeDataList = this.paymodeReadPlatformService.retrievemCodeDetails("Payment Mode");
						       
				             if(paymodeDataList.size()>0)
				                 {
				                   for(McodeData paymodeData:paymodeDataList)
				                      {
					                    if(paymodeData.getPaymodeCode().equalsIgnoreCase(currentLineData[2].toString()))
					                      {
					        	              jsonObject.put("paymentCode",paymodeData.getId());
					                      }
				                    }
				          
				                 jsonObject.put("clientId", currentLineData[0]);
				                 jsonObject.put("paymentDate",currentLineData[1]);
				                 jsonObject.put("amountPaid", currentLineData[3]);
				                 jsonObject.put("remarks",  currentLineData[4]);
				                 jsonObject.put("locale", "en");
				                 jsonObject.put("dateFormat","dd MMMM yyyy");
				                 totalRecordCount++;
				                 final CommandWrapper commandRequest = new CommandWrapperBuilder().createPayment(Long.valueOf(currentLineData[0])).withJson(jsonObject.toString().toString()).build();
				                 final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
				                 if(result!=null){
								    	processRecordCount++;
								    	errorData.add(new MRNErrorData((long)i, "Success."));
					              }
				               
				        }
						}else{
							errorData.add(new MRNErrorData((long)i, "Improper Data in this line"));
						}
						
						
						}catch (DataIntegrityViolationException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getLocalizedMessage()));
						}catch (PlatformApiDataValidationException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getErrors().get(0).getParameterName()+" : "+e.getErrors().get(0).getDefaultUserMessage()));
						}catch (PlatformDataIntegrityException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getParameterName()+" : "+e.getDefaultUserMessage()));
						}catch (NullPointerException e) {
							errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
						}catch (IllegalStateException e) {
							errorData.add(new MRNErrorData((long)i,e.getMessage()));
						}catch (ClientNotFoundException e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getDefaultUserMessage()));	
						}catch (Exception e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
							
						}
						i++;
					}
					
					uploadStatusForMrn.setProcessRecords(processRecordCount);
					uploadStatusForMrn.setUnprocessedRecords(totalRecordCount-processRecordCount);
					uploadStatusForMrn.setTotalRecords(totalRecordCount);
					writeCSVData(fileLocation, errorData,uploadStatusForMrn);
					processRecordCount=0L;totalRecordCount=0L;
					uploadStatusForMrn=null;
					
				}catch (FileNotFoundException e) {
					throw new PlatformDataIntegrityException("file.not.found", "file.not.found", "file.not.found", "file.not.found");					
				}catch (Exception e) {
					errorData.add(new MRNErrorData((long)i, "Error: "+e.getCause().getLocalizedMessage()));
				}finally{
					if(csvFileBufferedReader!=null){
						try{
							csvFileBufferedReader.close();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
								
				writeToFile(fileLocation,errorData);
				
			}else if(uploadProcess.equalsIgnoreCase("Epg")){
			Integer cellNumber = 11;
			UploadStatus uploadStatusForEpg = this.uploadStatusRepository.findOne(orderId);
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			Workbook wb = null;
			Long processRecordCount=0L;
			Long totalRecordCount=0L;
			try {

			
				
				
				wb = WorkbookFactory.create(new File(fileLocation));
				Sheet sheet = wb.getSheetAt(0);	
				int numberOfRows = sheet.getPhysicalNumberOfRows();
				System.out.println("Number of rows : "+numberOfRows);	
				
				for (int i = 1; i < numberOfRows; i++) {

						Row row = sheet.getRow(i);
						JSONObject jsonObject = new JSONObject();
					 
					 try {

						 if(row.getCell(0).getStringCellValue().equalsIgnoreCase("EOF"))
							 throw new EOFException();
						totalRecordCount++;
						jsonObject.put("channelName",row.getCell(0).getStringCellValue());
						jsonObject.put("channelIcon",row.getCell(1).getStringCellValue());
						jsonObject.put("programDate",row.getCell(2).getDateCellValue());
						jsonObject.put("startTime",row.getCell(3).getDateCellValue());
						jsonObject.put("stopTime",row.getCell(4).getDateCellValue());
						jsonObject.put("programTitle",row.getCell(5).getStringCellValue());
						jsonObject.put("programDescription",row.getCell(6).getStringCellValue());
						jsonObject.put("type",row.getCell(7).getStringCellValue());
						jsonObject.put("genre",row.getCell(8).getStringCellValue());
						jsonObject.put("locale",row.getCell(9).getStringCellValue());
						jsonObject.put("dateFormat",row.getCell(10).getStringCellValue());
						jsonObject.put("locale","en");

						context.authenticatedUser().validateHasReadPermission(EPG_RESOURCE_TYPE);
						final CommandWrapper commandRequest = new CommandWrapperBuilder().createEpgXsls(1L).withJson(jsonObject.toString().toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
					    if(result!=null){
					    	errorData.add(new MRNErrorData((long)i,"Success"));
						    processRecordCount++;
					    }
					} catch(PlatformDataIntegrityException e){
						errorData.add(new MRNErrorData((long)i, e.getParameterName()+" : "+e.getDefaultUserMessage()));
					}catch (PlatformApiDataValidationException e) {
						errorData.add(new MRNErrorData((long)i, e.getErrors().get(0).getParameterName()+" : "+e.getDefaultUserMessage()));
					}catch (NullPointerException e) {
						errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
					}catch (EOFException e){
						errorData.add(new MRNErrorData((long)i, "Completed: End Of Record"));
						break;
					}catch (IllegalStateException e) {
						errorData.add(new MRNErrorData((long)i,e.getMessage()));
					}catch (Exception e) {
							errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
						
					}
				}
				
				uploadStatusForEpg.setProcessRecords(processRecordCount);
				uploadStatusForEpg.setUnprocessedRecords(totalRecordCount-processRecordCount);
				uploadStatusForEpg.setTotalRecords(totalRecordCount);
				writeXLSXFileMediaEpgMrn(fileLocation, errorData,uploadStatusForEpg,cellNumber);
				processRecordCount=0L;totalRecordCount=0L;
				uploadStatusForEpg=null;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.getStackTrace();
			}
			
		}else if(uploadProcess.equalsIgnoreCase("MediaAssets")){
			Integer cellNumber = 15;
			UploadStatus uploadStatusForMediaAsset = this.uploadStatusRepository.findOne(orderId);
			ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
			Workbook wb = null;
			Long processRecordCount=0L;
			Long totalRecordCount=0L;
			try {

				
				wb = WorkbookFactory.create(new File(fileLocation));
				Sheet mediaSheet = wb.getSheetAt(0);
				Sheet mediaAttributeSheet = wb.getSheetAt(1);
				Sheet mediaLocationSheet = wb.getSheetAt(2);
				int msNumberOfRows = mediaSheet.getPhysicalNumberOfRows();
				System.out.println("Number of rows : "+msNumberOfRows);
				
				
				
				for (int i = 1; i < msNumberOfRows; i++) {

						Row mediaRow = mediaSheet.getRow(i);
						Row mediaAttributeRow = mediaAttributeSheet.getRow(i);
						Row mediaLocationRow = mediaLocationSheet.getRow(i);
						JSONObject jsonObject = new JSONObject();
						
					 try {
						 if(mediaRow.getCell(0).getStringCellValue().equalsIgnoreCase("EOF"))
							 throw new EOFException();
						totalRecordCount++;
						jsonObject.put("mediaTitle",mediaRow.getCell(0).getStringCellValue());//-
						jsonObject.put("mediaType",mediaRow.getCell(1).getStringCellValue());//-
						jsonObject.put("mediaCategoryId",mediaRow.getCell(2).getStringCellValue());//-
						jsonObject.put("image",mediaRow.getCell(3).getStringCellValue());//- 
						jsonObject.put("duration",mediaRow.getCell(4).getStringCellValue());//-
						jsonObject.put("genre",mediaRow.getCell(5).getStringCellValue());//-
						jsonObject.put("subject",mediaRow.getCell(6).getStringCellValue());//-
						jsonObject.put("overview",mediaRow.getCell(7).getStringCellValue());//-
						jsonObject.put("contentProvider",mediaRow.getCell(8).getStringCellValue());//-
						jsonObject.put("rated",mediaRow.getCell(9).getStringCellValue());//-
						jsonObject.put("rating",mediaRow.getCell(10).getNumericCellValue());//-
						jsonObject.put("status",mediaRow.getCell(11).getStringCellValue());//-
						SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
						jsonObject.put("releaseDate",formatter.format(mediaRow.getCell(12).getDateCellValue()));
						jsonObject.put("dateFormat",mediaRow.getCell(13).getStringCellValue());
						jsonObject.put("locale",mediaRow.getCell(14).getStringCellValue());
								
							JSONArray a = new JSONArray();
							Map<String, Object> m = new LinkedHashMap<String, Object>();
							m.put("attributeType",mediaAttributeRow.getCell(0).getStringCellValue());
							m.put("attributeName", mediaAttributeRow.getCell(1).getNumericCellValue());
							m.put("attributevalue", mediaAttributeRow.getCell(2).getStringCellValue());
							m.put("attributeNickname", mediaAttributeRow.getCell(3).getStringCellValue());
							m.put("attributeImage", mediaAttributeRow.getCell(4).getStringCellValue());
							
							a.put(m);
							jsonObject.put("mediaassetAttributes",a);
							
							
							JSONArray b = new JSONArray();
							Map<String, Object> n = new LinkedHashMap<String, Object>();
							n.put("languageId",mediaLocationRow.getCell(0).getNumericCellValue());	
							n.put("formatType",mediaLocationRow.getCell(1).getStringCellValue());
							n.put("location",mediaLocationRow.getCell(2).getStringCellValue());
							b.put(n);
							
							jsonObject.put("mediaAssetLocations",b);
						
						context.authenticatedUser().validateHasReadPermission(MEDIAASSETS_RESOURCE_TYPE );
						final CommandWrapper commandRequest = new CommandWrapperBuilder().createMediaAsset().withJson(jsonObject.toString()).build();
						final CommandProcessingResult result = this.commandsSourceWritePlatformService.logCommandSource(commandRequest);
						
						if(result!=null){
					    	//Long rsId = result.resourceId();
					    	errorData.add(new MRNErrorData((long)i, "Success"));
					    	processRecordCount++;
					    }
						
						
					}catch(PlatformDataIntegrityException e){
						errorData.add(new MRNErrorData((long)i, e.getParameterName()+" : "+e.getDefaultUserMessage()));
					}catch (PlatformApiDataValidationException e) {
						errorData.add(new MRNErrorData((long)i, e.getErrors().get(0).getParameterName()+" : "+e.getDefaultUserMessage()));
					}catch (NullPointerException e) {
						errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
					}catch (EOFException e){
						errorData.add(new MRNErrorData((long)i, "Completed: End Of Record"));
						break;
					}catch (IllegalStateException e) {
						errorData.add(new MRNErrorData((long)i,e.getMessage()));
					}catch (Exception e) {
						errorData.add(new MRNErrorData((long)i, "Error: "+e.getMessage()));
					}
				}
				
				uploadStatusForMediaAsset.setProcessRecords(processRecordCount);
				uploadStatusForMediaAsset.setUnprocessedRecords(totalRecordCount-processRecordCount);
				uploadStatusForMediaAsset.setTotalRecords(totalRecordCount);
				writeXLSXFileMediaEpgMrn(fileLocation, errorData,uploadStatusForMediaAsset,cellNumber);
				processRecordCount=0L;totalRecordCount=0L;
				uploadStatusForMediaAsset=null;

			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				e.getStackTrace();
			}
	
			
		}else{
		try {
			File file=new File(filePath);
			   OPCPackage excelFileToRead=OPCPackage.open(file);
			   XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);
			   
			 
			//InputStream excelFileToRead = new FileInputStream(filePath);
			//XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
		//	String serialno = "0";
			 
			if (countno == 0) {
				countno = countno + 2;
			} else if (countno == 1) {
				countno = countno + 1;
			}
			System.out.println("Excel Row No is: " + countno);
			
			Iterator rows = sheet.rowIterator();
			Vector<XSSFCell> v = new Vector<XSSFCell>();
			if (countno > 0) {
				countno = countno - 1;
			}
			while (rows.hasNext()) {
				
			

				row = (XSSFRow) rows.next();
				rownumber = row.getRowNum();
				
				if (rownumber > 0) {
					if (rownumber >= countno) {
						
						Iterator cells = row.cellIterator();
						while (cells.hasNext()) {

 							cell = (XSSFCell) cells.next();

							//if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								v.add(cell);
							//} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								//v.add(cell);
							//} else {
								//v.add(cell);
						//	}

						}
					
						//System.out.println(v.elementAt(0).toString());
						if(v.elementAt(0).toString().equalsIgnoreCase("EOF"))
						{
							long unprocessedRecords=totalRecords-processRecords;
							uploadStatus.update(currentDate,processStatus,processRecords,unprocessedRecords,errormessage,totalRecords);
							this.uploadStatusRepository.save(uploadStatus);
							processRecords=new Long(0);
							totalRecords=new Long(0);
							unprocessRecords=new Long(0);
							break;
						}
						else{
						
							


       JSONObject jsonobject = new JSONObject();
     
      if(uploadProcess.equalsIgnoreCase("Hardware Items"))
      {
    		totalRecords++;
      
    		 jsonobject.put("itemMasterId", new Double(v.elementAt(0).toString()).longValue());
             jsonobject.put("serialNumber", v.elementAt(1).toString());
             jsonobject.put("grnId", new Double(v.elementAt(2).toString()).longValue());
             jsonobject.put("provisioningSerialNumber", v.elementAt(3).toString());
             jsonobject.put("quality", v.elementAt(4).toString());
             jsonobject.put("status",v.elementAt(5).toString());
             jsonobject.put("warranty", new Double(v.elementAt(6).toString()).longValue());
             jsonobject.put("locale", "en");
             jsonobject.put("remarks", v.elementAt(7).toString());
             jsonobject.put("clientId", 1);
             jsonobject.put("officeId", 1);
             jsonobject.put("flag", 1);
      
      inventoryItemDetailsApiResource.addItemDetails(jsonobject.toString().toString());
      }
      else if (uploadProcess.equalsIgnoreCase("Adjustments")) {
    		totalRecords++;
       
         adjustmentDataList=this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
       
        if(adjustmentDataList.size()>0)
        {
         for(AdjustmentData adjustmentData:adjustmentDataList)
         {
         if( adjustmentData.getAdjustment_code().equalsIgnoreCase(v.elementAt(2).toString()));
          {          
          // jsonobject.put("clientId", new Double(v.elementAt(2).toString()).longValue());
                 jsonobject.put("adjustment_date", v.elementAt(1).toString());
                 jsonobject.put("adjustment_code", adjustmentData.getId());
                 jsonobject.put("adjustment_type",v.elementAt(3).toString());
                 jsonobject.put("amount_paid", v.elementAt(4).toString());
                 jsonobject.put("Remarks", v.elementAt(5).toString());
                 jsonobject.put("locale", "en");
                 jsonobject.put("dateFormat","dd MMMM yyyy");
                 adjustmentApiResource.addNewAdjustment(new Double(String.valueOf(v.elementAt(0)).toString()).longValue(), jsonobject.toString());
                 break;
          }
         }
        }
      }
        else if (uploadProcess.equalsIgnoreCase("Payments")) {
        	  
        	 totalRecords++;
             paymodeDataList = this.paymodeReadPlatformService.retrievemCodeDetails("Payment Mode");
       
             if(paymodeDataList.size()>0)
                 {
                   for(McodeData paymodeData:paymodeDataList)
                      {
                    if( paymodeData.getPaymodeCode().equalsIgnoreCase(v.elementAt(2).toString()));
                     {
        	              jsonobject.put("paymentCode",paymodeData.getId());
                      }
                    }
          
                     jsonobject.put("clientId", "");
                    
                 jsonobject.put("paymentDate", v.elementAt(1).toString());
                 jsonobject.put("amountPaid", v.elementAt(3).toString());
                 jsonobject.put("remarks",  v.elementAt(4).toString());
                 jsonobject.put("locale", "en");
                 jsonobject.put("dateFormat","dd MMMM yyyy");
                 paymentsApiResource.createPayment(new Double(String.valueOf(v.elementAt(0))).longValue()/*v.elementAt(0).toString())*/, jsonobject.toString());
               //  break;
        }
        }
						
						++processRecords;
						 resultStatus="Success";
						writeXLSXFile(filePath,resultStatus);
		           processStatus=UploadStatusEnum.COMPLETED.toString();
		          
						}
		                v.removeAllElements();
					}
				}
			
			}

		} 
		
		catch (Exception e) {
			//writeXLSXFile(filePath);
			unprocessRecords++;
			//System.out.println("exceptuon"+e);
			errormessage=UploadStatusEnum.ERROR.toString();
		 
		   resultStatus="Failure";
		   //System.out.println(e.toString());
		  
		   
		
			if(e.toString().contains("ClientNotFoundException"))
			{
			errormessage="Client with this id does not exist";
			
			}else if(e.toString().contains("NoGrnIdFoundException")){
				errormessage="GrnId is not a valid Id";
			
			}else if(e.toString().contains("PlatformDataIntegrityException")){
			   
				errormessage="Serial Number is already exist";	
			   
			}else if(e.toString().contains("OrderQuantityExceedsException")){
				errormessage ="order quntity is completed";
			}else if(e.toString().contains("PlatformApiDataValidationException")){
				errormessage ="missing some value in this record";
			}
			 writeXLSXFile(filePath,errormessage);
			
			if (rownumber+1 >= countno) {
				int rownum = rownumber;
				rownum = rownum + 2;
			
					//updateUploadStatusReadXls(filePath, rownum);
				updateUploadStatus(orderId,rownum,null);
			
			} 
		
	//	this.exception();
		}
		
	}	
		
		
		// if (order==null || order.getStatus() == 3) {
		// throw new ProductNotFoundException(order.getId());
		// }
		
		
		//totalRecords=new Long(0);
		//processRecords=totalRecords;
	//	unprocessedRecords=totalRecords;
		return new CommandProcessingResult(Long.valueOf(-1));

	}
	
	
	public CommandProcessingResult updateUploadStatusReadXls(String filepath,int rowvalue)
	{
		try {
			
		//	processRecords=(long)0;
			
			errormessage=UploadStatusEnum.COMPLETED.toString();
			InputStream excelFileToRead = new FileInputStream(filePath);

			XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);

			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;
			String serialno = "0";
			 countno = rowvalue;
			if (countno == 0) {
				countno = countno + 2;
			} else if (countno == 1) {
				countno = countno + 1;
			}
			System.out.println("Excel Row No is: " + countno);
			
			Iterator rows = sheet.rowIterator();
			Vector<XSSFCell> v = new Vector<XSSFCell>();
			if (countno > 0) {
				countno = countno - 1;
			}
			while (rows.hasNext()) {

				row = (XSSFRow) rows.next();
				rownumber = row.getRowNum();
				
				if (rownumber > 0) {
					if (rownumber >= countno) {
						
						Iterator cells = row.cellIterator();
						while (cells.hasNext()) {

							cell = (XSSFCell) cells.next();

							if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								v.add(cell);
							} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								v.add(cell);
							} else {
								v.add(cell);
							}

						}
					
						
						
						
					//	ItemDetailsCommand itemDetailsCommand=new ItemDetailsCommand();
						
						//System.out.println(v.elementAt(0).toString());
						if(v.elementAt(0).toString().equalsIgnoreCase("EOF"))
						{
							break;
						}
						else{

						       JSONObject jsonobject = new JSONObject();
						     
						      if(uploadProcess.equalsIgnoreCase("Hardware Items"))
						      {
						      
						       jsonobject.put("itemMasterId", new Double(v.elementAt(0).toString()).longValue());
						             jsonobject.put("serialNumber", v.elementAt(1).toString());
						             jsonobject.put("grnId", new Double(v.elementAt(2).toString()).longValue());
						             jsonobject.put("provisioningSerialNumber", v.elementAt(3).toString());
						             jsonobject.put("quality", v.elementAt(4).toString());
						             jsonobject.put("status",v.elementAt(5).toString());
						             jsonobject.put("warranty", new Double(v.elementAt(6).toString()).longValue());
						             jsonobject.put("locale", "en");
						             jsonobject.put("remarks", v.elementAt(7).toString());
						             jsonobject.put("clientId", 1);
						             jsonobject.put("officeId", 1);
						             jsonobject.put("flag", 1);
						      inventoryItemDetailsApiResource.addItemDetails(jsonobject.toString().toString());
						      }
						      else if (uploadProcess.equalsIgnoreCase("Adjustments")) {
						       
						       
						         adjustmentDataList=this.adjustmentReadPlatformService.retrieveAllAdjustmentsCodes();
						       
						      //  if(adjustmentDataList.size()>0)
						      //  {
						         for(AdjustmentData adjustmentData:adjustmentDataList)
						         {
						         if( adjustmentData.getAdjustment_code().equalsIgnoreCase(v.elementAt(2).toString()));
						          {
						        	  jsonobject.put("adjustment_code", adjustmentData.getId());
						          }
						         }
						          // jsonobject.put("clientId", new Double(v.elementAt(2).toString()).longValue());
						                 jsonobject.put("adjustment_date", v.elementAt(1).toString());
						               
						                 jsonobject.put("amount_paid", v.elementAt(4).toString());
						                 jsonobject.put("adjustment_type",v.elementAt(3).toString());
						                 jsonobject.put("Remarks", v.elementAt(5).toString());
						                 jsonobject.put("locale", "en");
						                 jsonobject.put("dateFormat","dd MMMM yyyy");
						                 adjustmentApiResource.addNewAdjustment(new Double(String.valueOf(v.elementAt(0))).longValue(), jsonobject.toString());
						                 break;
						          
						         }
						       						     // }
						        else if (uploadProcess.equalsIgnoreCase("Payments")) {
						        	
						        	totalRecords++;
						                          paymodeDataList = this.paymodeReadPlatformService.retrievemCodeDetails("Payment Mode");
						       
						       //                   if(paymodeDataList.size()>0)
						     //   {
						         for(McodeData paymodeData:paymodeDataList)
						         {
						         if( paymodeData.getPaymodeCode().equalsIgnoreCase(v.elementAt(2).toString()));
						          {
						        	  jsonobject.put("paymentCode",paymodeData.getId());
						          }
						         }
						          // jsonobject.put("clientId", new Double(v.elementAt(2).toString()).longValue());
						           jsonobject.put("clientId", "");
						                            
						                 jsonobject.put("paymentDate", v.elementAt(1).toString());
						                 jsonobject.put("amountPaid", v.elementAt(3).toString());
						                 jsonobject.put("remarks",  v.elementAt(4).toString());
						                 jsonobject.put("locale", "en");
						                 jsonobject.put("dateFormat","dd MMMM yyyy");
						                 paymentsApiResource.createPayment(new Double(String.valueOf(v.elementAt(0))).longValue(), jsonobject.toString());
						                 break;
						          
						          
						       
						     //   }
						        }
						++processRecords;
						resultStatus=UploadStatusEnum.COMPLETED.toString();
						writeXLSXFile(filePath,resultStatus);
                  // processStatus="Processed";
						}
                        v.removeAllElements();
					}
				}
			
			}

		} 
		catch (Exception e) {
			//writeXLSXFile(filePath);
			//System.out.println("exceptuon"+e);
			processStatus=UploadStatusEnum.ERROR.toString();
	       e.printStackTrace();
	       resultStatus=UploadStatusEnum.ERROR.toString();
	       
			
			//System.out.println("exception method");
			if(e.toString().contains("ClientNotFoundException"))
			{
			errormessage="Client with this id does not exist";
			}
			writeXLSXFile(filePath,errormessage);
			exception();
		}
		
		
		return null;
	}
	public  void exception() {
		if (rownumber >= countno) {
			int rownum = rownumber;
			rownum = rownum + 2;
			
			try {
				updateUploadStatusReadXls(filePath, rownum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				exception();
				e.printStackTrace();

			}
		} else {
			
			return;
		}
	}
	
	public  void writeXLSXFile(String filepath, String errormessage)  {
		
		
		try{
		int rowupdateno=rownumber;
			InputStream excelFileToRead = new FileInputStream(filepath);
			XSSFWorkbook wb = new XSSFWorkbook(excelFileToRead);

			XSSFSheet sheet = wb.getSheetAt(0);
			 
			XSSFRow row = sheet.getRow(rowupdateno);
			 
			 XSSFCell cell = row.getCell(8, row.CREATE_NULL_AS_BLANK);
			cell = row.getCell(8);
			
			/*if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return;
			} else {
				cell.setCellValue(resultStatus);
			}*/
			
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				cell.setCellValue(errormessage);
				//return;
			} else{
				cell.setCellValue(errormessage);
			}


			OutputStream fileOut = new FileOutputStream(filepath);
			
			// write this workbook to an Outputstream.
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			resultStatus=null;
			++countno;
	}catch (Exception e) {
		// TODO: handle exception
	}
	}
	
	
	
	
	@Transactional
	@Override
	public CommandProcessingResult addItem(UploadStatusCommand command) {
		UploadStatus uploadStatus;
	
		 try{
			 
			this.context.authenticatedUser();
			UploadStatusCommandValidator validator = new UploadStatusCommandValidator(command);
		    validator.validateForCreate();
        	String fileLocation=null;
			fileLocation = FileUtils.saveToFileSystem(command.getInputStream(), command.getFileUploadLocation(),command.getFileName());
			
			uploadStatus = UploadStatus.create(command.getUploadProcess(), fileLocation, command.getProcessDate(),command.getProcessStatus(),
					command.getProcessRecords(), command.getErrorMessage(),command.getDescription(),command.getFileName());
			
			 this.uploadStatusRepository.save(uploadStatus);
			 return new CommandProcessingResult(uploadStatus.getId());
			 
		} catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command,dve);
			return new CommandProcessingResult(Long.valueOf(-1));
		}catch (IOException e) {
			return new CommandProcessingResult(Long.valueOf(-1));
		}
		
		
		
	}

	private void handleCodeDataIntegrityIssues(final UploadStatusCommand command, final DataIntegrityViolationException dve) {
        Throwable realCause = dve.getMostSpecificCause();
        if (realCause.getMessage().contains("file_name_key")) {
            final String name = command.getFileName();
       
            throw new PlatformDataIntegrityException("error.msg.file.duplicate.name", "A file with name'"
                    + name + "'already exists", "displayName", name);
        }

//        logger.error(dve.getMessage(), dve);
        throw new PlatformDataIntegrityException("error.msg.cund.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }
	
	@Transactional
	@Override
	 public UploadStatusCommand convertJsonToUploadStatusCommand(Object object,String jsonRequestBody) {
	       
	        if(StringUtils.isBlank(jsonRequestBody)){
	            throw new InvalidJsonException();
	        }
	     
	       
	        Type typeOfMap = new TypeToken<Map<String,String>>(){}.getType();
	        Map<String,String> requestMap = gsonConverter.fromJson(jsonRequestBody, typeOfMap);
	        Set<String> supportedParams = new HashSet<String>(Arrays.asList("locale","dateFormat","uploadProcess","uploadFilePath","processDate","processStatus","processRecords","errorMessage","isDeleted"));
	        checkForUnsupportedParameters(requestMap, supportedParams);
	        Set<String> modifiedParameters = new HashSet<String>();
	       
	       String uploadProcess = extractStringParameter("uploadProcess", requestMap, modifiedParameters);
	        String uploadFilePath = extractStringParameter("uploadFilePath", requestMap, modifiedParameters);
	        LocalDate processDate = extractLocalDateParameter("processDate", requestMap, modifiedParameters);
	        String processStatus = extractStringParameter("processStatus", requestMap, modifiedParameters);
	        Long processRecords = extractLongParameter("processRecords", requestMap, modifiedParameters);
	        String errorMessage = extractStringParameter("errorMessage", requestMap, modifiedParameters);
	        String description = extractStringParameter("description", requestMap, modifiedParameters);
	        
	       // UploadStatusCommand(String uploadProcess,String uploadFilePath,Date processDate, String processStatus,Long processRecords,String errorMessage,char isDeleted,Set<String> modifiedParameters)
	        return new UploadStatusCommand(uploadProcess,uploadFilePath,processDate,processStatus,processRecords,errorMessage,modifiedParameters,description,null,null,null);
	    }
	private String extractStringParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		String paramValue = null;
		if (requestMap.containsKey(paramName)) {
			paramValue = (String) requestMap.get(paramName);
			modifiedParameters.add(paramName);
		}

		if (paramValue != null) {
			paramValue = paramValue.trim();
		}

		return paramValue;
	}
	private Long extractLongParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		Long paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				paramValue = Long.valueOf(Double.valueOf(valueAsString)
						.longValue());
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	private LocalDate extractLocalDateParameter(final String paramName,
			final Map<String, ?> requestMap,
			final Set<String> modifiedParameters) {
		LocalDate paramValue = null;
		if (requestMap.containsKey(paramName)) {
			String valueAsString = (String) requestMap.get(paramName);
			if (StringUtils.isNotBlank(valueAsString)) {
				final String dateFormat = (String) requestMap.get("dateFormat");
				final Locale locale = new Locale(
						(String) requestMap.get("locale"));
				paramValue = convertFrom(valueAsString, paramName, dateFormat,
						locale);
			}
			modifiedParameters.add(paramName);
		}
		return paramValue;
	}
	
		
	private LocalDate convertFrom(final String dateAsString,
			final String parameterName, final String dateFormat,
			final Locale clientApplicationLocale) {

		if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

			List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
			if (StringUtils.isBlank(dateFormat)) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'dateFormat' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.dateFormat.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			if (clientApplicationLocale == null) {
				String defaultMessage = new StringBuilder(
						"The parameter '"
								+ parameterName
								+ "' requires a 'locale' parameter to be passed with it.")
						.toString();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.missing.locale.parameter",
						defaultMessage, parameterName);
				dataValidationErrors.add(error);
			}
			throw new PlatformApiDataValidationException(
					"validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		LocalDate eventLocalDate = null;
		if (StringUtils.isNotBlank(dateAsString)) {
			try {
				// Locale locale = LocaleContextHolder.getLocale();
				eventLocalDate = DateTimeFormat
						.forPattern(dateFormat)
						.withLocale(clientApplicationLocale)
						.parseLocalDate(
								dateAsString
										.toLowerCase(clientApplicationLocale));
			} catch (IllegalArgumentException e) {
				List<ApiParameterError> dataValidationErrors = new ArrayList<ApiParameterError>();
				ApiParameterError error = ApiParameterError.parameterError(
						"validation.msg.invalid.date.format", "The parameter "
								+ parameterName
								+ " is invalid based on the dateFormat: '"
								+ dateFormat + "' and locale: '"
								+ clientApplicationLocale + "' provided:",
						parameterName, dateAsString, dateFormat);
				dataValidationErrors.add(error);

				throw new PlatformApiDataValidationException(
						"validation.msg.validation.errors.exist",
						"Validation errors exist.", dataValidationErrors);
			}
		}

		return eventLocalDate;
	}
	private void checkForUnsupportedParameters(Map<String, ?> requestMap,
			Set<String> supportedParams) {
		List<String> unsupportedParameterList = new ArrayList<String>();
		for (String providedParameter : requestMap.keySet()) {
			if (!supportedParams.contains(providedParameter)) {
				unsupportedParameterList.add(providedParameter);
			}
		}

		if (!unsupportedParameterList.isEmpty()) {
			throw new UnsupportedParameterException(unsupportedParameterList);
		}
	}
	
public synchronized void writeXLSXFileMediaEpgMrn(final String excelFileName, final ArrayList<MRNErrorData> errorData, UploadStatus uploadStatus, final Integer cellNumber) throws IOException {
		
		FileInputStream is = new FileInputStream(excelFileName);
		
		XSSFWorkbook wb = new XSSFWorkbook(is);
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		for(MRNErrorData err: errorData){			
				XSSFRow row = (XSSFRow)sheet.getRow(Integer.parseInt(err.getRowNumber().toString()));
				//XSSFCell cell = row.getCell(3, row.CREATE_NULL_AS_BLANK);
				XSSFCell cell = row.getCell(cellNumber, row.CREATE_NULL_AS_BLANK);
				if(cell == null)
					row.createCell(cellNumber);
				cell = row.getCell(cellNumber);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(err.getErrorMessage());
	
		}
		
		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		is.close();
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		 //uploadStatus.update(new LocalDate(),(totalRecords-processRecords!=0)?"ERROR":"COMPLETED",this.processRecords,totalRecords-processRecords,(totalRecords-processRecords!=0)?"ERROR":"SUCCESS",totalRecords);
		 uploadStatus.setProcessStatus((uploadStatus.getUnprocessedRecords()>0)?"ERROR":"COMPLETED");
		 uploadStatus.setErrorMessage((uploadStatus.getUnprocessedRecords()>0)?"ERROR":"SUCCESS");
		 uploadStatus.setProcessDate(new LocalDate().toDate());
		 this.uploadStatusRepository.save(uploadStatus);
		 uploadStatus = null;
	}

private void writeCSVData(String fileLocation,
		ArrayList<MRNErrorData> errorData, UploadStatus uploadStatusForMrn) {
		uploadStatusForMrn.setProcessStatus((uploadStatusForMrn.getUnprocessedRecords()>0)?"ERROR":"COMPLETED");
		uploadStatusForMrn.setErrorMessage((uploadStatusForMrn.getUnprocessedRecords()>0)?"ERROR":"SUCCESS");
		uploadStatusForMrn.setProcessDate(new LocalDate().toDate());
		this.uploadStatusRepository.save(uploadStatusForMrn);
		uploadStatusForMrn = null;
	}

	public void writeToFile(String fileLocation,ArrayList<MRNErrorData> errorData){
		FileWriter fw = null;
		try{
			File f = new File(fileLocation.replace(".csv", ".log"));
			if(!f.exists()){
				f.createNewFile();
			}
			fw = new FileWriter(f,true);
			for(int k=0;k<errorData.size();k++){
				if(!errorData.get(k).getErrorMessage().equalsIgnoreCase("Success.")){
					fw.append("Data at row: "+errorData.get(k).getRowNumber()+", Message: "+errorData.get(k).getErrorMessage()+"\n");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(fw!=null){
					fw.flush();
					fw.close();
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}

