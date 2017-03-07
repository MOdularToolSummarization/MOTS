package model.task.process.summarizeMethod.genetic.geneticScorers;

import java.util.HashMap;

import model.Model;
import model.task.process.summarizeMethod.genetic.GeneticIndividual;
import textModeling.Corpus;
import textModeling.SentenceModel;
import textModeling.wordIndex.Dictionnary;
import textModeling.wordIndex.InvertedIndex;

public class PosScorer extends GeneticIndividualScorer{

	public PosScorer(HashMap <GeneticIndividualScorer, Double> scorers, Corpus corpus, HashMap<Integer, String> hashMapWord, InvertedIndex invertedIndex, Dictionnary index, Double divWeight, Double delta, Double firstSentenceConceptsFactor, Integer window, Double fsc_factor) {
		super(null, null, null, null, null, null, null, null, null, null);
	}
	
	@Override
	public double computeScore(GeneticIndividual gi) {
		// TODO Auto-generated method stub
		double score = 0.;
		
		
		for (SentenceModel p : gi.getGenes())
		{
			score += p.getPosScore();
			//System.out.println(score);
		}
		score /= (double)gi.getGenes().size();
		//System.out.println("final score : "+score);
		return score;
	}

}
