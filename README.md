# Microservices with Spring Boot and Spring Cloud

This project is in reference to the mircroservices project in 2nd Edition of Magnus Larsson's book, 
Microservices with Spring Boot and Spring Cloud: Build Resilient and Scalable Microservices Using Spring Cloud, Istio, and Kubernetes.
The project starts out with the basics in the initial chapters and then gains extra level of complexity as you progress in the book.

## Setup

If you are interested in setting up this project then:

* Clone repository.

        git clone https://github.com/ofoe-fiergbor/microservices-with-spring-boot-and-spring-cloud.git
* Build.

        ./gradlew build
* Run tests.

        ./gradlew test

* Run single services eg. product-service.

        java -jar microservices/product-service/build/libs/*.jar
* Run all services.

        ./run
        -------------- or ------------------
        java -jar microservices/product-composite-service/build/libs/*.jar &
        java -jar microservices/product-service/build/libs/*.jar &
        java -jar microservices/recommendation-service/build/libs/*.jar &
        java -jar microservices/review-service/build/libs/*.jar &

## Dependencies

        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        implementation 'org.springframework.boot:spring-boot-starter-webflux'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
        testImplementation 'io.projectreactor:reactor-test'
