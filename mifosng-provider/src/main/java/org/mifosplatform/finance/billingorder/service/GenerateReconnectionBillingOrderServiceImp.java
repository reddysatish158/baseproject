package org.mifosplatform.finance.billingorder.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.billing.discountmaster.domain.DiscountMasterRepository;
import org.mifosplatform.finance.billingorder.commands.BillingOrderCommand;
import org.mifosplatform.finance.billingorder.data.BillingOrderData;
import org.mifosplatform.finance.billingorder.domain.InvoiceRepository;
import org.mifosplatform.finance.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.finance.data.DiscountMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GenerateReconnectionBillingOrderServiceImp implements	GenerateReconnectionBillingOrderService {

	private final GenerateReconnectionBill generateBill;
	private final BillingOrderReadPlatformService billingOrderReadPlatformService;
	private final InvoiceRepository invoiceRepository;
	private final DiscountMasterRepository discountMasterRepository;
	//private final OrderRepository orderRepository;

	@Autowired
	public GenerateReconnectionBillingOrderServiceImp(GenerateReconnectionBill generateBill,BillingOrderReadPlatformService billingOrderReadPlatformService,
			InvoiceRepository invoiceRepository,final DiscountMasterRepository discountMasterRepository) {
		this.generateBill = generateBill;
		this.billingOrderReadPlatformService = billingOrderReadPlatformService;
		this.invoiceRepository = invoiceRepository;
		this.discountMasterRepository = discountMasterRepository;
		//this.orderRepository = orderRepository;
	}

	@Override
	public List<BillingOrderCommand> generateReconnectionBillingOrder(
			List<BillingOrderData> products,LocalDate reconnectionDate) {
		
		
		
		BillingOrderCommand billingOrderCommand = null;
		List<BillingOrderCommand> billingOrderCommands = new ArrayList<BillingOrderCommand>();

		if (products.size() != 0) {

			for (BillingOrderData billingOrderData : products) {
				// discount master 7
				DiscountMasterData discountMasterData = null;
				
				List<DiscountMasterData> discountMasterDatas = billingOrderReadPlatformService.retrieveDiscountOrders(billingOrderData.getClientOrderId(),billingOrderData.getOderPriceId());
				
				if(discountMasterDatas.size()!=0){
					discountMasterData = discountMasterDatas.get(0);	
				}
				

				if(billingOrderData.getOrderStatus() ==3){
					billingOrderCommand=generateBill.getCancelledOrderBill(billingOrderData,discountMasterData);	
					billingOrderCommands.add(billingOrderCommand);
				}
				
				else if (generateBill.isChargeTypeNRC(billingOrderData)) {
						
						System.out.println("---- NRC ---");
							billingOrderCommand = generateBill.getOneTimeBill(billingOrderData,discountMasterData);
							billingOrderCommands.add(billingOrderCommand);

					} else if (generateBill.isChargeTypeRC(billingOrderData)) {

						System.out.println("---- RC ----");

						// monthly
						if (billingOrderData.getDurationType().equalsIgnoreCase("month(s)") ) {
							if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {

								billingOrderCommand = generateBill.getMonthyBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);

							} else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {

								if (billingOrderData.getInvoiceTillDate() == null) {

									billingOrderCommand = generateBill.getProrataMonthlyFirstBill(billingOrderData,discountMasterData);
									billingOrderCommands.add(billingOrderCommand);

								} else if (billingOrderData.getInvoiceTillDate() != null) {

									billingOrderCommand = generateBill.getNextMonthBill(billingOrderData,discountMasterData);
									billingOrderCommands.add(billingOrderCommand);

								}
							}

						// weekly
						} else if (billingOrderData.getDurationType().equalsIgnoreCase("week(s)")) {

							if (billingOrderData.getBillingAlign().equalsIgnoreCase("N")) {
			
								billingOrderCommand = generateBill.getWeeklyBill(billingOrderData,discountMasterData);
								billingOrderCommands.add(billingOrderCommand);
							
					       } else if (billingOrderData.getBillingAlign().equalsIgnoreCase("Y")) {

								if (billingOrderData.getInvoiceTillDate() == null) {

									billingOrderCommand = generateBill.getProrataWeeklyFirstBill(billingOrderData,discountMasterData);
									billingOrderCommands.add(billingOrderCommand);

								} else if (billingOrderData.getInvoiceTillDate() != null) {

									billingOrderCommand = generateBill.getNextWeeklyBill(billingOrderData,discountMasterData);
									billingOrderCommands.add(billingOrderCommand);
								}
							}

						// daily
						} else if (billingOrderData.getDurationType()
								.equalsIgnoreCase("Day(s)")) {
							
							
							billingOrderCommand = generateBill.getDailyBill(billingOrderData,discountMasterData);
							billingOrderCommands.add(billingOrderCommand);
							
							
						}
					}
				
			}
		} else if (products.size() == 0) {
			throw new BillingOrderNoRecordsFoundException();
		}
		// return billingOrderCommand;
		return billingOrderCommands;
	}

	

	
	
	
}
