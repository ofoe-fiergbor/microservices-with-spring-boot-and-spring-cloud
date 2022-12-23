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

* Build & Run with docker in detached mode.

        ./run.bash
        -------------- or ------------------
        ./gradlew clean build && docker-compose build && docker-compose up -d
        -------------- or ------------------
        docker-compose build && docker-compose up -d

## Dependencies

        implementation 'org.springdoc:springdoc-openapi-common:1.5.9'
        implementation 'org.springframework.boot:spring-boot-starter-webflux'
        implementation 'org.springframework.boot:spring-boot-starter-actuator'
        testImplementation 'io.projectreactor:reactor-test'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
