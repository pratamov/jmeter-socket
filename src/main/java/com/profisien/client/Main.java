package com.profisien.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.IOUtils;

import stormpot.PoolException;

public class Main implements Runnable{

	private static final String HOST = "10.35.65.175";
	//private static final String HOST = "localhost";
	private static final int PORT = 9103;
	private static byte[] request = null;
	
	private static Integer success = 0;
	
	private final SocketConnectionDao dao;
	private static CountDownLatch latch;
	
	public Main(SocketConnectionDao dao) {
		this.dao = dao;
	}
	
	public static void main(String[] args) {
		
		int poolSize = 300;
		int threadCount = 100;
		
		// initializate pool class
		SocketConnectionConfiguration configuration = new SocketConnectionConfiguration(HOST, PORT);
		configuration.setPoolSize(poolSize);
		SocketConnectionBlazePool pool = new SocketConnectionBlazePool(configuration);
		// --------------
		
		long startTime = System.nanoTime();
		long beforeUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		
		latch = new CountDownLatch(threadCount);
		// creating new threads
		for (int i = 0; i < threadCount; i++) {
			
			try {
				SocketConnectionDao dao = pool.claim();
				new Thread(new Main(dao)).start();
				
			} catch (PoolException | InterruptedException e) {
				System.out.println(e.getMessage());
			}
			
		}
		// --------------------
		try {
			latch.await();
			
			System.out.println("----- Configuration -----");
			System.out.println("Pool size: " + poolSize);
			System.out.println("Thread count: " + threadCount);
			
			long endTime = System.nanoTime();
			long afterUsedMem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			double duration = (endTime - startTime)/1000000;
			double tps = threadCount*1000/duration;
			double memoryConsumption = (afterUsedMem - beforeUsedMem) / (1024 * 1024);
			long poolAllocation = pool.allocationCount();
			
			System.out.println("----- Performance -----");
			System.out.println("Memory Consumption: " + memoryConsumption + " MB");
			System.out.println("Pool allocation count: " + poolAllocation);
			System.out.println("Execution time: " + duration + " MS");
			System.out.println("Transation per second: " + String.format("%.2f", tps) + " TPS");
			System.out.println("Success rate: " + (success*100 / threadCount) + "%");
			
		} catch (InterruptedException e1) {}
		
		try {
			
			pool.close();
		
		} catch (InterruptedException e) {
		
		}

	}
	
	/**
	 * 
	 * Generate dummy request from file
	 */
	private static byte[] generateRequest(String filename) {
		
		if (request != null && request.length > 0)
			return request;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(filename);
			request = IOUtils.toByteArray(is);
			
		} catch (IOException e) {
		}
		return request;
		
	}

	@Override
	public void run() {
		
		byte[] request = generateRequest("newas400_req.txt");
		
		try {
			
			// consume the response here
			byte[] response = dao.invoke(request);
			if (response.length > 0) {

				synchronized(success) {
					success++;
				}
				
			}
			
		} catch (IOException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			System.out.println(sw.toString());
		} finally {
			// make sure the socket is ready to reused by another thread
			dao.release();
		}
		
		latch.countDown();
		
	}

}
