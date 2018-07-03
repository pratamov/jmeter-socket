package com.profisien.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MinaDummyServerHandler implements IoHandler {

	@Override
	public void exceptionCaught(IoSession session, Throwable e) throws Exception {
		
		System.out.println(e.getMessage());

	}

	@Override
	public void inputClosed(IoSession session) throws Exception {
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		
		Path path = Paths.get(ClassLoader.getSystemResource("abcsout2000_correctressize.txt").toURI());
		byte[] inputFileByte = Files.readAllBytes(path);
		IoBuffer buffer = IoBuffer.wrap(inputFileByte);
		
		String str = message.toString();
		System.out.println(str);
		
		session.write(buffer);
		session.closeOnFlush();

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus arg1) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
	}

}
