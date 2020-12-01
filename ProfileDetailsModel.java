package com.cnb.components.core.models.impl;


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
public class ProfileDetailsModel {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	protected static final String PROFILE_CARD_PATH = "/jcr:content/root/profile-card";

	@Self
	public SlingHttpServletRequest request;
	
	@Inject @Source("script-bindings")
	Page currentPage;

	private ResourceResolver resolver = null;
	
	private ValueMap profileProps = null;

	@PostConstruct
	private void init() {
		if(request != null && currentPage != null) {
			resolver = request.getResourceResolver();
			Resource profileRes= resolver.getResource(currentPage.getPath()+PROFILE_CARD_PATH);
			if(profileRes != null)
				profileProps = profileRes.getValueMap();
		}
	}

	public ValueMap getProfileProps() {
		return profileProps;
	}

	
}
