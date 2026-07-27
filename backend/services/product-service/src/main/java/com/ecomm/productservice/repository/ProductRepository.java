package com.ecomm.productservice.repository;

import com.ecomm.productservice.model.Product;
//import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

//    @Override
//    Product save(Product product);
//
//    @Override
//    Optional<Product> findById(@NonNull Long id);
//
//    @Override
//    List<Product> findAll();

    Optional<Product> findBySkuIgnoreCase(String sku);
    boolean existsBySkuIgnoreCase(String sku);

}
