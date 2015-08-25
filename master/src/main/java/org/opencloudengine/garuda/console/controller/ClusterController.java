package org.opencloudengine.garuda.console.controller;

import org.opencloudengine.garuda.action.ActionRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by swsong on 2015. 5. 11..
 */
@Controller
@RequestMapping("/")
public class ClusterController {

    @RequestMapping(value = "/cluster", method = RequestMethod.GET)
    public ModelAndView clusters() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("cluster");
        ActionRequest request = null;
        return mav;
    }

    @RequestMapping(value = "/addCluster", method = RequestMethod.GET)
    public ModelAndView addCluster() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("addCluster");
        return mav;
    }

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    public ModelAndView apps() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("apps");
        return mav;
    }

    @RequestMapping(value = "/monitoring", method = RequestMethod.GET)
    public ModelAndView monitoring() throws Exception {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("monitoring");
        return mav;
    }

}
