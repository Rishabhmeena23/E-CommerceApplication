package com.ecommerce.customer_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecommerce.customer_service.dto.CustomerRequest;
import com.ecommerce.customer_service.dto.CustomerResponse;
import com.ecommerce.customer_service.entity.Customer;
import com.ecommerce.customer_service.exception.ResourceAlreadyExistsException;
import com.ecommerce.customer_service.exception.ResourceNotFoundException;
import com.ecommerce.customer_service.repository.CustomerRepository;

@Service
public class CustomerServiceImpl implements CustomerService{

	private final CustomerRepository repository;
	
	@Override
	public CustomerResponse createCustomer(Long userId, String email, CustomerRequest request) {
		if (repository.existsByUserId(userId) || repository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException("Customer profile already exists");
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .userId(userId)
                .phone(request.getPhone())
                .gender(request.getGender())
                .build();

        Customer saved = repository.save(customer);

        return mapToResponse(saved);
	}
	
	private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .userId(customer.getUserId())
                .fullName(customer.getFirstName() + " " + customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .gender(customer.getGender())
                .build();
    }

	@Override
	public CustomerResponse getCustomerById(Long id) {
		Customer customer = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		
		return mapToResponse(customer);
	}

    @Override
    public CustomerResponse getCurrentCustomer(Long userId) {
        return mapToResponse(repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found")));
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
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		
		customer.setFirstName(request.getFirstName());
		customer.setLastName(request.getLastName());
		customer.setPhone(request.getPhone());
		customer.setGender(request.getGender());
		
		Customer updated = repository.save(customer);
		
		return mapToResponse(updated);
	}

    @Override
    public CustomerResponse updateCurrentCustomer(Long userId, CustomerRequest request) {
        Customer customer = repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        return updateCustomerEntity(customer, request);
    }

	@Override
	public void deleteCustomer(Long id) {
		repository.deleteById(id);	
	}

    @Override
    public void deleteCurrentCustomer(Long userId) {
        Customer customer = repository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        repository.delete(customer);
    }

    private CustomerResponse updateCustomerEntity(Customer customer, CustomerRequest request) {
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setGender(request.getGender());
        return mapToResponse(repository.save(customer));
    }

	public CustomerServiceImpl(CustomerRepository repository) {
		super();
		this.repository = repository;
	}

	
}
