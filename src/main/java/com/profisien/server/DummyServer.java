package com.profisien.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DummyServer implements Runnable {

	private static final int PORT = 8888;
	private Socket socket;
	private String name;
	private byte[] data;

	public static void main(String[] args) throws IOException {
		boolean run = true;
		ServerSocket serverSocket = new ServerSocket(PORT);
		int index = 0;

		byte[] inputFileByte = null;
		try {
			Path path = Paths.get(ClassLoader.getSystemResource("abcsout2000_correctressize.txt").toURI());
			inputFileByte = Files.readAllBytes(path);
		} catch (IOException | URISyntaxException e) {
		}
		while (run) {
			Socket socket = serverSocket.accept();
			new Thread(new DummyServer(socket, "#" + index++, inputFileByte)).start();
		}

		serverSocket.close();
	}

	public DummyServer(Socket socket, String name, byte[] data) {
		this.socket = socket;
		this.name = name;
		this.data = data;
	}

	@Override
	public void run() {
		DataOutputStream dout = null;
		//DataInputStream din = null;
		try {
			//din = new DataInputStream(socket.getInputStream());
			//int length = din.readInt();
			//if (length > 0) {
				//byte[] resbyte = new byte[length];
				//din.readFully(resbyte);
				System.out.println("Receive request " + this.name);
			//}
			
			dout = new DataOutputStream(socket.getOutputStream());
			dout.write(data);
			dout.flush();
		} catch (IOException e) {

		} finally {
			try {
				dout.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
