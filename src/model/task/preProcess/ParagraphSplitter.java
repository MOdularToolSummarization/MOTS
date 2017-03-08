package model.task.preProcess;

import java.util.Iterator;

import exception.LacksOfFeatures;
import textModeling.Corpus;
import textModeling.ParagraphModel;
import textModeling.TextModel;

public class ParagraphSplitter extends AbstractPreProcess {

	public ParagraphSplitter(int id) {
		super(id);	
	}
	
	@Override
	public void init() throws LacksOfFeatures {
	}
	
	@Override
	public void process() {
		Iterator<Corpus> corpusIt = getModel().getCurrentMultiCorpus().iterator();
		while (corpusIt.hasNext()) {
			Iterator<TextModel> textIt = corpusIt.next().iterator();
			while (textIt.hasNext()) {
				TextModel textModel = textIt.next();
				splitTextIntoParagraph(textModel);
			}
		}
	}
	
	@Override
	public void finish() {
		Iterator<Corpus> corpusIt = getModel().getCurrentMultiCorpus().iterator();
		while (corpusIt.hasNext()) {
			Iterator<TextModel> textIt = corpusIt.next().iterator();
			while (textIt.hasNext()) {
				TextModel textModel = textIt.next();
				Iterator<ParagraphModel> paragraphIt = textModel.iterator();
				while (paragraphIt.hasNext()) {
					ParagraphModel paragraphModel = paragraphIt.next();
					textModel.setNbSentence(textModel.getNbSentence()+paragraphModel.getNbSentence());
				}
			}
		}
	}
	
	public void splitTextIntoParagraph(TextModel textModel) {
		int start = 0;
		for (int i = 0;i<textModel.getText().length();i++) {
			if (textModel.getText().charAt(i) == '\n') { //start+1 pour enlever le \n r�siduel
				if (!textModel.getText().substring(start, i).replace("\n", "").isEmpty())
					textModel.add(new ParagraphModel(textModel.getText().substring(start, i).replace("\n", ""), textModel));
				start = i;
			}
		}
	}
}
