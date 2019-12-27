# Oldranger.club

## Логирование
### Установка
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

4. _Запуск_  

    Запускаем elasticsearch от имени админа bin/elasticsearch.exe  
    Запускаем logstash :  
    Открываем PowerShell от админа и запускаем logstash с созданной конфигурацией  
     
    ```
    cd C:\Elastic\logstash-7.5.1\
   .\bin\logstash.bat -f .\config\logback.conf
    ```
   
   Запускаем kibana bin/kibana.bat  
   
5. Теперь кибана доступна по адресу http://localhost:5601/  




