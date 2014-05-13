package org.mifosplatform.scheduledjobs.importfile.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.api.ApiConstants;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformApiDataValidationException;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.logistics.itemdetails.domain.InventoryItemDetailsRepository;
import org.mifosplatform.logistics.mrn.api.MRNDetailsJpaRepository;
import org.mifosplatform.logistics.mrn.service.MRNDetailsWritePlatformService;
import org.mifosplatform.scheduledjobs.importfile.data.MRNErrorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

@Path("/uploadxsl")
@Component
@Scope("singleton")
public class FileImportApiResource {

	final MRNDetailsJpaRepository mrnDetailsJpaRepository;
	final InventoryItemDetailsRepository itemDetailsRepository;
	final MRNDetailsWritePlatformService mrnDetailsWritePlatformService;
	final PortfolioCommandSourceWritePlatformService commandSourceWritePlatformService;
	final DefaultToApiJsonSerializer<Object> toApiJsonDeserializer;
	final PlatformSecurityContext context;
	final FromJsonHelper fromJsonHelper;
	
	private final static String resourceType = "MRNDETAILS";
	
	
	@Autowired
	public FileImportApiResource(
			final MRNDetailsJpaRepository mrnDetailsJpaRepository,
			final MRNDetailsWritePlatformService mrnDetailsWritePlatformService,
			final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService,
			final DefaultToApiJsonSerializer<Object>  toApiJsonDeserializer,
			final PlatformSecurityContext context,
			final InventoryItemDetailsRepository itemDetailsRepository,
			final FromJsonHelper fromJsonHelper) {
		this.mrnDetailsJpaRepository = mrnDetailsJpaRepository;
		this.mrnDetailsWritePlatformService = mrnDetailsWritePlatformService;
		this.commandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
		this.toApiJsonDeserializer = toApiJsonDeserializer;
		this.context = context;
		this.itemDetailsRepository = itemDetailsRepository;
		this.fromJsonHelper = fromJsonHelper;
	}

/*
 * 
 * MRN
 * 
 * 	@POST
	@Path("/documents")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON })
	public String uploadXslFile(@HeaderParam("Content-Length") Long fileSize,
			@FormDataParam("file") InputStream inputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetails,
			@FormDataParam("file") FormDataBodyPart bodyPart,
			@FormDataParam("status") String name,
			@FormDataParam("description") String description) {
		String fileLocation = null;
		FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name,
				ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
		String fileUploadLocation = FileUtils.generateXlsFileDirectory();
		File file = null;
		File directory = null;
		
		ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
		long startTime = System.currentTimeMillis();
		Workbook wb = null;
		try {

			directory = new File(fileUploadLocation);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			fileLocation = FileUtils.saveToFileSystem(inputStream,
					fileUploadLocation, fileDetails.getFileName());

			
			
			
			wb = WorkbookFactory.create(new File(fileLocation));
			Sheet sheet = wb.getSheetAt(0);	
			System.out.println("Number of rows : "+sheet.getPhysicalNumberOfRows());	
			
			for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {

					Row row = sheet.getRow(i);
					JSONObject jsonObject = new JSONObject();
				 
				 try {
	
					jsonObject.put("movedDate",row.getCell(0).getDateCellValue());
					jsonObject.put("mrnId",row.getCell(1).getNumericCellValue());
					jsonObject.put("serialNumber",row.getCell(2).getStringCellValue());
					jsonObject.put("locale","en");

					context.authenticatedUser().validateHasReadPermission(resourceType);
					final CommandWrapper commandRequest = new CommandWrapperBuilder().moveMrnExcel().withJson(jsonObject.toString().toString()).build();
					final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
				    if(result!=null){
				    	Long rsId = result.resourceId();
				    	if(rsId != null ){
					    	if(rsId>1){
					    		//errorData.add(new MRNErrorData((long)i, row.getCell(2).getStringCellValue(), (long)row.getCell(1).getNumericCellValue(), row.getCell(0).getDateCellValue()));
					    		errorData.add(new MRNErrorData((long)i, result.getTransactionId()));
	 		
					    	}else{
					    		errorData.add(new MRNErrorData((long)i, result.getTransactionId()));
					    	}
					    }
				    }
					
					//System.out.println(i);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			writeXLSXFile(fileLocation, errorData);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.getStackTrace();
		}
		
		
		System.out.println("XLSX to DataBase is Completed: "+(System.currentTimeMillis()-startTime));
		System.out.println("Appending Error's to XLSX file -- Started");
		startTime = System.currentTimeMillis();
		
	
		
		System.out.println("Completed XSLX appending: "+(System.currentTimeMillis()-startTime));
		return toApiJsonDeserializer.serialize(fileLocation);
	}*/
	
	
	
	
	
public static void writeXLSXFile(final String excelFileName, final ArrayList<MRNErrorData> errorData) throws IOException {
		
		FileInputStream is = new FileInputStream(excelFileName);
		
		XSSFWorkbook wb = new XSSFWorkbook(is);
		
		XSSFSheet sheet = wb.getSheetAt(0);
		
		
		for(MRNErrorData err: errorData){			
				XSSFRow row = (XSSFRow)sheet.getRow(Integer.parseInt(err.getRowNumber().toString()));
				//XSSFCell cell = row.getCell(3, row.CREATE_NULL_AS_BLANK);
				XSSFCell cell = row.getCell(15, row.CREATE_NULL_AS_BLANK);
				if(cell == null)
					row.createCell(15);
				cell = row.getCell(15);
				cell.setCellType(Cell.CELL_TYPE_STRING);
				cell.setCellValue(err.getErrorMessage());
	
		}
		
		FileOutputStream fileOut = new FileOutputStream(excelFileName);
		is.close();
		
		wb.write(fileOut);
		fileOut.flush();
		fileOut.close();
		
	}




/*
 * 
 * EPG
@POST
@Path("/documents")
@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.APPLICATION_JSON })
public String uploadEpgXslFile(@HeaderParam("Content-Length") Long fileSize,
		@FormDataParam("file") InputStream inputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetails,
		@FormDataParam("file") FormDataBodyPart bodyPart,
		@FormDataParam("status") String name,
		@FormDataParam("description") String description) {
	String fileLocation = null;
	FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name,
			ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
	String fileUploadLocation = FileUtils.generateXlsFileDirectory();
	File directory = null;
	
	ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
	long startTime = System.currentTimeMillis();
	Workbook wb = null;
	try {

		directory = new File(fileUploadLocation);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		fileLocation = FileUtils.saveToFileSystem(inputStream,
				fileUploadLocation, fileDetails.getFileName());

		
		
		
		wb = WorkbookFactory.create(new File(fileLocation));
		Sheet sheet = wb.getSheetAt(0);	
		System.out.println("Number of rows : "+sheet.getPhysicalNumberOfRows());	
		
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {

				Row row = sheet.getRow(i);
				JSONObject jsonObject = new JSONObject();
			 
			 try {

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

				context.authenticatedUser().validateHasReadPermission(resourceType);
				final CommandWrapper commandRequest = new CommandWrapperBuilder().createEpgXsls(1L).withJson(jsonObject.toString().toString()).build();
				final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
			    if(result!=null){
			    	Long rsId = result.resourceId();
			    	if(rsId != null ){
				    		//errorData.add(new MRNErrorData((long)i, row.getCell(2).getStringCellValue(), (long)row.getCell(1).getNumericCellValue(), row.getCell(0).getDateCellValue()));
				    		errorData.add(new MRNErrorData((long)i, rsId>1?result.getTransactionId():"saved"));
				    }
			    }
				
				//System.out.println(i);
			} catch (Exception e) {
				e.printStackTrace();
				errorData.add(new MRNErrorData((long)i, e.getMessage()));
				writeXLSXFile(fileLocation, errorData);
			}
		}
		writeXLSXFile(fileLocation, errorData);

	} catch (IOException e) {
		e.printStackTrace();
	} catch (InvalidFormatException e) {
		e.getStackTrace();
	}
	
	
	System.out.println("XLSX to DataBase is Completed: "+(System.currentTimeMillis()-startTime));
	System.out.println("Appending Error's to XLSX file -- Started");
	startTime = System.currentTimeMillis();
	

	
	System.out.println("Completed XSLX appending: "+(System.currentTimeMillis()-startTime));
	return toApiJsonDeserializer.serialize(fileLocation);
}*/

/*
 * MediaAssets
 * 
 * */

@POST
@Path("/documents")
@Consumes({ MediaType.MULTIPART_FORM_DATA })
@Produces({ MediaType.APPLICATION_JSON })
public String uploadEpgXslFile(@HeaderParam("Content-Length") Long fileSize,
		@FormDataParam("file") InputStream inputStream,
		@FormDataParam("file") FormDataContentDisposition fileDetails,
		@FormDataParam("file") FormDataBodyPart bodyPart,
		@FormDataParam("status") String name,
		@FormDataParam("description") String description) {
	String fileLocation = null;
	FileUtils.validateFileSizeWithinPermissibleRange(fileSize, name,
			ApiConstants.MAX_FILE_UPLOAD_SIZE_IN_MB);
	String fileUploadLocation = FileUtils.generateXlsFileDirectory();
	File directory = null;
	
	ArrayList<MRNErrorData> errorData = new ArrayList<MRNErrorData>();
	long startTime = System.currentTimeMillis();
	Workbook wb = null;
	try {

		directory = new File(fileUploadLocation);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		fileLocation = FileUtils.saveToFileSystem(inputStream,fileUploadLocation, fileDetails.getFileName());

		wb = WorkbookFactory.create(new File(fileLocation));
		Sheet mediaSheet = wb.getSheetAt(0);
		Sheet mediaAttributeSheet = wb.getSheetAt(1);
		Sheet mediaLocationSheet = wb.getSheetAt(2);
		System.out.println("Number of rows : "+mediaSheet.getPhysicalNumberOfRows());	
		
		for (int i = 1; i < mediaSheet.getPhysicalNumberOfRows(); i++) {

				Row mediaRow = mediaSheet.getRow(i);
				Row mediaAttributeRow = mediaAttributeSheet.getRow(i);
				Row mediaLocationRow = mediaLocationSheet.getRow(i);
				JSONObject jsonObject = new JSONObject();
			 
			 try {
				 
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
				
				context.authenticatedUser().validateHasReadPermission(resourceType);
				final CommandWrapper commandRequest = new CommandWrapperBuilder().createMediaAsset().withJson(jsonObject.toString()).build();
				final CommandProcessingResult result = this.commandSourceWritePlatformService.logCommandSource(commandRequest);
				
				if(result!=null){
			    	Long rsId = result.resourceId();
			    	errorData.add(new MRNErrorData((long)i, rsId>0?"saved":"something went wrong"));
			    	
			    	/*if(rsId != null ){
				    		//errorData.add(new MRNErrorData((long)i, row.getCell(2).getStringCellValue(), (long)row.getCell(1).getNumericCellValue(), row.getCell(0).getDateCellValue()));
				    		errorData.add(new MRNErrorData((long)i, rsId>1?result.getTransactionId():"saved"));
				    }*/
			    }
				
				
			} catch (PlatformApiDataValidationException e) {
				errorData.add(new MRNErrorData((long)i, e.getErrors().get(0).getParameterName()+" : "+e.getDefaultUserMessage()));
			}catch (NullPointerException e) {
				errorData.add(new MRNErrorData((long)i, "Error: value cannot be null"));
			}catch (Exception e) {
				errorData.add(new MRNErrorData((long)i, e.getMessage()));
			}
		}
		writeXLSXFile(fileLocation, errorData);

	} catch (IOException e) {
		e.printStackTrace();
	} catch (InvalidFormatException e) {
		e.getStackTrace();
	}
	
	
	System.out.println("XLSX to DataBase is Completed: "+(System.currentTimeMillis()-startTime));
	System.out.println("Appending Error's to XLSX file -- Started");
	startTime = System.currentTimeMillis();
	

	
	System.out.println("Completed XSLX appending: "+(System.currentTimeMillis()-startTime));
	return toApiJsonDeserializer.serialize(fileLocation);
}


}
