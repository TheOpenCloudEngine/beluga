<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>
<%@include file="top.jsp" %>

<div class="container" id="content">
    <div class="row">
        <div class="col-md-12">

            <div class="page-header">
                <h1 id="tables">Monitoring</h1>
            </div>

            <!--
             TOOD : cAdvisor의 데이터를 받아와서 google chart로 뿌려준다.
             url : http://52.69.141.5:8080/api/v1.2/docker/cadvisor
            -->
            <div class="tab-pane fade active in" id="overview">

                <!-- 차트삽입-->
            </div>
            <div class="tab-pane fade" id="cluster1">

                <!-- 차트삽입-->
            </div>
            <div class="tab-pane fade" id="cluster2">

                <!-- 차트삽입-->
            </div>
        </div>
    </div>
</div>

<%@include file="bottom.jsp" %>