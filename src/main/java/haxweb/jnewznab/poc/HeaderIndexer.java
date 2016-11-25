package haxweb.jnewznab.poc;

import java.util.List;

import haxweb.jnewznab.dao.HeaderIndex;
import haxweb.jnewznab.dao.IndexJobDao;
import haxweb.jnewznab.exec.IndexerJob;
import haxweb.jnewznab.exec.IndexerJob.IndexerJobStatus;

public class HeaderIndexer implements Runnable {

	private List<NNTPHeader> headers;
	
	private IndexerJob job;
	
	public HeaderIndexer(List<NNTPHeader> headers, IndexerJob job) {
		this.headers = headers;
		this.job = job;
	}
	
	@Override
	public void run() {
		System.out.println("Saving to elastic search.");
		if (HeaderIndex.saveHeaders(headers)) {
			this.job.setStatus(IndexerJobStatus.FINISHED);
		} else {
			this.job.setStatus(IndexerJobStatus.ERROR);
		}
		
		IndexJobDao.update(job);
		this.headers = null;
	}
	
}
