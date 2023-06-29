package jwc.com.model;

import lombok.Getter;

@Getter
class DeliveryServiceModel {
	
	private String serviceName;
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	private String invoiceNumber;
	
	

}
