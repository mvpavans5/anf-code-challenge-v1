package com.anf.core.services.impl;

import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.internal.util.UUID;
import com.anf.core.services.ContentService;

@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	final Logger log = LoggerFactory.getLogger(ContentService.class);

	@Override
	public void commitUserDetails(ResourceResolver resourceResolver, Map<String, Object> formDataMap) throws Exception {
		Resource userInfoRootResource = resourceResolver.getResource("/var/anf-code-challenge");
		if (null != userInfoRootResource) {
			resourceResolver.create(userInfoRootResource, UUID.createUUID(), formDataMap);
			resourceResolver.commit();
		}
	}

	@Override
	public boolean validateUserDetails(ResourceResolver resourceResolver, int userAge) {
		boolean isvalidAge = false;
		try {
			Resource ageResource = resourceResolver.getResource("/etc/age");
			if (null != ageResource) {
				ValueMap map = ageResource.adaptTo(ValueMap.class);
				int minAge = Integer.parseInt(map.get("minAge").toString());
				int maxAge = Integer.parseInt(map.get("maxAge").toString());
				if (userAge >= minAge && userAge <= maxAge) {
					 isvalidAge = true;
				}
			}
		} catch (NumberFormatException e) {
			log.error("Error age value", e);
		}
		return isvalidAge;
		
	}

}
