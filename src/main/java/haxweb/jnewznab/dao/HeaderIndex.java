package haxweb.jnewznab.dao;

import java.util.List;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;

import haxweb.jnewznab.poc.NNTPHeader;

public class HeaderIndex extends AbstractElasticDao {

	public static boolean saveHeaders(List<NNTPHeader> headers) {
		BulkProcessor processor = getBulkProcessorBuilder().setBulkActions(10000).build();
		headers.iterator().forEachRemaining(header -> {
			try {
				processor.add(new IndexRequest("headers", "header").source(getObjectMapper().writeValueAsBytes(header)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		processor.close();
		return true;
	}
	
}
