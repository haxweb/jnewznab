package haxweb.jnewznab.poc;

import org.apache.commons.net.nntp.NewsgroupInfo;
import org.elasticsearch.search.DocValueFormat.DateTime;

import com.fasterxml.jackson.annotation.JsonCreator;

public class NewsGroupIndex {

	private int id;
	
	private String newsgroup;
	
	private DateTime lastRefresh;
	
	private boolean enableIndex;
	
	private Long firstArticleId;
	
	private Long lastArticleId;
	
	private Long indexedFirstArticleId;
	
	private Long indexedLastArticleId;
	
	@JsonCreator
	public NewsGroupIndex() {
		
	}
	
	public NewsGroupIndex(NewsgroupInfo nntpNewsGroup, boolean enableIndex) {
		this.newsgroup = nntpNewsGroup.getNewsgroup();
		this.firstArticleId = nntpNewsGroup.getFirstArticleLong();
		this.lastArticleId = nntpNewsGroup.getLastArticleLong();
		this.enableIndex = enableIndex;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NewsGroupIndex) {
			return this.newsgroup.equals(((NewsGroupIndex) obj).getNewsgroup());
		}
		return false;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DateTime getLastRefresh() {
		return lastRefresh;
	}

	public void setLastRefresh(DateTime lastRefresh) {
		this.lastRefresh = lastRefresh;
	}

	public String getNewsgroup() {
		return newsgroup;
	}

	public void setNewsgroup(String newsgroup) {
		this.newsgroup = newsgroup;
	}

	public Long getLastArticleId() {
		return lastArticleId;
	}

	public void setLastArticleId(Long lastArticleId) {
		this.lastArticleId = lastArticleId;
	}

	public Long getFirstArticleId() {
		return firstArticleId;
	}

	public void setFirstArticleId(Long firstArticleId) {
		this.firstArticleId = firstArticleId;
	}

	public boolean isEnableIndex() {
		return enableIndex;
	}

	public void setEnableIndex(boolean enableIndex) {
		this.enableIndex = enableIndex;
	}

	public Long getIndexedFirstArticleId() {
		return indexedFirstArticleId;
	}

	public void setIndexedFirstArticleId(Long indexedFirstArticleId) {
		this.indexedFirstArticleId = indexedFirstArticleId;
	}

	public Long getIndexedLastArticleId() {
		return indexedLastArticleId;
	}

	public void setIndexedLastArticleId(Long indexedLastArticleId) {
		this.indexedLastArticleId = indexedLastArticleId;
	}
	
}
