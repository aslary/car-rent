# Rent A Car
`Rent A Car` is a sample Spring Boot MVC (Thymeleaf + REST) project.

## Description
`Rent A Car` is an application which provides the user with a web application and a REST API to perform CRUD operations on customers, cars, rentals and many more.

## Technologies
The goal of this application is to get an in-depth feeling for the usage of the `Spring framework`. 
The following subjects were applied inside this project:
* Spring Boot
* Spring Security
* Spring MVC
  * Thymeleaf
  * [Thymeleaf Layout Dialect](https://ultraq.github.io/thymeleaf-layout-dialect/)
  * Bootstrap 4
* H2 in memory database
* JPA ORM (Hibernate impl.)
  * With the usage of the `Bean Validation Framework`

# (Maven) project structure
There are many packages inside the project, in order to keep the code clean:

### src/main/java/...
* `bean`: Contains all form backing beans.
* `configuration`: Contains all configuration classes. In particular messages and security.
* `controller`: This package is divided into two subpackages
  * `rest`: Classes annotated with `@RestController`, which contain REST endpoints.
  * `view`: Classes annotated with `@Controller`, which are used to return dynamically rendered HTML views.
* `exception`: Contains logic to handle exceptions.
* `model`: Contains all JPA entity classes.
* `repository`: Contains all repositories to access the database.
* `service`: Contains all service classes which perform further business logic.
* `util`: Contains classes that help reducing code repeating.

### src/main/resources/...
Contains templates, insert scripts and configuration files.
* `data.sql`: Used for initial test data.
* `application.properties`: Contains the server configuration.
* `messages_xx.properties`: Message files used to internationalize the application.
* `templates`: This package consists of the root layout file _index.html_ (using the Thymeleaf Layout Dialect). This root layout consists of fragments, which are seperated in static content that should be visible on every page and dynamic content that should only be rendered depending on the current request.
  * The static content consists of the HTML5 tag `<head>`, a sidenav and a topnav.
  * The dynamic content is used for the specific requests. E. g. _search-rentals.html_ or _create-rental.html_.

# Domain model (JPA entity classes)
The main entity class is _Rental.java_. This class contains several unidirectional `@ManyToOne` mappings to the other entities. It was decided to do unidirectional mapping because any sort of dependency to other classes is done through this class only. Thus, all other classes do not need an extra reference to this class.

# Messages and internationalization
Many fields of form backing beans and entity classes contain validation steps done with the `Java Bean Validation Framework`. To make the application accessable and user friendly for several regions, corresponding `messages_xx.properties` files can be created. Until now the only supported language is English, however, adapting the properties file to another locale prevents from hardcoding strings inside the bean validation annotations.

# Authentication
## - Roles
There are three access types (user roles) in this application:
### Employee
By logging in as a user that has the role of `ROLE_EMPLOYEE` the user can issue new rentals. Also, the employee has the possibility to finish those. Employees can access all paths starting with `/employee/**`.

### Admin
Users with the role of `ROLE_ADMIN` have access to several REST endpoints. Admins can access all paths starting with `/admin/**`. Admins cannot visit employee roles and vice versa employees do not have access to the REST endpoints.

### Public
Anyone can call the root website. The user can see a form in which he can select a city. After submitting the form, the server will return a new website where all free cars in the selected city are displayed in a pretty way.

Public users do not have a role defined in the code. They have access to all paths different from the mentioned ones. Those would be `/**`.

## - In memory users
To keep things simple, two users were created with Spring Security HTTP basic auth. _(username, password)_
* `ROLE_EMPLOYEE`: This user has (emp, emp) as credentials.
* `ROLE_ADMIN`: This user has (admin, admin) as credentials. 

### Redirection
If unauthenticated users try to access paths of a specific role, which they are not part of, they will be redirected to the login screen.
