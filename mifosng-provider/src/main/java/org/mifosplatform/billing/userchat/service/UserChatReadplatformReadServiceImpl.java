
package org.mifosplatform.billing.userchat.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.userchat.data.UserChatData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserChatReadplatformReadServiceImpl implements UserChatReadplatformReadService{

	private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;


    
    @Autowired
    public UserChatReadplatformReadServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);


    }


    @Transactional
	@Override
	public List<UserChatData> getUserChatDetails() {
        
    	try{
             
    		final String userName=this.context.authenticatedUser().getUsername();
    		UserChatDataMapper mapper = new UserChatDataMapper();
    		String sql = "select " + mapper.schema()+" where u.username=?";
    		return this.jdbcTemplate.query(sql, mapper, new Object[] { userName });
    		
    		
    	}catch(EmptyResultDataAccessException accessException){
    		return null;
    	}
		
    
    }
    
    private static final class UserChatDataMapper implements RowMapper<UserChatData> {

		public String schema() {
			return " u.id AS id,u.username AS userName,u.message_date AS messageDate,u.message AS message, u.createdby_user AS createdbyUser" +
					" FROM b_userchat u ";

		}

		@Override
		public UserChatData mapRow(final ResultSet rs,
				@SuppressWarnings("unused") final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String userName = rs.getString("userName");
			String message = rs.getString("message");
			String createdByUser = rs.getString("createdbyUser");
			LocalDate messageDate=JdbcSupport.getLocalDate(rs, "messageDate");

			return new UserChatData(id,userName,messageDate,message,createdByUser);

		}
	}

	@Override
	public List<UserChatData> getUserSentMessageDetails() {
        
    	try{
             
    		final String userName=this.context.authenticatedUser().getUsername();
    		UserChatDataMapper mapper = new UserChatDataMapper();
    		String sql = "select " + mapper.schema()+" where u.createdby_user = ?";
    		return this.jdbcTemplate.query(sql, mapper, new Object[] { userName });
    	}catch(EmptyResultDataAccessException accessException){
    		return null;
    	}
		
    
    }

}
