package org.mifosplatform.crm.clientprospect.service;


public final class SearchSqlQuery {

	private final String sqlSearch;
    private final Integer offset;
    private final Integer limit;


    public static SearchSqlQuery forSearch(final String sqlSearch,final Integer offset,final Integer limit){

        Integer maxLimitAllowed = 200;
        if (limit != null && limit < maxLimitAllowed && limit > 0) {
            maxLimitAllowed = limit;
        }
        return new SearchSqlQuery(sqlSearch, offset, maxLimitAllowed);

    }
    
    public  SearchSqlQuery(final String sqlSearch,final Integer offset,final Integer limit){
 	
    	this.sqlSearch=sqlSearch;
    	this.offset=offset;
    	this.limit=limit;

    }

	public String getSqlSearch() {
		return sqlSearch;
	}

	public Integer getOffset() {
		return offset;
	}
	public Integer getLimit() {
		return limit;
	}

    public boolean isLimited() {
        return this.limit != null && this.limit.intValue() > 0;
    }

    public boolean isOffset() {
        return this.offset != null;
    }

}
