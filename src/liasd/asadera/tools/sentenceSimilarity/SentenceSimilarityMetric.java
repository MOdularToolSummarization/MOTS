package liasd.asadera.tools.sentenceSimilarity;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.textModeling.SentenceModel;

public abstract class SentenceSimilarityMetric {	

	protected List<ParametrizedType> listParameterIn;
	protected List<ParametrizedType> listParameterOut;
	
	public SentenceSimilarityMetric() {
		listParameterIn = new ArrayList<ParametrizedType>();
		listParameterOut = new ArrayList<ParametrizedType>();
	}

	/*public double computeSimilarity(Map<SentenceModel, Object> sentenceCaracteristic, SentenceModel s1, SentenceModel s2) throws Exception {
		if (sentenceCaracteristic.values().iterator().next() instanceof double[])
			return computeSimilarity((double[])sentenceCaracteristic.get(s1), (double[])sentenceCaracteristic.get(s2));
		else if (sentenceCaracteristic.values().iterator().next() instanceof double[][])
			return computeSimilarity((double[][])sentenceCaracteristic.get(s1), (double[][])sentenceCaracteristic.get(s2));
		else
			throw new RuntimeException("Sentence caracteristic error when computing similarity !");
	}*/
	
	public double computeSimilarity(Map<SentenceModel, Object> sentenceCaracteristic, SentenceModel s1, SentenceModel s2) throws Exception {
		return computeSimilarity(sentenceCaracteristic.get(s1), sentenceCaracteristic.get(s2));
	}
	
	public double computeSimilarity(Map<SentenceModel, Object> sentenceCaracteristic, Object s1, SentenceModel s2) throws Exception {
		if (s1.getClass() == sentenceCaracteristic.get(s2).getClass())
			return computeSimilarity(s1, sentenceCaracteristic.get(s2));
		else
			throw new RuntimeException("Type error when computing sentence similarity !");
	}
	
	public abstract double computeSimilarity(Object s1, Object s2) throws Exception;
	
	public static SentenceSimilarityMetric instanciateSentenceSimilarity(ParametrizedMethod compatibleMethod, String similarityMethod) throws Exception {
		Class<?> cl;
		cl = Class.forName("liasd.asadera.tools.sentenceSimilarity." + similarityMethod);
	    @SuppressWarnings("rawtypes")
		Constructor ct = cl.getConstructor();
	    SentenceSimilarityMetric o = (SentenceSimilarityMetric) ct.newInstance();
	    if (!o.isOutCompatible(compatibleMethod))
	    	throw new RuntimeException("Error when trying compatibility between SimilaritryMetric and ParametrizedMethod.");
	    o.setCompatibility(compatibleMethod);
	    return (SentenceSimilarityMetric) o;
	}
	
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return compatibleMethod.getParameterTypeIn().containsAll(listParameterIn);
	}
	
	public void setCompatibility(ParametrizedMethod compatibleMethod) {
	}
}
