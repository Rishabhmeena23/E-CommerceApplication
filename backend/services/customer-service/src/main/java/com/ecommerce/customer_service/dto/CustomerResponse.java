package com.ecommerce.customer_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
	private Long customerId;
	
	private String fullName;
	
	private String email;
	
	private String phone;
	
	private String gender;
}
