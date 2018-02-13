package liasd.asadera.model.task.process.caracteristicBuilder;

import java.util.HashMap;
import java.util.List;

import liasd.asadera.model.task.process.AbstractProcess;
import liasd.asadera.model.task.process.processCompatibility.ParameterizedMethod;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;

public abstract class AbstractCaracteristicBuilder extends ParameterizedMethod {

	protected AbstractProcess currentProcess;

	public AbstractCaracteristicBuilder(int id) throws SupportADNException {
		super(id);
	}

	public abstract AbstractCaracteristicBuilder makeCopy() throws Exception;

	protected void initCopy(AbstractCaracteristicBuilder p) {
		p.setCurrentProcess(currentProcess);
		p.setSupportADN(new HashMap<String, Class<?>>(supportADN));
		p.setModel(model);
	}

	public abstract void initADN() throws Exception;

	public abstract void processCaracteristics(List<Corpus> listCorpus) throws Exception;

	public abstract void finish();/*
									 * { sentenceCaracteristic.clear(); }
									 */

	public void setCurrentProcess(AbstractProcess p) {
		currentProcess = p;
	}

	public AbstractProcess getCurrentProcess() {
		return currentProcess;
	}

	/*
	 * @Override public Map<SentenceModel, T> getVectorCaracterisic() { return
	 * sentenceCaracteristic; }
	 */
}