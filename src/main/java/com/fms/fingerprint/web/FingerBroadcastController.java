package com.fms.fingerprint.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.fms.fingerprint.model.FingerBroadcastModel;

@Controller
public class FingerBroadcastController {
	
	  @MessageMapping("/zk")
	  @SendTo("/finger-data")
	  public void greeting(FingerBroadcastModel model) throws Exception {
	    Thread.sleep(1000); // simulated delay
	    System.out.println("ini muncul broadcast");
//	    return model;
	  }

}
