package telrob.mdubbo.provider;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Map;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import telrob.mdubbo.ClassPathXmlContext;
import telrob.mdubbo.provider.parse.CallInvoke;
import telrob.mdubbo.provider.parse.ParseBin;
import telrob.mdubbo.utils.ByteUtils;

public class EchoServerHandler extends ChannelInboundHandlerAdapter{
	private ClassPathXmlContext classPathXmlContext;
	public EchoServerHandler(ClassPathXmlContext classPathXmlContext) {
		this.classPathXmlContext=classPathXmlContext;
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buff=(ByteBuf)(msg);
		ByteArrayOutputStream byteout=new ByteArrayOutputStream();
		int total=0;
		for(int i=0;i<4;i++) {
			byte[]temp=new byte[4];
			buff.readBytes(temp);
			total+=ByteUtils.bytes2int(temp);
			byteout.write(temp);
		}
		byte[]rr=new byte[total];
		buff.readBytes(rr);
		byteout.write(rr);
		byte[]data=byteout.toByteArray();
		byteout.close();
		//开始解析并回调
		ParseBin parse=new ParseBin(data);
		byte[]result=CallInvoke.invoke(classPathXmlContext, parse);
		ByteBuf buffOut=Unpooled.buffer();
		buffOut.writeBytes(ByteUtils.int2byte(result.length));
		buffOut.writeBytes(result);
		ctx.writeAndFlush(buffOut);
		super.channelRead(ctx, msg);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//System.out.println("EchoServerHandler.channelReadComplete");
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//System.out.println("关闭本次连接.......");
        ctx.close();
	}
	
	
}
