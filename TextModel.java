package com.cnb.core.models;


import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;

@Model(
        adaptables = {SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class TextModel {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Self
	public SlingHttpServletRequest request;
	
	@Inject @Source("script-bindings")
	Page currentPage;

	private ResourceResolver resolver = null;
	
	private ValueMap profileProps = null;
	private ValueMap currentResProps = null;
	private String text = "";
	

	@PostConstruct
	private void init() {
		if(request != null && currentPage != null) {
			resolver = request.getResourceResolver();
			profileProps = ProfileUtils.getProfileProperties(resolver, currentPage);
			/*
			Resource profileRes= resolver.getResource(currentPage.getPath()+PROFILE_CARD_PATH);
			if(profileRes != null)
				profileProps = profileRes.getValueMap();*/
			
			Resource currentRes = request.getResource();
			if(currentRes != null)
				currentResProps = currentRes.getValueMap();
			
		}
	}

	public ValueMap getProfileProps() {
		return profileProps;
	}
	
	public String getText() {
		String text = "";
		if(currentResProps != null) {
			text = currentResProps.get("text","");
			System.out.println(text+"--->>");
			text = text.replace("#name#",profileProps.get("name",""));
		}
		return text;
		
	}

	
}
