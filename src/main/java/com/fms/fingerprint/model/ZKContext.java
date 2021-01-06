package com.fms.fingerprint.model;

public class ZKContext {

	public static int fpWidth = 0;
	public static int fpHeight = 0;
	public static long mhDevice = 0;
	public static boolean mbStop = true;
	public static long mhDB = 0;
	public static byte[] imgbuf = null;
	public static byte[] template = new byte[2048];
	public static int[] templateLen = new int[1];
	public static int nFakeFunOn = 1;
	public static boolean REGISTER_MODE = false;
	public static boolean IDENTIFY_MODE = false;
	public static int iFid = 1;
	public static int enroll_cnt = 3;
	public static int enroll_idx = 0;
	public static byte[][] regtemparray = new byte[3][2048];
	public static byte[] lastRegTemp = new byte[2048];
	public static int cbRegTemp = 0;
	
}
