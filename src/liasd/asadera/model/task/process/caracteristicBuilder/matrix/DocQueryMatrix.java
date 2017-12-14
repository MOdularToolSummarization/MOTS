package liasd.asadera.model.task.process.caracteristicBuilder.matrix;

import java.util.List;

import liasd.asadera.model.task.process.caracteristicBuilder.QueryBasedIn;
import liasd.asadera.model.task.process.caracteristicBuilder.QueryBasedOut;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.Query;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.textModeling.TextModel;
import liasd.asadera.textModeling.wordIndex.WordIndex;
import liasd.asadera.textModeling.wordIndex.WordVector;

public class DocQueryMatrix extends ConcatMatrixSentence implements QueryBasedOut {

	private Query query;
	
	public DocQueryMatrix(int id) throws SupportADNException {
		super(id);
		
		query = new Query();
		
		listParameterOut.add(new ParametrizedType(null, double[][].class, QueryBasedOut.class));
	}

	@Override
	public DocQueryMatrix makeCopy() throws Exception {
		DocQueryMatrix p = new DocQueryMatrix(id);
		initCopy(p);
		return p;
	}
	
	//@SuppressWarnings("unlikely-arg-type")
	@Override
	public void processCaracteristics(List<Corpus> listCorpus) {
		super.processCaracteristics(listCorpus);
		
		int nbMot = 0;
		for (Corpus corpus: listCorpus)
			for (TextModel text : corpus)
				for (SentenceModel sen : text)
				nbMot += sen.size(); // text.getNbWord(getCurrentProcess().getFilter());
		
		double[][] matrixDoc = new double[nbMot][dimension];
		int i = 0;
		for (Corpus corpus : listCorpus)
			for (TextModel text : corpus)
				for (SentenceModel s : text)
					for (WordIndex w : s) {
						matrixDoc[i] = ((WordVector)w).getWordVector(); //.index.get(w.getmLemma()).getWordVector();
						i++;
					}
			
		query.setQuery(matrixDoc);
	}
	
	@Override
	public void finish() {
		super.finish();
		query.clear();
	}
	
	@Override
	public Query getQuery() {
		return query;
	}

	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		boolean a = super.isOutCompatible(compatibleMethod);
		boolean b = compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(null, double[][].class, QueryBasedIn.class));
		return a || (a && b);
	}

	/**
	 * donne le/les paramètre(s) d'output en input à la class comp méthode
	 */
	@Override
	public void setCompatibility(ParametrizedMethod compatibleMethod) {
		super.setCompatibility(compatibleMethod);
		if (compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(null, double[][].class, QueryBasedIn.class)))
			((QueryBasedIn)compatibleMethod).setQuery(query);
	}
}
