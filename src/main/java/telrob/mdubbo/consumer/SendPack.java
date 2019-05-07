package telrob.mdubbo.consumer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 发送包的程序
 * @author wh
 *
 */
public class SendPack {
	private String host;
	private int port;
	private LinkedList<Socket>listSock=new LinkedList<Socket>();
	public SendPack(String host,int port,int size) throws Exception {
		this.host=host;
		this.port=port;
		for(int i=0;i<size;i++) {
			Socket temp=new Socket(host, port);
			listSock.add(temp);
		}
	}
	
	/**
	 * 发送数据并等待
	 * @param data
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public synchronized byte[] sendData(final byte[]data) throws Exception {
		ExecutorService executorService=Executors.newSingleThreadExecutor();
		final Socket sock=getSocket();
		
		Future<byte[]> future=executorService.submit(new Callable<byte[]>() {
			public byte[] call() throws Exception {
				OutputStream out=sock.getOutputStream();
				out.write(data);
				InputStream in=sock.getInputStream();
				//获取数据长度
				byte[]bb=new byte[4];
				in.read(bb, 0, 4);
				int length=bytes2int(bb);
				byte[]dataB=new byte[length];
				in.read(dataB);
				return dataB;
			}
		});
		addSocket(sock);
		//超时直接返回
		byte[] result=future.get(3, TimeUnit.SECONDS);
		executorService.shutdown();
		return result;
	}
	
	public synchronized Socket getSocket() {
		return listSock.pop();
	}
	
	public synchronized void addSocket(Socket sock) throws Exception {
		if(sock!=null&&sock.isConnected()) {
			listSock.push(sock);
		}else {
			listSock.push(new Socket(host, port));
		}
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
}
