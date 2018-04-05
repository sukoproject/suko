<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/form.css" />
    <link rel="icon" href="icon.gif" />
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>SuKo</title>
	</head>
	<body>
	<div id="bandeau">
	Prototype v0.4 
	<a href="${pageContext.request.contextPath}/config">Parameters</a></div>
	<div id="contenu">
		<div id="question">
		<img src="logo_suko.png" alt="SuKo"  style="width:400px;height:160px;">
		<form method="post" action="result">
                <input type="text" id="question" name="question" value="${param.question}" size="60" maxlength="60" />
                <input type="submit" id="ask" value="Ask" class="sansLabel" />
        </form>
        </div>
		 <%
		 //String attribut = (String) request.getAttribute("result");
		 //out.println( attribut );
		  %>
	</div>
	<div id="piedpage">Nicoas Zangari, Jeremy Lesaint</div>
	</body>
</html>