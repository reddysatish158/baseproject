package org.mifosplatform.billing.userchat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserChatRepository  extends
JpaRepository<UserChat, Long>,
JpaSpecificationExecutor<UserChat>{

}
