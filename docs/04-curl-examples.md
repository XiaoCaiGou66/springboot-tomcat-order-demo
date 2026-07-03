# curl 示例

## 健康检查

```bash
curl -i "http://127.0.0.1:8080/actuator/health"
```

## 正常创建订单

Windows 下最稳的方式是使用项目自带 payload 文件：

```bash
scripts/test-normal.cmd
```

或者：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  --data-binary "{\"orderId\":1003}"
```

## Body 字段校验失败

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  --data-binary "{\"orderId\":-1}"
```

## JSON 格式错误

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  --data-binary "{\"orde"
```

## 题目中的 Content-Length 错误

Windows 下最稳的方式：

```bash
scripts/test-content-length-error.cmd
```

或者：

```bash
curl -i -X POST "http://127.0.0.1:8080/api/order/create" \
  -H "Content-Type: application/json" \
  -H "Content-Length: 6" \
  --data-binary "{\"orderId\":1003}"
```

## 查询订单

```bash
curl -i "http://127.0.0.1:8080/api/order/1003"
```
