package com.ecommerce.customer_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.customer_service.dto.CustomerRequest;
import com.ecommerce.customer_service.dto.CustomerResponse;
import com.ecommerce.customer_service.entity.Customer;
import com.ecommerce.customer_service.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
public class CustomerServiceImpl implements CustomerService{

	private final CustomerRepository repository;
	
	@Override
	public CustomerResponse createCustomer(CustomerRequest request) {
		if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .gender(request.getGender())
                .build();

        Customer saved = repository.save(customer);

        return mapToResponse(saved);
	}
	
	private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .fullName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .gender(customer.getGender())
                .build();
    }

	@Override
	public CustomerResponse getCustomerById(Long id) {
		Customer customer = repository.findById(id)
				.orElseThrow(()-> new RuntimeException("Customer not found"));
		
		return mapToResponse(customer);
	}

	@Override
	public List<CustomerResponse> getAllCustomers() {
		
		return repository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Override
	public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
		Customer customer = repository.findById(id)
				.orElseThrow(()-> new RuntimeException("customer not found"));
		
		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setPhone(request.getPhone());
		customer.setGender(request.getGender());
		
		Customer updated = repository.save(customer);
		
		return mapToResponse(customer);
	}

	@Override
	public void deleteCustomer(Long id) {
		repository.deleteById(id);	
	}

	public CustomerServiceImpl(CustomerRepository repository) {
		super();
		this.repository = repository;
	}

	
}
