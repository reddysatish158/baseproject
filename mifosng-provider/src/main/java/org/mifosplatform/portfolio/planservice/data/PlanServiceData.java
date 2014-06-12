package org.mifosplatform.portfolio.planservice.data;

public class PlanServiceData {
	
	private final Long serviceId;
	private final Long clientId;
	private final String channelName;
	private final String channelDescription;
	private final String image;
	private final String url;
	private final String category;
	private final String subCategory;

	public PlanServiceData(Long serviceId, Long clientId, String serviceName,
			String logo, String url, String channelName, String category, String subCategory) {
           this.serviceId=serviceId;
           this.clientId=clientId;
           this.channelName=channelName;
           this.channelDescription=serviceName;
           this.image=logo;
           this.url=url;
           this.category=category;
           this.subCategory=subCategory;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public Long getClientId() {
		return clientId;
	}

	public String getServiceName() {
		return channelName;
	}

	public String getLogo() {
		return image;
	}

	public String getChannelName() {
		return channelName;
	}

	public String getChannelDescription() {
		return channelDescription;
	}

	public String getImage() {
		return image;
	}

	public String getUrl() {
		return url;
	}

	public String getCategory() {
		return category;
	}

	public String getSubCategory() {
		return subCategory;
	}

}
