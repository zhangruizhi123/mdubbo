package telrob.mdubbo.provider.parse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import com.caucho.hessian.io.HessianInput;


/**
 * 解析包
 * @author wh
 *
 */
public class ParseBin {
	/**
	 * 类名
	 */
	private String className;
	/**
	 * 方法名
	 */
	private String methodName;
	/**
	 * 参数类型
	 */
	private Class<?>[]paramType;
	/**
	 * 参数
	 */
	private Object[]param;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParamType() {
		return paramType;
	}
	public void setParamType(Class<?>[] paramType) {
		this.paramType = paramType;
	}
	public Object[] getParam() {
		return param;
	}
	public void setParam(Object[] param) {
		this.param = param;
	}
	
	public ParseBin(byte[]data) throws IOException {
		ByteArrayInputStream in=new ByteArrayInputStream(data);
		byte[]temp=new byte[4];
		in.read(temp, 0, 4);
		int classNameLen=bytes2int(temp);
		in.read(temp, 0, 4);
		int methodNameLen=bytes2int(temp);
		in.read(temp, 0, 4);
		int paramTypeLen=bytes2int(temp);
		in.read(temp, 0, 4);
		int paramDataLen=bytes2int(temp);
		
		byte[]classNameData=new byte[classNameLen];
		in.read(classNameData, 0, classNameLen);
		className=new String(classNameData);
		
		byte[]methodNameData=new byte[methodNameLen];
		in.read(methodNameData, 0, methodNameLen);
		methodName=new String(methodNameData);
		
		if(paramTypeLen>0) {
			byte[]paramTypeData=new byte[paramTypeLen];
			in.read(paramTypeData, 0, paramTypeLen);
			ByteArrayInputStream hbin=new ByteArrayInputStream(paramTypeData);
			HessianInput Hessian2Input=new HessianInput(hbin);
			paramType=(Class<?>[]) Hessian2Input.readObject();
			Hessian2Input.close();
			hbin.close();
		}
		if(paramDataLen>0) {
			byte[]paramDataData=new byte[paramDataLen];
			in.read(paramDataData, 0, paramDataLen);
			
			ByteArrayInputStream hbin=new ByteArrayInputStream(paramDataData);
			HessianInput Hessian2Input=new HessianInput(hbin);
			param= (Object[]) Hessian2Input.readObject();
			Hessian2Input.close();
			hbin.close();
		}
		in.close();
	}
	/**
	 * 将数组转换成int
	 * @return
	 */
	private int bytes2int(byte[]data) {
		int result=0;
		result=(data[0]&0x0FF)|((data[1]&0x0FF)<<8)|((data[2]&0x0FF)<<16)|((data[3]&0x0FF)<<24);
		return result;
	}
	@Override
	public String toString() {
		return "ParseBin [className=" + className + ", methodName=" + methodName + ", paramType="
				+ Arrays.toString(paramType) + ", param=" + Arrays.toString(param) + "]";
	}
	
	
}
