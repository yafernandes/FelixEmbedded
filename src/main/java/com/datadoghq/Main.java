package com.datadoghq;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.felix.framework.FrameworkFactory;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.launch.Framework;

import datadog.trace.api.Trace;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		//  Deletes existing cache
		FileUtils.deleteDirectory(new File("felix-cache"));
		
		for (int i = 0; i < 5; i++) {
			doSomethingBefore(1 * 1000);
		}
				
		FrameworkFactory factory = new FrameworkFactory();

		Properties config = new Properties();

		// cd /Required for 4.6.1
		config.setProperty("org.osgi.framework.system.capabilities", "osgi.ee; osgi.ee=\"JavaSE\";version:List=\"1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8\"");
		config.setProperty("org.osgi.service.http.port", "8080");
//		config.setProperty("org.osgi.framework.bootdelegation",
//				"datadog.slf4j.*,datadog.slf4j,datadog.trace.api.*,datadog.trace.api,datadog.trace.bootstrap.*,datadog.trace.bootstrap,datadog.trace.context.*,datadog.trace.context,io.opentracing.*,io.opentracing");

		Framework felix = factory.newFramework(config);

		felix.init();

		BundleContext bc = felix.getBundleContext();

		AutoProcessor.process(config, bc);
		felix.start();

		Bundle hw = bc.installBundle(
				"file:///Users/alex.fernandes/eclipse-workspace/FelixHelloWorld/target/FelixHelloWorld-0.0.1.jar");
		hw.start();

		for (int i = 0; i < 5; i++) {
			doSomethingDuring(1 * 1000);
		}

		System.out.println("=== Bundles ===");
		for (Bundle bundle : bc.getBundles()) {
			System.out.println(bundle.getBundleId() + " : " + bundle.getSymbolicName() + " : " + bundle.getState() + " : " + bundle.getVersion());
		}

		felix.stop();
		felix.waitForStop(0);
		System.exit(0);

	
		for (int i = 0; i < 5; i++) {
			doSomethingAfter(1 * 1000);
		}


	}

	@Trace
	private static void doSomethingBefore(int i) throws InterruptedException {
		Thread.sleep(i);
	}


	@Trace
	private static void doSomethingDuring(int i) throws InterruptedException {
		Thread.sleep(i);
	}


	@Trace
	private static void doSomethingAfter(int i) throws InterruptedException {
		Thread.sleep(i);
	}

}
