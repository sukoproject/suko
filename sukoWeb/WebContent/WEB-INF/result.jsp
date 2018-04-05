<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import ="com.suko.DAO.business.Person" %>
<%@ page import ="com.suko.DAO.business.Word" %>
<%@ page import ="java.util.List" %>
<%@ page import ="java.util.Map.Entry" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/form.css" />
    <link rel="icon" href="icon.gif" />
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>SuKo</title>
	</head>
	<body>
		<div id="resultframe">
			<div id="resultQuestion">
				 <%=session.getAttribute("question") %>
			</div>
		
			<div id="results">
					<div class="keywords">
					<%
					List<String> listKnowledges =(List<String>) request.getAttribute("listknowledges");
					for(String knowledge : listKnowledges){%>
							<div class="keyword">
	        					<%=knowledge%>
	        					<a href="<%=application.getContextPath()%>/result?remove=<%=knowledge%>">
								<img align="middle" src="delete.png" title="ignore">
								</a>
	        				</div>
					<%}%>
							<div class="keyword">
			        			<form method="post" action="result">
						                <input type="text" id="addSkill" name="addSkill" value="" size="10" maxlength="10" />
						                <input type="submit" id="addSkillSubmit" value="+" />
						        </form>
			        		</div>
					</div>
					<table>			
					<tr>	
					<%
					List<Person> listPerson =(List<Person>) request.getAttribute("listPerson");
					for(Person pMatch : listPerson){%>
						<td>
							<div class="itemResult">
	        					<%=pMatch.getEmail()+"("+pMatch.getRate()+")"%>
	        					<br>
	        					<table>
	        					<%
	        					for(Entry<String,Integer> p: pMatch.getSkills().entrySet()){
	        						%>
	        						<tr>
	        							<td><%=p.getKey()%></td><td><%=p.getValue()%></td>
	        						</tr>
	        						<%
	        					}%>
	        					</table>
	        				</div>
	        				<br/><br/>
	        			</td>
					<%}%>
					<tr>
				</table>
			</div>
		</div>
	</body>
</html>