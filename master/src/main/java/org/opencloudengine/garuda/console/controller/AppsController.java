package org.opencloudengine.garuda.console.controller;

import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClusterTopology;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by swsong on 2015. 5. 11..
 */
@Controller
@RequestMapping("/apps")
public class AppsController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView apps() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("apps");
        return mav;
    }

}
