package com.profisien.client;

import java.net.InetAddress;
import java.net.Socket;

import stormpot.Allocator;
import stormpot.Slot;

public class SocketConnectionAllocator implements Allocator<SocketConnectionDao>{
	
	private final SocketConnectionConfiguration configuration;
	
	public SocketConnectionAllocator(SocketConnectionConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public SocketConnectionDao allocate(Slot slot) throws Exception {
		synchronized(configuration) {
			Socket socket = new Socket(InetAddress.getByName(configuration.getHost()), configuration.getPort());
			return new SocketConnectionDao(slot, socket);
		}
	}

	@Override
	public void deallocate(SocketConnectionDao dao) throws Exception {
		dao.close();
	}


}
