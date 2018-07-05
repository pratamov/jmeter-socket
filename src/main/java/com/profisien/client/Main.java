package com.profisien.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import stormpot.PoolException;

public class Main implements Runnable{

	private static final String HOST = "10.35.65.175";
	private static final int PORT = 9103;
	private static byte[] request = null;
	
	private final SocketConnectionDao dao;
	
	public Main(SocketConnectionDao dao) {
		this.dao = dao;
	}
	
	public static void main(String[] args) {
		
		int poolSize = 1000;
		int threadCount = 10000;
		
		SocketConnectionConfiguration configuration = new SocketConnectionConfiguration(HOST, PORT);
		configuration.setPoolSize(poolSize);
		SocketConnectionPool pool = new SocketConnectionPool(configuration);
		
		long startTime = System.nanoTime();
		long beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		
		long maxPoolUsage = 0;
		
		for (int i = 1; i <= threadCount; i++) {
			
			try {
				SocketConnectionDao dao = pool.clain();
				new Thread(new Main(dao)).start();
				maxPoolUsage = Math.max((long)maxPoolUsage, pool.allocationCount());
				
			} catch (PoolException | InterruptedException e) {}
			
		}
		long endTime = System.nanoTime();
		long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		
		double duration = (endTime - startTime)/1000000;
		double tps = threadCount*1000/duration;
		
		double memoryConsumption = (afterUsedMem - beforeUsedMem) / (1024 * 1024);
		
		System.out.println("----- Configuration -----");
		System.out.println("Pool size: " + poolSize);
		System.out.println("Thread count: " + threadCount);
		
		System.out.println("----- Performance -----");
		System.out.println("Memory Consumption: " + memoryConsumption + " MB");
		System.out.println("Maximum Pool usage: " + (100 * maxPoolUsage / poolSize) + "%");
		System.out.println("Execution time: " + duration + " MS");
		System.out.println("Transation per second: " + String.format("%.2f", tps) + " TPS");
		
		try {
			
			pool.close();
		
		} catch (InterruptedException e) {
		
		}

	}
	
	private static byte[] generateRequest(String filename) {
		
		if (request != null && request.length > 0)
			return request;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(filename);
			request = IOUtils.toByteArray(is);
			
		} catch (IOException e) {}
		return request;
		
	}

	@Override
	public void run() {
		
		byte[] request = generateRequest("newas400_res.txt");
		
		try {
			
			byte[] response = dao.invoke(request);
			// consume the response here
			dao.close();
			
		} catch (IOException e) {
			
		} finally {
			dao.release();
		}
		
	}

}
