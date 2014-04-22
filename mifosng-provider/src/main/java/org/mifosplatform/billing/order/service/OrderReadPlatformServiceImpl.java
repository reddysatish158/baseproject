package org.mifosplatform.billing.order.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.order.data.OrderData;
import org.mifosplatform.billing.order.data.OrderDiscountData;
import org.mifosplatform.billing.order.data.OrderHistoryData;
import org.mifosplatform.billing.order.data.OrderLineData;
import org.mifosplatform.billing.order.data.OrderPriceData;
import org.mifosplatform.billing.order.data.OrderStatusEnumaration;
import org.mifosplatform.billing.order.domain.OrderRepository;
import org.mifosplatform.billing.payterms.data.PaytermData;
import org.mifosplatform.billing.plan.data.PlanCodeData;
import org.mifosplatform.billing.plan.data.ServiceData;
import org.mifosplatform.billing.pricing.service.PriceReadPlatformService;
import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderReadPlatformServiceImpl implements OrderReadPlatformService

{
	
	    private final JdbcTemplate jdbcTemplate;
	    private final PlatformSecurityContext context;
	    private OrderRepository orderRepository;
        private  static  PriceReadPlatformService priceReadPlatformService;
        
	    @Autowired
	    public OrderReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource,
			final PriceReadPlatformService priceReadPlatformService,final OrderRepository repository) {
	        this.context = context;
	        this.jdbcTemplate = new JdbcTemplate(dataSource);
	        this.orderRepository=repository;
	       OrderReadPlatformServiceImpl.priceReadPlatformService=priceReadPlatformService;

	    }
	@Override
	public List<PlanCodeData> retrieveAllPlatformData(Long planId) {
		  context.authenticatedUser();

	        String sql = "select s.id as id,s.plan_code as plan_code,s.is_prepaid as isPrepaid from b_plan_master s " +
	        		" where s.plan_status=1 and  s.is_deleted='n'  and s.id !=? ";
	        RowMapper<PlanCodeData> rm = new PeriodMapper();
	        return this.jdbcTemplate.query(sql, rm, new Object[] {planId});
	}

	 private static final class PeriodMapper implements RowMapper<PlanCodeData> {

	        @Override
	        public PlanCodeData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

	        Long id = rs.getLong("id");
	            String plan_code = rs.getString("plan_code");
	            String isPrepaid = rs.getString("isPrepaid");
	            List<ServiceData> services= priceReadPlatformService.retrievePrcingDetails(id);
	            return new PlanCodeData(id,plan_code,services,isPrepaid);

	        }


	 }

	@Override
	public List<PaytermData> retrieveAllPaytermData() {


		 context.authenticatedUser();

	        String sql = "select s.id as id,s.paymode_code as payterm_type,s.paymode_description as units from b_paymodes s";
	        RowMapper<PaytermData> rm = new PaytermMapper();
	        return this.jdbcTemplate.query(sql, rm, new Object[] {});
	}

	 private static final class PaytermMapper implements RowMapper<PaytermData> {

	        @Override
	        public PaytermData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

	        Long id = rs.getLong("id");
	            String paytermtype = rs.getString("payterm_type");
	            String units = rs.getString("units");
                 String data=units.concat(paytermtype);
	            return new PaytermData(id,data,null,null);
	        }
	}

	@Override
	public List<OrderPriceData> retrieveOrderPriceData(Long orderId) {
		 context.authenticatedUser();

	        /*String sql = "select s.id as id,s.order_id as order_id,s.charge_code as charge_code,s.service_id as service_id,s.charge_type as charge_type,s.charge_duration as charge_duration,"
                    +"s.duration_type as duration_type,s.price as price from order_price s where s.order_id = ?";*/
	        RowMapper<OrderPriceData> rm = new OrderPriceMapper();
	        final OrderPriceMapper orderPriceMapper=new OrderPriceMapper();
	        String sql = "select " + orderPriceMapper.schema();
	        return this.jdbcTemplate.query(sql, rm, new Object[] { orderId });
	}
	 private static final class OrderPriceMapper implements RowMapper<OrderPriceData> {
		 public String schema() {
			return  "s.id as id,s.order_id as order_id,s.charge_code as charge_code,s.service_id as service_id,s.charge_type as charge_type,s.charge_duration as charge_duration,"
	                    +"s.duration_type as duration_type,s.price as price from b_order_price s where s.order_id = ?";
			 
		 }

	        @Override
	        public OrderPriceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			  Long id = rs.getLong("id");
			  Long orderId = rs.getLong("order_id");
			  Long serviceId = rs.getLong("service_id");
	            String chargeCode = rs.getString("charge_code");
	            String chargeType = rs.getString("charge_type");
	            String chargeDuration = rs.getString("charge_duration");
	            String durationtype = rs.getString("duration_type");
	            BigDecimal price=rs.getBigDecimal("price");
	            return new OrderPriceData(id,orderId,serviceId,chargeCode,chargeType,chargeDuration,durationtype,price, null, null, null, null,null,null);
	        }

	}

	@Override
	public List<PaytermData> getChargeCodes(Long planCode) {

		   context.authenticatedUser();
	        String sql = "SELECT DISTINCT b.billfrequency_code AS billfrequency_code,b.id AS id,c.id AS duration,pm.is_prepaid AS isPrepaid" +
	        		" FROM b_plan_pricing a, b_charge_codes b, b_plan_master pm left join b_contract_period c on c.contract_period=pm.duration" +
	        		" WHERE a.charge_code = b.charge_code AND a.is_deleted = 'n' AND a.plan_id = ? AND pm.id = a.plan_id";


	        RowMapper<PaytermData> rm = new BillingFreaquencyMapper();
	        return this.jdbcTemplate.query(sql, rm, new Object[] { planCode });
	}

	 private static final class BillingFreaquencyMapper implements RowMapper<PaytermData> {

	        @Override
	        public PaytermData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			  Long id = rs.getLong("id");
	            String serviceType = rs.getString("billfrequency_code");
	            String duration = rs.getString("duration");
	            String isPrepaid = rs.getString("isPrepaid");
	            return new PaytermData(id,serviceType,duration,isPrepaid);
	}
	}
	@Override
	public List<OrderPriceData> retrieveOrderPriceDetails(Long orderId,Long clientId) {
		 RowMapper<OrderPriceData> rm = new OrderPriceDataMapper();

	      	        
	        String sql = "SELECT p.id AS id,o.client_id AS clientId,p.order_id AS order_id,c.charge_description AS chargeDescription,"
	        		+"s.service_description AS serviceDescription,p.charge_type AS charge_type,p.charge_duration AS chargeDuration, p.duration_type AS durationType,"
	        		+"p.price AS price,p.bill_start_date as billStartDate,p.bill_end_date as billEndDate,p.next_billable_day as nextBillableDay,p.invoice_tilldate as invoiceTillDate,"
	        		+"  o.billing_align as billingAlign, o.billing_frequency as billingFrequency FROM b_order_price p,b_charge_codes c,b_service s, b_orders o "
	        		+"WHERE p.charge_code = c.charge_code AND p.service_id = s.id  AND o.id = p.order_id  AND p.order_id =?";

	        return this.jdbcTemplate.query(sql, rm, new Object[] { orderId });
	}
	private static final class OrderPriceDataMapper implements RowMapper<OrderPriceData> {

        @Override
        public OrderPriceData mapRow(final ResultSet rs, final int rowNum) throws SQLException {


        	  Long id = rs.getLong("id");
			  Long orderId = rs.getLong("order_id");
			  Long clientId = rs.getLong("clientId");
	            String chargeCode = rs.getString("serviceDescription");
	            String chargeType = rs.getString("chargeDescription");
	            String chargeDuration = rs.getString("chargeDuration");
	            String durationtype = rs.getString("durationType");
	            String billingAlign = rs.getString("billingAlign");
	            String billingFrequency = rs.getString("billingFrequency");
	            BigDecimal price=rs.getBigDecimal("price");
	            LocalDate billStartDate=JdbcSupport.getLocalDate(rs,"billStartDate");
	            LocalDate billEndDate=JdbcSupport.getLocalDate(rs,"billEndDate");
	            LocalDate nextBillDate=JdbcSupport.getLocalDate(rs,"nextBillableDay");
	            LocalDate invoiceTillDate=JdbcSupport.getLocalDate(rs,"invoiceTillDate");
	            
	            return new OrderPriceData(id,orderId,clientId,chargeCode,chargeType,chargeDuration,durationtype,price,billStartDate,
	            		billEndDate,nextBillDate,invoiceTillDate,billingAlign,billingFrequency);
}
}

	@Override
	public List<OrderData> retrieveClientOrderDetails(Long clientId) {
		try {
			final ClientOrderMapper mapper = new ClientOrderMapper();

			final String sql = "select " + mapper.clientOrderLookupSchema()+" where o.plan_id = p.id and o.client_id= ? and o.is_deleted='n' and " +
					"o.contract_period = co.id   order by o.id desc";
			return jdbcTemplate.query(sql, mapper, new Object[] { clientId});
			} catch (EmptyResultDataAccessException e) {
			return null;
			}

			}

			private static final class ClientOrderMapper implements RowMapper<OrderData> {

			public String clientOrderLookupSchema() {
			return "o.id AS id,o.plan_id AS plan_id, o.start_date AS start_date,o.order_status AS order_status,p.plan_code AS plan_code,"
					+"o.end_date AS end_date,co.contract_period as contractPeriod,o.order_no as orderNo,o.user_action AS userAction," +
					" o.active_date AS activeDate,p.is_prepaid as isprepaid,p.allow_topup as allowTopUp," +
					"date_sub(o.next_billable_day,INTERVAL 1 DAY) as invoiceTillDate,(SELECT sum(ol.price) AS price FROM b_order_price ol"
					+" WHERE o.id = ol.order_id)  AS price,p.provision_sys as provSys  FROM b_orders o, b_plan_master p,b_contract_period co";
			}

			@Override
			public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

			final Long id = rs.getLong("id");
			final Long planId = rs.getLong("plan_id");
			final String plancode = rs.getString("plan_code");
			final String contractPeriod = rs.getString("contractPeriod");
			final int statusId = rs.getInt("order_status");
			LocalDate startDate=JdbcSupport.getLocalDate(rs,"start_date");
			LocalDate activaDate=JdbcSupport.getLocalDate(rs,"activeDate");
			LocalDate endDate=JdbcSupport.getLocalDate(rs,"end_date");
			LocalDate invoiceTillDate=JdbcSupport.getLocalDate(rs,"invoiceTillDate");
			final double price=rs.getDouble("price");
            final String isprepaid=rs.getString("isprepaid");
            final String allowtopup=rs.getString("allowTopUp");
            final String userAction=rs.getString("userAction");
            final String provSys=rs.getString("provSys");
            final String orderNo=rs.getString("orderNo");
           
			EnumOptionData Enumstatus=OrderStatusEnumaration.OrderStatusType(statusId);
			String status=Enumstatus.getValue();

			return new OrderData(id, planId, plancode, status, startDate,endDate,price,contractPeriod,isprepaid,allowtopup,userAction,
					provSys,orderNo,invoiceTillDate,activaDate);
			}
			}

			@Override
			public List<OrderHistoryData> retrieveOrderHistoryDetails(
					Long orderId) {
				

				try {
					final OrderHistoryMapper mapper = new OrderHistoryMapper();
					final String sql = "select " + mapper.clientOrderLookupSchema();
					return jdbcTemplate.query(sql, mapper, new Object[] { orderId});
					} catch (EmptyResultDataAccessException e) {
					return null;
					}

					}

					private static final class OrderHistoryMapper implements RowMapper<OrderHistoryData> {

					public String clientOrderLookupSchema() {
					return " h.id AS id,h.transaction_date AS transDate,h.actual_date AS actualDate,h.transaction_type AS transactionType," +
							"h.prepare_id AS PrepareRequsetId  FROM b_orders_history h  where h.order_id =?";
					}

					@Override
					public OrderHistoryData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

					final Long id = rs.getLong("id");
					final LocalDate transDate=JdbcSupport.getLocalDate(rs,"transDate");
					final LocalDate actualDate=JdbcSupport.getLocalDate(rs,"actualDate");
					final LocalDate provisionongDate=JdbcSupport.getLocalDate(rs,"actualDate");
					final String transactionType=rs.getString("transactionType");
					final Long PrepareRequsetId=rs.getLong("PrepareRequsetId");

					return new OrderHistoryData(id,transDate,actualDate,provisionongDate,transactionType,PrepareRequsetId );
					}
			}

					@Override
					public List<OrderData> getActivePlans(Long clientId,	String planType) {
						

						try {
							final ActivePlanMapper mapper = new ActivePlanMapper();
							
							 String sql =null;
							if(planType!=null)
							  {
								if(planType.equalsIgnoreCase("prepaid")){
									  sql = "select " + mapper.activePlanLookupSchema()+" AND p.is_prepaid = 'Y'";
								}else{
									  sql = "select " + mapper.activePlanLookupSchema()+" AND p.is_prepaid = 'N'";
								}
							  }else{
								    sql = "select " + mapper.activePlanLookupSchema();
							  }

							return jdbcTemplate.query(sql, mapper, new Object[] { clientId});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

							}

					private static final class ActivePlanMapper implements RowMapper<OrderData> {

						public String activePlanLookupSchema() {
						return "o.id AS orderId,p.plan_code AS planCode,p.plan_description as planDescription,o.billing_frequency AS billingFreq," +
								"o.end_date as endDate,c.contract_period as contractPeriod,(SELECT sum(ol.price) AS price FROM b_order_price ol"
					            +" WHERE o.id = ol.order_id)  AS price  FROM b_orders o, b_plan_master p, b_contract_period c WHERE client_id =?" +
								" AND p.id = o.plan_id  and o.contract_period=c.id and o.order_status=1 ";
						}

						@Override
						public OrderData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

						final Long orderId = rs.getLong("orderId");
						final String planCode=rs.getString("planCode");
						final String planDescription=rs.getString("planDescription");
						final String billingFreq=rs.getString("billingFreq");
						final String contractPeriod=rs.getString("contractPeriod");
						final LocalDate endDate=JdbcSupport.getLocalDate(rs,"endDate");
						final Double price=rs.getDouble("price");
						

						return new OrderData(orderId,planCode,planDescription,billingFreq,contractPeriod,price,endDate);
						}
				}

					@Override
					public OrderData retrieveOrderDetails(Long orderId) {
						try {
							final ClientOrderMapper mapper = new ClientOrderMapper();
							final String sql = "select " + mapper.clientOrderLookupSchema()+" where o.plan_id = p.id and o.id=? and " +
									" o.is_deleted='n' and o.contract_period = co.id order by o.id desc";
							return jdbcTemplate.queryForObject(sql, mapper, new Object[] { orderId});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

					}
					@Override
					public Long getRetrackId(Long id) {
						try {

							final String sql = "select MAX(h.id) as id from b_orders_history h where h.order_id=? and h.transaction_type LIKE '%tion%'";
							 RowMapper<Long> rm = new OSDMapper();
							return jdbcTemplate.queryForObject(sql, rm, new Object[] {id});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}
					 private static final class OSDMapper implements RowMapper<Long> {

					        @Override
					        public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							  Long id = rs.getLong("id");	
							  return id;
					}
					}

					@Override
					public String getOSDTransactionType(Long id) {
						try {

							final String sql = "select h.transaction_type as type from b_orders_history h where h.id=?";
							 RowMapper<String> rm = new OSDMapper1();
							return jdbcTemplate.queryForObject(sql, rm, new Object[] {id});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}
					 private static final class OSDMapper1 implements RowMapper<String> {

					        @Override
					        public String mapRow(final ResultSet rs, final int rowNum) throws SQLException {
							  String type = rs.getString("type");	
							  return type;
					}
					}
					 
					 @Override
					public String checkRetrackInterval(Long entityId) {
						 //final String sql = "select id FROM b_orders_history WHERE DATE_ADD((select created_date from b_orders_history where order_id = ? order by id desc limit 1), INTERVAL 1 HOUR) <= NOW() AND order_id = ? limit 1";
						 OSDMapper1 rm = new OSDMapper1();
						 final String sql= "select if (max(created_date) < date_sub(now(),INTERVAL 1 HOUR) , 'yes','no') as type" +
						 		" from b_orders_history where transaction_type in ('ACTIVATION','DISCONNECTION','RECONNECTION')" +
						 		" and order_id=?";
						 return jdbcTemplate.queryForObject(sql, rm , new Object[]{entityId});
					}
					@Override
					public List<OrderLineData> retrieveOrderServiceDetails(Long orderId) {
						
						try {
							final ClientOrderServiceMapper mapper = new ClientOrderServiceMapper();
							final String sql = "select " + mapper.orderServiceLookupSchema();
							return jdbcTemplate.query(sql, mapper, new Object[] { orderId});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}
					
					private static final class ClientOrderServiceMapper implements RowMapper<OrderLineData> {

						public String orderServiceLookupSchema() {
						return " ol.id as id,ol.order_id as orderId,s.service_code as serviceCode, s.service_description as serviceDescription,s.service_type as serviceType FROM b_order_line ol, b_service s" +
								" WHERE order_id =? and ol.service_id=s.id and ol.is_deleted ='N'";
						}

						@Override
						public OrderLineData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                        
					    final Long id = rs.getLong("id");
						final Long orderId = rs.getLong("orderId");
						final String serviceCode=rs.getString("serviceCode");
						final String serviceDescription=rs.getString("serviceDescription");
						final String serviceType=rs.getString("serviceType");
						
						return new OrderLineData(id,orderId,serviceCode,serviceDescription,serviceType);
						}
				}

					@Override
					public List<OrderDiscountData> retrieveOrderDiscountDetails(Long orderId) {

						try {
							final ClientOrderDiscountMapper mapper = new ClientOrderDiscountMapper();
							final String sql = "select " + mapper.orderDiscountLookupSchema();
							return jdbcTemplate.query(sql, mapper, new Object[] { orderId});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}
					}
					
					private static final class ClientOrderDiscountMapper implements RowMapper<OrderDiscountData> {

						public String orderDiscountLookupSchema() {
						return "od.id as id,od.orderprice_id as priceId,od.discount_rate as discountAmount,d.discount_code as discountCode," +
								"d.discount_description as discountdescription,d.discount_type as discountType,od.discount_startdate as startDate," +
								" od.discount_enddate as endDate  FROM b_order_discount od, b_discount_master d" +
								" where od.discount_id=d.id and od.is_deleted='N' and od.order_id=?";
						}

						@Override
						public OrderDiscountData mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                        
					    final Long id = rs.getLong("id");
						final Long priceId = rs.getLong("priceId");
						final String discountCode=rs.getString("discountCode");
						final String discountdescription=rs.getString("discountdescription");
						final String discountType=rs.getString("discountType");
						final LocalDate startDate=JdbcSupport.getLocalDate(rs,"startDate");
						final LocalDate endDate=JdbcSupport.getLocalDate(rs,"endDate");
						final BigDecimal discountAmount=rs.getBigDecimal("discountAmount");						
						return new OrderDiscountData(id,priceId,discountCode,discountdescription,discountAmount,discountType,startDate,endDate);
						}
				}

					@Override
					public Long retrieveClientActiveOrderDetails(Long clientId,String serialNo) {

						try {
							final ClientActiveOrderMapper mapper = new ClientActiveOrderMapper();
                          
							String sql="select "+mapper.activeOrderLookupSchema();
							if(serialNo !=null){
								sql="select "+mapper.activeOrderLookupSchema()+" and a.hw_serial_no='"+serialNo+"'";
							}
							return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId});
							} catch (EmptyResultDataAccessException e) {
							return null;
							}

							
					}
					
					private static final class ClientActiveOrderMapper implements RowMapper<Long> {
						
						public String activeOrderLookupSchema() {
							return "  ifnull(max(o.id),0) as orders from b_orders o, b_association a where  o.id = a.order_id and o.client_id = ? " +
									" and  o.order_status=1 ";
							}

						@Override
						public Long mapRow(final ResultSet rs, final int rowNum) throws SQLException {
					    final Long activeOrdersCount = rs.getLong("orders");
						 return activeOrdersCount;
						}
				}
	}

