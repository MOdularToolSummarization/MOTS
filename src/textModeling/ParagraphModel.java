package textModeling;

import java.util.ArrayList;

/**
 * � mettre en place extends ArrayList<SentenceModel>
 * @author Val
 *
 */
public class ParagraphModel extends ArrayList<SentenceModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4074259901680254039L;
	protected String paragraph;
	//protected ArrayList<SentenceModel> listSentences = new ArrayList<SentenceModel>();
	protected int nbSentence;
	
	private TextModel text;
	
	public ParagraphModel() {
		super();
	}

	public ParagraphModel(String text) {
		super();
		paragraph = text;
	}
	
	public ParagraphModel(String text, TextModel textModel) {
		super();
		paragraph = text;
		this.text = textModel;
	}
	
	public TextModel getText() {
		return text;
	}

	public void setText(TextModel text) {
		this.text = text;
	}

	public String getParagraph() {
		return paragraph;
	}

	public void setParagraph(String paragraph) {
		this.paragraph = paragraph;
	}

	/*public ArrayList<SentenceModel> getListSentences() {
		return listSentences;
	}

	public void setListSentences(ArrayList<SentenceModel> listSentences) {
		this.listSentences = listSentences;
	}*/
	
	public int getNbSentence() {
		return nbSentence;
	}

	public void setNbSentence(int nbSentence) {
		this.nbSentence = nbSentence;
	}

	@Override
	public String toString() {
		return paragraph;
	}
}
