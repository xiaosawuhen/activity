<%@ page language="java" pageEncoding="utf-8" isELIgnored="false"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%@ include file="/common/global.jsp"%>
<%@ include file="/common/meta.jsp" %>
<title>请假申请</title>
<link href="${ctx }/style/style.css" type="text/css" rel="stylesheet">
<script src="${ctx }/js/jquery-1.7.1.js" type="text/javascript"></script>
<script language="javascript" type="text/javascript" src="${ctx }/My97DatePicker/WdatePicker.js"></script>
</head>
<body>
<h1>填写请假单</h1>
<font color="red">${message }</font>
<form:form id="leaveform" action="${ctx}/leave/start" method="post">
	<fieldset>
		<legend><small>请假申请</small></legend>
		<table width="50%">
		<tr>
			<td align="right">请假类型：</td>
			<td>
				<select id="leaveType" name="leaveType">
					<option>公休</option>
					<option>病假</option>
					<option>调休</option>
					<option>事假</option>
					<option>婚假</option>
				</select>
			</td>
		</tr>
		<tr>
			<td align="right">开始时间：</td>
			<td><input type="text" id="startTime" name="startTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"/></td>
		</tr>
		<tr>
			<td align="right">结束时间：</td>
			<td><input type="text" id="endTime" name="endTime" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"/></td>
		</tr>
		<tr>
			<td align="right">请假原因：</td>
			<td>
				<textarea name="reason"></textarea>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td>
				<button type="submit">申请</button>
			</td>
		</tr>
	</table>
	</fieldset>
</form:form>
</html>