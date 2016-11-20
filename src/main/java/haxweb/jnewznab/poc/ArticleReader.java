package haxweb.jnewznab.poc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.net.io.DotTerminatedMessageReader;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;

import haxweb.jnewznab.dao.IndexJobDao;
import haxweb.jnewznab.exec.IndexerJob;
import haxweb.jnewznab.exec.MasterExecutor;
import haxweb.jnewznab.exec.IndexerJob.IndexerJobStatus;
import haxweb.jnewznab.nntp.client.NntpClientPool;

public class ArticleReader implements Runnable {

    private Long firstArticleId;
    
    private Long lastArticleId;
    
    private NNTPClient newsClient;
    
    private String newsgroup;
    
    private DotTerminatedMessageReader headerBuffer;
    
    private String currentHeaderLine;
    
    private boolean debug;
    
    private IndexerJob jobTodo;
    
    private NNTPHeader header;
    
    public static class builder {
    	
    	private boolean debug = false;
    	
    	private Long firstArticleId;
    	
    	private Long lastArticleId;
    	
    	private String newsGroup;
    	
    	public ArticleReader.builder firstArticleId(Long articleId) {
    		this.firstArticleId = articleId;
    		return this;
    	}
    	
    	public ArticleReader.builder lastArticleId(Long articleId) {
    		this.lastArticleId = articleId;
    		return this;
    	}
    	
    	public ArticleReader.builder debug(boolean debug) {
    		this.debug = debug;
    		return this;
    	}
    	
    	public ArticleReader.builder newsGroup(String newsGroup) {
    		this.newsGroup = newsGroup;
    		return this;
    	}
    	
    	public ArticleReader build() {
    		return new ArticleReader(this);
    	}
    }
    
    public ArticleReader(IndexerJob job) {
    	this.firstArticleId = job.getFirstArticleId();
    	this.lastArticleId = job.getLastArticleId();
    	this.newsgroup = job.getNewsgroup();
    	this.jobTodo = job;
    }
    
    private ArticleReader(ArticleReader.builder builder) {
    	this.firstArticleId = builder.firstArticleId;
    	this.lastArticleId = builder.lastArticleId;
    	this.debug = builder.debug;
    	this.newsgroup = builder.newsGroup;
    }

    private boolean updateStatus(IndexerJob.IndexerJobStatus status) {
    	this.jobTodo.setStatus(status);
		return IndexJobDao.update(jobTodo);
    }
    
	@Override
	public void run() {
		try {
			NewsgroupInfo groupInfo = new NewsgroupInfo();
			NNTPClient client = NntpClientPool.getInstance().acquire();
			client.selectNewsgroup(newsgroup, groupInfo);
			
			headerBuffer = (DotTerminatedMessageReader) client.retrieveArticleInfo(firstArticleId, lastArticleId);
			
			updateStatus(IndexerJobStatus.RUNNING);
			List<NNTPHeader> headers = new ArrayList<>();
			header = new NNTPHeader();
			
			String[] headerArray = null;
	        if (headerBuffer != null) {
	        	System.out.println("Fetcing " + firstArticleId + " to " + lastArticleId);
	            while((currentHeaderLine = headerBuffer.readLine()) != null) {
	            	headerArray = currentHeaderLine.split("\t");
	            	
	            	header.put("articleId", headerArray[0]);
	            	header.put("subject", headerArray[1]);
	            	header.put("author", headerArray[2]);
	            	header.put("date", headerArray[3]);
	            	header.put("message-id", headerArray[4]);
	            	header.put("references", headerArray[5]);
	            	header.put("byte-count", headerArray[6]);
	            	header.put("line-count", headerArray[7]);
	            	
	            	headers.add(header);
	            	headerArray = null;
	            }
	            headerBuffer.close();
	        }
	        
	        NntpClientPool.getInstance().recycle(client);
	        System.out.println("Fetched " + headers.size() + " headers.");
	        MasterExecutor.addIndexHeaderJob(new HeaderIndexer(headers, jobTodo));
		} catch (Exception e) {
			e.printStackTrace();
			updateStatus(IndexerJobStatus.ERROR);
		}
	}
	
	public static void main(String[] args) {
		try {
			NewsgroupInfo group = new NewsgroupInfo();
			NNTPClient client = NntpClientPool.getInstance().acquire();
			client.selectNewsgroup("alt.binaries.hdtv.french", group);
			
			Long firstArticle = group.getFirstArticleLong();
			Long lastArticle = group.getLastArticleLong();
			NntpClientPool.getInstance().recycle(client);
			
			List<Runnable> fetchList = new ArrayList<Runnable>();
			ArticleReader.builder builder = new ArticleReader.builder();
			builder.newsGroup("alt.binaries.hdtv.french");
			
			while (firstArticle <= lastArticle) {
				fetchList.add(builder.firstArticleId(firstArticle).lastArticleId(firstArticle+10000).build());
				firstArticle += 10000;
			}
			
			MasterExecutor.addTodosMaster(fetchList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		
//		try {
//			NNTPHeader headerResult = header.get();
//			for (Entry<String, String> headerEntry : headerResult.entrySet()) {
//				System.out.println("Key " + headerEntry.getKey());
//				System.out.println("Value " + headerEntry.getValue());
//			}
//			master.shutdown();
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		} 
	}
	
}
