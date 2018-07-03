package com.profisien.jmeter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.commons.io.IOUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class As400SocketSamplerClient extends AbstractJavaSamplerClient implements Serializable {

	private static final long serialVersionUID = 1L;
	private static byte[] request = null;

	@Override
	public Arguments getDefaultParameters() {
		Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("HOST", "localhost");
        defaultParameters.addArgument("PORT", "8888");
        return defaultParameters;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String host = context.getParameter("HOST");
		int port = Integer.parseInt(context.getParameter("PORT"));
		
		SampleResult result = new SampleResult();
        result.sampleStart();
        
        Socket socket = null;
        DataOutputStream dout = null;
        DataInputStream din = null;
		try {
			
			socket = new Socket(InetAddress.getByName(host), port);
			dout = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());

			byte[] request = doRequest();
			dout.write(request);
			dout.flush();

			int length = din.readInt();
			String responseData = "";
			
			if (length > 0) {
				byte[] resbyte = new byte[length];
				din.read(resbyte);
				responseData = new String(resbyte);
			}
			
			result.sampleEnd();
			result.setSuccessful(true);
			result.setResponseData(responseData, "IBM285");
			result.setResponseCodeOK();
			result.setResponseMessage("OK");
			
		} catch (Exception e) {
			
			result.sampleEnd();
			result.setSuccessful(false);
			result.setResponseCode("500");
			result.setResponseMessage(e.getMessage());
			
		} finally {
			try {
				dout.close();
			} catch (Exception e) {}
			try {
				din.close();
			} catch (Exception e) {}
			try {
				socket.close();
			} catch (Exception e) {}
		}
		return result;
	}
	
	public static byte[] doRequest() {
		if (request != null && request.length > 0)
			return request;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("abcsin2000_req.txt");
			request = IOUtils.toByteArray(is);
			
		} catch (IOException e) {}
		return request;
	}

}
