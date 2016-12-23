CREATE TABLE batch_job_instance (
  job_instance_id BIGINT       NOT NULL PRIMARY KEY,
  version         BIGINT,
  job_name        VARCHAR(100) NOT NULL,
  job_key         VARCHAR(32)  NOT NULL,
  CONSTRAINT job_inst_un UNIQUE (job_name, job_key)
)
  ENGINE = innodb;

CREATE TABLE batch_job_execution (
  job_execution_id           BIGINT        NOT NULL PRIMARY KEY,
  version                    BIGINT,
  job_instance_id            BIGINT        NOT NULL,
  create_time                DATETIME      NOT NULL,
  start_time                 DATETIME DEFAULT NULL,
  end_time                   DATETIME DEFAULT NULL,
  status                     VARCHAR(10),
  exit_code                  VARCHAR(2500),
  exit_message               VARCHAR(2500),
  last_updated               DATETIME,
  job_configuration_location VARCHAR(2500) NULL,
  CONSTRAINT job_inst_exec_fk FOREIGN KEY (job_instance_id)
  REFERENCES batch_job_instance (job_instance_id)
)
  ENGINE = innodb;

CREATE TABLE batch_job_execution_params (
  job_execution_id BIGINT       NOT NULL,
  type_cd          VARCHAR(6)   NOT NULL,
  key_name         VARCHAR(100) NOT NULL,
  string_val       VARCHAR(250),
  date_val         DATETIME DEFAULT NULL,
  long_val         BIGINT,
  double_val       DOUBLE PRECISION,
  identifying      CHAR(1)      NOT NULL,
  CONSTRAINT job_exec_params_fk FOREIGN KEY (job_execution_id)
  REFERENCES batch_job_execution (job_execution_id)
)
  ENGINE = innodb;

CREATE TABLE batch_step_execution (
  step_execution_id  BIGINT       NOT NULL PRIMARY KEY,
  version            BIGINT       NOT NULL,
  step_name          VARCHAR(100) NOT NULL,
  job_execution_id   BIGINT       NOT NULL,
  start_time         DATETIME     NOT NULL,
  end_time           DATETIME DEFAULT NULL,
  status             VARCHAR(10),
  commit_count       BIGINT,
  read_count         BIGINT,
  filter_count       BIGINT,
  write_count        BIGINT,
  read_skip_count    BIGINT,
  write_skip_count   BIGINT,
  process_skip_count BIGINT,
  rollback_count     BIGINT,
  exit_code          VARCHAR(2500),
  exit_message       VARCHAR(2500),
  last_updated       DATETIME,
  CONSTRAINT job_exec_step_fk FOREIGN KEY (job_execution_id)
  REFERENCES batch_job_execution (job_execution_id)
)
  ENGINE = innodb;

CREATE TABLE batch_step_execution_context (
  step_execution_id  BIGINT        NOT NULL PRIMARY KEY,
  short_context      VARCHAR(2500) NOT NULL,
  serialized_context TEXT,
  CONSTRAINT step_exec_ctx_fk FOREIGN KEY (step_execution_id)
  REFERENCES batch_step_execution (step_execution_id)
)
  ENGINE = innodb;

CREATE TABLE batch_job_execution_context (
  job_execution_id   BIGINT        NOT NULL PRIMARY KEY,
  short_context      VARCHAR(2500) NOT NULL,
  serialized_context TEXT,
  CONSTRAINT job_exec_ctx_fk FOREIGN KEY (job_execution_id)
  REFERENCES batch_job_execution (job_execution_id)
)
  ENGINE = innodb;

CREATE TABLE batch_step_execution_seq (
  id         BIGINT  NOT NULL,
  unique_key CHAR(1) NOT NULL,
  CONSTRAINT unique_key_un UNIQUE (unique_key)
)
  ENGINE = innodb;

INSERT INTO batch_step_execution_seq (id, unique_key) SELECT *
                                                      FROM (SELECT
                                                              0   AS id,
                                                              '0' AS unique_key) AS tmp
                                                      WHERE NOT exists(SELECT *
                                                                       FROM batch_step_execution_seq);

CREATE TABLE batch_job_execution_seq (
  id         BIGINT  NOT NULL,
  unique_key CHAR(1) NOT NULL,
  CONSTRAINT unique_key_un UNIQUE (unique_key)
)
  ENGINE = innodb;

INSERT INTO batch_job_execution_seq (id, unique_key) SELECT *
                                                     FROM (SELECT
                                                             0   AS id,
                                                             '0' AS unique_key) AS tmp
                                                     WHERE NOT exists(SELECT *
                                                                      FROM batch_job_execution_seq);

CREATE TABLE batch_job_seq (
  id         BIGINT  NOT NULL,
  unique_key CHAR(1) NOT NULL,
  CONSTRAINT unique_key_un UNIQUE (unique_key)
)
  ENGINE = innodb;

INSERT INTO batch_job_seq (id, unique_key) SELECT *
                                           FROM (SELECT
                                                   0   AS id,
                                                   '0' AS unique_key) AS tmp
                                           WHERE NOT exists(SELECT *
                                                            FROM batch_job_seq);
