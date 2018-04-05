<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import ="java.util.List" %>
<%@page import ="com.suko.DAO.business.Parameter" %>
<%@page import ="com.suko.service.dowser.DowserBatch" %>
<%@page import="com.suko.DAO.business.Document"%>
<%@page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/form.css" />
    <link rel="icon" href="icon.gif" />

    <%response.setIntHeader("Refresh", 5);%>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>SuKo</title>
	</head>
	<body>
		<div id="resultframe">
			<div id="resultQuestion">
				Batch
			</div></br>
			<div id="config">
				<%
				List<DowserBatch> listBatch = (List<DowserBatch>)request.getAttribute("batchList");			
				if(listBatch!=null){
					%><table>
						  <tr>
						  	<td width="13%"><b>Name				</b></td>
						  	<td width="13%"><b>Started at			</b></td>
						  	<td width="13%"><b>Progress			</b></td>
						  	<td width="13%"><b>Active			</b></td>
						  	<td width="13%"><b>Ended at			</b></td>
						  	<td width="13%"><b>Next Run Date	</b></td>
						  	<td width="3%"></td>
						  	<td width="3%"></td>
						  	<td width="3%"></td>
					<%
					SimpleDateFormat sdf = new SimpleDateFormat(Document.DATE_FORMAT);
					for(DowserBatch batch : listBatch){
						String startDate="";
						String endDate="";
						String nextRunDate ="";
						if(batch!=null){
							if(batch.getStartDate()!=null){
								startDate= sdf.format(batch.getStartDate());
							}
							if(batch.getEndate()!=null){
								endDate=sdf.format(batch.getEndate());
							}
							if(batch.getNextRunDate()!=null){
								nextRunDate=sdf.format(batch.getNextRunDate());
							}
							%><tr>
								<td><%=batch.getThreadNamePrefix()%></td>
								<td><%=startDate%></td>
								<td><%=batch.getProgress()%></td>
								<td><%=batch.getActiveCount()%></td>
								<td><%=endDate%></td>
								<td><%=nextRunDate%></td>
								<td><img align="middle" src="log.png" title="<%=batch.getLog()%>"></td>
								<td><a href="<%=application.getContextPath().replace("sukoBatch", "sukoWeb")%>/config?category=Batch&subCategory=<%=batch.getThreadNamePrefix()%>">
									<img align="middle" src="config.png" title="Config">
								</a></td>
								<td><a href="<%=application.getContextPath()%>/batch?restart=<%=batch.getThreadNamePrefix()%>">
									<img align="middle" src="restart.png" title="restart">
								</a></td>
							</tr><%
						}
					}
					%></table><%
				}
				%>
			</div>
		</div>
	</body>
</html>