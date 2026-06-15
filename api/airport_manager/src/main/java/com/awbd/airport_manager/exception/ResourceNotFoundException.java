package com.awbd.airport_manager.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Object obj) {
        super(resource + " not found with id: " + obj.toString());
    }
}
