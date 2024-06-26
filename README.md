# Опис роботи
TU Academy освітній сайт з можливістю 
- створення тестів 
- навчальних матеріалів 
- та конференцій 

кожна робота буде оцінена. Наявний календарь з розкладом занять та оцінками, в кінці навчання одного з курсів є можливість отримати сертифікати: 
- за участь
- за гарні результати
- за відмінні результати.

## Перелік основних технологій:
- платформа java spring для швидкого та якісного написання кода,
- база данних MySQL надає широкий спектор функцій що спрошують запити до бд не втрачаюи швидкодії, яка тут і так досить висока
- сховище для файлів amazon s3, надає швидкий доступ для запису, скачування та видалення файлів

## Запуск

Для запуску проекту потрібна версія java мінімум 17, та встановити MySQL, у якому створити базу данних, та створити користувача з правами адміна. Версія зборки використовується maven

Також для запуску потрібно мати акаунт для доступу до в amazon s3 якщо немає, якщо немає можна скористатися нашими ключамим.
В самому сховищі amazon s3 якщо створювати своє сховище доведеться створити такі папки:
- avatars
- banners
- homework
- exam_resources
- education_material_resources
- certificates

Технологія сборки maven

## .env
- в .env файлі змінюємо ACCESS_KEY_S3, BUCKET_S3 та SECRET_KEY_S3 на свої ключи, та назву bucket до сховща amazon s3, можете спробувати наші ключи ACCESS_KEY_S3=AKIAZER4TNZFXX4BRJM2, SECRET_KEY_S3=opJ/hilicQSYl1ChbJsQRMsQSN5ZjwwJHiK8nkN, BUCKET_S3=educatoapp-bucket
- SERVER_GMAIL та SERVER_GMAIL_PASSWORD ставимо почту та ключ доступу для надсилань повідомлень з цего адреса
- DB_USERNAME, DB_PASSWORD, DB_URL вказуємо адресу до вашої бази данних MySQL
- SERVER_PORT вказуємо порт по якому буде працювати сервер, якщо не вставити то буде працювати за замовчуванням по 8080
- CHIEF_TEACHER_EMAIL, CHIEF_TEACHER_PASSWORD, CHIEF_TEACHER_FIRSTNAME, CHIEF_TEACHER_LASTNAME вказуємо данні для створення акаунту адміністратора при запуску сервера, якщо адміністраторів немає
- LINK_EXAM, LINK_RECOVERY_PASSWORD вказати початок силки по якій буде працюввати силка на екзамен та силка на відновлення пароля на фронтенді
- MAX_FILE_SIZE=50MB вказуємо для корректної передачи файлів

Ось візуальний вигляд нашої бази данних

![img.png](img.png)

для запуска проекта на windows виконайте команди у корневій директорії проекту
mvnw clean install
mvnw spring-boot:run

Розробник: Максим Маньківський
