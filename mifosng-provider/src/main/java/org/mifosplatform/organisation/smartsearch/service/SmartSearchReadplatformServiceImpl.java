/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.organisation.smartsearch.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.organisation.smartsearch.data.SmartSearchData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SmartSearchReadplatformServiceImpl implements SmartSearchReadplatformService {
	
	private final PaginationHelper<SmartSearchData> paginationHelper = new PaginationHelper<SmartSearchData>();
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SmartSearchReadplatformServiceImpl(final TenantAwareRoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private static final class SmartSearchMapper implements RowMapper<SmartSearchData> {

        public String schema() {
            return " p.id AS id, p.payment_date AS paymentDate,p.client_id as clientId,p.amount_paid as amount," +
            		" p.receipt_no as receiptNo,c.display_name as clientName,mc.code_value as paymodeType  " +
            		"FROM b_payments p, m_client c, m_code_value mc WHERE  p.client_id=c.id and p.paymode_id=mc.id ";
        }

        @Override
        public SmartSearchData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final LocalDate paymentDate = JdbcSupport.getLocalDate(rs, "paymentDate");
            final Long clientId = rs.getLong("clientId");
            final BigDecimal amount = rs.getBigDecimal("amount");
            final String receiptNo = rs.getString("receiptNo");
            final String clientName = rs.getString("clientName");
            final String paymodeType = rs.getString("paymodeType");

            return new SmartSearchData(id,clientId,clientName,paymentDate,paymodeType,receiptNo,amount);
        }
    }

    @Override
    public Page<SmartSearchData> retrieveAllSearchData(final String searchText,final Date fromDate, final Date toDate,
    		  final Integer  limit,final Integer offset) {
    	
        final SmartSearchMapper rm = new SmartSearchMapper();
        String sql = "select " + rm.schema();
        final Object[] objectArray = new Object[3];
        int arrayPos = 0;

        if (searchText != null ) {
            sql += " and p.receipt_no like '%"+searchText+"%'";
          /*  objectArray[arrayPos] = searchText;
            arrayPos = arrayPos + 1;*/
        }

        if (fromDate != null || toDate != null) {
        	
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String fromDateString = null;
            String toDateString = null;
            
            if (fromDate != null && toDate != null) {
                sql += "  AND p.payment_date BETWEEN ? AND ?";
                fromDateString = df.format(fromDate);
                toDateString = df.format(toDate);
                objectArray[arrayPos] = fromDateString;
                arrayPos = arrayPos + 1;
                objectArray[arrayPos] = toDateString;
                arrayPos = arrayPos + 1;
                
            } else if (fromDate != null) {
                sql += " AND p.payment_date >= ? ";
                fromDateString = df.format(fromDate);
                objectArray[arrayPos] = fromDateString;
                arrayPos = arrayPos + 1;
                
            } else if (toDate != null) {
                sql += "  AND p.payment_date <= ? ";
                toDateString = df.format(toDate);
                objectArray[arrayPos] = toDateString;
                arrayPos = arrayPos + 1;
            }
        }

        sql += "  order by p.payment_date limit "+limit+" offset "+offset;
      
        final Object[] finalObjectArray = Arrays.copyOf(objectArray, arrayPos);
        return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sql, finalObjectArray, rm);
    }

   

}
