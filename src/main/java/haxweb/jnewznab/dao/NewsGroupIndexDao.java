package haxweb.jnewznab.dao;

import org.elasticsearch.action.get.GetResponse;

import haxweb.jnewznab.poc.ElasticClient;
import haxweb.jnewznab.poc.NewsGroupIndex;

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
	
}
