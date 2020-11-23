package com.cnb.components.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model(adaptables = Resource.class)

public class ProfileModel {
	private final Logger LOG = LoggerFactory.getLogger(getClass());


	@Self
	public Resource resource;

	String imagePath;
	String textPath;
	

	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getTextPath() {
		return textPath;
	}
	public void setTextPath(String textPath) {
		this.textPath = textPath;
	}
	@PostConstruct
	protected void init() throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException, PersistenceException {
		 if(resource != null) {
			 String path = resource.getValueMap().get("path",null);
			 if(path != null) {
				 ResourceResolver resolver = resource.getResourceResolver();
				 Resource profileRes = resolver.getResource(path+"/jcr:content/root");
				 if(profileRes != null) {
					 Iterator<Resource> itr = profileRes.listChildren();
					 while(itr.hasNext()) {
						 Resource res = itr.next();
						 if(res.getName().equals("image")) {
							 imagePath = res.getPath();
						 }
						 else if(res.getName().equals("text")) {
							 Node textNode = res.adaptTo(Node.class);
							/* String text = res.getValueMap().get("text","");
							 text = text.replace("{name}", resource.getValueMap().get("name",""));
							 textNode.setProperty("text", text);*/
							 textPath = res.getPath();
						 }
					 }
					 resolver.commit();
				 }
			 }
		 }
	}
	}
