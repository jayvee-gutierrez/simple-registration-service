# Registration Service

This is a sample application that provides endpoints for managing uses. Below are its key functionalities:
1. Register a user
2. Send email upon successful registration
2. Retrieve details for a single user or for all the registered users
3. Update details of users
4. Soft delete users

---

## How to Run

This repository provides a single executable shell script named `run.sh` that builds, package, and run the application using Maven and Docker

1. Clone this repository
2. Make sure the following are installed in your local machine
  * Bash
  * Docker
  * JDK 17
  * Maven 3.x (Make sure `mvn` uses JDK 17)
3. Run `run.sh` with optional `port` argument. The application will run on port `8887` by default

```
bash run.sh <port>
```
* Check the stdout to make sure no exceptions are thrown. You will see the following lines once the application started successfully:
```
2024-02-12T21:33:24.332+08:00  INFO 3970 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path ''
2024-02-12T21:33:24.341+08:00  INFO 3970 --- [           main] c.p.r.RegistrationServiceApplication     : Started RegistrationServiceApplication in 5.746 seconds (process running for 6.289)
```
4. After this, you can access the endpoints through <a>localhost:8887/</a>. Replace the port in the link if you provided a different port.

---

## API documentation

Run the application and go to <a>localhost://8887/swagger-ui/index.html</a>. Replace the port in the link if you used a different port while running the application.

It can also be accessed as `JSON` format through <a>localhost://8887/api-docs</a>.

---

## Notification Feature
For demo purposes, this application uses a fake SMTP server provided by **https://mailtrap.io/**. All emails sent can be viewed in the mailtrap dashboard.

The following configurable properties are available for the notification feature, including a feature flag:
```
spring.mail.host=
spring.mail.port=
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=
spring.mail.properties.mail.smtp.starttls.enable=

pccw.email.enabled=
pccw.email.sender=
pccw.email.default-subject=
```


---

## Further Improvements
Since the requirements for this application are not as detailed and extensive, it has limited functionalities and therefore subject to further improvements. Following are some functionalities/features that can be added:
1. Detailed logging. Logging can be in a format readily readable by monitoring tools such as Splunk, e.g.,
```
event="REGISTER_USER", status="START", reason="", ...
event="REGISTER_USER", status="SUCCESS", reason="", ...
event="REGISTER_USER", status="FAILED", reason="UserNotFoundException", ...
event="REGISTER_USER", status="INFO", reason="", ...
```
2. Better business logic specifically in the update/delete features
3. Better exception handling
3. Better error messages (error messages produced by the application are currently not as detailed)
4. Implement endpoint security
5. More secure password management
6. Implement pagination
7. Add event-driven email sending
8. Separate API docs (not hosted with the application)
8. Other improvements
---

### For questions and concerns, contact:
`jayveegutierrez14@gmail.com`

