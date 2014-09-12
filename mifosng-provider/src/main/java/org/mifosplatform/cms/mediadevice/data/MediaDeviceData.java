package org.mifosplatform.cms.mediadevice.data;

import java.math.BigDecimal;
import java.util.List;

import org.mifosplatform.infrastructure.configuration.domain.GlobalConfigurationProperty;

public class MediaDeviceData {
	
	
	private  Long deviceId;
	private  Long clientId;
	private  String clientType;
	private Long clientTypeId;
	private BigDecimal balanceAmount;
	private boolean balanceCheck;
	private List<MediaDeviceData> data;
	private GlobalConfigurationProperty paypalConfigData;
    private String currency;
	private GlobalConfigurationProperty paypalConfigDataForIos;
	
	
	public MediaDeviceData(Long deviceId, Long clientId, String clientType, Long clientTypeId, BigDecimal balanceAmount, String currency) {
           this.deviceId=deviceId;
           this.clientId=clientId;
           this.clientType=clientType;
           this.clientTypeId=clientTypeId;
           if(balanceAmount == null)
           this.balanceAmount=BigDecimal.ZERO;
           else
        	   this.balanceAmount=balanceAmount;
           this.currency=currency;
	}
	public MediaDeviceData(List<MediaDeviceData> data){
		this.data = data;
	}
	

	public Long getDeviceId() {
		return deviceId;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getClientType() {
		return clientType;
	}

	public Long getClientTypeId() {
		return clientTypeId;
	}



	public List<MediaDeviceData> getData() {
		return data;
	}



	public void setData(List<MediaDeviceData> data) {
		this.data = data;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}
	public boolean isBalanceCheck() {
		return balanceCheck;
	}
	public void setClientTypeId(Long clientTypeId) {
		this.clientTypeId = clientTypeId;
	}
	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public void setBalanceCheck(boolean isCheck) {
		this.balanceCheck =isCheck;
	}

	public void setPaypalConfigData(GlobalConfigurationProperty paypalConfigData) {
		this.paypalConfigData = paypalConfigData;
	}
	public void setPaypalConfigDataForIos(GlobalConfigurationProperty paypalConfigDataForIos) {
		this.paypalConfigDataForIos =paypalConfigDataForIos;
		
		
	}
	
}
