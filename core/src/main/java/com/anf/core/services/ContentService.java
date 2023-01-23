package com.anf.core.services;

import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

public interface ContentService {
	void commitUserDetails(ResourceResolver resourceResolver, Map<String, Object> formDataMap) throws Exception;
	boolean validateUserDetails(ResourceResolver resourceResolver, int userAge);
}
