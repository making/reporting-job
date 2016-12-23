CREATE TABLE job_request_execution (
  job_request_id   VARCHAR(36) NOT NULL,
  job_execution_id BIGINT      NOT NULL,
  CONSTRAINT job_request_execution_fk FOREIGN KEY (job_request_id)
  REFERENCES job_request (job_request_id)
)
  ENGINE = innodb;