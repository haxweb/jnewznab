package haxweb.jnewznab.dao;

import java.net.UnknownHostException;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;

import haxweb.jnewznab.poc.ElasticClient;

public class AbstractElasticDao {
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static ObjectMapper getObjectMapper() {
		return mapper;
	}
	
	public static BulkProcessor.Builder getBulkProcessorBuilder() {
		try {
			return BulkProcessor.builder(ElasticClient.getInstance(), new BulkProcessor.Listener() {
				@Override
				public void beforeBulk(long executionId, BulkRequest request) {}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, Throwable failure) {}
				
				@Override
				public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {}
			});
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
}
