package com.fms.fingerprint.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zkteco.biometric.FingerprintCaptureListener;

public class ListenerFingerprintCapture implements FingerprintCaptureListener {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public void captureError(int arg0) {
		logger.debug("Capture Error", arg0);
		
	}

	@Override
	public void captureOK(byte[] arg0) {
		logger.debug("Capture Success {}", arg0);
		
	}

	@Override
	public void extractOK(byte[] arg0) {
		logger.debug("Extract Success {}", arg0);
		
	}

}
