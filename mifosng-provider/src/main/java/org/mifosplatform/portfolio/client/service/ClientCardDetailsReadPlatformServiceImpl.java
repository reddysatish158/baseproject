/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.client.data.ClientCardDetailsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ClientCardDetailsReadPlatformServiceImpl implements ClientCardDetailsReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public ClientCardDetailsReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<ClientCardDetailsData> retrieveClientDetails(Long clientId) {

        this.context.authenticatedUser();

        final ClientDetailsMapper rm = new ClientDetailsMapper();

        String sql = "select " + rm.schema();

        return this.jdbcTemplate.query(sql, rm, new Object[] { clientId });
	}
	
	private static final class ClientDetailsMapper implements RowMapper<ClientCardDetailsData> {

        public String schema() {
        	
        	return "cd.id as id,cd.client_id as clientId,cd.type as type,cd.name as name,cd.card_number as cardNumber," +
        			"cd.card_type as cardType, cd.aba_routing_number as routingNumber,cd.bank_account_number as bankAccountNumber," +
        			"cd.bank_name as bankName,cd.account_type as accountType,cd.card_expiry_date as cardExpiryDate,cd.cvv_number as cvvNumber" +
        			" from m_client_card_details cd,m_client c where cd.client_id=c.id and c.id=? and cd.is_deleted='N'";
        }

        @Override
        public ClientCardDetailsData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        	final Long id = JdbcSupport.getLong(rs, "id");
            final Long clientId = JdbcSupport.getLong(rs, "clientId");
            final String type = rs.getString("type");
            final String name = rs.getString("name");
            final String cardNumber = rs.getString("cardNumber");
            final String cardType = rs.getString("cardType");
            final String routingNumber = rs.getString("routingNumber");
            final String bankAccountNumber = rs.getString("bankAccountNumber");
            final String bankName = rs.getString("bankName");
            final String accountType = rs.getString("accountType");
            final String cardExpiryDate = rs.getString("cardExpiryDate");
            final String cvvNumber = rs.getString("cvvNumber");

            return new ClientCardDetailsData(id, clientId, name, cardNumber, routingNumber,bankName,accountType,
            		cardExpiryDate,bankAccountNumber,cardType,type,cvvNumber);
        }

    }

	@Override
	public ClientCardDetailsData retrieveClient(Long id, String type,Long clientId) {
		this.context.authenticatedUser();

        final ClientDetailsMapper rm = new ClientDetailsMapper();

        String sql = "select " + rm.schema();
        sql=sql+" and cd.id=? and cd.type=?";

        return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { clientId,id,type });
	}
}
