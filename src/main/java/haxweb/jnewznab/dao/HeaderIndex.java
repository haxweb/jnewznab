package haxweb.jnewznab.dao;

import java.util.List;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;

import haxweb.jnewznab.poc.NNTPHeader;

public class HeaderIndex extends AbstractElasticDao {

	public static boolean saveHeaders(List<NNTPHeader> headers) {
		for (NNTPHeader header : headers) {
			try {
				getBulkProcessor().add(new IndexRequest("headers", "header").source(getObjectMapper().writeValueAsBytes(header)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}
	
}
