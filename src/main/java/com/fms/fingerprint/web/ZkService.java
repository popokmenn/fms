package com.fms.fingerprint.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fms.fingerprint.listener.ListenerFingerprintCapture;
import com.fms.fingerprint.model.ZKContext;
import com.zkteco.biometric.FingerprintCaptureListener;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import com.zkteco.biometric.FingerprintSensorEx;

@Component
public class ZkService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private WorkThread workThread = null;

	
	public ZkService() {
		init();
	}

	public void init() {
		FingerprintCaptureListener fcl = new ListenerFingerprintCapture();
		int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
		
		//Initialize
		ZKContext.cbRegTemp = 0;
		ZKContext.REGISTER_MODE = false;
		ZKContext.IDENTIFY_MODE = false;
		ZKContext.iFid = 1;
		ZKContext.enroll_idx = 0;

		if (0 != ZKContext.mhDevice) {
			logger.warn("Please close device first!\n");
			return;
		}
		if (FingerprintSensorErrorCode.ZKFP_ERR_OK != FingerprintSensorEx.Init()) {
			logger.error("Init failed!\\n");
			return;
		}
		ret = FingerprintSensorEx.GetDeviceCount();
		if (ret < 0) {
			logger.error("No devices connected!\n");
			FreeSensor();
			return;
		}
		if (0 == (ZKContext.mhDevice = FingerprintSensorEx.OpenDevice(0)))
		{
			logger.error("Open device fail, ret = " + ret + "!\n");
			FreeSensor();
			return;
		}
		if (0 == (ZKContext.mhDB = FingerprintSensorEx.DBInit()))
		{
			logger.error("Init DB fail, ret = " + ret + "!\n");
			FreeSensor();
			return;
		}
		
		byte[] paramValue = new byte[4];
		int[] size = new int[1];
		
		size[0] = 4;
		FingerprintSensorEx.GetParameters(ZKContext.mhDevice, 1, paramValue, size);
		ZKContext.fpWidth = byteArrayToInt(paramValue);
		size[0] = 4;
		FingerprintSensorEx.GetParameters(ZKContext.mhDevice, 2, paramValue, size);
		ZKContext.fpHeight = byteArrayToInt(paramValue);
		
		ZKContext.imgbuf = new byte[ZKContext.fpWidth*ZKContext.fpHeight];
		ZKContext.mbStop = false;
		workThread = new WorkThread(fcl);
	    workThread.start();
		logger.debug("Open succ! Finger Image Width:" + ZKContext.fpWidth + ",Height:" + ZKContext.fpHeight +"\n");
	}

	private void FreeSensor() {
		ZKContext.mbStop = true;
		try { // wait for thread stopping
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (0 != ZKContext.mhDB) {
			FingerprintSensorEx.DBFree(ZKContext.mhDB);
			ZKContext.mhDB = 0;
		}
		if (0 != ZKContext.mhDevice) {
			FingerprintSensorEx.CloseDevice(ZKContext.mhDevice);
			ZKContext.mhDevice = 0;
		}
		FingerprintSensorEx.Terminate();
	}
	
	public static int byteArrayToInt(byte[] bytes) {
		int number = bytes[0] & 0xFF;  
	    // "|="按位或赋值。  
	    number |= ((bytes[1] << 8) & 0xFF00);  
	    number |= ((bytes[2] << 16) & 0xFF0000);  
	    number |= ((bytes[3] << 24) & 0xFF000000);  
	    return number;  
	 }
	
	private class WorkThread extends Thread {
		
		private FingerprintCaptureListener fpListener;
				
        public WorkThread(FingerprintCaptureListener fpListener) {
			super();
			this.fpListener = fpListener;
		}

		@Override
        public void run() {
            super.run();
            int ret = 0;
            while (!ZKContext.mbStop) {
            	ZKContext.templateLen[0] = 2048;
            	if (0 == (ret = FingerprintSensorEx.AcquireFingerprint(ZKContext.mhDevice, ZKContext.imgbuf, ZKContext.template, ZKContext.templateLen)))
            	{
            		if (ZKContext.nFakeFunOn == 1)
                	{
                		byte[] paramValue = new byte[4];
        				int[] size = new int[1];
        				size[0] = 4;
        				int nFakeStatus = 0;
        				//GetFakeStatus
        				ret = FingerprintSensorEx.GetParameters(ZKContext.mhDevice, 2004, paramValue, size);
        				nFakeStatus = byteArrayToInt(paramValue);
        				logger.warn("ret = "+ ret +",nFakeStatus=" + nFakeStatus);
        				if (0 == ret && (byte)(nFakeStatus & 31) != 31)
        				{
        					logger.warn("Is a fake finger?\n");
        					return;
        				}
                	}
                	fpListener.captureOK(ZKContext.imgbuf);
                	fpListener.extractOK(ZKContext.imgbuf);
//                	OnExtractOK(template, templateLen[0]);
            	}
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}

//FingerprintSensor fp = new FingerprintSensor();
//FingerprintCaptureListener fcl = new ListenerFingerprintCapture();
//fp.setFingerprintCaptureListener(fcl);
//logger.debug("Initialize ZKService");

