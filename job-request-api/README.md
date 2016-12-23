

```
curl -v localhost:8080/v1/job_requests -d '{"jobId":"'`uuidgen | tr "[:upper:]" "[:lower:]"`'"}' -H 'Content-Type: application/json'
```