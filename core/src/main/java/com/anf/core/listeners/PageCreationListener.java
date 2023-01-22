/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.anf.core.listeners;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service to handle page creation event.
 */
@Component(service = EventHandler.class, immediate = true, property = {
		EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
		EventConstants.EVENT_FILTER + "(path=/content/anf-code-challenge/us/en/*/jcr:content)" })
@ServiceDescription("A service to handle page creation event")
public class PageCreationListener implements EventHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final String REPOSITORY_SUBSERVICE_NAME = "repositoryService";
	
	@Reference
	private ResourceResolverFactory resolverFactory;

	/*
	 * Alternative approaches for this other than the event listener / event handler, when complexity increases, AEM blacklists long running event handler and event listener, however we can override this, but not a ideal approach.
	 * A workflow can be considered for this.
	 * A config component can be baked into the template.
	 * 
	 */
	public void handleEvent(final Event event) {
		
		if (null != getResourceResolver()) {
			Resource pageContentResource = getResourceResolver().getResource(event.getProperty(SlingConstants.PROPERTY_PATH).toString());
			if (null != pageContentResource) {
				ModifiableValueMap map = pageContentResource.adaptTo(ModifiableValueMap.class);
				map.put("pageCreated", Boolean.TRUE);
				try {
					pageContentResource.getResourceResolver().commit();
				} catch (PersistenceException e) {
					logger.error("Error saving repository. ", e);
				}
			}
		}
	}
	
	// Make sure the service user repositoryService is available as ensure user.
	private ResourceResolver getResourceResolver() {
		ResourceResolver resourceResolver = null;
		try {
			final Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, REPOSITORY_SUBSERVICE_NAME);
			resourceResolver = resolverFactory.getServiceResourceResolver(param);
		} catch (LoginException e) {
			logger.error("Error accessing repository. ", e);
		}
		return resourceResolver;
	}
}
