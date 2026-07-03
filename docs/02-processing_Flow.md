# 完整处理流程

## 1. 客户端构建了错误请求
脚本发送：
```http
POST /api/order/create HTTP/1.1
Content-Type: application/json
Content-Length: 6

{"orderId":1003}
```
请求体实际大于 6 字节，但 HTTP 头只声明 6 字节。

## 2. Tomcat 接收 HTTP 请求
Tomcat负责：
```text
监听 8080 端口
解析请求行
解析请求头
识别 Content-Length
向上层提供请求输入流
```
由于 Content-Length 为 6，上层得到的有效请求体类似：
```text
{"orde
```
## 3. 执行 TraceIdFilter
核心代码：
```text
String traceId = request.getHeader(TRACE_ID);

if (traceId == null || traceId.trim().isEmpty()) {
traceId = UUID.randomUUID().toString();
}

MDC.put("traceId", traceId);
response.setHeader(TRACE_ID, traceId);
```
它会：
```text
读取 X-Trace-Id
没有就生成 UUID
放入 MDC
写入响应头
```
即使后续请求失败，响应中仍然可以包含：
```text
X-Trace-Id: ...
```
## 4. DispatcherServlet 查找接口
Spring MVC 的 DispatcherServlet 根据：
```text
POST /api/order/create
```
找到
```text
OrderController.create()
```
对应：
```java
@PostMapping("/create")
public ApiResponse<OrderResponse> create(
        @Valid @RequestBody CreateOrderRequest request) {}
```
此时只是找到目标方法，方法还没有真正执行。

## 5. 执行 AccessLogInterceptor
WebConfig 将拦截器注册到：
```text
registry.addInterceptor(accessLogInterceptor)
        .addPathPatterns("/api/**");
```
因此 /api/order/create 会进入：
```text
AccessLogInterceptor.preHandle()
```
打印：
```text
request.getContentLengthLong()
```
日志会显示：
```text
contentLength=6
```
## 6. 处理 @RequestBody
Spring 需要先执行两件事：
```text
1. 将 JSON 转换为 CreateOrderRequest
2. 对 CreateOrderRequest 执行参数校验
```
Jackson 实际读取到：
```text
{"orde
```
因为字段名称和 JSON 结构都没有结束，所以无法完成解析。

## 7. 执行 抛出异常和全局异常处理
Jackson 底层解析异常被 Spring 包装为：
```java
HttpMessageNotReadableException
```
异常处理：
```java
@ExceptionHandler(HttpMessageNotReadableException.class)
public ResponseEntity<Map<String, Object>> handleUnreadableBody(
HttpMessageNotReadableException ex,
HttpServletRequest request) {}
```
记录警告日志：
```text
log.warn(
"request body is not readable, path={}, reason={}",
request.getRequestURI(),
ex.getMessage()
);
```
然后构造响应体：
```text
body.put("status", 400);
body.put("error", "Bad Request");
body.put("path", request.getRequestURI());
```
最终返回：
```text
ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
```
即
```http
HTTP/1.1 400 Bad Request
```