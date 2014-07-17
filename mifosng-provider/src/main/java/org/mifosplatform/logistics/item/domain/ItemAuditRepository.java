/**
 * 
 */
package org.mifosplatform.logistics.item.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author hugo
 *
 */
public interface ItemAuditRepository extends JpaRepository<ItemMasterAudit,Long >,JpaSpecificationExecutor<ItemMasterAudit>{

}
