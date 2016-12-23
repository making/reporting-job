CREATE TABLE IF NOT EXISTS job_request (
  job_request_id VARCHAR(36) NOT NULL,
  job_id         VARCHAR(36) NOT NULL UNIQUE,
  created_at     TIMESTAMP   NOT NULL,
  PRIMARY KEY (job_request_id)
) /*! ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_general_ci */;

CREATE TABLE IF NOT EXISTS job_event (
  job_event_id   VARCHAR(36) NOT NULL,
  job_request_id VARCHAR(36) NOT NULL,
  job_status     TINYINT     NOT NULL,
  created_at     TIMESTAMP   NOT NULL,
  PRIMARY KEY (job_event_id),
  FOREIGN KEY (job_request_id) REFERENCES job_request (job_request_id)
    ON DELETE CASCADE
) /*! ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_general_ci */;

ALTER TABLE job_request
  ADD INDEX idx_for_sort(created_at);
ALTER TABLE job_event
  ADD INDEX idx_for_sort(created_at);
