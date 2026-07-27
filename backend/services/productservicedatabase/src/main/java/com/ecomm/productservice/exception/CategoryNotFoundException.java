package com.ecomm.productservice.exception;

public class CategoryNotFoundException extends RuntimeException{

    public CategoryNotFoundException(Long id){
        super("Category Not Found with id : " +id);
    }
}
