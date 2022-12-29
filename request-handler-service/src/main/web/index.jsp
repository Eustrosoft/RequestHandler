<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login page</title>
    <link rel="stylesheet" href="css/main.css">
</head>
<body>
<div class="mainContainer">
    <div class="containerHeader">
        <h1>Welcome to Request Dispatcher!</h1>
    </div>
    <div class="containerBody">
        <div class="containerFormHeader">
            <h3>Login page</h3>
        </div>
        <form action="login" method="post" class="containerForm">
            <label>Login: </label>
            <input type="text" placeholder="login" name="username">
            <br/>
            <label>Password: </label>
            <input type="password" placeholder="password" name="password">
            <input type="submit" content="Login!" id="sendLoginData">
        </form>
    </div>
</div>
</body>
</html>
