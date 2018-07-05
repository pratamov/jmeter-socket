package com.profisien.client;

import java.util.concurrent.TimeUnit;

import stormpot.Config;
import stormpot.PoolException;
import stormpot.QueuePool;
import stormpot.Timeout;

public class SocketConnectionPool {

	private final QueuePool<SocketConnectionDao> pool;

	public SocketConnectionPool(SocketConnectionConfiguration configuration) {
		SocketConnectionAllocator allocator = new SocketConnectionAllocator(configuration);
		Config<SocketConnectionDao> config = new Config<SocketConnectionDao>().setAllocator(allocator);
		pool = new QueuePool<>(config);
		pool.setTargetSize(configuration.getPoolSize());
	}

	public void close() throws InterruptedException {
		pool.shutdown().await(new Timeout(1, TimeUnit.MINUTES));
	}
	
	public SocketConnectionDao clain() throws PoolException, InterruptedException {
		return pool.claim(new Timeout(1, TimeUnit.SECONDS));
	}
	
	public long allocationCount() {
		return pool.getAllocationCount();
	}
	/*
	public byte[] invoke(byte[] request) throws PoolException, InterruptedException, IOException {
		SocketConnectionDao dao = pool.claim(new Timeout(1, TimeUnit.SECONDS));
		try {
			byte[] response = dao.invoke(request);
			return response;
		} finally {
			dao.release();
		}
	}
	*/

}
