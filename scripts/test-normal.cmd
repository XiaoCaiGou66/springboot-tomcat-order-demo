@echo off
curl.exe -i -X POST "http://127.0.0.1:8080/api/order/create" ^
  -H "Content-Type: application/json" ^
  --data-binary "@scripts/payload.json"
