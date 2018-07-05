package com.profisien.jmeter;

import java.io.Serializable;

import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class TestPoolSamplerClient extends AbstractJavaSamplerClient implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static int counter = 0;
	
	@Override
	public SampleResult runTest(JavaSamplerContext context) {
		counter++;
		SampleResult result = new SampleResult();
        result.sampleStart();
		result.sampleEnd();
		result.setSuccessful(true);
		result.setResponseCodeOK();
		result.setResponseMessage("counter : " + counter);
		return result;
	}

}
