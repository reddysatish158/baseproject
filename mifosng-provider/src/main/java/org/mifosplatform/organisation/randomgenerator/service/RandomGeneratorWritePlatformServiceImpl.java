package org.mifosplatform.organisation.randomgenerator.service;

import java.text.ParseException;

import org.apache.commons.lang.RandomStringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.exception.PlatformDataIntegrityException;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGenerator;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetails;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGeneratorDetailsRepository;
import org.mifosplatform.organisation.randomgenerator.domain.RandomGenertatorRepository;
import org.mifosplatform.organisation.randomgenerator.exception.AlreadyProcessedException;
import org.mifosplatform.organisation.randomgenerator.serialization.RandomGeneratorCommandFromApiJsonDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RandomGeneratorWritePlatformServiceImpl implements
		RandomGeneratorWritePlatformService {
	
	int i, j, x=0 , beginKeyLength,RemainingKeyLength;

	private static final String Alpha = "Alpha";
	private static final String Numeric = "Numeric";
	private static final String AlphaNumeric = "AlphaNumeric";
	
	private final PlatformSecurityContext context;
	private final RandomGenertatorRepository randomGeneratorRepository;
	private final RandomGeneratorDetailsRepository randomGeneratorDetailsRepository;
	private final RandomGeneratorCommandFromApiJsonDeserializer fromApiJsonDeserializer;
	private final RandomGeneratorReadPlatformService randomGeneratorReadPlatformService;
	
	@Autowired
	public RandomGeneratorWritePlatformServiceImpl(
			final PlatformSecurityContext context,
			final RandomGenertatorRepository randomGeneratorRepository,
			final RandomGeneratorReadPlatformService randomGeneratorReadPlatformService,
			final RandomGeneratorCommandFromApiJsonDeserializer fromApiJsonDeserializer,
			final RandomGeneratorDetailsRepository randomGeneratorDetailsRepository) {
		
		this.context = context;
		this.randomGeneratorRepository = randomGeneratorRepository;
		this.fromApiJsonDeserializer = fromApiJsonDeserializer;
		this.randomGeneratorReadPlatformService = randomGeneratorReadPlatformService;
		this.randomGeneratorDetailsRepository=randomGeneratorDetailsRepository;

	}

	@Transactional
	@Override
	public CommandProcessingResult createRandomGenerator(JsonCommand command) {
		try {
			context.authenticatedUser();
			this.fromApiJsonDeserializer.validateForCreate(command.json());
			final RandomGenerator randomGenerator = RandomGenerator.fromJson(command);
			//generateRandomNumbers(randomGenerator);
			this.randomGeneratorRepository.save(randomGenerator);	
			return new CommandProcessingResult(randomGenerator.getId());

		}  catch (DataIntegrityViolationException dve) {
			handleCodeDataIntegrityIssues(command, dve);
			return CommandProcessingResult.empty();
		}  catch (ParseException e) {
			return CommandProcessingResult.empty();
		}

	}
	
	@Transactional
	@Override
	public CommandProcessingResult GenerateVoucherPinKeys(Long batchId) {
		try{
			
			RandomGenerator randomGenerator=this.randomGeneratorRepository.findOne(batchId);
			
			if(randomGenerator.getIsProcessed()=='N'){
				
				long id= generateRandomNumbers(randomGenerator);
				
				return new CommandProcessingResult(id);
			}else{
				
				throw new AlreadyProcessedException("VoucherPin Already Generated with this "+randomGenerator.getBatchName());
				
			}
			
		} catch (RuntimeException e) {
			
			return new CommandProcessingResult(new Long(-1));
			
		} catch (Exception e) {
			
			return new CommandProcessingResult(new Long(-1));
		}
		
		
	}

	public Long generateRandomNumbers(RandomGenerator randomGenerator) throws RuntimeException,Exception {
		try{
		
			String minSerialSeries = "";
			String maxSerialSeries = "";
			int length = Integer.valueOf(randomGenerator.getLength().toString());
			Long SerialNo =randomGenerator.getSerialNo();
		
			for (x = 0; x < SerialNo; x++) {
				if (x == 0) {
					minSerialSeries += "1";
					maxSerialSeries += "9";
				} else {
					minSerialSeries += "0";
					maxSerialSeries += "9";
				}
			}
					
			Long minNo = Long.parseLong(minSerialSeries);
			Long maxNo = Long.parseLong(maxSerialSeries);
			
			long no = this.randomGeneratorReadPlatformService.retrieveMaxNo(minNo,maxNo);
			
			if(no==0){
				no=minNo;
			}
			
			Long quantity = randomGenerator.getQuantity();
			beginKeyLength = randomGenerator.getBeginWith().length();
			RemainingKeyLength = length - beginKeyLength;
			
			return RandomValueGeneration(quantity,randomGenerator,no);
			
		}catch(RuntimeException e){
			
			throw new RuntimeException(e.getMessage());
			
		}catch(Exception e){
			
			throw new Exception(e.getMessage());
		}
		
	
	}
	
	private Long RandomValueGeneration(Long quantity,RandomGenerator randomGenerator, long no) throws RuntimeException,Exception {
		
		try{
			
			for (i = 0; i < quantity; i++) {
				String name = "";
				name += randomGenerator.getBeginWith();
				String Type = randomGenerator.getPinCategory();	
				name = name + GenerateRandomSingleCode(Type);	
				
				String value = this.randomGeneratorReadPlatformService.retrieveIndividualPin(name);
					if (value == null) {
						no += 1;
						RandomGeneratorDetails randomGeneratordetails = new RandomGeneratorDetails(name, no,randomGenerator);
						this.randomGeneratorDetailsRepository.save(randomGeneratordetails);		
						
					} else {					
						i--;						
					}				
				
			}
			randomGenerator.setIsProcessed('Y');
			this.randomGeneratorRepository.save(randomGenerator);
			
			return randomGenerator.getId();
			
		}catch(RuntimeException e){
			randomGenerator.setIsProcessed('F');
			this.randomGeneratorRepository.save(randomGenerator);
			
			throw new RuntimeException(e.getMessage());
			
		}catch(Exception e){
			
			randomGenerator.setIsProcessed('F');
			this.randomGeneratorRepository.save(randomGenerator);
			
			throw new Exception(e.getMessage());
			
		}
		
	}
	
	private String GenerateRandomSingleCode(String Type) throws RuntimeException,Exception {
		
		try{			
			String generatedKey;
			if (Type.equalsIgnoreCase(Alpha)) {	
				generatedKey = RandomStringUtils.randomAlphabetic(RemainingKeyLength);
				return generatedKey;
				
			} else if (Type.equalsIgnoreCase(Numeric)) {
				
				generatedKey = RandomStringUtils.randomNumeric(RemainingKeyLength);
				return generatedKey;
				
			} else if (Type.equalsIgnoreCase(AlphaNumeric)) {	
				
				generatedKey = RandomStringUtils.randomAlphanumeric(RemainingKeyLength);
				return generatedKey;
				
			} else{
				return null;
			}
			
		}catch(RuntimeException e){
			
			throw new RuntimeException(e.getMessage());
			
		}catch(Exception e){
			
			throw new Exception(e.getMessage());
		}
		
		
		
	}

	private void handleCodeDataIntegrityIssues(final JsonCommand command,
			final DataIntegrityViolationException dve) {
		Throwable realCause = dve.getMostSpecificCause();
		if (realCause.getMessage().contains("batch_name")) {
			final String name = command
					.stringValueOfParameterNamed("batchName");
			throw new PlatformDataIntegrityException(
					"error.msg.code.duplicate.batchname", "A batch with name'"
							+ name + "'already exists", "displayName", name);
		}
		if (realCause.getMessage().contains("serial_no_key")) {
			throw new PlatformDataIntegrityException(
					"error.msg.code.duplicate.serial_no_key", "A serial_no_key already exists", "displayName", "serial_no");
		}

		throw new PlatformDataIntegrityException(
				"error.msg.cund.unknown.data.integrity.issue",
				"Unknown data integrity issue with resource: "
						+ realCause.getMessage());
	}

}
