<#ftl encoding="UTF-8">
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вітаємо ${first_name} ${last_name}</title>
    <style>
        #body {
            display: flex;
            flex-direction: column;
            font-family: "Segoe UI", serif;
            align-items: center;
            background-color: #607FF2;
            font-size: 20px;
            height: 100%;
        }

        #div {
            display: block;
            flex-direction: column;
            margin: 10%;
            font-family: "Segoe UI", serif;
            background-color: white;
            padding: 3%;
            border-radius: 20px;
            color: black;
        }

        #a {
            text-decoration: none;
        }
    </style>
</head>
<body>
<div id="body">
    <div id="div">
        <p>Вітаємо, ${first_name}!</p>
        <p>Ось  ${password}</p>
        <p>Одразу ж після входу ви можете змінити пароль на будь-який інший</p>
        <p>З найкращими побажаннями</br>     <i>TetradUnity</i></p>
    </div>
</div>
</body>
</html>