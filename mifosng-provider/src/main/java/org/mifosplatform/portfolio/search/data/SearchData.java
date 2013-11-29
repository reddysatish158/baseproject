package org.mifosplatform.portfolio.search.data;


public class SearchData {
    
    private final Long entityId;
    private final String entityAccountNo;
    private final String entityExternalId;
    private final String entityName;
    private final String entityType;
    private final Long parentId;
    private final String parentName;
    private final String address;
    
    private final String phone;
    private final String email;
    
    public SearchData(Long entityId, String entityAccountNo, String entityExternalId, String entityName, String entityType, Long parentId, String parentName, 
    		String addr1, String street, String email, String phone) {
        
        this.entityId = entityId;
        this.entityAccountNo = entityAccountNo;
        this.entityExternalId = entityExternalId;
        this.entityName = entityName;
        this.entityType = entityType;
        this.parentId = parentId;
        this.parentName = parentName;
        this.address=addr1+","+street;
        this.email=email;
        this.phone=phone;
        		
    }

    
    public Long getEntityId() {
        return this.entityId;
    }

    
    public String getEntityAccountNo() {
        return this.entityAccountNo;
    }

    
    public String getEntityExternalId() {
        return this.entityExternalId;
    }

    
    public String getEntityName() {
        return this.entityName;
    }

    
    public String getEntityType() {
        return this.entityType;
    }


    
    public Long getParentId() {
        return this.parentId;
    }


    
    public String getParentName() {
        return this.parentName;
    }


	public String getAddress() {
		return address;
	}



	public String getPhone() {
		return phone;
	}


	public String getEmail() {
		return email;
	}
    
    
    
}
