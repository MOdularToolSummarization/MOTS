package liasd.asadera.model.task.process.selectionMethod.genetic.geneticScorers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import liasd.asadera.model.task.process.selectionMethod.genetic.GeneticIndividual;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.textModeling.wordIndex.Index;
import liasd.asadera.textModeling.wordIndex.InvertedIndex;
import liasd.asadera.textModeling.wordIndex.NGram;
import liasd.asadera.textModeling.wordIndex.WordIndex;

/**
 * 
 * @author aurelien
 * Computes a diversity score based on the number of different ngrams divided by the total number of ngrams
 */
public class SimpleNGramDiversity extends GeneticIndividualScorer{
	
	public SimpleNGramDiversity(HashMap <GeneticIndividualScorer, Double> scorers, ArrayList<SentenceModel> ss, Corpus corpus, InvertedIndex<WordIndex> invertedIndex, Index<WordIndex> index, Double divWeight, Double delta, Double firstSentenceConceptsFactor, Integer window, Double fsc_factor) {
		super(null, ss, corpus, null, index, null, null, null, window, null);
	}
	
	@Override
	public double computeScore(GeneticIndividual gi) {
		ArrayList<NGram> curr_ngram_list;
		TreeSet<NGram> total_ngram_set = new TreeSet<NGram> ();
		int total_number_of_ngrams = 0;
		
		for (SentenceModel p : gi.getGenes())
		{
			curr_ngram_list = new ArrayList<NGram>(p.getNGrams(this.window, this.index, null));
			total_number_of_ngrams += curr_ngram_list.size();
			total_ngram_set.addAll(curr_ngram_list);
		}
		
		return (double)total_ngram_set.size() / (double) total_number_of_ngrams;
	}

	
	
	
	
	
}
