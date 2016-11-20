package haxweb.jnewznab.poc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import haxweb.jnewznab.utils.PropertiesLoader;

public class ElasticClient {

	private static TransportClient instance;
	
	public static TransportClient getInstance() throws UnknownHostException {
		if (instance == null) {
			instance = new PreBuiltTransportClient(Settings.EMPTY)
				.addTransportAddress(
					new InetSocketTransportAddress(
							InetAddress.getByName(PropertiesLoader.getProperty("elastic.host")), 
							Integer.valueOf(PropertiesLoader.getProperty("elastic.port"))
						)
				);
		}
		return instance;
	}
	
	public static void closeClient() {
		if (instance != null) {
			instance.close();
		}
	}
}
