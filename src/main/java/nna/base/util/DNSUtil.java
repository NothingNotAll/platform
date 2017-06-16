package nna.base.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

public class DNSUtil {
	static private SelectorProvider provider=SelectorProvider.provider();
	public static SocketChannel getSocektChannel(String dnsname) throws IOException{
		SocketChannel channel=provider.openSocketChannel();
		channel.configureBlocking(false);
		channel.bind(getSocketWithDNSName(getDNSAndContext(dnsname)[0]));
		return channel;
	}
	public static String[] getDNSAndContext(String url){
		String[] strs=new String[2];
		int index=url.indexOf("/", 7);
		if(index==-1){
			strs[0]=url.substring(7);
			strs[1]="/";
		}else{
			strs[0]=url.substring(7,index);
			strs[1]=url.substring(index);
		}
		return strs;
	}
	public static SocketAddress getSocketWithDNSName(String dnsName){
		return new InetSocketAddress(dnsName, 80);
	}
	public static void main(String[] args) {
		try {
			System.out.println(InetAddress.getByName("www.baidu.com").getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println("01234".substring(1));
		System.out.println(getSocketWithDNSName("www.nnall.cn"));
		System.out.println(getSocketWithDNSName("www.nnaall.com"));
		System.out.println(getSocketWithDNSName("www.baidu.com"));
	}
}
