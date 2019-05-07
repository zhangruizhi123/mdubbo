package telrob.mdubbo.utils;

public class ByteUtils {
	public static byte[] int2byte(int value) {
		byte[]bb=new byte[4];
		bb[0]=(byte) (value&0xFF);
		bb[1]=(byte) ((value>>8)&0xFF);
		bb[2]=(byte) ((value>>16)&0xFF);
		bb[3]=(byte) ((value>>24)&0xFF);
		return bb;
	}
	
	/**
	 * 将数组转换成int
	 * @return
	 */
	public static int bytes2int(byte[]data) {
		int result=0;
		result=(data[0]&0x0FF)|((data[1]&0x0FF)<<8)|((data[2]&0x0FF)<<16)|((data[3]&0x0FF)<<24);
		return result;
	}
}
