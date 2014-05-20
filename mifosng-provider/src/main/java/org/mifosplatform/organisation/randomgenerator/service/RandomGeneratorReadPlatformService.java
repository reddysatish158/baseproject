package org.mifosplatform.organisation.randomgenerator.service;

import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import org.mifosplatform.infrastructure.core.data.EnumOptionData;
import org.mifosplatform.organisation.randomgenerator.data.RandomGeneratorData;

public interface RandomGeneratorReadPlatformService {

	String retrieveIndividualPin(String pinId);
	
	List<RandomGeneratorData> getAllData();

	List<EnumOptionData> pinCategory();

	List<EnumOptionData> pinType();

	Long retrieveMaxNo(Long minNo, Long maxNo);

	StreamingOutput retrieveVocherDetailsCsv(Long batchId);

}
