<#ftl encoding="UTF-8">
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Вітаємо ${first_name}</title>
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
            <p>З радістю повідомляємо, що ви завершили навчання на предметі ${subject_title}.</p>
            <p>Нагадуємо що ви можете переглянути у вкладці Сертифікати, ваш новий сертифікат.</p>
            <p>Також ви завжди можете спробувати себе на нових наших предметах</p>
            <p>З найкращими побажаннями</br>     <i>TetradUnity</i></p>
    </div>
</body>
</html>