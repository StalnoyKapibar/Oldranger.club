# Oldranger.club
## Запуск проекта (бэк)

###Прежде всего для развертывания backend можно использовать Docker
для этого в папке проекта запускаем `docker-compose up -d`

После этого поднимется окружение, состоящее из Mysql 8.0.18, elasticsearch, kibana, logstash

Пароль для базы и имя пользователя test/test

Пароль для входа kibana - elastic/changeme

Также поднимается SonarQube на порту 9001

Логип и пароль для входа admin/admin

Для загрузки проекта для анализа из папки проекта запустить `mvn sonar:sonar`

При данном способе предварительно нужно установить только JDK 8 и IDEA

###Длинный способ ниже

1. Устанавливаем JDK 8  
    [Идем на страницу Oracle](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)  
    Кликаем на Accept License Agreement и выбираем версию под свою ОС. Для Win10x64 нужный файл будет иметь вид типа jdk-8u231-windows-x64.exe  
    Загружаем файл и устанавливаем все с дефолтными настройками (для загрузки может потребовать создать оракл аккаунт).    
2. Устанавливаем MySQL8  
    [Скачиваем инсталлятор](https://dev.mysql.com/get/Downloads/MySQLInstaller/mysql-installer-community-8.0.18.0.msi)  
    Выбираем setup type - developer default (с воркбенчем и т.п.).   
    При установке, когда попросит задать пароль для root пользователя, ставим пароль - root (или потом надо будет поменять пароль в datasource.properties на свой).    
    Подробнее процесс установки расписан, например, [в этой статье](https://info-comp.ru/install-mysql-on-windows-10).    
    Запускаем MySQL Workbench и подключаемся к локальному серверу MySQL.    
    Создаем новую Schema с именем jm_oldc (Сайдбар Schemas(слева) - нажимаем ПКМ - Create Schema - Apply).    
3. Устанавливаем [IntelliJ Idea Ultimate](https://www.jetbrains.com/ru-ru/idea/download/#section=windows) 
4. Подготовка к запуску  
    Запускаем IntelliJ Idea   
    Выбираем Get from Version Control - Git  
    Клонируем репозиторий https://github.com/StalnoyKapibar/Oldranger.club  
    Проект должен начать скачиваться и подтягивать зависимости или в правом нижнем углу должна появиться плашка на которой нужно выбрать Enable auto import, и тогда начнется загрузка зависимостей.  
    Пуллим себе ветку dev: VCS - Git - Pull - origin/dev (или через терминал) и снова ждем, когда подтянутся новые зависимости.  
    Идем в File - Settings - Plugins - Набираем Lombok - Жмем install и перезагружаем IDE  
    В самом проекте идем в папку config (src/main/resources/config), где лежат 5 файлов типа aplication.properties.sample    
    Копируем эти файлы и убираем расширение .sample  
    В итоге в папке config должны лежать 10 файлов:
    application.properties  
    application.properties.sample  
    datasource.properties  
    datasource.properties.sample  
    ....и.т.д.  
5. Теперь можно запускать проект (Shift+F10 или через иконку зеленого треугольника справа вверху)  
    
## Запуск проекта (фронт)
1. Устанавливаем [nodejs](https://nodejs.org/en/)  
2. Клонируем репозиторий https://github.com/alex-karo/oldranger-front  
3. В корне проекта (ветка dev) устанавливаем зависимости npm install  
4. Запускаем проект npm start  
## Документация по API  
1. Swagger 
 
   При запущенном проекте, доступна по адресу http://localhost:8888/api/swagger-ui.html  
   
2. Часть документации в формате текста будет лежать в папке /docs 
3. Схемы:  
    [Чат](https://docs.google.com/drawings/d/1e7nZ5BALdJmZghc7dPhhMRx-0zAa0k-xncfr51crP3Q/edit)  
    [Регистрация и пароли](https://docs.google.com/drawings/d/1tFSkJmKRnUHsNjP1pTRoO5NkoIfaxB31Bwa4VzRIJkk/edit)  
    [Рассылки](https://docs.google.com/drawings/d/117F1DHL4LERX4tS_f5026e90QS-kfQ0ISHs3owov5E8/edit)  
    [Альбомы](https://docs.google.com/drawings/d/13uOpd_mbEMlvYg11OusrfMVd1SPdISUuptv-v3VSlqo/edit)  
    [Статьи](https://docs.google.com/drawings/d/1aI1VuiHNHp4bytGOc7xF9_Haeq8ZbSAnhHaGokRCvpA/edit)  
    [Профиль](https://docs.google.com/drawings/d/1XoBj-VYsJPJpCAEKbCYViD3TIcF-OxGCHhBr3pH27CY/edit)  
    [Топики и т.д.](https://docs.google.com/drawings/d/1TsszY_Gc_AhhJLatqoo-8-lqvEXgW2MkULSKKC5Kol8/edit)  
    [Спредшит с рестами](https://docs.google.com/spreadsheets/d/1yZiFGW12mHiobuNovcvFW0pMsVYBNWZuywedkbQJWTU/edit#gid=0)  
    
4. Часть функционала портала была ранее реализована с помощью обычных контроллеров (src/main/java/controller/), шаблонизатора thymeleaf  и jquery.   
   Большинство контроллеров помечены как Depricated, но их все еще можно использовать в качестве примеров.   
   Так, к примеру, чтобы посмотреть как был реализован общий чат, необходимо зайти в controller/GroupChatController и найти методы помеченные @GetMapping.  
   ```
   @GetMapping("/chat")
       public String getChatPage() {
           return "chat/chat";
       }
   ```
   В скобках после GetMapping указан относительный путь, замапленный с данным методом. T.е. для того, чтобы зайти на страницу с чатом необходимо перейти по URL: http://localhost:8888/chat.  
   А в return метода указан путь по которому можно просмотреть html, отдаваемый сервером (лежит в src/main/resources/templates/chat/chat.html)  
   В случае, если бы над классом контроллера был бы указан какой-то RequestMapping, например @RequestMapping("/test"), то полный путь бы складывался из RequestMapping + GetMapping т.е http://localhost:8888/test/chat  
## Просмотр логов через Kibana
### Установка

Прежде всего можно использовать Docker

Для этого в папке проекта запускаем `docker-compose up -d`

После этого поднимется окружение, состоящее из Mysql 8.0.18, elasticsearch, kibana, logstash

Пароль для базы и имя пользователя test/test

Пароль для входа kibana - elastic/changeme

Другой способ ниже...

1. _Скачиваем elasticsearch, logstash, kibana_  

   Для винды:  
    * [Elasticsearch](https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.5.1-windows-x86_64.zip)
    * [Logstash](https://www.elastic.co/downloads/logstash)
    * [Kibana](https://www.elastic.co/downloads/kibana)
2. _Разархивируем все это, например в C:/Elastic_
3. _Добавляем конфигурацию logstash:_  

    Заходим в папку куда разархивировали logstash, идем в папку config и создаем там файл logback.conf  
    Добавляем в него: 
     
    ```
    input { 
        file { 
            path => "C:/Users/{Папка пользователя}/logs/*.log" codec => "json" type => "logback" 
            }
        }
   
   output { 
        if [type]=="logback" { 
            elasticsearch { 
                hosts => [ "localhost:9200" ] index => "logback-%{+YYYY.MM.dd}" }
            }
        }
    ```

### Запуск 
Для работы ELK необходимо удостовериться, что в системе установлена JDK и путь к папке пользователя не содержит кириллицы (логи сохраняются в C:/Users/{Папка пользователя}/logs/) 
1.  
    Запускаем elasticsearch от имени админа bin/elasticsearch.exe  
    Запускаем logstash :  
    Открываем PowerShell от админа и запускаем logstash с созданной конфигурацией  
     
    ```
    cd C:\Elastic\logstash-7.5.1\
    .\bin\logstash.bat -f .\config\logback.conf
    ```
   
   Запускаем kibana bin/kibana.bat  
   
2. Теперь кибана доступна по адресу http://localhost:5601/  
3. Идем в kibana  
   Слева в меню (самая последняя) — Management — там выбираем Index Patterns — и там Create index pattern.  
   Пишем имя нашего индекса logback-* и он должен появиться в списке. Выбираем его.   
   Жмем Next step — выбираем в списке timestamp — Ok.  
   Индекс создан.  
4. Идем в меню в Discover (самый верхний пункт в меню слева) — там выбираем слева в окошке этот индекс и выбираем поля которые мы хотим видеть при выводе и жмем на кнопку Refresh.  
5. Теперь мы увидим здесь все наши логи.  