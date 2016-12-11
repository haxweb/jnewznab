package haxweb.jnewznab.dao;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;

import haxweb.jnewznab.exec.IndexerJob;
import haxweb.jnewznab.exec.IndexerJob.IndexerJobStatus;
import haxweb.jnewznab.poc.ElasticClient;

public class IndexJobDao extends AbstractElasticDao {

	public static boolean save(List<IndexerJob> jobs) {
		jobs.iterator().forEachRemaining(job -> {
			try {
				getBulkProcessor().add(new IndexRequest("indexjobs", "indexjob", job.getId()).source(getObjectMapper().writeValueAsBytes(job)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		getBulkProcessor().flush();
		return true;
	}
	
	public static boolean update(IndexerJob job) {
		try {
			ElasticClient.getInstance()
				.prepareUpdate("indexjobs", "indexjob", job.getId())
				.setDoc(getObjectMapper().writeValueAsBytes(job))
				.execute();
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static List<IndexerJob> getJobsByStatus(String newsgroup, String status, int limit) {
		try {
			SearchResponse scrollResp;
			scrollResp = ElasticClient.getInstance().prepareSearch("indexjobs")
					.setTypes("indexjob")
					.addSort("lastArticleId", SortOrder.DESC)
					.setQuery(QueryBuilders.queryStringQuery("newsgroup:" + newsgroup + " AND status:" + status))
					.setSize(limit).execute().actionGet();
			
			List<IndexerJob> results = new ArrayList<>();
			scrollResp.getHits().iterator().forEachRemaining(item -> {
				try {
					results.add(getObjectMapper().readValue(item.getSourceAsString(), IndexerJob.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return results;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static List<IndexerJob> getRunningJobs(String newsgroup, int limit) {
		return getJobsByStatus(newsgroup, IndexerJobStatus.RUNNING.toString(), limit);
	}
	
	public static List<IndexerJob> getErrorJobs(String newsgroup, int limit) {
		return getJobsByStatus(newsgroup, IndexerJobStatus.ERROR.toString(), limit);
	}
	
	public static List<IndexerJob> getPendingJobs(String newsgroup, int limit) {
		try {
			SearchResponse scrollResp;
			scrollResp = ElasticClient.getInstance().prepareSearch("indexjobs")
					.setTypes("indexjob")
			        .addSort("lastArticleId", SortOrder.DESC)
			        .setQuery(QueryBuilders.queryStringQuery("newsgroup:" + newsgroup + " AND status:PENDING"))
			        .setSize(limit).execute().actionGet();
		
			List<IndexerJob> results = new ArrayList<>();
			scrollResp.getHits().iterator().forEachRemaining(item -> {
				try {
					results.add(getObjectMapper().readValue(item.getSourceAsString(), IndexerJob.class));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			return results;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(IndexJobDao.getPendingJobs("alt.binaries.boneless", 1000).size());
	}
}
