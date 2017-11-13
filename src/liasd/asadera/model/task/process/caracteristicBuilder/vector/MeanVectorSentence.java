package liasd.asadera.model.task.process.caracteristicBuilder.vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import liasd.asadera.model.task.process.caracteristicBuilder.AbstractCaracteristicBuilder;
import liasd.asadera.model.task.process.caracteristicBuilder.SentenceCaracteristicBasedIn;
import liasd.asadera.model.task.process.caracteristicBuilder.SentenceCaracteristicBasedOut;
import liasd.asadera.model.task.process.indexBuilder.IndexBasedIn;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.textModeling.TextModel;
import liasd.asadera.textModeling.WordModel;
import liasd.asadera.textModeling.wordIndex.Index;
import liasd.asadera.textModeling.wordIndex.WordVector;

public class MeanVectorSentence extends AbstractCaracteristicBuilder implements IndexBasedIn<WordVector>, SentenceCaracteristicBasedOut {

	private int dimension;
	private Index<WordVector> index;
	protected Map<SentenceModel, Object>  sentenceCaracteristic;
	
	public MeanVectorSentence(int id) throws SupportADNException {
		super(id);

		sentenceCaracteristic = new HashMap<SentenceModel, Object>();
		
		listParameterIn.add(new ParametrizedType(WordVector.class, Index.class, IndexBasedIn.class));
		listParameterOut.add(new ParametrizedType(double[].class, Map.class, SentenceCaracteristicBasedOut.class));
	}

	@Override
	public AbstractCaracteristicBuilder makeCopy() throws Exception {
		MeanVectorSentence p = new MeanVectorSentence(id);
		initCopy(p);
		p.setDimension(dimension);
		return p;
	}

	@Override
	public void initADN() throws Exception {
	}

	@Override
	public void processCaracteristics(List<Corpus> listCorpus) {
		dimension = index.values().iterator().next().getDimension();
		
		for (Corpus corpus : listCorpus) {
			for (TextModel text : corpus) {
				for (SentenceModel sentenceModel : text) {
					int nbWord = 0;
					double[] sentenceVector = new double[dimension];
					Iterator<WordModel> wordIt = sentenceModel.iterator();
					while (wordIt.hasNext()) {
						WordModel wm = wordIt.next();
						if (getCurrentProcess().getFilter().passFilter(wm)) {
							@SuppressWarnings("unlikely-arg-type")
							WordVector word = index.get(wm.getmLemma());
							for (int i=0; i<dimension; i++)
								sentenceVector[i]+=word.getWordVector()[i];
							nbWord++;
						}
					}
					for (int i=0; i<dimension; i++)
						sentenceVector[i]/=nbWord;
					sentenceCaracteristic.put(sentenceModel, sentenceVector);
				}
			}
		}
	}
	
	public void finish() {
		sentenceCaracteristic.clear();
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	@Override
	public void setIndex(Index<WordVector> index) {
		if (index == null)
			throw new NullPointerException("Index is null.");
		this.index = index;
	}
	
	@Override
	public Map<SentenceModel, Object>  getVectorCaracterisic() {
		return sentenceCaracteristic;
	}

	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(double[].class, Map.class, SentenceCaracteristicBasedIn.class));
	}

	@Override
	public void setCompatibility(ParametrizedMethod compMethod) {
		((SentenceCaracteristicBasedIn)compMethod).setCaracterisics(sentenceCaracteristic);;
	}
}
