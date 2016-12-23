package com.example;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobRequestService {
	private final JdbcTemplate jdbcTemplate;
	private final static Logger log = LoggerFactory.getLogger(JobRequestService.class);

	public JobRequestService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JobStatus getLatestJobStatus(String jobRequestId) {
		Integer statusCode = jdbcTemplate.queryForObject(
				"SELECT job_status FROM job_request AS jr INNER JOIN job_event AS je ON jr.job_request_id = je.job_request_id WHERE jr.job_request_id = ? ORDER BY je.created_at DESC LIMIT 1",
				Integer.class, jobRequestId);
		return JobStatus.valueOf(statusCode);
	}

	public String appendEvent(String jobRequestId, JobStatus jobStatus) {
		log.info("Append Status(jobStatus={}, jobRequestId={})", jobStatus, jobRequestId);
		String jobEventId = UUID.randomUUID().toString();
		jdbcTemplate.update(
				"INSERT INTO job_event(job_event_id, job_request_id, job_status, created_at) VALUES (?, ?, ?, ?)",
				jobEventId, jobRequestId, jobStatus.statusCode(),
				Timestamp.from(Instant.now()));
		return jobEventId;
	}

	public void saveRelationship(JobExecution jobExecution, String jobRequestId,
			JobStatus jobStatus) {
		appendEvent(jobRequestId, jobStatus);
		jdbcTemplate.update(
				"INSERT INTO job_request_execution(job_request_id, job_execution_id) VALUES (?, ?)",
				jobRequestId, jobExecution.getJobId());
	}
}
