package com.ascend.auth;

import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class DomainValidator {
    
    private static final List<String> ALLOWED_DOMAINS = Arrays.asList(
        "ascend.com",
        "gmail.com",
        "yahoo.com",
        "hotmail.com",
        "outlook.com",
        "icloud.com",
        "example.com" // Added for testing purposes
    );
    
    public boolean isValidDomain(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        
        String domain = email.substring(email.lastIndexOf("@") + 1).toLowerCase();
        return ALLOWED_DOMAINS.contains(domain);
    }
    
    public List<String> getAllowedDomains() {
        return ALLOWED_DOMAINS;
    }
} 