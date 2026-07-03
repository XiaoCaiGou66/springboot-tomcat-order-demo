# 相关知识文档


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

## HTTP Content-Length 知识

Content-Length 表示 HTTP 消息体的字节数，不是字符数。 在 UTF-8 下，一个中文字符通常占 3 字节。

因此不能简单按照 Java 字符串的字符数量手工填写 Content-Length，实际项目中应该使用自动计算。


## 本题中为什么Controller 断点没有进入
一个请求在进入 Controller 方法体前还要经过许多阶段：
```text
Tomcat 解析 HTTP
Filter
DispatcherServlet 路由匹配
Interceptor
参数解析
类型转换
JSON 反序列化
参数校验
Controller 方法体
```
只要前面的任何阶段失败，Controller 方法体就不会执行。


## @RequestBody 和 @Valid 的执行顺序
Controller 参数：
```text
@Valid @RequestBody CreateOrderRequest request
```
实际顺序是：先执行 @RequestBody ，再执行 @Valid，也就是

```text
JSON 字符串
    ↓ Jackson 反序列化
CreateOrderRequest
    ↓ Bean Validation 校验
校验通过后进入 Controller
```
因此存在三类不同问题。
```text
1、JSON 本身无法解析

例如{"orde 或者 {"orderId":  
产生  HttpMessageNotReadableException,此时@Valid 还没有机会执行。

2、JSON 合法，但数据不符合校验规则
例如 {"orderId":-1}
可以正常转换成对象，但违反：@Positive
产生 MethodArgumentNotValidException

3、参数合法，但业务处理失败
例如查询不存在的订单：GET /api/order/9999
Service 抛出：IllegalArgumentException
```

## HTTP 400 排查 Checklist

### 请求基本信息

- Method 是否正确
- URL 是否正确
- Query 参数是否正确
- Header 是否正确
- Body 是否正确

### JSON 请求体

- 是否是合法 JSON
- 字段名是否和 DTO 一致
- 字段类型是否匹配
- 是否缺少必填字段
- 是否多了非法字符
- 是否被截断

### Header

- `Content-Type` 是否是 `application/json`
- 是否手动设置了错误的 `Content-Length`
- Token/Cookie 是否缺失

### Spring Boot

- 请求是否进入 Filter
- 请求是否进入 Interceptor
- 请求是否进入 Controller
- 是否出现 `HttpMessageNotReadableException`
- 是否出现 `MethodArgumentNotValidException`