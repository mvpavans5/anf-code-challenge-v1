package com.anf.core.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.QueryService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(
        service = QueryService.class
)
public class QueryServiceImpl implements QueryService {
	
	private static final Logger log = LoggerFactory.getLogger(QueryService.class);
	
	@Reference
	private QueryBuilder queryBuilder;

	
	// Note: An alternate approach to consider is iteration over the resource / nodes getting the child resources path, it is more performant and we can skip queries.
	// Also, if the anfCodeChallenge is not an index, we can create index on it. Non indexed fields results in larger scans.
	
	@Override
	public List<String> getResultsFromQueryBuilder(ResourceResolver resourceResolver) {
		List<String> pagePathList = new ArrayList<String>();

		final Map<String, String> map = new HashMap<String, String>();
        // https://docs.adobe.com/content/docs/en/aem/6-2/develop/ref/javadoc/com/day/cq/search/eval/TypePredicateEvaluator.html
        map.put("type", "cq:Page");
        // https://docs.adobe.com/content/docs/en/aem/6-2/develop/ref/javadoc/com/day/cq/search/eval/PathPredicateEvaluator.html
        map.put("path", "/content/anf-code-challenge/us/en");
        map.put("path.exact", "true"); // defaults to true
        map.put("path.flat", "true");
        map.put("path.self", "true");
        map.put("property", "jcr:content/anfCodeChallenge");
        map.put("property.operation", "exists");
        map.put("p.limit", "10");
        // Always set guessTotal to true unless you KNOW your result set will be small and counting it will be fast!
        map.put("p.guessTotal", "true");
        
        Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
        SearchResult result = query.getResult();
        // QueryBuilder has a leaking ResourceResolver, so the following work around is required.
        ResourceResolver leakingResourceResolver = null;
        
        try {
            // Iterate over the Hits if you need special information
            for (final Hit hit : result.getHits()) {
                if (leakingResourceResolver == null) {
                   // Get a reference to QB's leaking ResourceResolver
                   leakingResourceResolver = hit.getResource().getResourceResolver();
                }
                // Returns the path of the hit result
                pagePathList.add(hit.getPath());
                
            }

        } catch (RepositoryException e) {
            log.error("Error collecting search results", e);
        } finally {
            if (leakingResourceResolver != null) {
                // Always Close the leaking QueryBuilder resourceResolver.
                leakingResourceResolver.close();    
            }        
        }        

		return pagePathList;
	}

	@Override
	public List<String> getResultsFromSql2(ResourceResolver resourceResolver) {
		List<String> pagePathList = new ArrayList<String>();
		final String query = "SELECT * FROM [cq:Page] AS page WHERE ISDESCENDANTNODE(page ,\"/content/anf-code-challenge/us/en\") AND [jcr:content/anfCodeChallenge] = \"true\"";
		Iterator<Resource> pageResources = resourceResolver.findResources(query, javax.jcr.query.Query.JCR_SQL2);
		int i = 0;
		while (pageResources.hasNext() && i < 10) {
			pagePathList.add(pageResources.next().getPath());
		}
		return pagePathList;
	}


}
