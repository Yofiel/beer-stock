# Person API 

## API Rest built with Spring Boot for registries of persons, possibly users

The API contains all the typical REST API services. The API isn't specific to any given database. This project uses the 
H2 in memory database.

### Versions

- Java 11;
- Maven 3.8.1
- Spring Boot 2.5.1; 
- H2 1.4.2;
- Mapstruct 1.4.2;
- Lombok 1.18.20 --- lombok-mapstruct-binding on 0.2.0;
- OpenAPI UI (Swagger) 1.5.9.

### Getting started

`mvn spring-boot:run` on the root folder of the project is enough if you have a maven wrapper or maven itself installed.
Also, you can import the project to Eclipse, Intellij IDEA or another IDE which will do the job for you.

### About the API

For detailed information on the API, the OpenAPI specification (OAS) for RESTful APIs is used with the graphical interface
(web based) provided by the Swagger project. The swagger-ui can be accessed with `http://[your-domain]/swagger-ui.html`.
With swagger-ui you will be able to consult and test the functionalities provided by this RESTful API.

Look below for a preview of Swagger.

![personapi](https://user-images.githubusercontent.com/64466694/123513082-81a24800-d661-11eb-9349-fb76f45a7afd.png)

For practicality, a JSON file with the API definitions is in the repository. Search for `api-docs.json`.
