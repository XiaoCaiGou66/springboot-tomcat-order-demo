# 题目一排查思路：HTTP 请求失败根因排查

## 1. 先看现象

题目给出的请求：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Content-Length: 6" \
  --data-binary "{\"orderId\":1003}"
```

现象是服务端返回 400 Bad Request。

## 2. 关键异常点

请求体真实内容是：

```json
{"orderId":1003}
```

这个字符串明显不止 6 个字节，但请求头里写了：

```text
Content-Length: 6
```

HTTP 协议中，`Content-Length` 表示请求体长度。Tomcat 看到长度是 6，就只会把 6 字节作为请求体交给上层应用。

因此 Spring MVC 实际拿到的内容类似：

```text
{"orde
```

这是一个不完整 JSON。

## 3. 为什么没进入 Controller

Controller 方法是：

```java
@PostMapping("/create")
public ApiResponse<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request)
```

执行 Controller 之前，Spring 需要先用 Jackson 把 JSON 转成 `CreateOrderRequest` 对象。

流程大概是：

```text
Tomcat 接收请求
  -> DispatcherServlet 分发
  -> RequestResponseBodyMethodProcessor 处理 @RequestBody
  -> Jackson 解析 JSON
  -> JSON 不完整，抛出 HttpMessageNotReadableException
  -> 返回 400
```

所以问题发生在参数解析阶段，而不是业务代码阶段。

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
