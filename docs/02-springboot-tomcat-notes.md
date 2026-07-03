# Spring Boot 和 Tomcat 学习笔记

## Spring Boot 是什么

Spring Boot 是为了让 Spring 项目更容易启动、配置和运行。它帮你自动配置了很多默认能力，比如 Web MVC、JSON 解析、日志、内嵌 Tomcat。

## Tomcat 是什么

Tomcat 是 Servlet 容器，也是 HTTP 服务容器。Spring Boot Web 项目默认使用内嵌 Tomcat。

启动 Spring Boot 后，你不需要单独安装 Tomcat，因为依赖 `spring-boot-starter-web` 会引入：

```text
spring-webmvc
jackson
tomcat-embed-core
tomcat-embed-websocket
```

## 一次请求的核心链路

```text
浏览器/curl/Postman
  -> Tomcat 接收 HTTP 请求
  -> Filter 过滤器
  -> DispatcherServlet
  -> HandlerInterceptor
  -> Controller 方法参数解析
  -> Controller
  -> Service
  -> Repository
  -> 返回 JSON
```

## Controller 常用注解

```java
@RestController
@RequestMapping("/api/order")
@PostMapping("/create")
@RequestBody
@PathVariable
```

## 本题涉及的关键类

- `OrderController`：提供接口
- `CreateOrderRequest`：接收 JSON 请求体
- `OrderService`：写业务逻辑
- `GlobalExceptionHandler`：统一处理异常
- `TraceIdFilter`：演示过滤器
- `AccessLogInterceptor`：演示拦截器

## 测试开发要会看什么

遇到 400 问题，不要只看业务代码，要按链路排查：

```text
请求方法是否正确
URL 是否正确
Header 是否正确
Body 是否正确
参数类型是否匹配
JSON 是否合法
Controller 是否进入
日志里是否有参数解析异常
```
