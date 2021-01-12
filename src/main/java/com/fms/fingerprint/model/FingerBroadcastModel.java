package com.fms.fingerprint.model;

public class FingerBroadcastModel {
	
	private byte[] fingerImg;

	public FingerBroadcastModel(byte[] fingerImg) {
		super();
		this.fingerImg = fingerImg;
	}

	public byte[] getFingerImg() {
		return fingerImg;
	}

	public void setFingerImg(byte[] fingerImg) {
		this.fingerImg = fingerImg;
	}

}
