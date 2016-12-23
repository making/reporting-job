package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public class JobRequestListener {

	private final ReportingJob reportingJob;
	private final JobRequestService jobRequestService;
	private final static Logger log = LoggerFactory.getLogger(JobRequestListener.class);

	public JobRequestListener(ReportingJob reportingJob,
			JobRequestService jobRequestService) {
		this.reportingJob = reportingJob;
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
			jobRequestService.changeStatus(jobRequestId, JobStatus.STARTED);
			reportingJob.run(jobId);
			jobRequestService.changeStatus(jobRequestId, JobStatus.FINISHED);
		}
		catch (RuntimeException e) {
			log.warn("Aborted (jobRequestId={}, message={})", jobRequestId,
					e.getMessage());
			jobRequestService.changeStatus(jobRequestId, JobStatus.ABORTED);
			throw e;
		}
	}
}
