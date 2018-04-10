<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="/common/global.jsp"%>
<%@ include file="/common/meta.jsp" %>
<title>待办任务列表</title>
</head>
<body>
<font color="red">${message }</font>
<h1>待办任务列表</h1>
<table width="100%" border="1" style="border-collapse: collapse;border-style: solid;border-color: maroon;">
<thead>
	<tr align="center">
		<th>假种</th>
		<th>申请人</th>
		<th>申请时间</th>
		<th>开始时间</th>
		<th>结束时间</th>
		<th>当前节点</th>
		<th>任务创建时间</th>
		<th>流程状态</th>
		<th>操作</th>
	</tr>
</thead>
<tbody>
	<c:forEach items="${tasklist }" var="leave">
		<c:set var="task" value="${leave.task }" />
		<c:set var="pi" value="${leave.processInstance }" />
		<tr id="${leave.id }" align="center">
			<td>${leave.leaveType }</td>
			<td>${leave.userId }</td>
			<td><fmt:formatDate value="${leave.applyTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td><fmt:formatDate value="${leave.startTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td><fmt:formatDate value="${leave.endTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>
				<a class="trace" href='${ctx }/workflow/view/${task.executionId}/page/${task.processInstanceId}' title="点击查看流程图">${task.name }</a>
			</td>
			<td><fmt:formatDate value="${task.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
			<td>${pi.suspended ? "已挂起" : "正常" }；<b title='流程版本号'>V: ${leave.processDefinition.version }</b></td>
			<td>
				<c:if test="${empty task.assignee }">
					<a class="claim" href="${ctx }/leave/task/${task.id}/claim" target="main">签收</a>
				</c:if>
				<c:if test="${not empty task.assignee }">
					<a href="${ctx }/leave/detail/leave/${task.id}">办理</a>
				</c:if>
			</td>
		</tr>
	</c:forEach>
</tbody>
</table>
</html>