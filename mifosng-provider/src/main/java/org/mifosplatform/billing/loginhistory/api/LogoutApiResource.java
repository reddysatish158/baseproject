package org.mifosplatform.billing.loginhistory.api;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.mifosplatform.billing.loginhistory.domain.LoginHistory;
import org.mifosplatform.billing.loginhistory.domain.LoginHistoryRepository;
import org.mifosplatform.commands.domain.CommandWrapper;
import org.mifosplatform.commands.service.CommandWrapperBuilder;
import org.mifosplatform.commands.service.PortfolioCommandSourceWritePlatformService;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.serialization.DefaultToApiJsonSerializer;
import org.mifosplatform.portfolio.contract.data.SubscriptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Path("/logout")
@Component
@Scope("singleton")
public class LogoutApiResource {

	    private final DefaultToApiJsonSerializer<SubscriptionData> toApiJsonSerializer;
	    private final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService;
	    private final LoginHistoryRepository loginHistoryRepository;
	   
	    @Autowired
	    public LogoutApiResource(final DefaultToApiJsonSerializer<SubscriptionData> toApiJsonSerializer,
	    							final PortfolioCommandSourceWritePlatformService commandsSourceWritePlatformService,
	    							final LoginHistoryRepository loginHistoryRepository) {
		       
		        this.toApiJsonSerializer = toApiJsonSerializer;
		        this.commandsSourceWritePlatformService = commandsSourceWritePlatformService;
		        this.loginHistoryRepository=loginHistoryRepository;
		       
		}		
		
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_JSON})
	public String createNewContract(final String apiRequestBodyAsJson,@QueryParam("id") final Long id,@Context HttpServletRequest req) {
				
        		LoginHistory loginHistory=this.loginHistoryRepository.findOne(id);
            	final Map<String, Object> changes = loginHistory.update();
            	if(!changes.isEmpty()){
            		this.loginHistoryRepository.save(loginHistory);
            	}
        		req.getSession().invalidate();
        		//return "successfully logout";
        		return "loginHistoryId"+":"+id.toString();
	}

}
