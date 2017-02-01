package view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommandView extends AbstractView {
	
	private String[] args;
	
	private static final Logger logger = Logger.getLogger("Logger"); 
	
	public CommandView(String[] args) {
		super();
		this.args = args;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("\n" + (String) arg);
	}

	@Override
	public void display() {
		//Diff�rent notify � faire
		loadConfiguration(args[1]);
	}

	@Override
	public void close() {

	}
	
	private String loadConfiguration(String configFilePath) {
		try {
			File fXmlFile = new File(configFilePath);//"src/ressources/XML regex.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Element racine =  doc.getDocumentElement(); //.normalize();
			NodeList listTask = racine.getChildNodes();
			//NodeList nList = doc.getElementsByTagName("CONFIG");
			for (int i = 0; i<listTask.getLength(); i++) {
				if(listTask.item(i).getNodeType() == Node.ELEMENT_NODE) {
			        Element task = (Element) listTask.item(i);
			        
			        getCtrl().notifyTaskChanged(Integer.parseInt(task.getAttribute("ID")));

			        getCtrl().notifyLanguageChanged(task.getElementsByTagName("LANGUAGE").item(0).getTextContent());
			        //getCtrl().notifyInputPathChanged(task.getElementsByTagName("INPUT_PATH").item(0).getTextContent());
			        getCtrl().notifyOutputPathChanged(task.getElementsByTagName("OUTPUT_PATH").item(0).getTextContent());
			        
			        NodeList corpusList = task.getElementsByTagName("CORPUS");
			        for (int j = 0; j<corpusList.getLength(); j++) {
			        	if(corpusList.item(j).getNodeType() == Node.ELEMENT_NODE) {
			        		Element corpus = (Element) corpusList.item(j);
			        		//Boolean one_Summary_Per_Doc = Boolean.parseBoolean(corpus.getElementsByTagName("ONE_SUMMARY_PER_DOC").item(0).getTextContent());
			        		String summaryInputPath = corpus.getElementsByTagName("SUMMARY_PATH").item(0).getTextContent();
			        		String corpusInputPath = corpus.getElementsByTagName("INPUT_PATH").item(0).getTextContent();
			        		NodeList documentList = corpus.getElementsByTagName("DOCUMENT");
							List<String> docNames = new ArrayList<String>();
					        for (int k = 0; k<documentList.getLength(); k++) {
					        	if(documentList.item(k).getNodeType() == Node.ELEMENT_NODE) {
					        		docNames.add(documentList.item(k).getTextContent());
					        	}
							}
					        NodeList summaryList = corpus.getElementsByTagName("SUMMARY");
							List<String> summaryNames = new ArrayList<String>();
					        for (int k = 0; k<summaryList.getLength(); k++) {
					        	if(summaryList.item(k).getNodeType() == Node.ELEMENT_NODE) {
					        		summaryNames.add(summaryList.item(k).getTextContent());
					        	}
							}
					        getCtrl().notifyCorpusChanged(summaryInputPath, summaryNames, corpusInputPath, docNames);
			        	}
					}
				        
			        
			        NodeList preProcessList = task.getElementsByTagName("PREPROCESS");
		        	for (int j=0;j<preProcessList.getLength();j++) {
		        		if(preProcessList.item(j).getNodeType() == Node.ELEMENT_NODE) {
			        		Element preProcess = (Element) preProcessList.item(j);
				        	getCtrl().notifyPreProcessChanged(preProcess.getAttribute("NAME"));
				        	
				        	getCtrl().notifyProcessOptionChanged(getProcessOptionMap(preProcess));
		        		}
	        		}
			        
			        NodeList processList = task.getElementsByTagName("PROCESS");
			        for (int j = 0; j<processList.getLength(); j++) {
			        	if(processList.item(j).getNodeType() == Node.ELEMENT_NODE) {
				        	Element process = (Element) processList.item(j);
				        	getCtrl().notifyProcessChanged(process.getAttribute("NAME"));
				        	
				        	getCtrl().notifyProcessOptionChanged(getProcessOptionMap(process));
				        	
				        	NodeList scoringMethodList = process.getElementsByTagName("SCORING_METHOD");
				        	for (int k=0;k<scoringMethodList.getLength();k++) {
				        		if(scoringMethodList.item(k).getNodeType() == Node.ELEMENT_NODE) {
						        	Element scoringMethod = (Element) process.getElementsByTagName("SCORING_METHOD").item(k);
						        	getCtrl().notifyScoringMethodChanged(process.getAttribute("NAME"), scoringMethod.getAttribute("NAME"));
						        	getCtrl().notifyProcessOptionChanged(getProcessOptionMap(scoringMethod));
				        		}
				        	}
				        	
				        	Element summarizeMethod = (Element) process.getElementsByTagName("SUMMARIZE_METHOD").item(0);
				        	if (summarizeMethod != null) {
				        		getCtrl().notifySummarizeMethodChanged(process.getAttribute("NAME"), summarizeMethod.getAttribute("NAME"));
				        		getCtrl().notifyProcessOptionChanged(getProcessOptionMap(summarizeMethod));
				        	}
				        	
				        	NodeList postProcessList = process.getElementsByTagName("POSTPROCESS");
				        	for (int k=0;k<postProcessList.getLength();k++) {
				        		if(postProcessList.item(k).getNodeType() == Node.ELEMENT_NODE) {
					        		Element postProcess = (Element) postProcessList.item(k);
						        	getCtrl().notifyPostProcessChanged(process.getAttribute("NAME"), postProcess.getAttribute("NAME"));
						        	
						        	getCtrl().notifyProcessOptionChanged(getProcessOptionMap(postProcess));
				        		}
			        		}
			        	}
			        }
			        NodeList rougeList = task.getElementsByTagName("ROUGE_EVALUATION");
		        	if (rougeList.getLength() > 0) {
		        		getCtrl().notifyRougeEvaluationChanged(true);
			        	Element rouge = (Element) rougeList.item(0);
			        	getCtrl().notifyModelRootChanged(rouge.getElementsByTagName("MODEL-ROOT").item(0).getTextContent());
			        	getCtrl().notifyPeerRootChanged(rouge.getElementsByTagName("PEER-ROOT").item(0).getTextContent());
			        }
		        	else
		        		getCtrl().notifyRougeEvaluationChanged(false);
		        	
			        getCtrl().run();
			    }
			}
			return null;
		}
		catch (Exception e)
		{
			logger.log(Level.WARNING, e.getMessage(), e);
			return null;
		}
	}
	
	private Map<String, String> getProcessOptionMap(Element element) {
		NodeList optionList = element.getElementsByTagName("OPTION");
    	if (optionList.getLength() > 0) {
    		Map<String, String> processOption = new HashMap<String, String>();
        	for (int k = 0; k<optionList.getLength(); k++) {
        		Element option = (Element) optionList.item(k);
        		if (option.getParentNode().isEqualNode(element)) {
            		processOption.put(option.getAttribute("NAME"), option.getTextContent());
        		}
        	}
        	return processOption;
    	}
    	else
    		return null;
	}
}
