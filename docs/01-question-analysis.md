# HTTP 请求失败根因排查

## 1. 先看现象

题目给出的请求：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Content-Length: 6" \
  --data-binary "{\"orderId\":1003}"
```

现象是服务端返回 400 Bad Request。

响应体是：

```json
{
  "status": 400,
  "error": "Bad Request",
  "path": "/api/order/create"
}
```

## 2. 根本原因

请求体真实内容是：

```json
{"orderId":1003}
```

这个字符串明显不止 6 个字节，但请求头里写了：

```cmd
-H "Content-Length: 6"
```

HTTP 协议中，`Content-Length` 表示请求体长度。Tomcat 看到长度是 6，就只会把 6 字节作为请求体交给上层应用。

因此 Spring MVC 实际拿到的内容类似：

```text
{"orde
```

这不是完整 JSON，因此 Jackson 无法转换为 Java 对象，最终产生 HTTP 400。


## 3. 排查分析

第一步：确认接口地址和请求方法

Controller 中的配置是：

```java
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @PostMapping("/create")
    public ApiResponse<OrderResponse> create() {
    }
}
```
因此正确接口为：
```text
POST /api/order/create
```
当前请求的 URL 和 Method 都正确，可以排除路由错误。

第二步：确认 Content-Type

请求头是：
```text
Content-Type: application/json
```

Controller使用：
```java
@RequestBody CreateOrderRequest request;
```
两者匹配，因此不是媒体类型错误。


第三步：比较实际请求体与 Content-Length

请求体：
```json
{"orderId":1003}
```

实际请求体长度为 16 字节，但请求声明只有 6 字节。


## 4. 完整排查路径

1. 确认 URL 是否正确：`/api/order/create`
2. 确认 Method 是否正确：POST
3. 确认 `Content-Type` 是否正确：`application/json`
4. 确认请求体是否是合法 JSON
5. 重点检查 `Content-Length` 是否与真实 body 长度一致
6. 看服务端日志是否出现 JSON parse error
7. 判断请求是否进入 Controller
8. 如果没进入 Controller，优先排查容器、过滤器、拦截器、参数解析、全局异常处理

## 5. 结论

根因是客户端手动设置了错误的 `Content-Length`，导致 Tomcat 按错误长度截断请求体，Spring 解析 JSON 失败，最终返回 400。

## 6. 修复建议

不要手动指定 `Content-Length`。让 curl 或 HTTP 客户端自动计算。

正确请求：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  --data-binary "{\"orderId\":1003}"
```
