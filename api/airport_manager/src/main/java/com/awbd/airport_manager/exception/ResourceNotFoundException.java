package com.awbd.airport_manager.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object obj) {
        super(resource + " not found with id: " + String.valueOf(obj));
    }
}
