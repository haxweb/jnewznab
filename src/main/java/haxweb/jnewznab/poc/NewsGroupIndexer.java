package haxweb.jnewznab.poc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.nntp.NewsgroupInfo;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;

import com.fasterxml.jackson.databind.ObjectMapper;

import haxweb.jnewznab.nntp.client.NntpClientPool;

public class NewsGroupIndexer {

	public static List<NewsgroupInfo> getNewsGroupList() throws IOException, Exception {
		List<NewsgroupInfo> newsgroups = new ArrayList<>();
		NntpClientPool.getInstance().acquire().iterateNewsgroups().forEach(info -> {
			newsgroups.add(info);
		});
		return newsgroups;
	}
	
	public static boolean refreshNewsGroupsList() throws IOException, Exception {
		List<NewsgroupInfo> infos = getNewsGroupList();
		List<NewsGroupIndex> remoteNewsGroupIndexes = new ArrayList<>();
		TransportClient elasticDb = ElasticClient.getInstance();
		ObjectMapper mapper = new ObjectMapper();
		
		BulkProcessor bulkInsert = BulkProcessor.builder(elasticDb, new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				// TODO Auto-generated method stub
				
			}
		}).setBulkActions(10000).build();
		
		infos.iterator().forEachRemaining(info -> {
			try {
				Long max = Long.valueOf("100000");
				NewsGroupIndex index = new NewsGroupIndex(info, false);
				bulkInsert.add(new IndexRequest("newsgroups", "newsgroup", index.getNewsgroup()).source(mapper.writeValueAsBytes(index)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		bulkInsert.close();
		
//		SearchRequest request = new SearchRequest("newsgroups");
//		SearchResponse responses = elasticDb.prepareSearch("newsgroups").setScroll(new TimeValue(60000)).execute().actionGet();
//		do {
//			SearchHit[] results = responses.getHits().getHits();
//			ObjectMapper mapper = new ObjectMapper();
//			NewsGroupIndex newsGroup = mapper.readValue(results[0].getSourceAsString(), NewsGroupIndex.class);
//		    responses = elasticDb.prepareSearchScroll(responses.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
//		} while(responses.getHits().getHits().length != 0);
		
		return true;
	}
	
	public static void main(String[] args) {
		try {
//			NewsGroupIndexer.getNewsGroupList().iterator().forEachRemaining(item -> {
//				System.out.println(item.getNewsgroup());
//			});
			refreshNewsGroupsList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
