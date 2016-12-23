package com.example;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobRequestService {
	private final JdbcTemplate jdbcTemplate;
	private final Source source;

	public JobRequestService(JdbcTemplate jdbcTemplate, Source source) {
		this.jdbcTemplate = jdbcTemplate;
		this.source = source;
	}

	public Optional<JobRequest> findByJobRequestId(String jobRequestId) {
		return jdbcTemplate.query(
				"SELECT jr.job_request_id, jr.job_id, jr.created_at, je.job_event_id, je.job_status, je.created_at FROM job_request AS jr LEFT JOIN job_event AS je ON jr.job_request_id = je.job_request_id WHERE jr.job_request_id = ?  ORDER BY je.created_at DESC",
				rs -> {
					if (!rs.next()) {
						return Optional.empty();
					}
					JobRequest jobRequest = new JobRequest(rs.getString("job_request_id"),
							rs.getString("job_id"), new ArrayList<>(),
							rs.getTimestamp("jr.created_at").toInstant());
					do {
						JobEvent event = new JobEvent(rs.getString("job_event_id"),
								rs.getString("job_request_id"),
								JobStatus.valueOf(rs.getInt("job_status")),
								rs.getTimestamp("je.created_at").toInstant());
						jobRequest.getJobEvents().add(event);
					}
					while (rs.next());
					return Optional.of(jobRequest);
				}, jobRequestId);
	}

	@Transactional
	public String registerJob(JobRequest jobRequest) {
		String jobRequestId = UUID.randomUUID().toString();
		String jobId = jobRequest.getJobId();
		Instant now = Instant.now();

		jobRequest.setJobRequestId(jobRequestId);
		jobRequest.setCreatedAt(now);

		jdbcTemplate.update(
				"INSERT INTO job_request(job_request_id, job_id, created_at) VALUES(?, ?, ?)",
				jobRequestId, jobId, Timestamp.from(now));
		jdbcTemplate.update(
				"INSERT INTO job_event(job_event_id, job_request_id, job_status, created_at) VALUES (?, ?, ?, ?)",
				UUID.randomUUID().toString(), jobRequestId,
				JobStatus.REQUESTED.statusCode(), Timestamp.from(now));

		requestJob(jobRequest);
		return jobRequestId;
	}

	@Transactional
	public void requeueJob(String jobRequestId, boolean force) {
		Instant now = Instant.now();
		String jobId = jdbcTemplate.queryForObject(
				"SELECT job_id FROM job_request WHERE job_request_id = ?", String.class,
				jobRequestId);
		if (force) {
			jdbcTemplate.update(
					"INSERT INTO job_event(job_event_id, job_request_id, job_status, created_at) VALUES (?, ?, ?, ?)",
					UUID.randomUUID().toString(), jobRequestId,
					JobStatus.REQUESTED.statusCode(), Timestamp.from(now));
		}
		requestJob(new JobRequest(jobRequestId, jobId));
	}

	private void requestJob(JobRequest jobRequest) {
		Message<JobRequest> message = MessageBuilder.withPayload(jobRequest.copyOnlyIds())
				.build();
		source.output().send(message);
	}
}
