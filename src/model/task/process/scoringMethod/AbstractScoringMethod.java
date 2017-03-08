package model.task.process.scoringMethod;

import java.util.Map;

import model.task.process.AbstractProcess;
import optimize.Individu;
import optimize.SupportADNException;
import textModeling.wordIndex.Index;

public abstract class AbstractScoringMethod extends Individu {

	protected AbstractProcess currentProcess;
	
	protected Index dictionnary;
	
	public AbstractScoringMethod(int id) throws SupportADNException {
		super(id);
	}

	public void init(AbstractProcess currentProcess, Index dictionnary) throws Exception {
		this.currentProcess = currentProcess;
		this.dictionnary = dictionnary;
		
		if (dictionnary.isEmpty())
			throw new Exception("Dictionnary is empty !");
	}
	
	/**
	 * 
	 * When implementing this method, don't forget to set this.areSentencesScored to true
	 * when scoring is complete. Otherwise, sortByScore() will throw a SentencesNotScoredException.
	 * @return 
	 * @throws Exception 
	 */
	public abstract void computeScores() throws Exception;
	
	public AbstractProcess getCurrentProcess() {
		return currentProcess;
	}

	public void setCurrentProcess(AbstractProcess currentProcess) {
		this.currentProcess = currentProcess;
		currentProcess.getSupportADN().putAll(supportADN);
	}

	/*public String getInputType() {
		return inputType;
	}*/

	public Index getIndex() {
		return dictionnary;
	}

	public void setIndex(Index dictionnary) {
		this.dictionnary = dictionnary;
	}
}
