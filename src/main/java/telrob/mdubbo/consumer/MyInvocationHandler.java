package telrob.mdubbo.consumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class MyInvocationHandler implements InvocationHandler {
	private String beanName=null;
	SendPack sendPack;
	/**
	 * bean的名字
	 * @param beanName
	 */
	public MyInvocationHandler(String beanName,SendPack sendPack) {
		this.sendPack=sendPack;
		this.beanName=beanName;
	}
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//这里会调用对应的方法
		//对参数进行序列化
		ByteArrayOutputStream resultOut=new ByteArrayOutputStream();
		byte[]paramData=null;
		
		byte[]paramType=null;
		if(args!=null) {
			ByteArrayOutputStream byteout=new ByteArrayOutputStream();
			HessianOutput out=new HessianOutput(byteout);
			out.writeObject(args);
			paramData=byteout.toByteArray();
			byteout.close();
			out.close();
			
			Class<?>[]param=method.getParameterTypes();
			ByteArrayOutputStream typeout=new ByteArrayOutputStream();
			HessianOutput tout=new HessianOutput(typeout);
			tout.writeObject(param);
			paramType=typeout.toByteArray();
			typeout.close();
			tout.close();
		}
		byte[]beanNameData=beanName.getBytes();
		byte[]methodNameData=method.getName().getBytes();
		resultOut.write(int2byte(beanNameData.length));
		resultOut.write(int2byte(methodNameData.length));
		if(paramData!=null) {
			resultOut.write(int2byte(paramType.length));
			resultOut.write(int2byte(paramData.length));
		}else {
			resultOut.write(int2byte(0));
			resultOut.write(int2byte(0));
		}
		resultOut.write(beanNameData);
		resultOut.write(methodNameData);
		if(paramData!=null) {
			resultOut.write(paramType);
			resultOut.write(paramData);
		}
		byte[]resultData=resultOut.toByteArray();
		resultOut.close();
		byte[]returnObject=sendPack.sendData(resultData);
		ByteArrayInputStream bino=new ByteArrayInputStream(returnObject);
		HessianInput hin=new HessianInput(bino);
		Object retObj=hin.readObject();
		hin.close();
		bino.close();
		return retObj;
	}
	
	public byte[] int2byte(int value) {
		byte[]bb=new byte[4];
		bb[0]=(byte) (value&0xFF);
		bb[1]=(byte) ((value>>8)&0xFF);
		bb[2]=(byte) ((value>>16)&0xFF);
		bb[3]=(byte) ((value>>24)&0xFF);
		return bb;
	}

}
