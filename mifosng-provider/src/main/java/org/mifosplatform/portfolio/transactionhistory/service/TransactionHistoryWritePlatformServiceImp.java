package org.mifosplatform.portfolio.transactionhistory.service;

import java.util.Date;

import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.portfolio.transactionhistory.domain.TransactionHistory;
import org.mifosplatform.portfolio.transactionhistory.domain.TransactionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class TransactionHistoryWritePlatformServiceImp implements TransactionHistoryWritePlatformService {

	private TransactionHistoryRepository transactionHistoryRepository;
	
	@Autowired
	public TransactionHistoryWritePlatformServiceImp(TransactionHistoryRepository transactionHistoryRepository) {
		this.transactionHistoryRepository = transactionHistoryRepository;
	}
	

	@Override
	public boolean saveTransactionHistory(Long clientId, String transactionType, Date transactionDate, Object... history) {
		StringBuilder builder = new StringBuilder();
		try{
			
			for(Object objToString: history){
				builder.append(objToString+" ");
			}
			builder.trimToSize();
			String persistHistory = builder.toString();
			TransactionHistory transactionHistory = new TransactionHistory(clientId,transactionType,transactionDate,persistHistory);
			transactionHistoryRepository.save(transactionHistory);
			return true;
		}catch(DataIntegrityViolationException exception){
			throw new PlatformDataIntegrityException("unnable to save history","unnable to save history","unnable to save history","unnable to save history");
		}
		
	}

	
	
}
