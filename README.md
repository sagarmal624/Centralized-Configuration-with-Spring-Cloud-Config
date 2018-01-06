# Centralized-Configuration-with-Spring-Cloud-Config
https://www.sagarandcompany.com/

                                                # Centralized configuration with Spring Cloud Config
                                                
                                                
                                                
In this blog post I am looking into Spring Cloud Config project which is a great tool for centralized configuration management in a microservice environment. I have prepared a simple example project which you can find on my github account. It contains a config-service leveraging Spring Cloud Config Server and three little services using Spring Cloud Config Client.

Clone the configuration and example repositories into your home directory and build the project.
```java
git clone https://github.com/sagarmal624/Centralized-Configuration-with-Spring-Cloud-Config.git 

git clone https://github.com/sagarmal624/config-repo.git

```

In this Example we have two web services application(cloudnary,linkSharing).we have HomeController and application.proerties file 
in both web services application.Each Web Services has seprate db configuration.



!alt [text](http://docs.pivotal.io/spring-cloud-services/1-4/common/config-server/images/config-server-fig1.png)

we have one config server which is mainly used to fetch config properties from git repo and serve that config file to different
web services which are config clients.

our config server is running on 

http://localhost:8888/


To Enable Config Server we have @EnableConfigServer Annotation.In config server application
we need to set git repo where you are going to push configuration properties file.


web services application is running on following port.
* cloudnary appplication:

http://localhost:8181/name

* linkSharing appplication:

http://localhost:8282/name

we need to set config server url in application .properties files for both web services. 
These application fetch application.properties configuration from config server.


Whenever application up and hit the url  ... you will get name.




When you are going to change something in config repo->cloudnary.properties and linksharing.properties.
it will automatically refresh in cloudnary application properties wihtout restarting the server.. To do this things we need to hit one url


http://localhost:8181/refresh   with post request


http://localhost:8282/refresh   with post request





                                                
                                                
