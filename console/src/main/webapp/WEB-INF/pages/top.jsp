<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="utf-8">
    <title>Garuda Console</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!-- Favicon -->
    <link rel="shortcut icon" href="/resources/favicon.ico">
    <link rel="stylesheet" href="/resources/bootstrap/css/bootstrap.min.css" media="screen">
    <link rel="stylesheet" href="/resources/css/main.css" media="screen">
    <link rel="stylesheet" href="/resources/css/enhance.css" media="screen">
    <script src="/resources/jquery/jquery-1.11.3.min.js"></script>
    <script src="/resources/bootstrap/js/bootstrap.min.js"></script>
</head>
<body>
<div class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a href="#" class="navbar-brand">Garuda</a>
            <button class="navbar-toggle" type="button" data-toggle="collapse" data-target="#navbar-main">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
        </div>
        <div class="navbar-collapse collapse" id="navbar-main">
            <ul class="nav navbar-nav">
                <li>
                    <a href="/a/cluster" class="current">Cluster</a>
                </li>
                <li>
                    <a href="http://master.fastcatsearch.com:8080" target="_pop_marathon_admin">Apps</a>
                </li>
                <%--<li>--%>
                    <%--<a href="/a/monitoring">Monitoring</a>--%>
                <%--</li>--%>
                <%--<li>--%>
                    <%--<a href="https://github.com/TheOpenCloudEngine/garuda/blob/master/README.md" target="_blank">Docs</a>--%>
                <%--</li>--%>
            </ul>

            <ul class="nav navbar-nav navbar-right">
                <%--<li class="dropdown">--%>
                    <%--<a class="dropdown-toggle" data-toggle="dropdown" href="#" id="themes">개발클러스터 <span class="caret"></span></a>--%>
                    <%--<ul class="dropdown-menu" aria-labelledby="themes">--%>
                        <%--<li><a href="#">개발클러스터</a></li>--%>
                        <%--<li><a href="#">운영클러스터</a></li>--%>
                        <%--<li class="divider"></li>--%>
                        <%--<li><a href="/login">New Cluster</a></li>--%>
                    <%--</ul>--%>
                <%--</li>--%>
                <li><a href="/a/settings"><i class="glyphicon glyphicon-cog"></i></a></li>
            </ul>

        </div>
    </div>
    <%--<div class="col-md-2"></div>--%>
</div>