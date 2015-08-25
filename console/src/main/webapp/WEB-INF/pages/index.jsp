<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<% String menuId = "home"; %>
<%@include file="top.jsp" %>
<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">Home</h1>
            </div>

            <h2>Clusters</h2>
            <table class="table table-hover table-bordered">
                <thead>
                <tr>
                    <th>Cluster</th>
                    <th>Definition</th>
                    <th>IaaS Profile</th>
                    <th>Master Size</th>
                    <th>Slave Size</th>
                    <th>Management Size</th>
                    <th>Resource Size</th>
                </tr>
                </thead>
                <tbody>
                <c:if test="${not empty topologyList}">
                    <c:forEach var="topoloy" items="${topologyList}">
                        <tr>
                            <td><a href="/clusters/${topoloy.clusterId}">${topoloy.clusterId}</a></td>
                            <td>${topoloy.definitionId}</td>
                            <td>${topoloy.iaasProfile}</td>
                            <td>${fn:length(topoloy.mesosMasterList)}</td>
                            <td>${fn:length(topoloy.mesosSlaveList)}</td>
                            <td>${fn:length(topoloy.proxyList)}</td>
                            <td>${fn:length(topoloy.managementList)}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topologyList}">
                    <tr>
                        <td colspan="7">No clusters</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@include file="bottom.jsp" %>