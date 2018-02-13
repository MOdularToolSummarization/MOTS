package liasd.asadera.model.task.process.selectionMethod;

import java.util.List;

import liasd.asadera.model.task.process.processCompatibility.ParameterizedMethod;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;

public class Grasshopper extends AbstractSelectionMethod {

	public Grasshopper(int id) throws SupportADNException {
		super(id);
	}

	@Override
	public AbstractSelectionMethod makeCopy() throws Exception {
		return null;
	}

	@Override
	public void initADN() throws Exception {
	}

	@Override
	public List<SentenceModel> calculateSummary(List<Corpus> listCorpus) throws Exception {
		return null;
	}

	@Override
	public boolean isOutCompatible(ParameterizedMethod compatibleMethod) {
		return false;
	}

	@Override
	public void setCompatibility(ParameterizedMethod compMethod) {
	}
}