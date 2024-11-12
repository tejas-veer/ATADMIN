<%--
  Created by IntelliJ IDEA.
  User: shubham-ar
  Date: 23/10/17
  Time: 6:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Primary Redirect</title>
</head>
<body>
<%
    response.setStatus(response.SC_TEMPORARY_REDIRECT);
    response.setHeader("Location", "web/");
%>
</body>
</html>
