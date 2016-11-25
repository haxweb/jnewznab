package haxweb.jnewznab.poc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.parser.NTFTPEntryParser;
import org.apache.commons.net.nntp.NNTPClient;
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
		NNTPClient client = NntpClientPool.getInstance().acquire();
		client.iterateNewsgroups().forEach(info -> {
			newsgroups.add(info);
		});
		NntpClientPool.getInstance().recycle(client);
		return newsgroups;
	}
	
	public static void main(String[] args) {
		try {
//			NewsGroupIndexer.getNewsGroupList().iterator().forEachRemaining(item -> {
//				System.out.println(item.getNewsgroup());
//			});
//			refreshNewsGroupsList();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
