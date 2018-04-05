<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import ="java.util.List" %>
<%@ page import ="com.suko.DAO.business.Parameter" %>
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
				Category > <%=session.getAttribute("categorySelected") %> > <%=session.getAttribute("subCategorySelected") %> 
			</div></br>
			<div id="config">
				<%
				List<List<String>> listCategory = (List<List<String>>)request.getAttribute("listCategory");%>
				<div>
					<ul>
					<%				
					for(List<String> subCatList : listCategory){
						int i=0;
						for(String cat : subCatList){							
							%><li>
							<a href="${pageContext.request.contextPath}/config?category=<%=subCatList.get(0)%>&subCategory=<%=cat%>"> <%=cat %></a>
							</li><%
							if(i==0){%><ul class=child><%}
							if(i==subCatList.size()-1){%></ul><%}
							i++;
						}
					}
					%>
					</ul>
				</div>
				<%List<Parameter> parameters = (List<Parameter>)request.getAttribute("parameters");
				if(parameters!=null && parameters.size()>0){%>
				<div>
					<form method="post" action="config">
					
					<table>
					<%
					for(Parameter parameter : parameters){
						%>
						<tr>
							<td>
							<%=parameter.getName().replace("_", " ")%>
							</td>
							<td>
								<input type="text" id="<%=parameter.getName()%>" 
										   name="<%=parameter.getName()%>" 
										   value="<%=parameter.getValue()%>"
										   size="80" maxlength="100" />
							</td>
							<td>
							<%=parameter.getDescription()%>
							</td>
							<td><a href="<%=application.getContextPath()%>/conifg?delete=<%=parameter.getName()%>">
								<img align="middle" src="delete.png" title="restart">
							</a></td>					
						</tr>
					<%}%>
					</table></br>
						<input type="submit" id="config" value="Save" class="sansLabel" />
					</form>
					<%}else{%>
						<img align="middle" src="logo_suko_grey.png" alt="SuKo"  style="width:300px;height:100px;">
					<%}%>
				</div>
				
			</div>
		</div>
	</body>
</html>