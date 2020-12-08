package com.cnb.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import com.day.cq.wcm.api.Page;

public class ProfileUtils {
	
	protected static final String PROFILE_CARD_PATH = "/jcr:content/parcontent/profile";
	
	public static ValueMap getProfileProperties(ResourceResolver resolver,Page page) {
		Resource profileRes= resolver.getResource(page.getPath()+PROFILE_CARD_PATH);
		if(profileRes != null)
			return profileRes.getValueMap();
		return null;
	}

}
