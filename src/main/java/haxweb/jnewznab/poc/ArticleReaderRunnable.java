package haxweb.jnewznab.poc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.naming.AuthenticationException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.nntp.NNTP;
import org.apache.commons.net.nntp.NNTPClient;
import org.apache.commons.net.nntp.NewsgroupInfo;

import haxweb.jnewznab.utils.PropertiesLoader;

/**
 * Sample program demonstrating the use of article header and body retrieval
 */
public class ArticleReaderRunnable implements Runnable {

    private Long articleId;
    
    private String articleStringId;
    
    private NNTPClient newsClient;
    
    private String newsgroup;
    
    private BufferedReader headerBuffer;
    
    private String currentHeaderLine;
    
    private boolean debug;
    
    private NNTPHeader header;
    
    private NNTPClient getClient() throws AuthenticationException, IOException {
    	if (newsClient == null) {
    		newsClient = new NNTPClient();
    		
    		if (debug) {
    			newsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
    		}
    	}

    	if (!newsClient.isConnected()) {
    		newsClient.connect(PropertiesLoader.getProperty("usenet.host"));
    	}

        if(!newsClient.authenticate(PropertiesLoader.getProperty("usenet.user"), PropertiesLoader.getProperty("usenet.password"))) {
            System.out.println("Authentication failed for user  !");
            throw new AuthenticationException("Authentication to UseNet server failed");
        }
    	
    	return newsClient;
    }
    
    public static class builder {
    	
    	private boolean debug = false;
    	
    	private Long articleId;
    	
    	private String articleStringId;
    	
    	private String newsGroup;
    	
    	public ArticleReaderRunnable.builder articleId(Long articleId) {
    		this.articleId = articleId;
    		return this;
    	}
    	
    	public ArticleReaderRunnable.builder articleStringId(String articleId) {
    		this.articleStringId = articleId;
    		return this;
    	}
    	
    	public ArticleReaderRunnable.builder debug(boolean debug) {
    		this.debug = debug;
    		return this;
    	}
    	
    	public ArticleReaderRunnable.builder newsGroup(String newsGroup) {
    		this.newsGroup = newsGroup;
    		return this;
    	}
    	
    	public ArticleReaderRunnable build() {
    		return new ArticleReaderRunnable(this);
    	}
    }
    
    private ArticleReaderRunnable(ArticleReaderRunnable.builder builder) {
    	this.articleId = builder.articleId;
    	this.articleStringId = builder.articleStringId;
    	this.debug = builder.debug;
    	this.newsgroup = builder.newsGroup;
    }
    
	@Override
	public void run() {
		try {
			NewsgroupInfo groupInfo = new NewsgroupInfo();
			getClient().selectNewsgroup(newsgroup, groupInfo);
			
			if (articleId != null) {
				headerBuffer = getClient().retrieveArticleHeader(articleId);
			} else if (articleStringId != null) {
				headerBuffer = (BufferedReader) getClient().retrieveArticleHeader(articleStringId);
			}
			
			header = new NNTPHeader();
	        if (headerBuffer != null) {
	            while((currentHeaderLine = headerBuffer.readLine()) != null) {
	            	String[] headerArray = currentHeaderLine.split(":");
	            	header.put(headerArray[0], headerArray[1].trim());
	            }
	            headerBuffer.close();
	        }
	        getClient().disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static NNTPClient getNNTPClientInstance(boolean debug) throws SocketException, IOException, AuthenticationException {
		NNTPClient newsClient = null;
		if (newsClient == null) {
    		newsClient = new NNTPClient();
    		if (debug) {
    			newsClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
    		}
    	}

    	if (!newsClient.isConnected()) {
    		newsClient.connect(PropertiesLoader.getProperty("usenet.host"));
    	}

        if(!newsClient.authenticate(PropertiesLoader.getProperty("usenet.user"), PropertiesLoader.getProperty("usenet.password"))) {
            System.out.println("Authentication failed for user  !");
            throw new AuthenticationException("Authentication to UseNet server failed");
        }
    	
    	return newsClient;
	}

	public static void main(String[] args) {
		ExecutorService master = Executors.newWorkStealingPool(20);
		
		try {
			NewsgroupInfo group = new NewsgroupInfo();
			
			NNTPClient client = getNNTPClientInstance(false);
			client.selectNewsgroup("alt.binaries.hdtv.french", group);
			
			Long firstArticle = group.getFirstArticleLong();
			Long lastArticle = Long.valueOf(1000000);
			
			Collection<ArticleReader> fetchList = new ArrayList<ArticleReader>();
			ArticleReaderRunnable.builder builder = new ArticleReaderRunnable.builder();
			builder.newsGroup("alt.binaries.hdtv.french");
			
			while (firstArticle <= lastArticle) {
				master.execute(builder.articleId(firstArticle).newsGroup("alt.binaries.hdtv.french").build());
				firstArticle++;
			}
			
			master.awaitTermination(Long.valueOf("1000"), TimeUnit.SECONDS);
		} catch (Exception e) {
			
		}
	}
	
}
