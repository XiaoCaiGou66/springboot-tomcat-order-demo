@echo off
curl.exe --max-time 5 -i -X POST "http://127.0.0.1:8080/api/order/create" ^
  -H "Content-Type: application/json" ^
  -H "Content-Length: 6" ^
  --data-binary "@scripts/payload.json"
