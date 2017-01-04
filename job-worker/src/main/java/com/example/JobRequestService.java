package com.example;

import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobRequestService {
	private final JobRequestMapper jobRequestMapper;
	private final static Logger log = LoggerFactory.getLogger(JobRequestService.class);

	public JobRequestService(JobRequestMapper jobRequestMapper) {
		this.jobRequestMapper = jobRequestMapper;
	}

	public JobStatus getLatestJobStatus(String jobRequestId) {
		Integer statusCode = jobRequestMapper
				.findLatestJobStatusByJobRequestId(jobRequestId);
		return JobStatus.valueOf(statusCode);
	}

	public String appendEvent(String jobRequestId, JobStatus jobStatus) {
		log.info("Append Status(jobStatus={}, jobRequestId={})", jobStatus, jobRequestId);
		String jobEventId = UUID.randomUUID().toString();
		Instant now = Instant.now();
		jobRequestMapper
				.insertJobEvent(new JobEvent(jobEventId, jobRequestId, jobStatus, now));
		return jobEventId;
	}

	public void saveRelationship(JobExecution jobExecution, String jobRequestId,
			JobStatus jobStatus) {
		appendEvent(jobRequestId, jobStatus);
		jobRequestMapper.insertJobExecution(jobRequestId, jobExecution.getJobId());
	}
}
