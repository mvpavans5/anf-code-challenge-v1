package com.anf.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.anf.core.pojo.NewsItem;

/**
 * Model class for NewsFeed component.
 */

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeedModel {

    @ValueMapValue
    @Default(values = "/var/commerce/products/anf-code-challenge/newsData")
    public String newsFeedRootPath;

    @SlingObject
    private ResourceResolver resourceResolver;
    

    private List<NewsItem> newsItemsList = new ArrayList<NewsItem>();

    @PostConstruct
    protected void init(){
        Resource resourceData = resourceResolver.getResource(newsFeedRootPath);
        if(resourceData != null && resourceData.hasChildren()) {
            Iterator<Resource> newsItems = resourceData.listChildren();
            while(newsItems.hasNext()) {
                processNewsItem(newsItems.next());
            }
        }
    }

    private void processNewsItem(Resource newsFeedItemResource) {
        
        if (null != newsFeedItemResource) {
        	ValueMap valueMap = newsFeedItemResource.getValueMap();

            NewsItem newsItem = new NewsItem();
            newsItem.setAuthor(valueMap.get("author", String.class));
            newsItem.setContent(valueMap.get("content", String.class));
            newsItem.setDescription(valueMap.get("description", String.class));
            newsItem.setTitle(valueMap.get("title", String.class));
            newsItem.setUrl(valueMap.get("url", String.class));
            newsItem.setUrlImage(valueMap.get("urlImage", String.class));
            newsItemsList.add(newsItem);
        }
    }


    public List<NewsItem> getNewsItemsList() {
        return newsItemsList;
    }

}