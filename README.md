# :factory:	 WareHouse
<a href="https://foojay.io/works-with-openjdk"><img align="right" src="https://github.com/foojayio/badges/raw/main/works_with_openjdk/Works-with-OpenJDK.png" width="100"></a>


![Java](https://img.shields.io/badge/-Java-000?&logo=Java&logoColor=007396)
![Spring](https://img.shields.io/badge/-Spring-000?&logo=Spring)


## Description:
The WareHouse API Backend allows performing following operation.
- Data Loaders
   * Load all articles from `inventory.json` specified by location in `application.properties`
   * Load all Products from `products.json` specified by location in `application.properties`
   * Maintains quantity of products available for sell based on underlying Article inventory in system
- REST APIS
  * list all products
  * sell the units of products in specified quantity
  * Update quantity of products available for sell based on underlying Article inventory in system


## :shield:	 Code coverage
![image](https://user-images.githubusercontent.com/14979620/160282319-3753476d-8a65-4efa-ad96-90257cead6d0.png)


## :balance_scale:	Assumptions :
- Validations for recipe creation and update are assumed as 
  - articleName/productName should be at least 3 to 255 characters
  - article can not have negative available stock
  - 
## :hammer_and_wrench:	Tech-Stack
![Java](https://img.shields.io/badge/-Java-000?&logo=Java&logoColor=007396)
![Spring](https://img.shields.io/badge/-Spring-000?&logo=Spring)	
- Java 8 
- Spring-Boot
- Apache Camel
- JPA
- In-Memory Database H2
- Maven
- Git bash

## :memo: Steps to run the application
- Checkout the code / Download from git repo()
- checkout : open git bash and run command `git clone https://github.com/Abhinav2510/warehouse.git`
- Option 1: Maven way of running
  - open command prompt(cmd) or terminal on Mac
  - navigate to the project folder
  - run command `mvn clean install`
  - once its successfully build run command `mvn spring-boot: run`

Now application is up and running on http://localhost:8080/

## :grey_question:	How to use this service
### :ballot_box:	Load Articles/products
- Apache Camel routes are configured to pick inventory.json and product.json from c:/folder1
 - Load Articles by placing `inventory.json` in `c:/folder1`
 - Load Products by placing `products.json` in `c:/folder1`
 - failure in one record will not break loaders instead failed record will be in `c:/folder1/failure/inventory` or `c:/folder1/failure/inventory`
### :spider_web: get products and sell product  
 - Open the URL in your browser : http://localhost:8080
 - User will see a swagger page with all the defined specs of the service.
 - There will have 2 Tags you can see.


### 1. inventory-product-controller
#### Description:
- Endpoint 1: `GET /inventory/product`
  - get all the products with pagination suppoty 
- Endpoint 2: `POST /inventory/product`
  - Allows user to sell quantity of product specified in post request body


### :test_tube: Testing using Swagger UI

#### Running application
- Run application using `mvn spring-boot: run`
- Navigate to http://localhost:8080


![image](https://user-images.githubusercontent.com/14979620/160282910-2189eccc-c58b-445f-b7a4-e14de9cdcef7.png)
