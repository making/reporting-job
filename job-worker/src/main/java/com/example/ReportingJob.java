package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportingJob {
	private final static Logger log = LoggerFactory.getLogger(ReportingJob.class);

	public void run(String jobId) {
		log.info("Start Job(jobId={})", jobId);
		for (int i = 0; i < 40; i++) {
			try {
				Thread.sleep(250);
				System.out.print("*");
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		System.out.println();
	}
}
