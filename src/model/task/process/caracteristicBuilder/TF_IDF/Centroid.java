package model.task.process.caracteristicBuilder.TF_IDF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import jgibblda.Pair;
import model.task.process.caracteristicBuilder.queryBuilder.QueryBasedIn;
import model.task.process.caracteristicBuilder.queryBuilder.QueryBasedOut;
import model.task.process.processCompatibility.ParametrizedMethod;
import model.task.process.processCompatibility.ParametrizedType;
import optimize.SupportADNException;
import optimize.parameter.Parameter;
import textModeling.Corpus;
import textModeling.SentenceQuery;
import textModeling.wordIndex.InvertedIndex;
import textModeling.wordIndex.TF_IDF.WordTF_IDF;

public class Centroid extends TfIdfVectorSentence implements QueryBasedOut<double[]> {
	
	public static enum Centroid_Parameter {
		NbMaxWordInCentroid("NbMaxWordInCentroid");

		private String name;

		private Centroid_Parameter(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	
	private SentenceQuery<double[]> query;
	protected double[] centroid;
	protected int nbMaxWordInCentroid;

	protected InvertedIndex<WordTF_IDF> invertIndex;
	
	public Centroid(int id) throws SupportADNException {
		super(id);
		
		query = new SentenceQuery<double[]>();
		
		supportADN = new HashMap<String, Class<?>>();
		supportADN.put("NbMaxWordInCentroid", Integer.class);
		
		listParameterOut.add(new ParametrizedType(null, double[].class, QueryBasedOut.class));
	}
	
	@Override
	public Centroid makeCopy() throws Exception {
		Centroid p = new Centroid(id);
		p.setNbMaxWordInCentroid(nbMaxWordInCentroid);
		initCopy(p);
		return p;
	}
	
	@Override
	public void initADN() throws Exception {
		int n = Integer.parseInt(getCurrentProcess().getModel().getProcessOption(id, "NbMaxWordInCentroid"));
		getCurrentProcess().getADN().putParameter(new Parameter<Integer>(Centroid_Parameter.NbMaxWordInCentroid.getName(), n));
		getCurrentProcess().getADN().getParameter(Integer.class, Centroid_Parameter.NbMaxWordInCentroid.getName()).setMaxValue(2*n+1);
		getCurrentProcess().getADN().getParameter(Integer.class, Centroid_Parameter.NbMaxWordInCentroid.getName()).setMinValue(1);
	}
	
	private void init() {
		nbMaxWordInCentroid = getCurrentProcess().getADN().getParameterValue(Integer.class, Centroid_Parameter.NbMaxWordInCentroid.getName());
		invertIndex = new InvertedIndex<WordTF_IDF>(index);
	}
	
	@Override
	public void processCaracteristics(List<Corpus> listCorpus) {
		super.processCaracteristics(listCorpus);
		
		init();
		centroid = new double[index.size()];
		List<Pair> listBestWord = new ArrayList<Pair>();
		double minTfIdf = 0;
		
		for (Corpus corpus : listCorpus) {
			int corpusId = corpus.getiD();
			
			for (WordTF_IDF w : invertIndex.getCorpusWordIndex().get(corpusId)) {
				double tfidf = w.getTfCorpus(corpusId)*w.getIdf();
				if (listBestWord.size() < nbMaxWordInCentroid) {
					listBestWord.add(new Pair(w.getiD(), tfidf));
					Collections.sort(listBestWord);
					minTfIdf = (double) listBestWord.get(listBestWord.size()-1).second;
				} else if (tfidf > minTfIdf) {
					listBestWord.remove(nbMaxWordInCentroid-1);
					listBestWord.add(new Pair(w.getiD(), tfidf));
					Collections.sort(listBestWord);
					minTfIdf = (double) listBestWord.get(listBestWord.size()-1).second;
				}
			}
			
			for (Pair p : listBestWord) {
				centroid[(int)p.first] = (double) p.second;
			}
		}
			
		query.setQuery(centroid);
	}
	
	@Override
	public void finish() {
		super.finish();
		query.clear();
	}

	public void setNbMaxWordInCentroid(int nbMaxWordInCentroid) {
		this.nbMaxWordInCentroid = nbMaxWordInCentroid;
	}

	@Override
	public SentenceQuery<double[]> getQuery() {
		return query;
	}
	
	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return super.isOutCompatible(compatibleMethod) || super.isOutCompatible(compatibleMethod) && compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(null, double[].class, QueryBasedIn.class));
	}

	/**
	 * donne le/les paramètre(s) d'output en input à la class comp méthode
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCompatibility(ParametrizedMethod compatibleMethod) {
		super.setCompatibility(compatibleMethod);
		if (compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(null, double[].class, QueryBasedIn.class)))
			((QueryBasedIn<double[]>)compatibleMethod).setQuery(query);
	}
}