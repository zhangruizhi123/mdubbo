package telrob.mdubbo;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import telrob.mdubbo.consumer.MyInvocationHandler;
import telrob.mdubbo.consumer.SendPack;
import telrob.mdubbo.provider.NettyService;

public class ClassPathXmlContext {
	//客户端
	public static final int CONSUMER=0;
	//服务器
	public static final int PROVIDER=1;
	//存储bean
	private Map<String,Object>beanResult=new HashMap<String,Object>();
	//客户端发包类
	private SendPack sendPack=null;
	//服务器发包类
	private NettyService nettyService;
	//端口
	private int port=8050;
	//加载xml
	/**
	 * 
	 * @param path
	 * @param type 0客户端，1服务提供者
	 */
	public ClassPathXmlContext(String path,int type) {
		String text;
		try {
			text = readXml(path);
			Document doc =DocumentHelper.parseText(text);
			Element rootEle=doc.getRootElement();
			if(type==CONSUMER) {
				Element protocol =rootEle.element("protocol");
				String host=null;
				int pool=20;
				if(protocol!=null) {
					host=protocol.attribute("host").getValue();
					try {
						port=Integer.parseInt(protocol.attribute("port").getValue());
						pool=Integer.parseInt(protocol.attribute("pool").getValue());
					}catch(Exception e) {
					}
				}
				sendPack=new SendPack(host, port, pool);
				Iterator iter =rootEle.elementIterator("interface");
				while(iter.hasNext()) {
					Element recordEle = (Element) iter.next();
					String value=recordEle.attribute("class").getValue();
					addBean(value);
				}
			}else if(type==PROVIDER) {
				Element protocol =rootEle.element("protocol");
				if(protocol!=null) {
					try {
						port=Integer.parseInt(protocol.attribute("port").getValue());
					}catch(Exception e) {
					}
				}
				Iterator iter =rootEle.elementIterator("service");
				while(iter.hasNext()) {
					Element recordEle = (Element) iter.next();
					String value=recordEle.attribute("class").getValue();
					addBean(value);
				}
				
				nettyService=new NettyService(port, this);
				nettyService.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * 读取xml
	 * @param path
	 * @return
	 * @throws Exception
	 */
	private String readXml(String path) throws Exception {
		FileInputStream file=new FileInputStream(path);
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		byte[]data=null;
		try {
			int len;
			byte[]bb=new byte[1024*4];
			while((len=file.read(bb))>0) {
				bout.write(bb,0,len);
			}
			data=bout.toByteArray();
		}finally {
			file.close();
			bout.close();
		}
		return new String(data);
	}
	
	
	/**
	 * 添加bean
	 * @param name
	 * @throws Exception 
	 */
	private void addBean(String name) throws Exception {
		Class<?>cls=Class.forName(name);
		if(cls.isInterface()) {
			String className=cls.getName();
			//添加代理
			Object obj=Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {cls}, new MyInvocationHandler(cls.getName(),sendPack));
			beanResult.put(className, obj);
		}else {
			Class<?>[]inter=cls.getInterfaces();
			String className=inter[0].getName();
			Object obj=cls.newInstance();
			beanResult.put(className, obj);
		}
	}
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Object getBean(String name) {
		return beanResult.get(name);
	}
	/**
	 * 获取bean
	 * @param cls
	 * @return
	 */
	public <T> T getBean(Class<T>cls) {
		return (T)beanResult.get(cls.getName());
	}
}
