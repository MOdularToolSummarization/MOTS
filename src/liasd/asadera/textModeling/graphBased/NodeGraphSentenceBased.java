package liasd.asadera.textModeling.graphBased;

import java.util.HashMap;
import java.util.Map;

import liasd.asadera.textModeling.SentenceModel;

public class NodeGraphSentenceBased {
	
	private SentenceModel currentSentence; //vertices (i.e. nodes)
	private int idNode;
	private Map<SentenceModel, Double> adjacentSentence = new HashMap<SentenceModel, Double>(); //edges (i.e. branch)
	//Map<SentenceModel, double[]> sentenceCaracteristic;
	
	public NodeGraphSentenceBased(int iD, SentenceModel currentSentence/*, Map<SentenceModel, double[]> sentenceCaracteristic*/) {
		super();
		this.idNode = iD;
		this.currentSentence = currentSentence;
		//this.sentenceCaracteristic = sentenceCaracteristic;
	}
	
	public void addAdjacentSentence(SentenceModel sentence, double weigth) {
		adjacentSentence.put(sentence, weigth);
	}

	public SentenceModel getCurrentSentence() {
		return currentSentence;
	}

	public void setCurrentSentence(SentenceModel currentSentence) {
		this.currentSentence = currentSentence;
	}

	public int getIdNode() {
		return idNode;
	}

	public void setIdNode(int idNode) {
		this.idNode = idNode;
	}

	public Map<SentenceModel, Double> getAdjacentSentence() {
		return adjacentSentence;
	}

	public void setAdjacentSentence(Map<SentenceModel, Double> adjacentSentence) {
		this.adjacentSentence = adjacentSentence;
	}
}
