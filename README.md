# Spring-Boot Flux-Notifier

[![Contributors][contributors-shield]][contributors-url]
[![MIT License][license-shield]][license-url]
[![linkedin-shield]][linkedin-url]

## About The Project

Sometimes, setting up a notification system can be complicated, and for it we can use different options (eg: Websockets, Polling), any of the mentioned examples have different implementations and conceptual problems, such as security in websockets or the overload of traffic in a polling system.

### We offer
* Use Webflux to make your systems efficient and robust :muscle:
* Refrain from your notifiers implementations :massage:
* Keep the architecture you want for your project and leave the rest to Flux-Notifier :blush:

### Built With

* [Project Reactor](https://projectreactor.io/)
* [Spring Boot](https://spring.io/projects/spring-boot)

## Getting Started

- **1** - **Importing dependencies**

  - **Maven**:
    ~~~
    <dependency>
        <groupId>io.github.jorgerojasdev</groupId>
        <artifactId>spring-boot-flux-notifier</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ~~~
  - **Gradle**:
    ~~~
    dependencies {
        implementation 'io.github.jorgerojasdev:spring-boot-flux-notifier:0.0.1-SNAPSHOT'
    }
    ~~~
    <br>

- **2** - **Generating a Notifier**
  
  This step is really simple, we just have to create an interface that extends Notifier <?>, where ? is the class we want to work on as a notification, Ex:
  <br>

    ~~~
    public interface NotificationExampleService extends Notifier<String> {
    }
    ~~~
    And voilá, we do not need to do anything else, the implementation has all the necessary methods to start working
    
    <p style="font-size:10px; font-style:oblique; margin-left:10px; margin-top:10px">
      * Do not implement more methods to the Notifier, it can lead to exceptions
    </p>
    <br>

- **3** - **Creating Configuration**<br><br>
    We must create a configuration that has the annotation EnableFluxNotifiers, this receives as a basePackages parameter the root on which FluxNotifier will search its implementations, Ex:
    ~~~
    @Configuration
    @EnableFluxNotifiers(basePackages = "io.github.jorgerojasdev")
    public class NotificatorExampleConfig {
        ...
    }
    ~~~
    <br>
- **4** - **Starting to Notify**<br><br>
    And that's all, now you can start subscribing to the notifier and start issuing notifications :smile:

[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/jorge-rojas-zafra-fullstack-developer
[contributors-shield]: https://img.shields.io/github/contributors/othneildrew/Best-README-Template.svg?style=for-the-badge
[contributors-url]: https://github.com/JorgeRojasDev/spring-boot-flux-notificator/graphs/contributors
[license-url]:https://github.com/JorgeRojasDev/spring-boot-flux-notifier/blob/main/LICENSE
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
