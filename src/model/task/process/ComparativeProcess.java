package model.task.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.AbstractModel;
import model.task.preProcess.GenerateTextModel;
import model.task.process.caracteristicBuilder.AbstractCaracteristicBuilder;
import model.task.process.comparativeMethod.AbstractComparativeMethod;
import model.task.process.indexBuilder.AbstractIndexBuilder;
import model.task.process.processCompatibility.ParametrizedMethod;
import model.task.process.scoringMethod.AbstractScoringMethod;
import optimize.SupportADNException;
import optimize.parameter.ADN;
import textModeling.Corpus;
import textModeling.MultiCorpus;
import textModeling.SentenceModel;
import tools.Pair;

/**
 * ComparativeProcess, défini comme l'extraction des différences entre deux ensembles de corpus.
 * @author valnyz
 *
 */
@SuppressWarnings("rawtypes")
public class ComparativeProcess extends AbstractProcess {

	//private Thread t;
	//private ComparativeProcess[] threads = null;
	int nbThreads = 0;
	
	protected List<Corpus> listCorpus = new ArrayList<Corpus>();
	
	protected List<AbstractIndexBuilder> indexBuilders;
	protected List<AbstractCaracteristicBuilder> caracteristicBuilders;
	protected List<AbstractScoringMethod> scoringMethods;
	protected AbstractComparativeMethod comparativeMethod;
	
	//Matrice de résumé : Dimension = MultiCorpus et List Corpus à résumer
	protected List<Pair<SentenceModel, String>> summary = new ArrayList<Pair<SentenceModel, String>>();
	
	public ComparativeProcess(int id) throws SupportADNException {
		super(id);
	}
	
	@Override
	public AbstractProcess makeCopy() throws Exception {
		return null;
	}

	@Override
	public void initADN() throws Exception {
		initCorpusToCompress();
		
		for (Corpus c : getCurrentMultiCorpus()) {
			if (listCorpusId.contains(c.getiD()))
				listCorpus.add(c);
		}
		
		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders)
				indexBuilder.setCurrentProcess(this);
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilder : caracteristicBuilders)
				caracteristicBuilder.setCurrentProcess(this);
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods)
				scoringMethod.setCurrentProcess(this);
		}
		if (comparativeMethod != null)
			comparativeMethod.setCurrentProcess(this);

		adn = new ADN(supportADN);

		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders)
				indexBuilder.initADN();
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilder : caracteristicBuilders)
				caracteristicBuilder.initADN();
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods)
				scoringMethod.initADN();
		}
		if (comparativeMethod != null)
			comparativeMethod.initADN();
		
		initCompatibility(indexBuilders, caracteristicBuilders, scoringMethods, comparativeMethod);
	}
	
	/**
	 * Initialisation du process
	 * Lecture du corpus à résumer
	 */
	@Override
	public void init() throws Exception {
		for (Corpus c : getCurrentMultiCorpus()) {
			GenerateTextModel.readTempDocument(getModel().getOutputPath() + File.separator + "temp", c, readStopWords);
			System.out.println("Corpus " + c.getiD() + " read");
		}
	}
	
	private void initCompatibility(List<AbstractIndexBuilder> indexBuilders, List<AbstractCaracteristicBuilder> caracteristicBuilders, List<AbstractScoringMethod> scoringMethods, AbstractComparativeMethod comparativeMethod) {
		List<ParametrizedMethod> listMethod = new ArrayList<ParametrizedMethod>();
		
		if (indexBuilders != null) {
			listMethod.addAll(indexBuilders);
		}
		if (caracteristicBuilders != null) {
			listMethod.addAll(caracteristicBuilders);
		}
		if (scoringMethods != null) {
			listMethod.addAll(scoringMethods);
		}
		if (comparativeMethod != null) {
			listMethod.add(comparativeMethod);
		}
		
		for (ParametrizedMethod pm : listMethod) {
			for (ParametrizedMethod pm2 : listMethod) {
				if (pm != pm2 && pm.isOutCompatible(pm2)) {
					pm.setCompatibility(pm2);
				}
			}
		}
	}

	/**
	 * Process en lui-même, application de :
	 * 	- IndexBuilders
	 * 	- CaracteristicBuilders
	 * 	- ScoringMethods
	 * sur un corpus
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void process() throws Exception {
		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders) {
				indexBuilder.processIndex(listCorpus);
			}
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilder : caracteristicBuilders) {
				caracteristicBuilder.processCaracteristics(listCorpus);
			}
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods) {
				scoringMethod.computeScores(listCorpus);
			}
		}
		if (comparativeMethod != null)
			summary = comparativeMethod.calculateDifference(listCorpus);
	}

	/**
	 * Application de SelectionMethod sur l'ensemble des corpus
	 */
	@Override
	public void finish() throws Exception {
		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders)
				indexBuilder.finish();
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilder : caracteristicBuilders)
				caracteristicBuilder.finish();
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods) {
				scoringMethod.finish();
			}
		}
		if (comparativeMethod != null)
			comparativeMethod.finish();
	}
	
	public boolean isListCorpus(Corpus corpus) {
		return isListCorpus(corpus.getiD());
	}
	
	public boolean isListCorpus(int corpusId) {
		return listCorpusId.contains(corpusId);
	}

	@Override
	public void initOptimize() throws Exception {
	}

	@Override
	public void optimize() throws Exception {
	}

	public List<AbstractIndexBuilder> getIndexBuilders() {
		return indexBuilders;
	}

	public void setIndexBuilders(List<AbstractIndexBuilder> indexBuilders) {
		this.indexBuilders = indexBuilders;
		for (AbstractIndexBuilder aib : indexBuilders) {
			if (aib.getSupportADN() != null)
				supportADN.putAll(aib.getSupportADN());
		}
	}

	public List<AbstractCaracteristicBuilder> getCaracteristicBuilders() {
		return caracteristicBuilders;
	}

	public void setCaracteristicBuilders(List<AbstractCaracteristicBuilder> caracteristicBuilders) {
		this.caracteristicBuilders = caracteristicBuilders;
		for (AbstractCaracteristicBuilder acb : caracteristicBuilders) {
			if (acb.getSupportADN() != null)
				supportADN.putAll(acb.getSupportADN());
		}
	}

	public List<AbstractScoringMethod> getScoringMethods() {
		return scoringMethods;
	}

	public void setScoringMethods(List<AbstractScoringMethod> scoringMethods) {
		this.scoringMethods = scoringMethods;
		for (AbstractScoringMethod acm : scoringMethods) {
			if (acm.getSupportADN() != null)
				supportADN.putAll(acm.getSupportADN());
		}
	}

	public AbstractComparativeMethod getComparativeMethod() {
		return comparativeMethod;
	}

	public void setComparativeMethod(AbstractComparativeMethod comparativeMethod) {
		this.comparativeMethod = comparativeMethod;
	}

	@Override
	public void setCurrentMultiCorpus(MultiCorpus currentMultiCorpus) {
		super.setCurrentMultiCorpus(currentMultiCorpus);
		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders)
				indexBuilder.setCurrentMultiCorpus(currentMultiCorpus);
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilders : caracteristicBuilders)
				caracteristicBuilders.setCurrentMultiCorpus(currentMultiCorpus);
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods)
				scoringMethod.setCurrentMultiCorpus(currentMultiCorpus);
		}
		if (comparativeMethod != null)
			comparativeMethod.setCurrentMultiCorpus(currentMultiCorpus);
	}
	
	@Override
	public void setModel(AbstractModel model) {
		super.setModel(model);
		if (indexBuilders != null) {
			for (AbstractIndexBuilder indexBuilder : indexBuilders)
				indexBuilder.setModel(model);
		}
		if (caracteristicBuilders != null) {
			for (AbstractCaracteristicBuilder caracteristicBuilders : caracteristicBuilders)
				caracteristicBuilders.setModel(model);
		}
		if (scoringMethods != null) {
			for (AbstractScoringMethod scoringMethod : scoringMethods)
				scoringMethod.setModel(model);
		}
		if (comparativeMethod != null)
			comparativeMethod.setModel(model);
	}

	public List<Pair<SentenceModel, String>> getSummary() {
		return summary;
	}
}
