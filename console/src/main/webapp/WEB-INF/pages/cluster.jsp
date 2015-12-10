<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<% String menuId = "cluster"; %>
<%@include file="top.jsp" %>
<script>
$(function(){
    $("#deleteClusterButton").on("click", function(){
        $.ajax({
            url: "/clusters/${topology.clusterId}",
            type: "DELETE",
            success: function() {
                location.href = "/";
            },
            error: function(xhr, status, e) {
                alert("cannot delete cluster : " + xhr.responseText);
            }
        })
    });
    $("#modifySlaveSize").on("click", function(){

    });
});

</script>
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
                    <dt>Iaas Type : </dt>
                    <dd>${iaasProvider.type}</dd>
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
                </tbody>
            </table>
        </div>

        <div class="col-md-12">
            <div class="box" >
                <div class="pull-right">
                    <button type="button" class="btn btn-lg btn-primary outline" data-toggle="modal" data-target="#modifySlaveModal"><i class="glyphicon glyphicon-resize-full"></i> Modify Worker Size</button>
                </div>
                <h2>Modify Cluster</h2>
                <p>This will increase or decrease slave worker size in the cluster.</p>
            </div>
        </div>

        <div>&nbsp;</div>
        <div class="col-md-12">
            <div class="box" >
                <div class="pull-right">
                    <button type="button" class="btn btn-lg btn-danger outline" data-toggle="modal" data-target="#deleteModal"><i class="glyphicon glyphicon-trash"></i> Delete Cluster</button>
                </div>
                <h2>Delete Cluster</h2>
                <p>This will terminate running app and permanently delete all instances.</p>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Are you sure?</h4>
            </div>
            <div class="modal-body">
                <p>This will terminate running app and permanently delete all instances.</p>
                <p><strong class="text-danger">Delete cluster "${topology.clusterId}".</strong></p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
                <button type="button" class="btn btn-danger" id="deleteClusterButton">Yes</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div class="modal fade" id="modifySlaveModal" tabindex="-1" role="dialog" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Modify Worker Size</h4>
            </div>
            <div class="modal-body">
                <p>This will resize slave node size, and decrease operation may restart running apps.</p>
                <p><strong class="text-primary">Resize slave worker size in the cluster "${topology.clusterId}".</strong></p>
                <select class="form-control" disabled>
                    <option>1</option>
                    <option>2</option>
                    <option>3</option>
                    <option>4</option>
                    <option>5</option>
                </select>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
                <button type="button" class="btn btn-primary" id="modifySlaveSize" disabled>Yes</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@include file="bottom.jsp" %>