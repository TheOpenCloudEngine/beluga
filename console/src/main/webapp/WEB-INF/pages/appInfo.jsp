<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@include file="top.jsp" %>

<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">Apps</h1>
            </div>
            <div class="sub-title"><a href="javascript:history.back(-1)"><i class="glyphicon glyphicon-arrow-left"></i></a> /oce-processcodi-php</div>
            <hr>
            <div class="col-md-6">
                <dl class="dl-horizontal">
                    <dt>Each CPUs: </dt>
                    <dd>0.5 </dd>
                    <dt>Each Memory: </dt>
                    <dd>50MB</dd>
                    <dt>Total CPUs: </dt>
                    <dd>1.5</dd>
                    <dt>Total Memory: </dt>
                    <dd>150MB</dd>
                    <dt>Version</dt>
                    <dd>2014-09-12 23:28:21</dd>
                </dl>
            </div>
            <div class="col-md-6">
                <dl class="dl-horizontal">
                    <dt>Scales: </dt>
                    <dd>3</dd>
                    <dt>Running</dt>
                    <dd>3</dd>
                    <dt>Healthy</dt>
                    <dd>2</dd>
                    <dt>Unhealthy</dt>
                    <dd>1</dd>
                    <dt>Staged</dt>
                    <dd>0</dd>
                </dl>
            </div>
            <div class="col-md-12">
                <div class="pull-right" style="margin-bottom: 10px;">
                    <a href="javascript:destroyApp('/oce-processcodi-php')" class="btn btn-info">Modify App</a>
                </div>
                <table class="table table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Status</th>
                        <th>Started</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>oce-processcodi-php.d2e904bb-f8ab-11e4-a9cb-22dab5464f33
                        <br>
                        <a href="#" class="app-link">10.132.37.104:31000</a></td>
                        <td><span class="glyphicon glyphicon-ok-sign running-status"></span></td>
                        <td>2015. 5. 12.<br>22:35:59</td>
                        <td>&nbsp;</td>
                    </tr>
                    <tr class="warning">
                        <td>2e72dbf1-2b2a-4204-b628-e8bd160945dd</td>
                        <td><span class="label label-default">Deploying</span></td>
                        <td>2015. 5. 12.<br>22:35:59</td>
                        <td><a href="btn btn-danger"><i class="glyphicon glyphicon-trash text-danger"></i></a></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="col-md-12">
                <a href="javascript:destroyApp('/oce-processcodi-php')" class="btn btn-default">Restart App</a>
                <div class="pull-right">
                    <a href="javascript:destroyApp('/oce-processcodi-php')" class="btn btn-danger outline">Destroy App</a>
                </div>
            </div>
        </div>

    </div>
</div>

<%@include file="bottom.jsp" %>