package org.mifosplatform.organisation.ippool.domain;
/*package org.mifosplatform.billing.ippool.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name="b_ippool_master")
public class IpPoolManagement extends AbstractPersistable<Long>  {
	
	
	@Column(name = "pool_code")
    private String ipCode;

    @Column(name = "pool_name")
    private String ipName;

    @LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "ipPoolManagement", orphanRemoval = true)
	private List<IpPoolManagementDetail> details = new ArrayList<IpPoolManagementDetail>();

   
    public IpPoolManagement(){
    	
    }
	
	

	public IpPoolManagement(String ipCode, String ipName) {
		this.ipCode=ipCode;
		this.ipName=ipName;
	}



	public static IpPoolManagement formJson(JsonCommand command) {
		
		String  ipCode=command.stringValueOfParameterNamed("ipCode");
		String ipName=command.stringValueOfParameterNamed("ipName");	
		return new IpPoolManagement(ipCode,ipName);
	}



	public void add(IpPoolManagementDetail ipPoolManagementDetail) {
		
		ipPoolManagementDetail.update(this);
		this.details.add(ipPoolManagementDetail);	
		
	}

}
*/