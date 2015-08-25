package org.opencloudengine.garuda.console.controller;

import org.opencloudengine.garuda.cloud.ClusterService;
import org.opencloudengine.garuda.cloud.ClustersService;
import org.opencloudengine.garuda.service.common.ServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Created by swsong on 2015. 5. 11..
 */
public class AuthMainInterceptor extends HandlerInterceptorAdapter {
	protected static Logger logger = LoggerFactory.getLogger(AuthMainInterceptor.class);

    private static final String LOGIN_PAGE = "/login";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession(true);

        ClustersService clustersService = ServiceManager.getInstance().getService(ClustersService.class);
        Set<String> clusterIdSet = clustersService.getClusterIdSet();
        request.setAttribute("_clusterIdSet", clusterIdSet);

        String clusterId = (String) session.getAttribute("_clusterId");
        if(clusterId == null) {
            if(clusterIdSet.size() > 0) {
                clusterId = clusterIdSet.iterator().next();
                session.setAttribute("_clusterId", clusterId);
            }
        }
//        User user = (User) session.getAttribute(User.USER_KEY);
//
//        if(user == null) {
//            if(request.getHeader("dev") != null) {
//                session.setAttribute(User.USER_KEY, new User("songaal@gmail.com", "fastcat", "A"));
//            } else {
//                checkLoginRedirect(request, response);
//                return false;
//            }
//        }
        return super.preHandle(request, response, handler);
    }

    public void checkLoginRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String loginURL = request.getContextPath() + LOGIN_PAGE;
        String method = request.getMethod();
        if(method.equalsIgnoreCase("GET")){
            String target = request.getRequestURI();
            String queryString = request.getQueryString();
            if(queryString != null && queryString.length() > 0){
                target += ("?" + queryString);
            }
            loginURL += ( "?redirect=" + target);
            logger.debug("REDIRECT >> {}, target = {}", method, target);
            logger.debug("RedirectURL >> {}", loginURL);
        }

        response.sendRedirect(loginURL);
    }
}
