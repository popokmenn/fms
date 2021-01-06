package com.fms.fingerprint.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fms.fingerprint.model.ZKContext;
import com.zkteco.biometric.FingerprintCaptureListener;
import com.zkteco.biometric.FingerprintSensorEx;

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
		if (ZKContext.REGISTER_MODE) {
			int[] fid = new int[1];
			int[] score = new int[1];
			int ret = FingerprintSensorEx.DBIdentify(ZKContext.mhDB, ZKContext.template, fid, score);
			if (ret == 0) {
				logger.debug("the finger already enroll by " + fid[0] + ",cancel enroll\n");
				ZKContext.REGISTER_MODE = false;
				ZKContext.enroll_idx = 0;
				return;
			}
			if (ZKContext.enroll_idx > 0 && FingerprintSensorEx.DBMatch(ZKContext.mhDB, ZKContext.regtemparray[ZKContext.enroll_idx - 1], ZKContext.template) <= 0) {
				logger.debug("please press the same finger 3 times for the enrollment\n");
				return;
			}
			System.arraycopy(ZKContext.template, 0, ZKContext.regtemparray[ZKContext.enroll_idx], 0, 2048);
			ZKContext.enroll_idx++;
			if (ZKContext.enroll_idx == 3) {
				int[] _retLen = new int[1];
				_retLen[0] = 2048;
				byte[] regTemp = new byte[_retLen[0]];

				if (0 == (ret = FingerprintSensorEx.DBMerge(ZKContext.mhDB, ZKContext.regtemparray[0], ZKContext.regtemparray[1], ZKContext.regtemparray[2],
						regTemp, _retLen)) && 0 == (ret = FingerprintSensorEx.DBAdd(ZKContext.mhDB, ZKContext.iFid, regTemp))) {
					ZKContext.iFid++;
					ZKContext.cbRegTemp = _retLen[0];
					System.arraycopy(regTemp, 0, ZKContext.lastRegTemp, 0, ZKContext.cbRegTemp);
					// Base64 Template
					logger.debug("enroll succ:\n");
				} else {
					logger.debug("enroll fail, error code=" + ret + "\n");
				}
				ZKContext.REGISTER_MODE = false;
			} else {
				logger.debug("You need to press the " + (3 - ZKContext.enroll_idx) + " times fingerprint\n");
			}
		} else {
			if (ZKContext.IDENTIFY_MODE) {
				int[] fid = new int[1];
				int[] score = new int[1];
				int ret = FingerprintSensorEx.DBIdentify(ZKContext.mhDB, ZKContext.template, fid, score);
				if (ret == 0) {
					logger.debug("Identify succ, fid=" + fid[0] + ",score=" + score[0] + "\n");
				} else {
					logger.debug("Identify fail, errcode=" + ret + "\n");
				}

			} else {
				if (ZKContext.cbRegTemp <= 0) {
					logger.debug("Please register first!\n");
				} else {
					int ret = FingerprintSensorEx.DBMatch(ZKContext.mhDB, ZKContext.lastRegTemp, ZKContext.template);
					if (ret > 0) {
						logger.debug("Verify succ, score=" + ret + "\n");
					} else {
						logger.debug("Verify fail, ret=" + ret + "\n");
					}
				}
			}
		}
	}

}
