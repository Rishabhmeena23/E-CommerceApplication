package com.ecommerce.customer_service.service;

import java.util.List;

import com.ecommerce.customer_service.dto.CustomerRequest;
import com.ecommerce.customer_service.dto.CustomerResponse;

public interface CustomerService {
	CustomerResponse createCustomer(CustomerRequest request);
	
	CustomerResponse getCustomerById(Long id);
	
	List<CustomerResponse> getAllCustomers();
		
	void deleteCustomer(Long id);

	CustomerResponse updateCustomer(Long id, CustomerRequest request);
}
