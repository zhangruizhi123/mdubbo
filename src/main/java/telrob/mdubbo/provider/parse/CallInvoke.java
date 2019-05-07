package telrob.mdubbo.provider.parse;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

import com.caucho.hessian.io.HessianOutput;

import telrob.mdubbo.ClassPathXmlContext;

public class CallInvoke {
	
	public static byte[] invoke(ClassPathXmlContext ctx,ParseBin parse) throws Exception {
		
		Object obj=ctx.getBean(parse.getClassName());
		if(obj!=null) {
			Method method=obj.getClass().getDeclaredMethod(parse.getMethodName(), parse.getParamType());
			Object data=method.invoke(obj, parse.getParam());
			ByteArrayOutputStream byteout=new ByteArrayOutputStream();
			HessianOutput hout=new HessianOutput(byteout);
			hout.writeObject(data);
			byte[]result=byteout.toByteArray();
			hout.close();
			byteout.close();
			return result;
		}
		return null;
	}
}
