```
cf create-service p-mysql 100mb-dev job-db
cf create-service p-rabbitmq standard binder 

cf push -f job-request-api/manifest.yml
cf push -f job-worker/manifest.yml
```