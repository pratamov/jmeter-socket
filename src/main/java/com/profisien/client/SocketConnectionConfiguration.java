package com.profisien.client;

public class SocketConnectionConfiguration {
	
	private int port;
	private String host;
	private int poolSize;
	
	public SocketConnectionConfiguration() {
		this.port = 9103;
		this.host = "10.35.65.175";
	}
	
	public SocketConnectionConfiguration(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
}
