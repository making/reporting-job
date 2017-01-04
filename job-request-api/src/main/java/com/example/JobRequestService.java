package com.example;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobRequestService {
	private final JobRequestMapper jobRequestMapper;
	private final Source source;

	public JobRequestService(JobRequestMapper jobRequestMapper, Source source) {
		this.jobRequestMapper = jobRequestMapper;
		this.source = source;
	}

	public Optional<JobRequest> findByJobRequestId(String jobRequestId) {
		return Optional.ofNullable(jobRequestMapper.findByJobRequestId(jobRequestId));
	}

	@Transactional
	public String registerJob(JobRequest jobRequest) {
		String jobRequestId = UUID.randomUUID().toString();
		Instant now = Instant.now();

		jobRequest.setJobRequestId(jobRequestId);
		jobRequest.setCreatedAt(now);

		JobEvent jobEvent = new JobEvent(UUID.randomUUID().toString(), jobRequestId,
				JobStatus.REQUESTED, now);

		jobRequestMapper.insertJobRequest(jobRequest);
		jobRequestMapper.insertJobEvent(jobEvent);

		requestJob(jobRequest);
		return jobRequestId;
	}

	@Transactional
	public void requeueJob(String jobRequestId, boolean force) {
		Instant now = Instant.now();
		String jobId = jobRequestMapper.findJobIdByJobRequestId(jobRequestId);
		if (force) {
			JobEvent jobEvent = new JobEvent(UUID.randomUUID().toString(), jobRequestId,
					JobStatus.REQUESTED, now);
			jobRequestMapper.insertJobEvent(jobEvent);
		}
		requestJob(new JobRequest(jobRequestId, jobId));
	}

	private void requestJob(JobRequest jobRequest) {
		Message<JobRequest> message = MessageBuilder.withPayload(jobRequest.copyOnlyIds())
				.build();
		source.output().send(message);
	}
}
