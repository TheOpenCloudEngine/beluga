<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@include file="top.jsp" %>

<div class="container" id="content">
    <div class="row">
        <div class="col-md-6 col-sm-6 col-xs-12">

            <div class="page-header">
                <h1 id="tables">Cluster</h1>
                <p class="lead">Add cluster</p>
            </div>

            <div>
                <form>
                    <div class="form-group">
                        <label for="clusterID" class="control-label">Cluster ID</label>
                        <input type="text" class="form-control" id="clusterID" placeholder="Cluster ID">
                    </div>
                    <div class="form-group">
                        <label for="clusterName" class="control-label">Cluster Name</label>
                        <input type="text" class="form-control" id="clusterName" placeholder="Cluster Name">
                    </div>
                    <div class="form-group">
                        <label for="iaasType" class="control-label">IaaS Type</label>

                        <select class="form-control" id="iaasType">
                            <option>OpenStack</option>
                            <option>Amazon Web Service</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="region" class="control-label">Region</label>

                        <select class="form-control" id="region">
                            <option>Local</option>
                            <option>Tokyo</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="intanceType" class="control-label">Instance Type</label>
                        <select class="form-control" id="intanceType">
                            <option>1GB/1CPU 30GB SSD</option>
                            <option>2GB/2CPU 60GB SSD</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="workerSize" class="control-label">Worker Size</label>
                        <input type="number" class="form-control" name="workerSize" id="workerSize" />
                    </div>

                    <button type="button" class="btn btn-primary">Create</button>
                </form>
            </div>


        </div>

    </div>
</div>

<%@include file="bottom.jsp" %>