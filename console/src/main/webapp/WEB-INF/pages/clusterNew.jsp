<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<% String menuId = "cluster"; %>
<%@include file="top.jsp" %>
<script src="/resources/js/spin.min.js"></script>
<script>
    var definitionId;
    function createCluster(defId) {
        $("#createClusterModal").modal('show');
        definitionId = defId;
    }
    $(function(){
       $("#createClusterButton").on("click", function() {
           clusterId = $("#clusterId").val();
           if(clusterId == "") {
               return;
           }

           $("#createClusterModal").modal('hide');
           showModalSpinner();

           $.ajax({
               url: "/clusters",
               type: "POST",
               processData: false,
               data: JSON.stringify({
                   id:clusterId,
                   definition:definitionId,
                   domain:domainName,
                   await:true
               }),
               success: function() {
                   hideModalSpinner();
                   alert("Create Cluster '"+clusterId+"' Success!");
                   location.href = "/clusters/" + clusterId;
               },
               error: function(xhr) {
                   hideModalSpinner();
                   console.log(xhr.responseText);
                   alert("Error : " + xhr.responseText);
               }

           });
       });
    });

</script>
<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">Create Cluster</h1>
            </div>

            <c:forEach var="definition" items="${definitions}">
                <h2><a href="javascript:createCluster('${definition.id}')" class="btn btn-lg btn-primary outline">${definition.id}</a></h2>
                <table class="table table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>Role</th>
                        <th>IaaS</th>
                        <th>Instance Type</th>
                        <th>CPU</th>
                        <th>Memory</th>
                        <th>Disk</th>
                        <th>Instance Size</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="role" items="${definition.roleList}">
                    <tr>
                        <td>${role.role}</td>
                        <td>${definition.providerType}</td>
                        <td>${role.instanceType}</td>
                        <td>${role.iaasSpec.cpu}</td>
                        <td>${role.iaasSpec.memory} GB</td>
                        <td>${role.diskSize} GB</td>
                        <td>${role.defaultSize}</td>
                    </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:forEach>
        </div>
    </div>
</div>

<div class="modal fade" id="createClusterModal" tabindex="-1" role="dialog" >
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Create Cluster</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="clusterId" class="col-sm-3 control-label">Cluster ID</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="clusterId">
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="domainName" class="col-sm-3 control-label">Domain</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="domainName" placeholder="mydomain.com">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" class="btn btn-primary outline" id="createClusterButton">Create</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<%@include file="bottom.jsp" %>