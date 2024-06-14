<#ftl encoding="UTF-8">
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вітаємо ${first_name} ${last_name} на ${subject_title}</title>
    <style>
        body {
        font-family: "Segoe UI", serif;
        color: #333333;
        margin: 0;
        padding: 0;
        width: 100%;
        -webkit-text-size-adjust: none;
        -ms-text-size-adjust: none;
        }

        .card {
        background: #fafafa;
        border-radius: 1.5em;
        padding: 2em 3em;
        max-width: 600px;
        margin: 2em auto;
        text-align: center;
        }

        .card h1 {
        font-size: 2.5em;
        color: #333333;
        margin-bottom: 0.5em;
        }

        .card p {
        font-size: 1.1em;
        color: #555555;
        margin: 1em 0;
        }

        .card a {
        color: #607FF2;
        text-decoration: none;
        font-weight: bold;
        }

        .card a:hover {
        text-decoration: underline;
        }

        .card i {
        font-style: italic;
        color: #999999;
        }
    </style>
</head>
<body>
<div id="body">
    <div id="div">
        <p>Вітаємо, ${first_name}!</p>
        <p>Ось ваш пароль ${password}</p>
        <p>Одразу ж після входу ви можете змінити пароль на будь-який інший</p>
        <p>З найкращими побажаннями</br>     <i>TetradUnity</i></p>
    </div>
</div>
</body>
</html>