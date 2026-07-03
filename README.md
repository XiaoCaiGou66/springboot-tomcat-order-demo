# Spring Boot + Tomcat HTTP 400 排查训练项目

这个项目用于练习题目中的场景：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Content-Length: 6" \
  --data-binary "{\"orderId\":1003}"
```

预期现象：

```json
{"status":400,"error":"Bad Request","path":"/api/order/create"}
```

核心原因：请求头声明 `Content-Length: 6`，但真实 JSON `{"orderId":1003}` 远大于 6 字节。Tomcat 作为 HTTP 容器会按照请求头只读取 6 字节，Spring MVC 收到的是被截断的 `{"orde`，Jackson 解析 JSON 失败，于是在进入 Controller 方法前返回 400。

## 项目结构

```text
src/main/java/com/training/orderdemo
  OrderDemoApplication.java
  api/OrderController.java
  api/dto/CreateOrderRequest.java
  api/dto/OrderResponse.java
  api/dto/ApiResponse.java
  service/OrderService.java
  service/OrderRepository.java
  service/InMemoryOrderRepository.java
  service/Order.java
  service/OrderStatus.java
  config/WebConfig.java
  web/TraceIdFilter.java
  web/AccessLogInterceptor.java
  web/GlobalExceptionHandler.java
src/test/java/com/training/orderdemo
  OrderControllerTest.java
docs
  01-question-analysis.md
  02-springboot-tomcat-notes.md
  03-debug-checklist.md
  04-curl-examples.md
```

## 运行方式

如果 IDEA 已打开本目录：

1. 等 IDEA 导入 Maven 依赖。
2. 打开 `OrderDemoApplication`。
3. 点击绿色运行按钮。
4. 访问 `http://127.0.0.1:8080/actuator/health`。

命令行方式：

```bash
mvn spring-boot:run
```

## 正常请求

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  --data-binary "{\"orderId\":1003}"
```

## 题目异常请求

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Content-Length: 6" \
  --data-binary "{\"orderId\":1003}"
```
