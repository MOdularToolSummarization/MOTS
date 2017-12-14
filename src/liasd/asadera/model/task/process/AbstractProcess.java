package liasd.asadera.model.task.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import liasd.asadera.exception.LacksOfFeatures;
import liasd.asadera.model.task.AbstractTask;
import liasd.asadera.optimize.Optimize;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.tools.wordFilters.StopWordsFilter;
import liasd.asadera.tools.wordFilters.TrueFilter;
import liasd.asadera.tools.wordFilters.WordFilter;
import liasd.asadera.tools.wordFilters.WordStopListFilter;

public abstract class AbstractProcess extends Optimize implements AbstractTask/*, Runnable*/ {
	//private Thread t;
	//private AbstractProcess[] threads = null;
	//int nbThreads = 0;
	
	//private static final Logger logger = Logger.getLogger("AbstractProcess"); 
	
	protected int summarizeIndex = 0;
	protected List<Integer> listCorpusId;
	protected Corpus corpusToSummarize;
	protected WordFilter filter;
	
	public AbstractProcess(int id) throws SupportADNException {
		super(id);
		
		supportADN = new HashMap<String, Class<?>>();
	}

	public abstract AbstractProcess makeCopy() throws Exception;
	
	protected void initCopy(AbstractProcess p) throws Exception {
		p.setListCorpusId(new ArrayList<Integer>(listCorpusId));
		p.setFilter(filter);
		p.setSupportADN(new HashMap<String, Class<?>>(supportADN));
	}

	public final void initCorpusToCompress() throws NumberFormatException, LacksOfFeatures {
		listCorpusId = new ArrayList<Integer>();
		if (getModel().getProcessOption(id, "CorpusIdToSummarize").equals("all"))
			for (int corpusId=0; corpusId<getCurrentMultiCorpus().size(); corpusId++)
				listCorpusId.add(corpusId);
		else
			for (String corpusId : getModel().getProcessOption(id, "CorpusIdToSummarize").split("\t"))
				listCorpusId.add(Integer.parseInt(corpusId));
	}
	
	@Override
	public abstract void initADN() throws Exception;

	@Override
	public void init() throws Exception {
		try {
			if (!Boolean.parseBoolean(getModel().getProcessOption(id, "ReadStopWords")))
				filter = new StopWordsFilter();
		}
		catch (LacksOfFeatures e) {
			try {
				filter = new WordStopListFilter(getModel().getProcessOption(id, "StopWordListFile"));
			}
			catch (LacksOfFeatures e2) {
			}
		}
	}
	
	/**
	 * Appel à super.process() une fois les process effectués.
	 */
	@Override
	public abstract void process() throws Exception;
	
	@Override
	public abstract void finish() throws Exception;
	
	@Override
	public abstract void initOptimize() throws Exception;

	@Override
	public abstract void optimize() throws Exception;
	
	public void setSummarizeIndex(int summarizeIndex) {
		this.summarizeIndex = summarizeIndex;
	}
	
	public List<Integer> getListCorpusId() {
		return listCorpusId;
	}

	public Corpus getCorpusToSummarize() {
		return corpusToSummarize;
	}
	
	public void setCorpusToSummarize(Corpus corpusToSummarize) {
		this.corpusToSummarize = corpusToSummarize;
	}
	
	/**
	 * Retourne l'Id du corpus résumé
	 * @return
	 */
	public Integer getSummarizeCorpusId() {
		return summarizeIndex;
	}
	
	
	public void setListCorpusId(List<Integer> listCorpusId) {
		this.listCorpusId = listCorpusId;
	}
	
	public WordFilter getFilter() {
		if (filter == null)
			filter = new TrueFilter();
		return filter;
	}

	public void setFilter(WordFilter filter) {
		this.filter = filter;
	}

	/**
	 * Nécessite utilisation de ROUGE
	 */
	@Override
	public double getScore() {
		if (!getModel().isbRougeEvaluation())
			return 0;
		else
			return score;
	}
}
