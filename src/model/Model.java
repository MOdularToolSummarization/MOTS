package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import control.Controller;
import exception.LacksOfFeatures;
import model.task.postProcess.EvaluationROUGE;
import model.task.preProcess.AbstractPreProcess;
import model.task.process.AbstractProcess;
import textModeling.Corpus;
import textModeling.SentenceModel;

public class Model extends Observable {
	protected Controller ctrl;
	
	protected int taskID;
	
	protected String language;
	//protected String inputPath;
	protected String outputPath;
	
	protected List<AbstractPreProcess> preProcess = new ArrayList<AbstractPreProcess>();
	protected List<AbstractProcess> process = new ArrayList<AbstractProcess>();
	protected Map<String,Integer> processIDs = new HashMap<String, Integer>();
	protected List<Map<String, String>> processOption = new ArrayList<Map<String, String>>();
	//protected List<String> docNames;
	protected List<Corpus> corpusModels = new ArrayList<Corpus>();
	protected Corpus currentCorpus;
	//protected List<String> summary = new ArrayList<String>();
	protected boolean bRougeEvaluation = false;
	protected String modelRoot;
	protected String peerRoot;
	
	public Model() {
		super();
	}

	public void run() {
		try {
			Iterator<AbstractPreProcess> preProIt = preProcess.iterator();
			while (preProIt.hasNext()) {
				AbstractPreProcess p = preProIt.next();
				p.setModel(this);
				//p.setCurrentProcess(this);
				p.init();
			}
			
			//dictionnary.clear();
			loadCorpusModels();
			
			Iterator<Corpus> corpusIt = corpusModels.iterator();
			while (corpusIt.hasNext()) {
				currentCorpus = corpusIt.next();
				preProIt = preProcess.iterator();
				while (preProIt.hasNext()) {
					AbstractPreProcess p = preProIt.next();
					p.process();
				}
				preProIt = preProcess.iterator();
				while (preProIt.hasNext()) {
					AbstractPreProcess p = preProIt.next();
					p.finish();
				}				

				System.out.println(currentCorpus);
			}
			
			corpusIt = corpusModels.iterator();
			while (corpusIt.hasNext()) {
				currentCorpus = corpusIt.next();		
				System.out.println("Corpus : " + currentCorpus.getiD());
				
				Iterator<AbstractProcess> proIt = process.iterator();
				while (proIt.hasNext()) {
					AbstractProcess p = proIt.next();
					p.setModel(this);
					p.init();
					p.process();
					p.finish();
					setChanged();
					
					if (p.getSummary() != null) {
						List<SentenceModel> summary = p.getSummary().get(currentCorpus.getiD());
						Collections.sort(summary);
						notifyObservers(SentenceModel.listSentenceModelToString(summary));
					}
				}
			}
			if (bRougeEvaluation) {
				EvaluationROUGE rouge = new EvaluationROUGE(getCtrl().incrementProcessID());
				rouge.setModel(this);
				rouge.init();
				rouge.process();
				rouge.finish();
			}
		}
		catch (LacksOfFeatures e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void loadCorpusModels() {
		Iterator<Corpus> corpusIt = corpusModels.iterator();
		while (corpusIt.hasNext()) {
			corpusIt.next().loadDocumentModels();
		}
	}
	
   /* protected void loadDocumentModels() {
		//List<TextModel> documentModels = new ArrayList<TextModel>();
		
		Iterator<String> it = docNames.iterator();
		while (it.hasNext()) {
			corpusModels.add(new TextModel(inputPath + "\\" + it.next()));
		}
		
    	//setDocumentModels(documentModels);
    }*/
	
	public Controller getCtrl() {
		return ctrl;
	}
	
	/*public SentenceModel getSentenceByID(int id) {
		int current = id;
		boolean notFind = true;
		SentenceModel sen = null;
		Iterator<TextModel> textIt = corpusModels.iterator();
		while (notFind && textIt.hasNext()) {
			TextModel text = textIt.next();
			if (text.getNbSentence() <= current)
				current -= text.getNbSentence();
			else {
				sen = text.getSentenceByID(id);
				notFind = false;
			}
		}
		return sen;
	}*/
	
	public void setCtrl(Controller ctrl) {
		this.ctrl = ctrl;
	}
	public List<AbstractProcess> getProcess() {
		return process;
	}
	public void setProcess(List<AbstractProcess> process) {
		this.process = process;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/*public Map<String, WordEmbeddings> getDictionnary() {
		return dictionnary;
	}

	public void setDictionnary(Map<String, WordEmbeddings> dictionnary) {
		this.dictionnary = dictionnary;
	}*/

	public String getProcessOption(int processId, String optionName) throws LacksOfFeatures {
		if (getProcessOption().get(processId) != null && !getProcessOption().get(processId).isEmpty() && getProcessOption().get(processId).containsKey(optionName)) {
			return getProcessOption().get(processId).get(optionName);
		}
		else
			throw new LacksOfFeatures(optionName);
	}
	
	public List<Map<String, String>> getProcessOption() {
		return processOption;
	}

	public void setProcessOption(List<Map<String, String>> processOption) {
		this.processOption = processOption;
	}

	public Map<String, Integer> getProcessIDs() {
		return processIDs;
	}

	public void setProcessIDs(Map<String, Integer> processIDs) {
		this.processIDs = processIDs;
	}
	
	public AbstractProcess getProcessByID(int ID) {
		AbstractProcess returnProcess = null;
		boolean notFind = true;
		Iterator<AbstractProcess> it = process.iterator();
		while (notFind && it.hasNext()) {
			AbstractProcess p = it.next();
			if (p.getId() == ID) {
				returnProcess = p;
				notFind = false;
			}
		}
		return returnProcess;
	}
	
	public void clear() {
		taskID = -1;
		language = "";
		//inputPath = "";
		outputPath = "";
		
		process.clear();
		preProcess.clear();
		processIDs.clear();
		processOption.clear();
		corpusModels.clear();
		//dictionnary.clear();
	}

	public boolean isbRougeEvaluation() {
		return bRougeEvaluation;
	}

	public void setbRougeEvaluation(boolean bRougeEvaluation) {
		this.bRougeEvaluation = bRougeEvaluation;
	}

	public String getModelRoot() {
		return modelRoot;
	}

	public void setModelRoot(String modelRoot) {
		this.modelRoot = modelRoot;
	}

	public String getPeerRoot() {
		return peerRoot;
	}

	public void setPeerRoot(String peerRoot) {
		this.peerRoot = peerRoot;
	}

	public int getTaskID() {
		return taskID;
	}

	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}

	/*public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}*/

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	/*public List<String> getDocNames() {
		return docNames;
	}

	public void setDocNames(List<String> docNames) {
		this.docNames = docNames;
	}*/

	public List<AbstractPreProcess> getPreProcess() {
		return preProcess;
	}

	public void setPreProcess(List<AbstractPreProcess> preProcess) {
		this.preProcess = preProcess;
	}

	public List<Corpus> getCorpusModels() {
		return corpusModels;
	}

	public void setCorpusModels(List<Corpus> corpusModels) {
		this.corpusModels = corpusModels;
	}

	public Corpus getDocumentModels() {
		return currentCorpus;
	}

	public void setDocumentModels(Corpus currentCorpus) {
		this.currentCorpus = currentCorpus;
	}
}
