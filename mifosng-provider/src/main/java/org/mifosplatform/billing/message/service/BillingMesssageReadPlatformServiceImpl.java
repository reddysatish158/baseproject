package org.mifosplatform.billing.message.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DataBindingException;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.media.domain.MediaEnum;
import org.mifosplatform.billing.media.domain.MediaTypeEnumaration;
import org.mifosplatform.billing.message.data.BillingMessageData;
import org.mifosplatform.billing.message.data.BillingMessageDataForProcessing;
import org.mifosplatform.billing.message.data.EnumMessageType;
import org.mifosplatform.billing.message.domain.BillingMessage;
import org.mifosplatform.billing.message.domain.BillingMessageTemplate;
import org.mifosplatform.billing.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.billing.message.domain.MessageDataRepository;
import org.mifosplatform.billing.plan.domain.UserActionStatusTypeEnum;
import org.mifosplatform.billing.processrequest.domain.ProcessRequest;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestDetails;
import org.mifosplatform.billing.processrequest.domain.ProcessRequestRepository;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.MediaEnumoptionData;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.core.serialization.FromJsonHelper;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.infrastructure.jobs.service.JobName;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;

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
				Date date = new Date();
				String dateTime = date.getHours() + "" + date.getMinutes() + ""+ date.getSeconds();
				String path = FileUtils.generateLogFileDirectory()+ JobName.ALL.toString() + File.separator+ "Messanger_"+ new LocalDate().toString().replace("-", "") + "_" + dateTime + ".log";
				File fileHandler = new File(path.trim());
				fileHandler.createNewFile();
				fw = new FileWriter(fileHandler);

				ArrayList<String> rowdata = new ArrayList<String>();
				ArrayList<String> columndata = new ArrayList<String>();
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
				for (int j = 1; j <= Rows; j++) {
					// resultset pointing to first position/next position
					rs.next();
					for (int i = 1; i <= columnCount; i++) {
						String name = rs.getString(i);
						columndata.add(name);
					}

					rowdata.addAll(columndata);

					// Assign rowdata to BillingMessageData bean class
					BillingMessageData MessageRowdata = new BillingMessageData(
							rowdata);
					ArrayList<String> MessageColumndata = new ArrayList<String>();
					MessageColumndata = MessageRowdata.getMessageColumndata();

					// For Retrieving no.of params for the MessageTemplate
					ArrayList<BillingMessageData> param = new ArrayList<BillingMessageData>();
					for (BillingMessageData params : messageparam) {
						param.add(params);
					}

					// getting the MessageTemplate data
					String header = templateData.getHeader();
					String footer = templateData.getFooter();
					String body = templateData.getBody();
					String subject = templateData.getSubject();
					char messgeType = templateData.getMessageType();
					String status = "N";
					String messageFrom = "OBS";
					int n = param.size();
					ArrayList<String> data = new ArrayList<String>();
					for (int i = 0; i < MessageColumndata.size(); i++) {
						data.add(i, MessageColumndata.get(i).toString());
					}
					if (n == data.size() - 1) {
						for (int i = 0, k = 1; i < n & k < data.size(); i++, k++) {
							String name = param.get(i).getParameter();
							String value = data.get(k).toString();
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
						String messageTo = data.get(0).toString();
						BillingMessageTemplate billingMessageTemplate = messageTemplateRepository.findOne(messageId);
						BillingMessage billingMessage = new BillingMessage(header, body, footer, messageFrom, messageTo,
								subject, status, billingMessageTemplate,messgeType);
						messageDataRepository.save(billingMessage);
					}

					if (messgeType == 'O') {
						String requstStatus = UserActionStatusTypeEnum.MESSAGE.toString();
						Long clientId = billingMesssageReadPlatformService.retrieveClientId(data.get(0).toString());
						if(clientId!=null){
							ProcessRequest processRequest = new ProcessRequest(clientId, new Long(0), "Comvenient", 'N', null,requstStatus, new Long(0));
							processRequest.setNotify();
							Long id = new Long(0);
							ProcessRequestDetails processRequestDetails = new ProcessRequestDetails(id, id, body, "Recieved", 
									data.get(0).toString(), new Date(), null, null,null, 'N', requstStatus);
							processRequest.add(processRequestDetails);
							processRequestRepository.save(processRequest);

						}
						else{
							fw.append("rowNo:"+j+" failed and provisioningSerialNo is= "+data.get(0).toString()+ " . \r\n");
						}
						
					}
					 
					rowdata.removeAll(rowdata);
					columndata.removeAll(columndata);

				}// for Rows
				fw.append("messages are created successfully . \r\n");
		    	fw.flush();
			    fw.close();
				return new BillingMessageData(messageId);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
			return new BillingMessageDataForProcessing(id, messageto,
					messagefrom, subject, header, body, footer, c);
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
	public Long retrieveClientId(String hardwareId) {
		try {
			String sql = "select b.client_id as clientId from b_item_detail b where b.provisioning_serialno = '"
					+ hardwareId + "' ";
			return jdbcTemplate.queryForLong(sql);
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}

	}

}
