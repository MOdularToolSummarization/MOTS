package liasd.asadera.model.task.process.comparativeMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import liasd.asadera.model.task.process.AbstractProcess;
import liasd.asadera.model.task.process.processCompatibility.ParameterizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParameterizedType;
import liasd.asadera.model.task.process.scoringMethod.ScoreBasedIn;
import liasd.asadera.model.task.process.scoringMethod.ScoreBasedOut;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.tools.Pair;
import liasd.asadera.tools.PairSentenceScore;

public abstract class AbstractComparativeMethod extends ParameterizedMethod {

	protected AbstractProcess currentProcess;

	public AbstractComparativeMethod(int id) throws SupportADNException {
		super(id);

		listParameterIn.add(new ParameterizedType(PairSentenceScore.class, ArrayList.class, ScoreBasedIn.class));
		listParameterOut.add(new ParameterizedType(PairSentenceScore.class, ArrayList.class, ScoreBasedOut.class));
	}

	public abstract AbstractComparativeMethod makeCopy() throws Exception;

	protected void initCopy(AbstractComparativeMethod p) {
		p.setCurrentProcess(currentProcess);
		if (supportADN != null)
			p.setSupportADN(new HashMap<String, Class<?>>(supportADN));
		p.setModel(model);
	}

	public abstract void initADN() throws Exception;

	public abstract List<Pair<SentenceModel, String>> calculateDifference(List<Corpus> listCorpus) throws Exception;

	public abstract void finish();

	public AbstractProcess getCurrentProcess() {
		return currentProcess;
	}

	public void setCurrentProcess(AbstractProcess currentProcess) {
		this.currentProcess = currentProcess;
	}

	@Override
	public abstract boolean isOutCompatible(ParameterizedMethod compatibleMethod);

	@Override
	public abstract void setCompatibility(ParameterizedMethod compMethod);
}
