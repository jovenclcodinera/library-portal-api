# Library Portal API

API service to provide book management and book borrowing services.

## Description
This project is build using Java and Spring Framework. A User is needed to register in order to user the services provided by this API. A User has roles which can be of the following:

* LIBRARIAN - Has all acccess privileges.
* AUTHOR - Can borrow books and create them.
* BORROWER - Can only borrow books

This API has CRUD functionality and Borrow/Return functionality for Books.

## Documentation
This API is integrated with Swagger UI for easy documentation purposes. The link is "<Your Host>/swagger-ui/index.html".

### Dependencies
* Spring MVC
* Spring Boot
* Spring Boot Security
* Spring Boot Devtools
* Lombok
* MySQL Connector Driver
* JJWT (JWT)
* Jaxb API
* Springfox Boot Starter

## Authors

Contributors names and contact info

Joven Codiñera

## Version History

* 0.1
    * Initial Release
