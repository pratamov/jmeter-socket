package com.profisien.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import stormpot.Poolable;
import stormpot.Slot;

public class SocketConnectionDao implements Poolable {
	
	private final Slot slot;
	private final Socket socket;
	
	public SocketConnectionDao(Slot slot, Socket socket) throws IOException {
		
		this.slot = slot;
		this.socket = socket;
		
	}
	
	public byte[] invoke(byte[] request) throws IOException {
		
		DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
		DataInputStream din = new DataInputStream(socket.getInputStream());
		
		dout.write(request);
		dout.flush();
		
		byte[] headerbyte = new byte[4];
		din.read(headerbyte);
        int length = ByteBuffer.wrap(headerbyte).order(ByteOrder.BIG_ENDIAN).getInt();
        
        byte[] responseBytes = new byte[length];
        din.read(responseBytes);
        
        dout.close();
        din.close();
        
        return responseBytes;
		
	}
	
	@Override
	public void release() {
		
		slot.release(this);
		
	}
	
	public void close() throws IOException {
		
		socket.close();
		
	}
	
	public boolean isClosed() {
		return socket.isClosed();
	}

}
