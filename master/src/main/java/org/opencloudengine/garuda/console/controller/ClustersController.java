package org.opencloudengine.garuda.console.controller;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.action.ActionService;
import org.opencloudengine.garuda.action.ActionStatus;
import org.opencloudengine.garuda.action.cluster.CreateClusterActionRequest;
import org.opencloudengine.garuda.action.cluster.DestroyClusterActionRequest;
import org.opencloudengine.garuda.cloud.*;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;
import org.opencloudengine.garuda.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

/**
 * Created by swsong on 2015. 5. 11..
 */
@Controller
@RequestMapping("/clusters")
public class ClustersController {

    private static final Logger logger = LoggerFactory.getLogger(ClustersController.class);
    /**
     * View cluster information
     * */
    @RequestMapping(value = "/{clusterId}", method = RequestMethod.GET)
    public ModelAndView viewCluster(@PathVariable String clusterId, HttpSession session) throws Exception {
        ClustersService clustersService = ServiceManager.getInstance().getService(ClustersService.class);
        ClusterService clusterService = clustersService.getClusterService(clusterId);
        ClusterTopology clusterTopology = clusterService.getClusterTopology();
        String domainName = clusterService.getDomainName();
        ModelAndView mav = new ModelAndView();
        IaasProvider iaasProvider = clusterService.getIaasProvider();
        ClusterDefinition clusterDefinition = clusterService.getClusterDefinition();
        mav.addObject("topology", clusterTopology);
        mav.addObject("domainName", domainName);
        mav.addObject("iaasProvider", iaasProvider);
        mav.addObject("clusterDefinition", clusterDefinition);
        session.setAttribute("_clusterId", clusterId);
        mav.setViewName("cluster");
        return mav;
    }

    /**
     * Edit cluster by clusterId
     * */
    @RequestMapping(value = "/{clusterId}", method = RequestMethod.PUT)
    public ModelAndView editCluster(@PathVariable String clusterId) throws Exception {

        //TODO

        ModelAndView mav = new ModelAndView();
        mav.setViewName("cluster");
        return mav;
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public ModelAndView viewClusterNew() throws Exception {
        ClustersService clustersService = ServiceManager.getInstance().getService(ClustersService.class);
        Collection<ClusterDefinition> definitions = clustersService.getDefinitions();
        IaasProviderConfig iaasProviderConfig = SettingManager.getInstance().getIaasProviderConfig();
        for(ClusterDefinition definition : definitions) {
            String profile = definition.getIaasProfile();
            IaasProvider provider = iaasProviderConfig.getIaasProvider(profile);
            String providerType = provider.getType();
            definition.setProviderType(providerType);
            for(ClusterDefinition.RoleDefinition roleDefinition : definition.getRoleList()) {
                String instanceType = roleDefinition.getInstanceType();
                IaasSpec spec = IaasSpec.getSpec(providerType, instanceType);
//                logger.debug("providerType={} instanceType={} spec = {}", providerType, instanceType, spec);
                roleDefinition.setIaasSpec(spec);
            }
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("definitions", definitions);
        mav.setViewName("clusterNew");
        return mav;
    }

    /**
     * Create new cluster
     * REST API
     * */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void clusterNew(@RequestBody String json, HttpServletResponse response) throws Exception {
        json = URLDecoder.decode(json, "utf-8");
        Map<String, Object> data = JsonUtil.json2Object(json);
        String clusterId = (String) data.get("id");
        if(clusterId == null) {
            response.sendError(500, "ID must be set.");
            return;
        }
        String definitionId = (String) data.get("definition");
        if (definitionId == null) {
            response.sendError(500, "Definition must be set.");
            return;
        }
        String domainName = (String) data.get("domain");
        if (domainName == null) {
            response.sendError(500, "Domain must be set.");
            return;
        }

        Boolean await = (Boolean) data.get("await");
        try {
            ActionRequest request = new CreateClusterActionRequest(clusterId, definitionId, domainName);
            ActionStatus actionStatus = ServiceManager.getInstance().getService(ActionService.class).request(request);
            if (await != null && await.booleanValue()) {
                actionStatus.waitForDone();
            }
            response.setStatus(200);
        } catch (Throwable t) {
            logger.error("", t);
            response.sendError(500, t.getMessage());
        }
    }

    @RequestMapping(value = "/{clusterId}", method = RequestMethod.DELETE)
    public void deleteCluster(@PathVariable String clusterId, HttpServletResponse response, HttpSession session) throws Exception {

        try {
            ActionRequest request = new DestroyClusterActionRequest(clusterId);
            ActionStatus actionStatus = ServiceManager.getInstance().getService(ActionService.class).request(request);
            actionStatus.waitForDone();
            session.removeAttribute("_clusterId");
            response.setStatus(200);
        } catch (Throwable t) {
            logger.error("", t);
            response.sendError(500, t.getMessage());
        }
    }

}
