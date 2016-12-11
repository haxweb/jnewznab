package haxweb.jnewznab.exec;

import com.fasterxml.jackson.annotation.JsonCreator;

public class IndexerJob {

	public enum IndexerJobStatus {
		PENDING,
		RUNNING,
		ERROR,
		FINISHED,
		PAUSED
	}
	
	private String id;
	
	private String newsgroup;
	
	private Long firstArticleId;
	
	private Long lastArticleId;
	
	private IndexerJobStatus status;
	
	@JsonCreator
	public IndexerJob() {}
	
	public IndexerJob(Long firstArticleId, Long lastArticleId, String newsgroup) {
		this.firstArticleId = firstArticleId;
		this.lastArticleId = lastArticleId;
		this.newsgroup = newsgroup;
		this.status = IndexerJobStatus.PENDING;
		this.id = this.newsgroup + "#" + firstArticleId + "-" + lastArticleId;
	}
	
	public String getNewsgroup() {
		return newsgroup;
	}

	public void setNewsgroup(String newsgroup) {
		this.newsgroup = newsgroup;
	}

	public Long getFirstArticleId() {
		return firstArticleId;
	}

	public void setFirstArticleId(Long firstArticleId) {
		this.firstArticleId = firstArticleId;
	}

	public Long getLastArticleId() {
		return lastArticleId;
	}

	public void setLastArticleId(Long lastArticleId) {
		this.lastArticleId = lastArticleId;
	}

	public IndexerJobStatus getStatus() {
		return status;
	}

	public void setStatus(IndexerJobStatus status) {
		this.status = status;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "IndexerJob#" + this.id + " [ " + this.status + " ][ " + this.newsgroup + " ][ " + this.firstArticleId + " to " + this.lastArticleId + " ]";
	}
}
