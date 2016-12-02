package haxweb.jnewznab.exec;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.cluster.metadata.IndexNameExpressionResolver;

import haxweb.jnewznab.dao.IndexJobDao;
import haxweb.jnewznab.dao.NewsGroupIndexDao;
import haxweb.jnewznab.exec.IndexerJob.IndexerJobStatus;
import haxweb.jnewznab.poc.ArticleReader;
import haxweb.jnewznab.poc.NewsGroupIndex;
import haxweb.jnewznab.utils.PropertiesLoader;

public class IndexerJobScheduler {

	private static int sliceSize = Integer.valueOf(PropertiesLoader.getProperty("scheduler.slice"));

	private NewsGroupIndex newsgroup;
	
	public IndexerJobScheduler(String newsgroup) {
		this.newsgroup = NewsGroupIndexDao.getById(newsgroup);
	}
	
//	alt.binaries.movies.french
	public static IndexerJobScheduler scheduleJobsForGroup(String newsgroup) {
		IndexerJobScheduler scheduler = new IndexerJobScheduler(newsgroup);
		IndexJobDao.save(scheduler.getSliceJobs());
		
		return scheduler;
	}
	
	public static void reScheduleErrorJobsForGroup(String newsgroup) {
		List<IndexerJob> errorJobs = IndexJobDao.getErrorJobs(newsgroup, 1000);
		errorJobs.iterator().forEachRemaining(job -> {
			job.setStatus(IndexerJobStatus.PENDING);
			IndexJobDao.update(job);
		});
		return;
	}
	
	public List<IndexerJob> getSliceJobs() {
		Long[] slice = getSlice();
		Long start = slice[0];
		Long end = slice[1];
		List<IndexerJob> jobs = new ArrayList<>();
		while (end > start) {
			Long from = (end - sliceSize);
			from = from < start ? start : from;
			jobs.add(new IndexerJob(from, end, newsgroup.getNewsgroup()));
			end = from;
		}
		
		return jobs;
	}
	
	public Long[] getSlice() {
		Long[] slice = new Long[2];
		if (newsgroup.getIndexedFirstArticleId() == null) {
			slice[0] = newsgroup.getFirstArticleId();
		} else {
			slice[0] = newsgroup.getIndexedLastArticleId();
		}
		
		slice[1] = newsgroup.getLastArticleId();
		return slice;
	}
	
	public static void runPendingJobsFor(String newsgroup) {
		List<IndexerJob> jobs;
		List<Runnable> readers = new ArrayList<>();
		jobs = IndexJobDao.getPendingJobs(newsgroup, 1000);
		if (jobs.size() == 0) {
			System.out.println("No job to do");
			return;
		}
		for (IndexerJob todo : jobs) {
			readers.add(new ArticleReader(todo));
		}
		
		System.out.println("Added " + readers.size() + " Header fetcher jobs.");
		MasterExecutor.addTodosMaster(readers);
	}

	public static void main(String[] args) {
		NewsGroupIndexDao.refreshNewsGroupsList();
		IndexerJobScheduler.scheduleJobsForGroup("alt.binaries.series");
//		reScheduleErrorJobsForGroup("alt.binaries.series");
		runPendingJobsFor("alt.binaries.series");
		MasterExecutor.shutdownAll();
	}
}
