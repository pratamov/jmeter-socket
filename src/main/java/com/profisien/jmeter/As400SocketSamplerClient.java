package com.profisien.jmeter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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
        defaultParameters.addArgument("HOST", "10.35.65.175");
        defaultParameters.addArgument("PORT", "9103");
        defaultParameters.addArgument("ERROR_ON_NO_RESPONSE", "false");
        defaultParameters.addArgument("SHOW_ERROR_ONLY", "false");
        defaultParameters.addArgument("REQUEST_MESSAGE_FILENAME", "newas400_res.txt");
        return defaultParameters;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		String host = context.getParameter("HOST");
		int port = Integer.parseInt(context.getParameter("PORT"));
		boolean errorOnNoResponse = "true".equals(context.getParameter("ERROR_ON_NO_RESPONSE"));
		boolean showErrorOnly = "true".equals(context.getParameter("SHOW_ERROR_ONLY"));
		String requestMessageFilename = context.getParameter("REQUEST_MESSAGE_FILENAME");
		
		SampleResult result = new SampleResult();
        result.sampleStart();
        
        Socket socket = null;
        DataOutputStream dout = null;
        DataInputStream din = null;
		try {

			int length = 0;
			byte[] resbyte = null;
			
			socket = new Socket(InetAddress.getByName(host), port);
			dout = new DataOutputStream(socket.getOutputStream());
			din = new DataInputStream(socket.getInputStream());

			byte[] request = getRequest(requestMessageFilename);
			dout.write(request);
			dout.flush();
			
			byte[] headerbyte = new byte[4];
			din.read(headerbyte);
            length = ByteBuffer.wrap(headerbyte).order(ByteOrder.BIG_ENDIAN).getInt();  
            resbyte = new byte[length];
            din.read(resbyte);
			
			try {
				dout.close();
				din.close();
				socket.close();
			} catch (Exception e) {}
			
			if (length == 0 && errorOnNoResponse)
				throw new Exception("NO RESPONSE");
			
			if (showErrorOnly)
				return null;
			
			result.sampleEnd();
			result.setSuccessful(true);
			result.setResponseData(resbyte);
			result.setResponseCodeOK();
			result.setResponseMessage(length+"");
			
		} catch (Exception e) {
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			result.sampleEnd();
			result.setSuccessful(false);
			result.setResponseCode("404");
			result.setResponseMessage(sw.toString());
			
		}
		return result;
	}
	
	public static byte[] getRequest(String filename) {
		if (request != null && request.length > 0)
			return request;
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream(filename);
			request = IOUtils.toByteArray(is);
			
		} catch (IOException e) {}
		return request;
	}

}
