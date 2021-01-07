package com.fms.fingerprint.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fms.fingerprint.listener.ListenerFingerprintCapture;
import com.zkteco.biometric.FingerprintCaptureListener;
import com.zkteco.biometric.FingerprintSensor;

@Component
public class ZkService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public ZkService() {
		init();
	}

	public void init() {
		FingerprintSensor fp = new FingerprintSensor();
		FingerprintCaptureListener fcl = new ListenerFingerprintCapture();
		fp.setFingerprintCaptureListener(fcl);
		logger.debug("Initialize ZKService");
	}
	
}
