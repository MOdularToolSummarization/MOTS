package model;

import java.util.Iterator;

import exception.LacksOfFeatures;
import model.task.preProcess.AbstractPreProcess;
import model.task.process.AbstractProcess;
import model.task.process.SummarizeProcess;
import textModeling.MultiCorpus;
import textModeling.SentenceModel;

public class SummarizeModel extends AbstractModel {

	/**
	 * Applique les PreProcess {@link #preProcess} sur les MultiCorpus {@link #multiCorpusModels}
	 * Lance l'exécution des AbstractProcess dans {@link #process} sur les MultiCorpus {@link #multiCorpusModels}
	 */
	@Override
	public void run() {
		try {
			loadMultiCorpusModels();
			
			Iterator<AbstractPreProcess> preProIt = getPreProcess().iterator();
			while (preProIt.hasNext()) {
				AbstractPreProcess p = preProIt.next();
				p.setModel(this);
				p.init();
			}
			
			Iterator<MultiCorpus> multiCorpusIt = getMultiCorpusModels().iterator();
			while (multiCorpusIt.hasNext()) {
				currentMultiCorpus = multiCorpusIt.next();
				preProIt = getPreProcess().iterator();
				while (preProIt.hasNext()) {
					AbstractPreProcess p = preProIt.next();
					p.setCurrentMultiCorpus(currentMultiCorpus);
					p.process();
				}
				preProIt = getPreProcess().iterator();
				while (preProIt.hasNext()) {
					AbstractPreProcess p = preProIt.next();
					p.finish();
				}				

				System.out.println(currentMultiCorpus);
			}
			
			multiCorpusIt = getMultiCorpusModels().iterator();
			while (multiCorpusIt.hasNext()) {
				currentMultiCorpus = multiCorpusIt.next();		
				System.out.println("MultiCorpus : " + currentMultiCorpus.getiD());
				
				Iterator<AbstractProcess> proIt = getProcess().iterator();
				while (proIt.hasNext()) {
					long time = System.currentTimeMillis();
					SummarizeProcess p = (SummarizeProcess) proIt.next();
					p.setCurrentMultiCorpus(currentMultiCorpus);
					p.setModel(this);
					p.initCorpusToCompress();
					p.initADN();
					runProcess(currentMultiCorpus, p);
					System.out.println(System.currentTimeMillis() - time);
				}
			}
			if (isbRougeEvaluation()) {
				getEvalRouge().setModel(this);
				getEvalRouge().setCurrentMultiCorpus(currentMultiCorpus);
				getEvalRouge().init();
				getEvalRouge().process();
				getEvalRouge().finish();
			}
		}
		catch (LacksOfFeatures e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Traitement du AbstractProcess p sur le MultiCorpus
	 * @param multiCorpus
	 * @param p
	 * @throws Exception
	 */
	public void runProcess(MultiCorpus multiCorpus, SummarizeProcess p) throws Exception {
		if (isMultiThreading()) {
			int nbThreads = p.getListCorpusId().size();
			
			SummarizeProcess[] threads = new SummarizeProcess[nbThreads];

			threads[0] = p;
			for (int i=0; i<nbThreads; i++) {
				if (i != 0)
					threads[i] = p.makeCopy();
				threads[i].setCurrentMultiCorpus(new MultiCorpus(multiCorpus));
				threads[i].setModel(this);
				threads[i].setSummarizeIndex(p.getListCorpusId().get(i));
				threads[i].initADN();
			}
			for (int i=0; i<nbThreads; i++) {
				threads[i].start();
			}
			for (int i=0; i<nbThreads; i++) {
				threads[i].join();
			}
			/*for (int i : p.getListCorpusId()) {
				setChanged();
				notifyObservers("Corpus " + i + "\n" + SentenceModel.listSentenceModelToString(p.getSummary().get(multiCorpus.getiD()).get(i)));
			}*/
		}
		else {
			for (int i : p.getListCorpusId()) {
				p.setCurrentMultiCorpus(multiCorpus);
				p.setSummarizeIndex(i);
				p.init();
				p.process();
				p.finish();
				setChanged();
				notifyObservers("Corpus " + i + "\n" + SentenceModel.listSentenceModelToString(p.getSummary().get(multiCorpus.getiD()).get(i)));
			}
		}
	}
}
