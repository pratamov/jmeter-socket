package com.profisien.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public class MinaDummyServer {

	private static final int PORT = 9103;

	public static void main(String[] args) throws IOException {
		
		IoAcceptor acceptor = new NioSocketAcceptor();		
		acceptor.setHandler( new MinaDummyServerHandler());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
		acceptor.bind(new InetSocketAddress(PORT));

	}

}
