# Spring Cloud Config

The idea of a config server is to be able to externalize properties or resource files as the values of these resources vary during runtime.

3 components required:

Property files hosted in Git
Spring Cloud Config Server
Spring Cloud Config Client

The Spring Cloud Bus component is required in order for the client to refresh itself in the event of configuration changes.

**Property files hosted in Git repo**

Sample repo which contains .yml files: https://github.com/vivekchulani/spring-cloud-config-properties

How to name files:

File name should match the client project name for example if a webservice is called testservice then resource files should be named testservice.yml so the config server knows which file to receive.
To use profiles append a hypen followed by the profile for example testservice-dev.yml
```
spring-test-client-dev.yml
configuration:
    name: Test Name
    age: 50
```


**Spring Cloud Config Server**

Create a spring boot application using Spring Assistant and enable the config server dependency. Enabling the Config Server dependency will add the following dependency to the pom.xml. Changes need to be made to the main class and the application.yml / application.properties file for this project.

```
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

Add '@EnableConfigServer' annotation above the main class which also has the '@SpringBootApplication' annotation. This tells spring that the artifact will act like a config server.
```
package com.spring.cloud.config;
 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
 
@SpringBootApplication
@EnableConfigServer
public class SpringTestConfigServerApplication {
 
    public static void main(String[] args) {
        SpringApplication.run(SpringTestConfigServerApplication.class, args);
    }  
 
}
```

Define server port, spring application name and spring cloud config server uri in application.yml file:

label can be specified under spring to point to a specific branch. By default it points to master branch
```
server:
  port: 8888 # standard convention is to use 8888
 
spring:
  application:
    name: spring-test-config-server # This is the name of your config-server project
  profiles:
    active: dev # The profile 'default' is used if this is not specified
  cloud:
    config:
      server:
        git:
          uri: https://github.com/vivekchulani/spring-cloud-config-properties.git # URI where all .yml or .properties files can be found
          username: # required for https connection
          password: #required for https connection
```
SSH keys need to be set up if https is not being used. *TODO

Once the properties have been added to the git repo and the config server is set up, we can run the config server and attempt to fetch the configurations using http requests.

Run spring cloud config server using 

`mvn clean install spring-boot:run`


The following url can be pasted in the browser to fetch the property file defined above. Profile is default if not defined in the application.yml for config server and branch is by default set to master if not specified in application.yml for config server.
```
http://localhost:8888/spring-test-client/dev
 
http://localhost:8888/{PROPERTY_FILE_NAME}/{PROFILE}/{BRANCH}
```

The response for the above url will be:
```
{ "name": "spring-test-client", "profiles": ["dev"], "label": null, "version": "60f5eb7c1d47f3312bf39acacf5c230c8a39d610", "state": null, "propertySources": [{ "name": "https://github.com/vivekchulani/spring-cloud-config-properties.git/spring-test-client-dev.yml", "source": { "configuration.name": "Test", "configuration.age": 50 } }] }
Changes to properties in the Git repo will be picked up by the config server on the next request
```


**Spring Cloud Config Client**

Create another spring boot application that has config client, config bootstrap and web dependencies. pom should contain these dependencies
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
 
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bootstrap</artifactId>
</dependency>
 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Create a controller class that has a http method such as
```
package com.spring.cloud.config.controller;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
public class SpringConfigClientController {
 
  @Value("${configuration.name}")
  private String name;
 
  @Value("${configuration.age}")
  private String age;
 
  @GetMapping("/info")
  public String getUserInfo() {
    return String.format("Hello %s, You are %s years old", name, age);
  }
 
}
```

Create a bootstrap.yml file in the /resources folder. This file will contain the cloud config client properties such as the port the application will be deployed on and the uri for the spring cloud config server
```
server:
  port: 8080
 
spring:
  application:
    name: spring-test-client
  profiles:
    active: dev
  cloud:
    bootstrap:
      enabled: true
    config:
      uri: http://localhost:8888
```

Run the application using `mvn clean install spring-boot:run`. After running the application successfuly you will see in the console output → Fetching config from server at http://localhost:8888

Using the example above send a request to `http://localhost:8080/info` and the fields name and age will be populated with the values defined in the properties file.



**Spring Cloud Bus**
