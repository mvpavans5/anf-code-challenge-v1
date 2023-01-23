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
package com.anf.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.ContentService;
import com.day.crx.JcrConstants;

@Component(service = { Servlet.class })
@SlingServletPaths(
        value = "/bin/saveUserDetails"
)
public class UserServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private ContentService contentService;
    
    final Logger log = LoggerFactory.getLogger(UserServlet.class);

    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	String excutionMsg = "fail";
    	try {
			int userAge = Integer.parseInt(req.getParameter("age"));
			boolean isValidAge = contentService.validateUserDetails(req.getResourceResolver(), userAge);
			if (isValidAge) {
				excutionMsg = "ok";
			}
		} catch (NumberFormatException e) {
			 log.error("Error input age", e);
		}
    	resp.setContentType("text/html; charset=UTF-8");
    	resp.setStatus(HttpStatus.SC_OK);
    	resp.getWriter().println(excutionMsg);	        
    }
    
    @Override
    protected void doPost(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	
    	String excutionMsg = "fail";
    	try {
    		Map<String, Object> formDataMap = new HashMap<>();
    		formDataMap.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
    		
    		if (StringUtils.isNotBlank(req.getParameter("firstName")))  {
    			formDataMap.put("firstName", req.getParameter("firstName"));
    		}
    		if (StringUtils.isNotBlank(req.getParameter("lastName")))  {
    			formDataMap.put("lastName", req.getParameter("lastName"));
    		}
    		if (StringUtils.isNotBlank(req.getParameter("age")))  {
    			formDataMap.put("age", req.getParameter("age"));
    		}
    		if (StringUtils.isNotBlank(req.getParameter("country")))  {
    			formDataMap.put("country", req.getParameter("country"));
    		}
    		contentService.commitUserDetails(req.getResourceResolver(), formDataMap);
    		excutionMsg = "ok";
    		resp.setStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			 log.error("Error saving user details", e);
			 resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
    	resp.setContentType("text/html; charset=UTF-8");
    	resp.getWriter().println(excutionMsg);	        
    }
}
