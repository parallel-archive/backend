# README #

OSA Backend Project

### Run project in Docker ###

* Build project: 

``` 
mvn package 
```

* Docker build: 

```
docker build --tag=osa-backend:latest .
```
If you see a permission error: 

```
sudo chmod -R 777 docker-data/
```

* Docker run: 

```
docker-compose up
```

### Import and run project in STS ###

* Install STS: [Spring Tools Suite](https://spring.io/tools/sts)

* Start STS and create an empty workspace

* Import project as 'Existing Maven Projects'.

* Configure environment variables in STS -> Run -> Run Configurations -> Spring Boot App : osa-backend - OsaBackendApplication -> Arguments -> VM arguments:

```
-DDATASOURCE_URL=<datasource_url> -DDATASOURCE_USERNAME=<datasource_username> -DDATASOURCE_PASSWORD=<datasource_password> -DMAIL_HOST=<mail_host> -DMAIL_USERNAME=<mail_username> -DMAIL_PASSWORD=<mail_password> -DAWS_ACCESS_KEY=<aws_access_key> -DAWS_SECRET_KEY=<aws_secret_key> -DAWS_S3_REGION=<aws_s3_region> -DAWS_S3_BUCKET=<aws_s3_bucket> -DELASTICSEARCH_REST_URIS=<elasticsearch_rest_uris> -DELASTICSEARCH_USERNAME=<elasticsearch_username> -DELASTICSEARCH_PASSWORD=<elasticsearch_password> -DIPFS_UPLOAD_URL=<ipfs_upload_url> -DIPFS_API_PASSWORD=<ipfs_api_password> -DOSA_BASE_URL=<osa_base_url> -DOSA_APP_BASE_URL=<osa_app_base_url> -DUMAMI_DATA_WEBSITE_ID=<umami_data_website_id>"

```


* To start the app: Find `OsaBackendApplication.java` in Package Explorer and Right click -> Run As -> Spring Boot App.

* See the available endpoints in the API documentation.


### Build and run the App in CLI ###

* Build Project in CLI : 

```
mvn clean compile
```

* Run APP in CLI : 

```
mvn spring-boot:run -Dspring-boot.run.arguments="--DATASOURCE_URL=<datasource_url> --DATASOURCE_USERNAME=<datasource_username> --DATASOURCE_PASSWORD=<datasource_password> --MAIL_HOST=<mail_host> --MAIL_USERNAME=<mail_username> --MAIL_PASSWORD=<mail_password> --AWS_ACCESS_KEY=<aws_access_key> --AWS_SECRET_KEY=<aws_secret_key> --AWS_S3_REGION=<aws_s3_region> --AWS_S3_BUCKET=<aws_s3_bucket> --ELASTICSEARCH_REST_URIS=<elasticsearch_rest_uris> --ELASTICSEARCH_USERNAME=<elasticsearch_username> --ELASTICSEARCH_PASSWORD=<elasticsearch_password> --IPFS_UPLOAD_URL=<ipfs_upload_url> --IPFS_API_PASSWORD=<ipfs_api_password> --OSA_BASE_URL=<osa_base_url> --OSA_APP_BASE_URL=<osa_app_base_url> --UMAMI_DATA_WEBSITE_ID=<umami_data_website_id>"
```

* See the available endpoints in the API documentation.


### APIs, templates and Login ###

* APIs : localhost:8080/api/…

* Login : localhost:8080/login

* Login redirects to /publicarchives

* Templates are in src/main/resources/templates


### OSA Image names ###

When an image is uploaded the file name is saved in two fields in the Image table in the database:
* name : The image name displayed on the page is loaded from this field. It gets updated if users edit the image name.
* image url fields (url, active_url, thumbnail_url, active_thumbnail_url): On image upload the generated key contains the original image name. However if the image name is edited the url does not change.


### Delete Images Task Configuration ###

Marked images are deleted by a scheduled task. Delete images task runs with a configured frequency which can be set via the property: scheduled-tasks.delete-images.fixed-delay

The property can be set to: 
* miliseconds (e.g.: 7200000 = 2 hrs)
* [ISO Duration format](https://www.digi.com/resources/documentation/digidocs/90001437-13/reference/r_iso_8601_duration_format.htm) (e.g.: PT02H = 2hrs) 


### Publish Document Tasks ###

Document publish flow consists of multiple separate scheduled tasks.

Each task's fixed delay can be configured in two different formats:
* miliseconds (e.g.: 7200000 = 2 hrs)
* [ISO Duration format](https://www.digi.com/resources/documentation/digidocs/90001437-13/reference/r_iso_8601_duration_format.htm) (e.g.: PT02H = 2hrs) 

Steps:

0. Generate Document OCR  
	* Initial delay property: scheduled-tasks.publish-documents.generate-ocr.initial-delay  
	* Fixed delay property: scheduled-tasks.publish-documents.generate-ocr.fixed-delay

1. Generate Document Hash  
	* Initial delay property: scheduled-tasks.publish-documents.generate-hash.initial-delay  
   	* Fixed delay property: scheduled-tasks.publish-documents.generate-hash.fixed-delay  

2. Generate Document PDF and upload to S3  
	* Initial delay property: scheduled-tasks.publish-documents.generate-pdf.initial-delay  
   	* Fixed delay property: scheduled-tasks.publish-documents.generate-pdf.fixed-delay  

3. Upload Published Document PDF to IPFS  
	* Initial delay property: scheduled-tasks.publish-documents.upload-to-ipfs.initial-delay  
  	* Fixed delay property: scheduled-tasks.publish-documents.upload-to-ipfs.fixed-delay  

4. Create and upload Published Document thumbnail to S3  
	* Initial delay property: scheduled-tasks.publish-documents.create-pdf-thumbnail.initial-delay  
	* Fixed delay property: scheduled-tasks.publish-documents.create-pdf-thumbnail.fixed-delay  

5. Delete original Document  
	* Initial delay property: scheduled-tasks.publish-documents.delete-original-document.initial-delay  
    * Fixed delay property: scheduled-tasks.publish-documents.delete-original-document.fixed-delay  

6. Index Published Document Data in Elasticsearch  
	* Initial delay property: scheduled-tasks.publish-documents.index-published-document.initial-delay  
    * Fixed delay property: scheduled-tasks.publish-documents.index-published-document.fixed-delay  

7. Validate and set Published Document status to Published  
	* Initial delay property: scheduled-tasks.publish-documents.publish-document.initial-delay  
    * Fixed delay property: scheduled-tasks.publish-documents.publish-document.fixed-delay  



### OCR setup ###

Download the Google Vision OCR service account JSON key file and set environment variable 

```
GOOGLE_APPLICATION_CREDENTIALS=<ocr_json_key_file_path>
```
  * in STS: STS -> Run -> Run Configurations -> Spring Boot App : osa-backend - OsaBackendApplication -> Environment
  * OR in command line: 

```
export GOOGLE_APPLICATION_CREDENTIALS=<ocr_json_key_file_path>
```


### OCR setup on EC2 ###

1. Copy json key file to EC2 home 

2. Create setenv.sh file under tomcat bin/ and set GOOGLE_APPLICATION_CREDENTIALS environment variable. Current setenv.sh:

```
export GOOGLE_APPLICATION_CREDENTIALS="/home/ubuntu/google-service-account-key/osa-pa-335315-b82b0dce470b.json"
```


### OCR upload limitations ###

* Generating OCR texts from document images is limited for each user (limit/user/hour). This limit can be set with the property:

``` 
osa.ocr.limit
```

* Images are uploaded to the OCR API in batches to minimize request number. Request payload size should be less than 41943040 bytes. As the current image size limit is set to 10MB, maximum 4 images are loaded in a batch. The batch size can be set with the property:

```
osa.ocr.upload.batch.limit
```



### Configure anonymous user ###
Anonymous user is used when a published document's original creator is deleted. These documents are assigned to the anonymous user.

1. Save an anonymous user in the database 

```
insert into my_shoe_box(name) values ("myshoebox");
insert into user (email, enabled, password, my_shoe_box_id) values ("anonymous", 0, "anonymous", <my_shoe_box_id>);
```

2. Set anonymous user id in application.yml  

```
osa:
	user:
    	anonymous: <user_id>
```

### Configure database to reset OCR page counter table (MariaDB) ###

* Switch on event scheduler

Make sure you added the following line to your MariaDB configuration, e.g.: /etc/mysql/mariadb.conf.d/50-server.cnf.

```
event_scheduler=ON
```

* You should see the event scheduler in the process list

```
show processlist; 
```

* If event scheduler is not present in the processlist, you can check its status

```
SHOW GLOBAL VARIABLES LIKE 'event_scheduler';
```


* Create an event that resets ocr counter table in every 1 hour

```
CREATE EVENT IF NOT EXISTS ocr_rate_limit_reset 
  ON SCHEDULE EVERY 60 MINUTE DO 
   TRUNCATE ocr_rate_limit;
```

* You can check the event details

```
show events;
```


### Configure database to save Document publish start and end timestamp (MariaDB) ###

You might need to save when a Document publish started and ended e.g. for testing purposes.

* Create a table to store these timestamps by the Published Document ID.

```
CREATE TABLE IF NOT EXISTS document_publish_duration 
	(id bigint not null auto_increment, 
	published_document_id bigint not null, 
	publish_start timestamp default 0, 
	publish_end timestamp default 0, 
	primary key (id), 
	constraint FK_DOCUMENT_PUBLISH_DURATION_PUBLISHED_DOCUMENT_ID foreign key (published_document_id) references published_document (id));
```


* Create a trigger to save Document publish start timestamp.

```
DELIMITER //
CREATE TRIGGER save_document_publish_start
  AFTER INSERT ON published_document
  FOR EACH ROW
BEGIN
  INSERT INTO document_publish_duration (published_document_id, publish_start) VALUES (NEW.id, NEW.created_at);
END; //
DELIMITER ;
```


* Create a trigger to save Document publish end timestamp.

```
DELIMITER //
CREATE TRIGGER save_document_publish_end
  AFTER UPDATE ON published_document
  FOR EACH ROW
BEGIN
   IF NEW.status = "PUBLISHED" THEN
		UPDATE document_publish_duration set publish_end = current_timestamp() where published_document_id = NEW.id;
   END IF;
END; //
DELIMITER ;
```


* You can check the created triggers

```
show triggers;
```


### Sonar ###

** When you first start Sonar **

* Download SonarQube [Docker image] (https://hub.docker.com/_/sonarqube)

* Start OSA Backend Project project with docker, it will automatically run SonarQube too

* Login to Sonar on http://localhost:9000. The default login is set to admin/admin. Modify the default password.

* Configure new password property in pom.xml (<sonar.password>) 

** Analyze project with Sonar **

* ```./mvnw sonar:sonar```

### SonarLint ###

* Download SonarLint from Eclipse Marketplace in STS

* Right click on project and analyze with SonarLint


### Generate API documentation ###

* Edit /src/docs/asciidoc/manual_content1.adoc or /src/docs/asciidoc/manual_content2.adoc if needed

* Run ```mvn clean test -P apidoc```

* API documentation is generated to: /src/main/resources/api-documentation


### Run tests ###

* Run ```mvn clean test -P unittest```


### Git pre-commit hook ###

* Copy osa-backend/git-hooks/pre-commit to osa-backend/.git/hooks/

* To be able to commit you should start SonarQube as described above.

* Pre-commit hook runs tests and analyzes code with Sonar.


### Tomcat configuration ###

Define new Executors and Connectors in Tomcat server.xml (/opt/tomcat/apache-tomcat-9.0.53/conf/server.xml).

```
 <!--The connectors can use a shared executor, you can define one or more named thread pools-->
    
    <Executor name="tomcatThreadPool" namePrefix="catalina-exec-"
        maxThreads="150" minSpareThreads="4"/>
    <Executor name="osaImageThreadPool" namePrefix="catalina-exec-osa-image-"
        maxThreads="150" minSpareThreads="4"/>



    <!-- A "Connector" represents an endpoint by which requests are received
         and responses are returned. Documentation at :
         Java HTTP Connector: /docs/config/http.html
         Java AJP  Connector: /docs/config/ajp.html
         APR (HTTP/AJP) Connector: /docs/apr.html
         Define a non-SSL/TLS HTTP/1.1 Connector on port 8080
    -->
    <!--
    <Connector port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
		redirectPort="8443" />
    -->
    <!-- A "Connector" using the shared thread pool-->
    <Connector executor="tomcatThreadPool"
               port="8080" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />
    <Connector executor="osaImageThreadPool"
               port="8081" protocol="HTTP/1.1"
               connectionTimeout="20000"
               redirectPort="8443" />

```

Add to context configuration in Tomcat context.xml (/opt/tomcat/apache-tomcat-9.0.53/conf/context.xml).

```
	<Context  useHttpOnly="true" sessionCookiePath="/" sessionCookieDomain="osa.codeandsoda.hu">
```

### File upload related settings ###

** Spring **

```
spring.servlet.multipart.max-file-size: 15MB
spring.servlet.multipart.max-request-size: 15MB
```

Copy above configuration to application.yml

** Nginx **

You can set client_max_body_size as: 

```
# set client body size
client_max_body_size 15M;
```

* Copy above configuration to: /etc/nginx/nginx.conf http context

* Copy above configuration to: /etc/nginx/conf.d/osa.conf /api/media location context 


### IPFS upload related settings ###

On document publish a PDF is generated from the document images. This PDF should be uploaded to IPFS as well. The current file upload size limit is set to 100MB. (See IPFS EC2 nginx configuration.)


### Connection timeout configuration ###

** Tomcat **

Can be configured in server.xml at the Connection configuration with setting the connectionTimeout. See example above.

```
connectionTimeout="20000" // 20 seconds
```

** Nginx ** 

Can be configured in /etc/nginx/nginx.conf.

```
http{
   ...
   proxy_read_timeout 300; // 300 seconds
   proxy_connect_timeout 300;
   proxy_send_timeout 300;
   ...
}
```


### Spring Boot Actuator Configuration ###

* Add Spring Boot Actuator and Micrometer Registry Prometheus dependecies

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

* Configure Spring Actuator in application.yml. You can configure exposed endpoints (/prometheus endpoint should be enabled).

```
management:
  security:
      enabled: false
  port: 8081
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    metrics: 
      enabled: true
```

* Update Spring Security configuration to allow access to /actuator endpoints.

```
http.authorizeRequests().antMatchers("/actuator/**").permitAll();
```




### Quality tools ###
Config files are available in osa-backend/quality/

#### 1. Checkstyle ####

**Spring Tool Suite setup**
* Install plugin from Eclipse Marketplace
* Go to Preferences > Checkstyle
* Add new Global Check Configuration
* Select external config file
* Set as Default
    
**Usage**
* Right click on any java project
* Checkstyle > Check code with Checkstyle
    
#### 2. SpotBugs Eclipse plugin ####
Install plugin from Eclipse Marketplace and use with default configuration.


#### 3. Eclipse Configuration ####

**Formatter Setup**
* Open Preferences > Java > Code Style > Formatter
* Import active profile
* Select eclipse-formatter.xml
    
**Clean Up Setup**
* Open Preferences > Java > Code Style > Clean Up
* Import active profile
* Select eclipse-cleanup.xml
    
**Member Sort Order**
* Open Preferences > Java > Appearances > Members Sort Order
* Enable default settings
    
**Save Actions**
* Open Preferences > Java > Editor > Save Actions
* Select Format source code, Format edited lines, Organize imports and Additional actions (with default settings)

