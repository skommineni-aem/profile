package com.mnt.axp.common.core.services.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.jcr.Session;

import com.mnt.axp.common.core.models.WorkflowReportModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;

import com.mnt.axp.common.core.services.WorkflowReportService;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = WorkflowReportService.class)
public class WorkflowReportServiceImpl implements WorkflowReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowReportServiceImpl.class);

    @Override
    public List<WorkflowReportModel> getWorkflowDetails(SlingHttpServletRequest request, ResourceResolver resolver) {
        String path = request.getParameter("path");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String wfModelId = request.getParameter("wfModel");

        LOGGER.debug("path::",path);
        LOGGER.debug("startDate::",startDate);
        LOGGER.debug("endDate::",endDate);
        LOGGER.debug("wfModelId::",wfModelId);

        QueryBuilder builder = resolver.adaptTo(QueryBuilder.class);
        Session session = resolver.adaptTo(Session.class);

        Map<String, String> map = new HashMap<>();
        map.put("path", "/var/workflow/instances");
        map.put("type", "cq:Payload");
        map.put("property", "path");
        //map.put("property.value", path + "%");

        if (path.contains("MTB")) {
            map.put("property.1_value", "/content/mtb-web" + "%");
            map.put("property.2_value", "/content/dam/mtb-web" + "%");
            map.put("property.3_value", "/content/experience-fragments/mtb-web" + "%");
        } else if (path.contains("WTB")) {
            map.put("property.1_value", "/content1" + "%");
            map.put("property.2_value", "/content2" + "%");
            map.put("property.3_value", "/content3" + "%");
        }

        map.put("property.operation", "like");
        map.put("p.limit", "-1");
        Query query = builder.createQuery(PredicateGroup.create(map), session);
        SearchResult result = query.getResult();
        Iterator<Resource> itr = result.getResources();
        List<WorkflowReportModel> list = new ArrayList<>();

        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (itr.hasNext()) {
            Resource payloadRes = itr.next();
            WorkflowReportModel model = new WorkflowReportModel();
            String payLoadPath = payloadRes.getValueMap().get("path", path);
            Resource payLoadPathRes = resolver.getResource(payLoadPath);
			String primaryType = payLoadPathRes.getValueMap().get("jcr:primaryType","");
            System.out.println("========"+payLoadPathRes.getValueMap().get("jcr:primaryType",""));
			if("cq:Page".equals(primaryType))
				payLoadPath = payLoadPath + ".html";
            model.setPath(payLoadPath);
            Resource wfModelRes = payloadRes.getParent().getParent();
            //Getting user
            Resource historyRes = wfModelRes.getChild("history");
            if (historyRes != null) {
                Iterator<Resource> historyChildsRes = historyRes.listChildren();
                int i = 0;
                while (historyChildsRes.hasNext()) {
                    Resource historyChildRes = historyChildsRes.next();
                    i++;
                    if (i == 3) {
                        LOGGER.debug("User::",historyChildRes.getValueMap().get("user", ""));
                        model.setUser(historyChildRes.getValueMap().get("user", ""));
                    }
                }
            }
            Resource dataRes = payloadRes.getParent().getChild("metaData");
            LOGGER.debug("Start Comment::",dataRes.getValueMap().get("startComment", ""));
            if (dataRes != null)
                model.setComments(dataRes.getValueMap().get("startComment", ""));
            model.setInitiator(wfModelRes.getValueMap().get("initiator", ""));
            model.setStatus(wfModelRes.getValueMap().get("status", ""));
            String modelId = wfModelRes.getValueMap().get("modelId", "");
            model.setModelName(modelId);
            Calendar startTimeCalendar = wfModelRes.getValueMap().get("startTime", Calendar.class);
            Calendar endTimeCalendar = wfModelRes.getValueMap().get("endTime", Calendar.class);
            if (startTimeCalendar != null){
                model.setStartDate(startTimeCalendar.getTime());
                model.setStartTime(outputFormat.format(startTimeCalendar.getTime()));
            }
            if (endTimeCalendar != null)
                model.setEndTime(outputFormat.format(endTimeCalendar.getTime()));

            try {
                if ((wfModelId != null && wfModelId.equals(modelId)) && StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate))
                    list.add(model);
                else if ((wfModelId != null && wfModelId.equals(modelId)) && startTimeCalendar != null && datesBetween(startDate, endDate, outputFormat.format(startTimeCalendar.getTime())))
                    list.add(model);
                else if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate) && StringUtils.isEmpty(wfModelId))
                    list.add(model);
            } catch (ParseException e) {
                e.getMessage();
            }
        }
        LOGGER.debug("Workflow report size::",list.size());
        list.sort(Comparator.comparing(WorkflowReportModel::getStartDate));
        return list;
    }

    public boolean datesBetween(String startDateStr, String endDateStr, String wfStartDateStr) throws ParseException {
        Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        Date wfStartDate = new SimpleDateFormat("yyyy-MM-dd").parse(wfStartDateStr);
        //return wfStartDate.compareTo(startDate) >= 0 && wfStartDate.compareTo(endDate) <= 0;
        return wfStartDate.after(startDate) && wfStartDate.before(endDate);
    }

}