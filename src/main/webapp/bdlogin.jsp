<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
用户名:<input type="text" id="username"/>
密码：<input type="password" id="password"/>
验证码：<input type="text" id="code"/>
<img id="codeImg" alt="加载中,点击刷新" src="https://wmpass.baidu.com/wmpass/openservice/imgcaptcha?token=AH0aHEd0AStWVhwNQwRBQUFBQUFBAVhvMixSc355U19gC00AFBQUFBQXEKbER1aV4RMVISWylodzUrejUEamYvQUFBQUFBQUE8WS42LQ0XN34hI0IWDSAnPXI5GRshEKjoCgCVjwRyUB&t=1492088743894&color=3c78d8">
<input type="button" id="loginButton" value="登录"/>
</body>
<script src="js/jquery-2.2.4.js" charset="utf-8"></script>
<script type="text/javascript" src="js/bdlogin.js"></script>
</html>