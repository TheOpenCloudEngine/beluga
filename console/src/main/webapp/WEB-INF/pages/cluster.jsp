<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@include file="top.jsp" %>

<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">Cluster</h1>
            </div>
            <hr>

            <div class="col-md-6">
                <dl class="dl-horizontal">
                    <dt>ID: </dt>
                    <dd>dev-cluster </dd>
                    <dt>Domain: </dt>
                    <dd>dev.mydomain.com </dd>
                    <dt>Iaas Provider: </dt>
                    <dd>ec2 </dd>
                    <dt>Iaas Definition : </dt>
                    <dd>AWS EC2 Asia Pacific</dd>
                    <dt>Iaas Profile : </dt>
                    <dd>ec2-dev</dd>
                </dl>
            </div>
            <div class="col-md-6">
                <dl class="dl-horizontal">
                    <dt>Key Pair : </dt>
                    <dd>aws-garuda</dd>
                    <dt>State: </dt>
                    <dd>Started </dd>
                    <dt>Started : </dt>
                    <dd>2015.06.29 04:01:45</dd>
                    <dt>Stopped : </dt>
                    <dd></dd>
                    <dt>Created : </dt>
                    <dd>2015.06.29 04:01:45</dd>
                </dl>
            </div>
            <div>
                <a href="javascript:refreshList()" class=""><i class="glyphicon glyphicon-refresh"></i> Refresh</a>
            </div>
            <table class="table table-hover table-bordered">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Instance Type</th>
                    <th>IP Address</th>
                    <th>Memory</th>
                    <th>Disk</th>
                    <th>Group</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>proxy</td>
                    <td>t2.micro</td>
                    <td>192.168.0.15</td>
                    <td>2GB</td>
                    <td>10GB</td>
                    <td>default</td>
                    <td><span class="glyphicon glyphicon-ok-sign running-status"></span></td>
                </tr>
                <tr>
                    <td>management</td>
                    <td>t2.micro</td>
                    <td>192.168.0.16</td>
                    <td>2GB</td>
                    <td>10GB</td>
                    <td>default</td>
                    <td><span class="glyphicon glyphicon-ok-sign running-status"></span></td>
                </tr>
                <tr>
                    <td>master</td>
                    <td>t2.micro</td>
                    <td>192.168.0.20</td>
                    <td>2GB</td>
                    <td>10GB</td>
                    <td>default</td>
                    <td><span class="label label-default">Pending</span></td>
                </tr>
                <tr>
                    <td>worker</td>
                    <td>t2.micro</td>
                    <td>192.168.0.25</td>
                    <td>4GB</td>
                    <td>10GB</td>
                    <td>default</td>
                    <td><span class="glyphicon glyphicon-minus-sign stop-status"></span></td>
                </tr>
                <tr>
                    <td>service-db</td>
                    <td>t2.micro</td>
                    <td>192.168.0.26</td>
                    <td>2GB</td>
                    <td>10GB</td>
                    <td>default</td>
                    <td><span class="glyphicon glyphicon-ok-sign running-status"></span></td>
                </tr>
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