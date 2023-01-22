package com.anf.core.services;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;

public interface QueryService {
	
	public List<String> getResultsFromQueryBuilder(ResourceResolver resourceResolver);
	public List<String> getResultsFromSql2(ResourceResolver resourceResolver);
	
}
