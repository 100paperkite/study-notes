# Servlet Container와 Spring Container

## Servlet

Java에서 클라이언트의 HTTP 요청과 응답을 처리하는 역할을 담당하는 객체. (MVC모델에서 Controller역할)

서블릿은 URI로 매핑되고, 해당 URI로 요청이 들어오면 서블릿 컨테이너가 이에 해당하는 하나의 서블릿 인스턴스를 찾아 요청을 처리하게 한다.

## Servlet Container

<span style="color:blue">a.k.a Servlet Engine</span>

ex. Tomcat, Jetty

Servlet Instance를 생성하고 초기화. Servlet의 생명 주기를 관리한다.

요청이 들어올 때마다 서블릿 인스턴스를 사용해서 응답을 생성한다.

서블릿 컨테이너는 클라이언트 요청이 발생하면 해당 요청에 대한 **새로운 스레드를 생성**하고, 그 스레드에서 해당 서블릿 인스턴스(대체로 싱글턴)를 실행한다.

스프링 컨테이너에서는 객체를 조회하기 위해 `WebApplicationContext`를 사용하고, 스프링 컨테이너에서는 이를 통해 서블릿과 상호작용 한다.

## Spring Container
<img width="387" alt="image" src="https://user-images.githubusercontent.com/98398243/225864102-0d0b4dbd-b2fc-4d75-b4f6-505f3219b9ab.png">

> 인프런 - [스프링 핵심 원리 - 기본편] by 김영한

`BeanFactory` 또는 `ApplicationContext`를 스프링 컨테이너라 한다.

Config를 기반으로 ApplicationContext를 생성해서 쓴다.

```java
ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
```

컨테이너 안에서 의존성 주입을 수행한 순수 자바 객체를 관리하며 이를 Bean이라 한다.

Bean은 이름으로 구분한다.

## Spring MVC

Spring MVC에서는 Servlet을 Front-Controller패턴으로,  `DispatcherServlet` 하나만 두고 모든 요청을 매핑한다. 

<img width="972" alt="image" src="https://user-images.githubusercontent.com/98398243/225864629-ff9ddae2-b924-4c5a-ba11-680a6f1deeac.png">

> 인프런 - [스프링 MVC 1편 - 백엔드 웹 개발 핵심 기술] by 김영한

Spring Boot가 Servlet Container(ex. Tomcat)을 띄울 때, DispatcherServlet을 등록시킨다. 

그리고 DispatcherServlet은 `WebApplicationContext`를 참조하여 스프링 컨테이너를 참조할 수 있으며, 이 컨텍스트 안에 코드로 작성한 Controller, Service, Repository 클래스가 존재한다.
이 컨텍스트는 상속 관계로 이루어져 있어서 만약 여러개의 DispatcherServlet을 둔다면 하위 클래스를 여러 개 두어서 중복없이 코드를 작성할 수 있다.

![image](https://user-images.githubusercontent.com/98398243/225866260-7530e660-51d9-41fe-8ec4-19599e169fc6.png)

> [스프링 공식 문서](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-servlet-context-hierarchy)
