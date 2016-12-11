package haxweb.jnewznab.dao;

import java.util.List;

import org.apache.commons.net.nntp.NewsgroupInfo;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;

import haxweb.jnewznab.poc.ElasticClient;
import haxweb.jnewznab.poc.NewsGroupIndex;
import haxweb.jnewznab.poc.NewsGroupIndexer;

public class NewsGroupIndexDao extends AbstractElasticDao {

	public static NewsGroupIndex getById(String newsgroup) {
		try {
			GetResponse response = ElasticClient.getInstance().prepareGet("newsgroups", "newsgroup", newsgroup).get();
			NewsGroupIndex index = getObjectMapper().readValue(response.getSourceAsBytes(), NewsGroupIndex.class);
			return index;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean update(NewsGroupIndex newsgroup, boolean flush) {
		try {
			getBulkProcessor()
				.add(new UpdateRequest("newsgroups", "newsgroup", newsgroup.getNewsgroup())
					.doc(getObjectMapper().writeValueAsBytes(newsgroup)));
			if (flush) {
				getBulkProcessor().flush();
			}
			return true;
		} catch (Exception e) {
			System.out.println("Error trying to update a NewsgroupIndex [ "+ newsgroup + " ] : ");
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean refreshNewsGroupsList() {
		try {
			List<NewsgroupInfo> newsList = NewsGroupIndexer.getNewsGroupList();
			System.out.println("Fethed " + newsList.size() + " Newsgroups");
			newsList.iterator().forEachRemaining(info -> {
				try {
					NewsGroupIndex index = new NewsGroupIndex(info, false);
					IndexRequest indexRequest = new IndexRequest("newsgroups", "newsgroup", index.getNewsgroup())
							.source(getObjectMapper().writeValueAsBytes(index));
					
					getBulkProcessor()
					.add(new UpdateRequest("newsgroups", "newsgroup", index.getNewsgroup())
							.doc(getObjectMapper().writeValueAsBytes(index))
							.upsert(indexRequest));
				} catch (Exception e) {
					System.out.println("Error trying to refresh newsgroups listing : ");
					e.printStackTrace();
				}
			});
			
			getBulkProcessor().flush();
			return true;
		} catch (Exception e) {
			System.out.println("Error trying to refresh newsgroups listing : ");
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
