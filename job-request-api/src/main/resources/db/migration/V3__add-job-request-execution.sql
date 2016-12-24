CREATE TABLE job_request_execution (
  job_request_id   VARCHAR(36) NOT NULL,
  job_execution_id BIGINT      NOT NULL,
  FOREIGN KEY (job_request_id) REFERENCES job_request (job_request_id),
  FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (JOB_EXECUTION_ID)
)
  ENGINE = innodb;