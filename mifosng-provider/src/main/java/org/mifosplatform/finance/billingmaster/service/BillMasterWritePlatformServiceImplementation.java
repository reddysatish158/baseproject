package org.mifosplatform.finance.billingmaster.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.finance.billingmaster.domain.BillDetail;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.finance.billingmaster.domain.BillMasterRepository;
import org.mifosplatform.finance.billingmaster.serialize.BillMasterCommandFromApiJsonDeserializer;
import org.mifosplatform.finance.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.financialtransaction.data.FinancialTransactionsData;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.infrastructure.security.service.TenantDetailsService;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetails;
import org.mifosplatform.organisation.groupsDetails.domain.GroupsDetailsRepository;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.mifosplatform.portfolio.service.service.ServiceMasterWritePlatformServiceImpl;
import org.mifosplatform.portfolio.transactionhistory.service.TransactionHistoryWritePlatformService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillMasterWritePlatformServiceImplementation implements
		BillMasterWritePlatformService {
	
	 private final static Logger logger = LoggerFactory.getLogger(ServiceMasterWritePlatformServiceImpl.class);
		private final PlatformSecurityContext context;
		private final BillMasterRepository billMasterRepository;
		private final BillMasterReadPlatformService billMasterReadPlatformService;
		private final BillWritePlatformService billWritePlatformService;
		private final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService;
	    private final TenantDetailsService tenantDetailsService;
	    private final BillMasterCommandFromApiJsonDeserializer  apiJsonDeserializer;
	    private final ClientRepository clientRepository;
	    private final GroupsDetailsRepository groupsDetailsRepository;
	    
	   
	@Autowired
	 public BillMasterWritePlatformServiceImplementation(final PlatformSecurityContext context,final BillMasterRepository billMasterRepository,
				final BillMasterReadPlatformService billMasterReadPlatformService,final BillWritePlatformService billWritePlatformService,
				final TransactionHistoryWritePlatformService transactionHistoryWritePlatformService,final TenantDetailsService tenantDetailsService,
				final BillMasterCommandFromApiJsonDeserializer apiJsonDeserializer,final ClientRepository clientRepository,
				final GroupsDetailsRepository groupsDetailsRepository) {

		    this.context = context;
			this.billMasterRepository = billMasterRepository;
			this.clientRepository=clientRepository;
			this.billMasterReadPlatformService=billMasterReadPlatformService;
			this.billWritePlatformService=billWritePlatformService;
			this.transactionHistoryWritePlatformService = transactionHistoryWritePlatformService;
			this.tenantDetailsService=tenantDetailsService;
			this.apiJsonDeserializer=apiJsonDeserializer;
			this.groupsDetailsRepository=groupsDetailsRepository;
			
	}
	
	
	@Transactional
	@Override
	public CommandProcessingResult createBillMaster(JsonCommand command,Long clientId) {
		try
		{
			
			this.apiJsonDeserializer.validateForCreate(command.json());
			//final MifosPlatformTenant tenant = this.tenantDetailsService.loadTenantById("default"); 
	        //ThreadLocalContextUtil.setTenant(tenant);
			Long groupId=new Long(0);
			Client client=this.clientRepository.findOne(clientId);
			if(client.getGroupName() != null){
			GroupsDetails groupsDetails=this.groupsDetailsRepository.findOne(client.getGroupName());//findOneByGroupName(client.getGroupName());
			groupId=groupsDetails.getId();
			}
		List<FinancialTransactionsData> financialTransactionsDatas = billMasterReadPlatformService.retrieveFinancialData(clientId);
		if (financialTransactionsDatas.size() == 0) {
			String msg = "no Bills to Generate";
			throw new BillingOrderNoRecordsFoundException(msg);
		}
	//	BillMaster billMaster = null;
		BigDecimal previousBal = BigDecimal.ZERO;
		List<BillMaster> billMasters = this.billMasterRepository.findAll();
		for (BillMaster data : billMasters) {
			if (data.getClientId().compareTo(clientId)==0) {
				previousBal = this.billMasterReadPlatformService.retrieveClientBalance(clientId);
			}
		}
		LocalDate billDate = new LocalDate();
		BigDecimal previousBalance = BigDecimal.ZERO;
		BigDecimal chargeAmount = BigDecimal.ZERO;
		BigDecimal adjustmentAmount = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		BigDecimal paidAmount = BigDecimal.ZERO;
		BigDecimal dueAmount = BigDecimal.ZERO;
		final LocalDate dueDate = command.localDateValueOfParameterNamed("dueDate");
		final String message = command.stringValueOfParameterNamed("message");
		BillMaster  billMaster = new BillMaster(clientId, clientId,billDate.toDate(), null, null, dueDate.toDate(),
		previousBalance, chargeAmount, adjustmentAmount, taxAmount,paidAmount, dueAmount, null,message,groupId);
		
		List<BillDetail> listOfBillingDetail = new ArrayList<BillDetail>();
		
		for (FinancialTransactionsData financialTransactionsData : financialTransactionsDatas) {
			
			BillDetail billDetail = new BillDetail(null,financialTransactionsData.getTransactionId(),
					financialTransactionsData.getTransactionDate(),	financialTransactionsData.getTransactionType(),
					financialTransactionsData.getDebitAmount());
			//this.billDetailRepository.save(billDetail);
			listOfBillingDetail.add(billDetail);
		    billMaster.addBillDetails(billDetail);
		
		}
	
		billMaster = this.billMasterRepository.saveAndFlush(billMaster);
		this.billWritePlatformService.ireportPdf(billMaster.getId());
		
		
		//List<BillDetail> billDetail = billWritePlatformService.createBillDetail(financialTransactionsDatas, billMaster);
		
		billWritePlatformService.updateBillMaster(listOfBillingDetail, billMaster,previousBal);
		billWritePlatformService.updateBillId(financialTransactionsDatas,billMaster.getId());
		//BillDetailsData billDetails = this.billMasterReadPlatformService.retrievebillDetails(billMaster.getId());
		
		transactionHistoryWritePlatformService.saveTransactionHistory(billMaster.getClientId(), "Statement", billMaster.getBillDate(),"DueAmount:"+billMaster.getDueAmount(),
				"AmountPaid:"+billMaster.getPaidAmount(),"AdjustmentAmount:"+billMaster.getAdjustmentAmount(),"PromotionDescription:"+billMaster.getPromotionDescription(),"BillNumber:"+billMaster.getBillNumber(),"StatementID:"+billMaster.getId());
       // this.billWritePlatformService.generatePdf(billDetails,financialTransactionsDatas);
        return new CommandProcessingResult(billMaster.getId());
	}   catch (DataIntegrityViolationException dve) {
		 handleCodeDataIntegrityIssues(command, dve);
		return  CommandProcessingResult.empty();
	}
}

	private void handleCodeDataIntegrityIssues(JsonCommand command,
			DataIntegrityViolationException dve) {
		// TODO Auto-generated method stub
		
	}
}