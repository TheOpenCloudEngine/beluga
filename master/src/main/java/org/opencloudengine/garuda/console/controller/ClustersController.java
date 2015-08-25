package org.opencloudengine.garuda.console.controller;

import org.opencloudengine.garuda.action.ActionRequest;
import org.opencloudengine.garuda.cloud.*;
import org.opencloudengine.garuda.env.SettingManager;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.opencloudengine.garuda.settings.ClusterDefinition;
import org.opencloudengine.garuda.settings.IaasProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.management.relation.RoleList;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @RequestMapping(value = "/{clusterId}", method = RequestMethod.POST)
    public ModelAndView editCluster(@PathVariable String clusterId) throws Exception {
        ClustersService clustersService = ServiceManager.getInstance().getService(ClustersService.class);
        ClusterService clusterService = clustersService.getClusterService(clusterId);
        ClusterTopology clusterTopology = clusterService.getClusterTopology();
        ModelAndView mav = new ModelAndView();
        mav.addObject("clusterTopology", clusterTopology);
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
                logger.debug("providerType={} instanceType={} spec = {}", providerType, instanceType, spec);
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
     * */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ModelAndView clusterNew() throws Exception {
        String clusterId = null;
        ModelAndView mav = new ModelAndView();
        mav.setViewName("cluster/"+clusterId);
        return mav;
    }

}
