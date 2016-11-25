package haxweb.jnewznab.nntp.client;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.AuthenticationException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.nntp.NNTPClient;

import haxweb.jnewznab.utils.PropertiesLoader;

public class NntpClientPool extends ResourcePool<NNTPClient> {

	protected boolean debug;
	
	private static NntpClientPool instance;
	
	public static synchronized NntpClientPool getInstance() {
		if (instance == null) {
			instance = new NntpClientPool(25, false);
		}
		return instance;
	}
	
	protected NntpClientPool(int size, boolean debug) {
		super(size);
		this.debug = debug;
	}

	@Override
    public NNTPClient acquire() throws Exception {
		NNTPClient client = super.acquire();
		if (!client.isConnected()) {
			client.connect(PropertiesLoader.getProperty("usenet.host"));
		}

	    if(!client.authenticate(PropertiesLoader.getProperty("usenet.user"), PropertiesLoader.getProperty("usenet.password"))) {
	        System.out.println("Authentication failed for user  !");
	        throw new AuthenticationException("Authentication to UseNet server failed");
	    }
	    return client;
    }
	
	public boolean closePool() {
        if (!lock.isLocked()) {
            if (lock.tryLock()) {
                try {
                	for (NNTPClient client : this.pool) {
        				client.disconnect();
            		}
            	} catch (IOException e) {
    				e.printStackTrace();
    				lock.unlock();
    				return false;
                } finally {
                    lock.unlock();
                }
            }
        }
		return true;
	}
	
	@Override
	protected NNTPClient createObject() {
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
