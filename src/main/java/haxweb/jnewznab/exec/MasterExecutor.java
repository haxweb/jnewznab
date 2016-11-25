package haxweb.jnewznab.exec;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import haxweb.jnewznab.utils.PropertiesLoader;

public class MasterExecutor {

	private static ExecutorService master;
	
	private static ExecutorService indexer;
	
	private static ExecutorService getMasterInstance() {
		if (master == null) {
			master = Executors.newFixedThreadPool(Integer.valueOf(PropertiesLoader.getProperty("jobs.master.pool")));
		}
		
		return master;
	}
	
	private static ExecutorService getIndexerInstance() {
		if (indexer == null) {
			indexer = Executors.newFixedThreadPool(Integer.valueOf(PropertiesLoader.getProperty("jobs.indexer.pool")));
		}
		
		return indexer;
	}
	
	public static void addIndexHeaderJob(Runnable job) {
		getIndexerInstance().execute(job);
	}
	
	public static void addTodosMaster(List<Runnable> jobs) {
		if (jobs != null) {
			for (Runnable job: jobs) {
				getMasterInstance().execute(job);	
			}
		}
	}
	
	public static void shutdownAll() {
		getMasterInstance().shutdown();
		getIndexerInstance().shutdown();
	}
	
}
