<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<% String menuId = "cluster"; %>
<%@include file="top.jsp" %>

<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">${topology.clusterId}</h1>
            </div>

            <div class="left-dl">
                <dl class="dl-horizontal">
                    <dt>ID : </dt>
                    <dd>${topology.clusterId}</dd>
                    <dt>Domain : </dt>
                    <dd>${domainName}</dd>
                    <dt>Iaas Provider : </dt>
                    <dd>${iaasProvider.name}</dd>
                    <dt>Iaas Definition : </dt>
                    <dd>${topology.definitionId}</dd>
                    <dt>Iaas Profile : </dt>
                    <dd>${topology.iaasProfile}</dd>
                    <dt>Key Pair : </dt>
                    <dd>${clusterDefinition.keyPair}</dd>
                </dl>
            </div>
            <table class="table table-hover table-bordered">
                <thead>
                <tr>
                    <th>Role</th>
                    <th>Instance Type</th>
                    <th>CPU</th>
                    <th>Memory</th>
                    <th>Public IP Address</th>
                    <th>Private IP Address</th>
                    <th>Group</th>
                    <th>State</th>
                </tr>
                </thead>
                <tbody>
                <!-- Mesos Master -->
                <c:if test="${not empty topology.mesosMasterList}">
                    <c:forEach var="instance" items="${topology.mesosMasterList}" varStatus="status">
                    <tr>
                        <c:if test="${status.index == 0}">
                        <td rowspan="${fn:length(topology.mesosMasterList)}"><strong>Master</strong></td>
                        </c:if>
                        <%--<td>${instance.name}</td>--%>
                        <%--<td>${instance.instanceId}</td>--%>
                        <td>${instance.instanceType}</td>
                        <td>${instance.iaasSpec.cpu}</td>
                        <td>${instance.iaasSpec.memory} GB</td>
                        <td>${instance.publicIpAddress}</td>
                        <td>${instance.privateIpAddress}</td>
                        <td>
                            <c:forEach var="groupName" items="${instance.groupList}">
                                <span>${groupName} <span>
                            </c:forEach>
                        </td>
                        <td>${instance.state}</td>
                    </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topology.mesosMasterList}">
                    <tr>
                        <td><strong>Master</strong></td>
                        <td colspan="7">No instances</td>
                    </tr>
                </c:if>

                <!-- Mesos Slave -->
                <c:if test="${not empty topology.mesosSlaveList}">
                    <c:forEach var="instance" items="${topology.mesosSlaveList}" varStatus="status">
                        <tr>
                            <c:if test="${status.index == 0}">
                                <td rowspan="${fn:length(topology.mesosSlaveList)}"><strong>Slave</strong></td>
                            </c:if>
                            <td>${instance.instanceType}</td>
                            <td>${instance.iaasSpec.cpu}</td>
                            <td>${instance.iaasSpec.memory} GB</td>
                            <td>${instance.publicIpAddress}</td>
                            <td>${instance.privateIpAddress}</td>
                            <td>
                                <c:forEach var="groupName" items="${instance.groupList}">
                                <span>${groupName} <span>
                                    </c:forEach>
                            </td>
                            <td>${instance.state}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topology.mesosSlaveList}">
                    <tr>
                        <td><strong>Slave</strong></td>
                        <td colspan="7">No instances</td>
                    </tr>
                </c:if>

                <!-- Proxy -->
                <c:if test="${not empty topology.proxyList}">
                    <c:forEach var="instance" items="${topology.proxyList}" varStatus="status">
                        <tr>
                            <c:if test="${status.index == 0}">
                                <td rowspan="${fn:length(topology.proxyList)}"><strong>Proxy</strong></td>
                            </c:if>
                            <td>${instance.instanceType}</td>
                            <td>${instance.iaasSpec.cpu}</td>
                            <td>${instance.iaasSpec.memory} GB</td>
                            <td>${instance.publicIpAddress}</td>
                            <td>${instance.privateIpAddress}</td>
                            <td>
                                <c:forEach var="groupName" items="${instance.groupList}">
                                <span>${groupName} <span>
                                    </c:forEach>
                            </td>
                            <td>${instance.state}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topology.proxyList}">
                    <tr>
                        <td><strong>Proxy</strong></td>
                        <td colspan="7">No instances</td>
                    </tr>
                </c:if>

                <!-- Management -->
                <c:if test="${not empty topology.managementList}">
                    <c:forEach var="instance" items="${topology.managementList}" varStatus="status">
                        <tr>
                            <c:if test="${status.index == 0}">
                                <td rowspan="${fn:length(topology.managementList)}"><strong>Management</strong></td>
                            </c:if>
                            <td>${instance.instanceType}</td>
                            <td>${instance.iaasSpec.cpu}</td>
                            <td>${instance.iaasSpec.memory} GB</td>
                            <td>${instance.publicIpAddress}</td>
                            <td>${instance.privateIpAddress}</td>
                            <td>
                                <c:forEach var="groupName" items="${instance.groupList}">
                                <span>${groupName} <span>
                                    </c:forEach>
                            </td>
                            <td>${instance.state}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topology.managementList}">
                    <tr>
                        <td><strong>Management</strong></td>
                        <td colspan="7">No instances</td>
                    </tr>
                </c:if>

                <!-- Resources -->
                <c:if test="${not empty topology.serviceNodeList}">
                    <c:forEach var="instance" items="${topology.serviceNodeList}" varStatus="status">
                        <tr>
                            <c:if test="${status.index == 0}">
                                <td rowspan="${fn:length(topology.serviceNodeList)}"><strong>Resource</strong></td>
                            </c:if>
                            <td>${instance.instanceType}</td>
                            <td>${instance.iaasSpec.cpu}</td>
                            <td>${instance.iaasSpec.memory} GB</td>
                            <td>${instance.publicIpAddress}</td>
                            <td>${instance.privateIpAddress}</td>
                            <td>
                                <c:forEach var="groupName" items="${instance.groupList}">
                                <span>${groupName} <span>
                                    </c:forEach>
                            </td>
                            <td>${instance.state}</td>
                        </tr>
                    </c:forEach>
                </c:if>
                <c:if test="${empty topology.serviceNodeList}">
                    <tr>
                        <td><strong>Resources</strong></td>
                        <td colspan="7">No instances</td>
                    </tr>
                </c:if>

                </tbody>
            </table>

            <div>
                <%--<div class="pull-left">--%>
                    <%--<a href="javascript:refreshList()" class="btn btn-default"> Add Instance</a>--%>
                    <%--&nbsp;<a href="javascript:refreshList()" class="btn btn-danger outline"> Remove Instance</a>--%>
                <%--</div>--%>
                <div class="pull-right">
                    <%--<a href="javascript:refreshList()" class="btn btn-default"> Stop Cluster</a>--%>
                    <%--&nbsp;--%>
                    <a href="javascript:refreshList()" class="btn btn-danger outline">Destroy Cluster</a>
                </div>
            </div>

        </div>
    </div>
</div>

<%@include file="bottom.jsp" %>