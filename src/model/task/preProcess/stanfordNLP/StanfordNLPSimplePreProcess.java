package model.task.preProcess.stanfordNLP;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import exception.LacksOfFeatures;
import model.task.preProcess.AbstractPreProcess;
import textModeling.Corpus;
import textModeling.SentenceModel;
import textModeling.TextModel;
import textModeling.WordModel;
import tools.Tools;

/**
 * Utilisation de la lib StanfordNLPCore
 * Seulement SentenceSplitter and WordSplitter
 * @author Val
 *
 */
public class StanfordNLPSimplePreProcess extends AbstractPreProcess{

	private Properties props;
	private StanfordCoreNLP pipeline;
	
	public StanfordNLPSimplePreProcess(int id) {
		super(id);
	}
	
	@Override
	public void init() throws LacksOfFeatures {
		/** TODO ajouter option dans XML */
		// creates a StanfordCoreNLP object, with properties
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	
	@Override
	public void process() throws Exception {
		int iD = 0;
		
		Iterator<Corpus> corpusIt = getModel().getCurrentMultiCorpus().iterator();
		while (corpusIt.hasNext()) {
			Iterator<TextModel> textIt = corpusIt.next().iterator();
			while (textIt.hasNext()) {
				TextModel textModel = textIt.next();
				// read some text in the text variable
				String text = textModel.getText();
				// create an empty Annotation just with the given text
				Annotation document = new Annotation(text);
				// run all Annotators on this text
				pipeline.annotate(document);
				// these are all the sentences in this document
				// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
				List<CoreMap> sentences = document.get(SentencesAnnotation.class);
				for(CoreMap sentence: sentences) {
					if (!sentence.toString().replace("_", "").isEmpty()) {
						SentenceModel sen = new SentenceModel(sentence.toString().replace("\n",  "\t"), iD, textModel);
						// traversing the words in the current sentence
						// a CoreLabel is a CoreMap with additional token-specific methods
						for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
							String w = token.get(TextAnnotation.class);
							if (!Tools.enleverPonctuation(token.get(TextAnnotation.class)).isEmpty()) {
								WordModel word = new WordModel();
								word.setmForm(w);
								word.setSentence(sen);
								word.setmPosTag(token.get(PartOfSpeechAnnotation.class));
								//System.out.println(token.get(LemmaAnnotation.class));
								word.setmLemma(token.get(LemmaAnnotation.class).toLowerCase());
								word.setWord(w);
								sen.add(word);
							}
						}
						if (sen.getLength() > 7)
							textModel.add(sen);
						iD++;
					}
				}
			}
		}
	}
	
	@Override
	public void finish() {
	}

	
}
