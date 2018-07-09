package com.profisien.client;

import java.util.concurrent.TimeUnit;

import stormpot.BlazePool;
import stormpot.Config;
import stormpot.PoolException;
import stormpot.Timeout;

public class SocketConnectionBlazePool {

	private final BlazePool<SocketConnectionDao> pool;

	public SocketConnectionBlazePool(SocketConnectionConfiguration configuration) {
		SocketConnectionAllocator allocator = new SocketConnectionAllocator(configuration);
		
		Config<SocketConnectionDao> config = new Config<SocketConnectionDao>();
		config.setAllocator(allocator);
		config.setExpiration(new SocketConnectionExpiration());
		//config.setBackgroundExpirationEnabled(true);
		config.setSize(configuration.getPoolSize());
		
		pool = new BlazePool<>(config);
		
	}

	public void close() throws InterruptedException {
		pool.shutdown().await(new Timeout(1, TimeUnit.MINUTES));
	}
	
	public SocketConnectionDao claim() throws PoolException, InterruptedException {
		return pool.claim(new Timeout(500, TimeUnit.MILLISECONDS));
	}
	
	public long allocationCount() {
		return pool.getAllocationCount();
	}

}
