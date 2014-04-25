package org.mifosplatform.portfolio.transactionhistory.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long>,JpaSpecificationExecutor<TransactionHistory> {

}
