# Payment Gateway
### Stack
* Java 11 
* Maven 
### Guidance
 To start the app type `mvn package`. The command will execute unit and integration tests and
 build the artifact. Other options to use maven wrapper. `./mvnw package` 
### Project package structure
* api - contains all controllers for the application endpoints
* dao - all code related to database: entity, repository etc. Current implementation 
use map and list instead of real datasource but with repository API migration to JPA will be quite easy.
- exception - custom exceptions used in the services.
- mapper - contains code related to transformation from dto to entity and vice versa.
- model - on current implementation it has only the models related to our `PaymentController`. In the future if 
there is a need to create a data structure to pass data between classes that is the right package. 
- service - business logic goes here
- validator - in order to perform validation for the whole transaction (bonus section) I decided to create custom validators. 

### Things to improve:
I'm not that familiar with PCI in real-world scenarios, and I'm not 100% sure I chose the proper place to encrypt data 
(in the repositories before persistence). 
 
I did not get a chance to spend more time on the audit logic. I'm aware current implementation does not suit real world cases. 

There is not logic to create new files when the file size getting too big. I had an assumption that logic is only mock and for 
real world scenario we will leverage JPA audit 3-rd party tools.  

I create two types of tests: API (use spring context without any mocks) unit (tests singe unit, manly validators and 
services). The test coverage is not good enough. In real world scenario such code should be tested in various ways: integration,
 end-to-end, and performance (it's better to spot concurrency issues at development stage)




  


