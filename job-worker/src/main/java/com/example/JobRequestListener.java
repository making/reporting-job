package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public class JobRequestListener {

	private final JobLauncher jobLauncher;
	private final Job job;
	private final JobRequestService jobRequestService;
	private final static Logger log = LoggerFactory.getLogger(JobRequestListener.class);

	public JobRequestListener(JobLauncher jobLauncher, Job job,
			JobRequestService jobRequestService) {
		this.jobLauncher = jobLauncher;
		this.job = job;
		this.jobRequestService = jobRequestService;
	}

	@StreamListener(Sink.INPUT)
	public void handleRequest(JobRequest jobRequest) {
		String jobRequestId = jobRequest.getJobRequestId();
		String jobId = jobRequest.getJobId();
		try {
			JobStatus latestStatus = jobRequestService.getLatestJobStatus(jobRequestId);
			if (latestStatus != JobStatus.REQUESTED
					&& latestStatus != JobStatus.ABORTED) {
				log.info("Skip processing because current status is {}.", latestStatus);
				return;
			}
			jobRequestService.appendEvent(jobRequestId, JobStatus.STARTED);
			JobParameters jobParameters = new JobParametersBuilder()
					.addString("jobRequestId", jobRequestId).addString("jobId", jobId)
					.toJobParameters();
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);
			jobRequestService.saveRelationship(jobExecution, jobRequestId,
					JobStatus.FINISHED);
		}
		catch (JobInstanceAlreadyCompleteException e) {
			log.info("{}", e.getMessage());
			jobRequestService.appendEvent(jobRequestId, JobStatus.FINISHED);
		}
		catch (JobExecutionException e) {
			log.warn("Job Execution Failed (jobRequestId={}, message={})", jobRequestId,
					e.getMessage());
			jobRequestService.appendEvent(jobRequestId, JobStatus.ABORTED);
			throw new RuntimeException(e);
		}
		catch (RuntimeException e) {
			log.warn("Aborted (jobRequestId={}, message={})", jobRequestId,
					e.getMessage());
			jobRequestService.appendEvent(jobRequestId, JobStatus.ABORTED);
			throw e;
		}
	}
}
