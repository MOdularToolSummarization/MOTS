package model.task.scoringMethod.TF_IDF;

import java.util.Iterator;
import java.util.TreeSet;

import model.task.scoringMethod.Centroid;
import model.task.scoringMethod.ScoreBasedOut;
import textModeling.SentenceModel;
import textModeling.WordModel;
import textModeling.cluster.ClusterCentroid;
import textModeling.wordIndex.TF_IDF.WordTF_IDF;
import tools.PairSentenceScore;

public class ScoringCentroidTF_IDF extends Centroid implements ScoreBasedOut {

	private TreeSet<PairSentenceScore> sentencesScores;
	
	public ScoringCentroidTF_IDF(int id) throws Exception {
		super(id);
	}

	@Override
	public void computeScores() throws Exception {
		sentencesScores = new TreeSet<PairSentenceScore>();
		
		/** Scoring des phrases en fonction des centro�ds des clusters */
		Iterator<ClusterCentroid> itCluster = listCluster.values().iterator();
		while (itCluster.hasNext()) {
			ClusterCentroid cluster = itCluster.next();
			Iterator<SentenceModel> sentenceIt = cluster.iterator();
			while (sentenceIt.hasNext()) {
				SentenceModel sentence = sentenceIt.next();
				double score = 0;
				Iterator<WordModel> wordIt = sentence.iterator();
				while(wordIt.hasNext()) {
					WordModel word = wordIt.next();
					WordTF_IDF w = (WordTF_IDF) cluster.getClusterDictionnary().get(word.getmLemma());
					if (w != null) {
						double idf = ((WordTF_IDF)dictionnary.get(word.getmLemma())).getIdf();
						score += w.getTf()*idf;
					}
				}
				sentencesScores.add(new PairSentenceScore(sentence, score));
			}
		}	
		System.out.println(sentencesScores);	
		
		super.computeScores();
	}

	@Override
	public TreeSet<PairSentenceScore> getScore() {
		return sentencesScores;
	}
}
