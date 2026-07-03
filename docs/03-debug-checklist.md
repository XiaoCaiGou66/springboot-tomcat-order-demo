# HTTP 400 排查 Checklist

## 请求基本信息

- Method 是否正确
- URL 是否正确
- Query 参数是否正确
- Header 是否正确
- Body 是否正确

## JSON 请求体

- 是否是合法 JSON
- 字段名是否和 DTO 一致
- 字段类型是否匹配
- 是否缺少必填字段
- 是否多了非法字符
- 是否被截断

## Header

- `Content-Type` 是否是 `application/json`
- 是否手动设置了错误的 `Content-Length`
- Token/Cookie 是否缺失

## Spring Boot 侧

- 请求是否进入 Filter
- 请求是否进入 Interceptor
- 请求是否进入 Controller
- 是否出现 `HttpMessageNotReadableException`
- 是否出现 `MethodArgumentNotValidException`

## 测试结论模板

```text
问题现象：
调用 /api/order/create 返回 400。

复现方式：
curl 请求中手动指定 Content-Length: 6，并发送完整 JSON。

定位过程：
1. URL 和 Method 正确。
2. Content-Type 正确。
3. 检查请求头发现 Content-Length 与真实 Body 长度不一致。
4. Tomcat 按 Content-Length 读取请求体，导致 JSON 被截断。
5. Spring 在 @RequestBody 参数解析阶段抛出 HttpMessageNotReadableException。

根因：
客户端错误设置 Content-Length。

修复建议：
删除手动 Content-Length，让 HTTP 客户端自动计算。
```
