package org.mifosplatform.organisation.message.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.cms.media.domain.MediaTypeEnumaration;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.message.data.BillingMessageData;
import org.mifosplatform.organisation.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.organisation.message.data.EnumMessageType;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.domain.MessageDataRepository;
import org.mifosplatform.portfolio.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequest;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.provisioning.processrequest.domain.ProcessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class BillingMesssageReadPlatformServiceImpl implements
		BillingMesssageReadPlatformService {
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final FromJsonHelper fromApiJsonHelper;
	private static List<BillingMessageData> messageparam;
	private static BillingMessageData templateData;
	private static MessageDataRepository messageDataRepository;
	private static BillingMessageTemplateRepository messageTemplateRepository;
	private static ProcessRequestRepository processRequestRepository;
	private static Long messageId;
	private static String messagingType;
	private static BillingMesssageReadPlatformService billingMesssageReadPlatformService;
	private static FileWriter fw;

	@Autowired
	public BillingMesssageReadPlatformServiceImpl(
			final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource,
			final MessageDataRepository messageDataRepository,
			final FromJsonHelper fromApiJsonHelper,
			final ProcessRequestRepository processRequestRepository,
			final BillingMessageTemplateRepository messageTemplateRepository) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.fromApiJsonHelper = fromApiJsonHelper;
		this.messageDataRepository = messageDataRepository;
		this.processRequestRepository = processRequestRepository;
		this.messageTemplateRepository = messageTemplateRepository;
	}

	// for mesage template

	@Override
	public List<BillingMessageData> retrieveAllMessageTemplates() {
		// TODO Auto-generated method stub

		// context.authenticatedUser();
		BillingAllMessageTemplateMapper mapper = new BillingAllMessageTemplateMapper();
		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class BillingAllMessageTemplateMapper implements
			RowMapper<BillingMessageData> {

		public String schema() {

			return "mt.id ,mt.template_description, mt.subject ,mt.header,mt.body , mt.footer,mt.message_type as messageType from  b_message_template mt";
		}

		@Override
		public BillingMessageData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			int id1 = rs.getInt("id");
			String templateDescription = rs.getString("template_description");
			String subject = rs.getString("subject");
			String header = rs.getString("header");
			String body = rs.getString("body");
			String footer = rs.getString("footer");
			String messageTypeInDB = rs.getString("messageType");
			char c = messageTypeInDB.charAt(0);
			Long id = new Long(id1);
			return new BillingMessageData(id, templateDescription, subject,
					header, body, footer, null, c);
		}
	}

	@Override
	public BillingMessageData retrieveMessageTemplate(Long command) {
		// context.authenticatedUser();
		BillingMessageTemplateMapper mapper = new BillingMessageTemplateMapper();

		String sql = "select " + mapper.schema() + command;

		return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {});
	}

	private static final class BillingMessageTemplateMapper implements
			RowMapper<BillingMessageData> {

		public String schema() {

			return "mt.id ,mt.template_description, mt.subject ,mt.header,mt.body , mt.footer,mt.message_type as messageType from  b_message_template mt where mt.id=";
		}

		@Override
		public BillingMessageData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			int id1 = rs.getInt("id");
			String templateDescription = rs.getString("template_description");
			String subject = rs.getString("subject");
			String header = rs.getString("header");
			String body = rs.getString("body");
			String footer = rs.getString("footer");
			String messageTypeInDB = rs.getString("messageType");
			char c = messageTypeInDB.charAt(0);
			Long id = new Long(id1);

			return new BillingMessageData(id, templateDescription, subject,
					header, body, footer, null, c);
		}
	}

	// for message params
	@Override
	public List<BillingMessageData> retrieveMessageParams(Long entityId) {
		// TODO Auto-generated method stub
		// context.authenticatedUser();
		BillingMessageParamMapper mapper = new BillingMessageParamMapper();
		String sql = "select " + mapper.schema() + entityId;

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class BillingMessageParamMapper implements
			RowMapper<BillingMessageData> {

		public String schema() {

			return "mp.id as msgTemplateId,mp.parameter_name as parameterName,mp.sequence_no as sequenceNo "
					+ "from b_message_params mp where mp.msgtemplate_id=";
		}

		@Override
		public BillingMessageData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			Long messageTemplateId = rs.getLong("msgTemplateId");
			String parameterName = rs.getString("parameterName");
			return new BillingMessageData(messageTemplateId, parameterName);
		}
	}

	// for messageData
	@Override
	public List<BillingMessageData> retrieveData(
			Long id,
			String query,
			BillingMessageData templateData,
			List<BillingMessageData> messageparam,
			BillingMesssageReadPlatformService billingMesssageReadPlatformService) {
		// TODO Auto-generated method stub
		// context.authenticatedUser();
		this.messageparam = messageparam;
		this.templateData = templateData;
		this.messageId = id;
		this.billingMesssageReadPlatformService = billingMesssageReadPlatformService;
		BillingMessageDataMapper mapper = new BillingMessageDataMapper();

		return this.jdbcTemplate.query(query, mapper, new Object[] {});

	}

	private static final class BillingMessageDataMapper implements
			RowMapper<BillingMessageData> {

		@Override
		public BillingMessageData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			try {
				String fileUploadLocation=FileUtils.generateLogFileDirectory()+ JobName.MESSAGE_MERGE.toString()+File.separator+"BillingMessage";
				File file = new File(fileUploadLocation);			
				if(!file.isDirectory()){
					file.mkdirs();
				}
				Date date = new Date();
				String dateTime = date.getHours() + "" + date.getMinutes() + ""+ date.getSeconds();
				String path = fileUploadLocation + File.separator+ "billingMessage_"+ new LocalDate().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				fw = new FileWriter(fileHandler);

				
				// To know the number of rows
				rs.last();
				int Rows = rs.getRow();
				fw.append("Total Number Of Rows for Processing is= "+Rows+" . \r\n");
				// Resultset object pointer position to before first record.
				rs.beforeFirst();

				// To know the column count of a Row
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();

				// processing each row and save the row as record in
				// b_message_data table
				ArrayList<String> columndata = new ArrayList<String>();
				for (int j = 1; j <= Rows; j++) {
					// resultset pointing to first position/next position
					rs.next();
					for (int i = 1; i <= columnCount; i++) {
						String name = rs.getString(i);
						columndata.add(name);
					}

					// getting the MessageTemplate data
					String header = templateData.getHeader();
					String footer = templateData.getFooter();
					String body = templateData.getBody();
					String subject = templateData.getSubject();
					char messgeType = templateData.getMessageType();					
					String status = "N";
					String messageFrom = "OBS";
					
					if (messageparam.size() == columndata.size() - 1) {
						for (int i = 0, k = 1; i < messageparam.size() & k < columndata.size(); i++, k++) {
							String name = messageparam.get(i).getParameter();
							String value = columndata.get(k).toString();
							if (!org.apache.commons.lang.StringUtils.isBlank(body)) {
								body = body.replaceAll(name, value);
							}
							if (!org.apache.commons.lang.StringUtils.isBlank(header)) {
								header = header.replaceAll(name, value);
							}
							if (!org.apache.commons.lang.StringUtils.isBlank(footer)) {
								footer = footer.replaceAll(name, value);
							}
						}
					} else {
						handleCodeDataIntegrityIssues();
					}
					if (messgeType == 'E' || messgeType == 'M') {
						String messageTo = columndata.get(0).toString();
						BillingMessageTemplate billingMessageTemplate = messageTemplateRepository.findOne(messageId);
						BillingMessage billingMessage = new BillingMessage(header, body, footer, messageFrom, messageTo,
								subject, status, billingMessageTemplate,messgeType,null);
						messageDataRepository.save(billingMessage);
					}

					if (messgeType == 'O') {
						String requstStatus = UserActionStatusTypeEnum.MESSAGE.toString();
						Long clientId = billingMesssageReadPlatformService.retrieveClientId(columndata.get(0).toString());
						if(clientId!=null){
							ProcessRequest processRequest = new ProcessRequest(clientId, new Long(0), "Comvenient", 'N', null,requstStatus, new Long(0));
							processRequest.setNotify();
							Long id = new Long(0);
							ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(id, id, body, "Recieved", 
									columndata.get(0).toString(), new Date(), null, null,null, 'N', requstStatus,null);
							processRequest.add(processRequestDetails);
							processRequestRepository.save(processRequest);
						}
						else{
							fw.append("rowNo:"+j+" failed . \r\n");
						}
						
					}
					 
					columndata.removeAll(columndata);

				}// for Rows
				fw.append("messages are created successfully . \r\n");
		    	fw.flush();
			    fw.close();
				return new BillingMessageData(messageId);
			} catch (IOException e) {
				return null;
			}

		}
	}

	@Override
	public List<BillingMessageData> retrieveAllMessageTemplateParams() {
		// TODO Auto-generated method stub

		// context.authenticatedUser();
		BillingAllMessageTemplateParamMapper mapper = new BillingAllMessageTemplateParamMapper();
		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});

	}

	private static final class BillingAllMessageTemplateParamMapper implements
			RowMapper<BillingMessageData> {

		public String schema() {

			return " mt.id,mt.template_description,mt.subject,mt.header,mt.body,mt.footer,mt.message_type as messageType,"
					+ "(select group_concat(mp.parameter_name separator ', ') from b_message_params mp"
					+ " where  mp.msgtemplate_id = mt.id )  as messageParameters  from b_message_template mt  where mt.is_deleted='N'";
		}

		@Override
		public BillingMessageData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			int id1 = rs.getInt("id");
			String templateDescription = rs.getString("template_description");
			String subject = rs.getString("subject");
			String header = rs.getString("header");
			String body = rs.getString("body");
			String footer = rs.getString("footer");
			String messageParameters = rs.getString("messageParameters");
			String messageType = rs.getString("messageType");
			char c = messageType.charAt(0);
			Long id = new Long(id1);
			return new BillingMessageData(id, templateDescription, subject,
					header, body, footer, messageParameters, c);
		}
	}

	@Override
	public List<BillingMessageDataForProcessing> retrieveMessageDataForProcessing() {
		// TODO Auto-generated method stub
		BillingMessageDataForProcessingMapper mapper = new BillingMessageDataForProcessingMapper();
		String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class BillingMessageDataForProcessingMapper implements
			RowMapper<BillingMessageDataForProcessing> {

		public String schema() {

			return "md.id as id,md.message_to as messageto,md.message_from as messagefrom,md.subject as subject,md.header as header,"
					+ "md.body as body,md.footer as footer,md.message_type as messageType from b_message_data md where md.status='N' ";
		}

		@Override
		public BillingMessageDataForProcessing mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			int id1 = rs.getInt("id");
			String messageto = rs.getString("messageto");
			String messagefrom = rs.getString("messagefrom");
			String subject = rs.getString("subject");
			String header = rs.getString("header");
			String body = rs.getString("body");
			String footer = rs.getString("footer");
			String messageType = rs.getString("messageType");
			char c = messageType.charAt(0);
			Long id = new Long(id1);
			String attachment = rs.getString("attachment");
			return new BillingMessageDataForProcessing(id, messageto,
					messagefrom, subject, header, body, footer, c,attachment);
		}
	}

	@Override
	public BillingMessageData retrieveTemplate() {

		MediaEnumoptionData email = MediaTypeEnumaration
				.enummessageData(EnumMessageType.EMAIL);
		MediaEnumoptionData message = MediaTypeEnumaration
				.enummessageData(EnumMessageType.Message);
		MediaEnumoptionData osdMessage = MediaTypeEnumaration
				.enummessageData(EnumMessageType.OSDMESSAGE);
		List<MediaEnumoptionData> categotyType = Arrays.asList(email, message,
				osdMessage);
		BillingMessageData messagedata = new BillingMessageData();
		messagedata.setMessageType(categotyType);
		return messagedata;

	}

	private static void handleCodeDataIntegrityIssues() {
		throw new PlatformDataIntegrityException(
				"error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: message params count is lessthan or greaterthan to sending query parameters count ");

	}

	@Override
	public Long retrieveClientId(String hardwareId) throws IOException {
		try {
			String sql = "select b.client_id as clientId from b_item_detail b where b.provisioning_serialno = '"+ hardwareId + "' ";
			return jdbcTemplate.queryForLong(sql);
		} catch (EmptyResultDataAccessException e) {
			fw.append("provisioningSerialNo is= "+hardwareId+ " Failed. Exception Reason is: "+e.getMessage()+ " .\r\n");
			return null;
		} catch (Exception e) {
			fw.append("provisioningSerialNo is= "+hardwareId+ " Failed. Exception Reason is: "+e.getMessage()+ " .\r\n");
			return null;
		}

	}

}
