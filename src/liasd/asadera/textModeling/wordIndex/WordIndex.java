package liasd.asadera.textModeling.wordIndex;

import java.util.HashMap;

/**
 * change extends to list instead of arraylist ?
 * voir pour ajouter <T>
 * @author valnyz
 *
 */
public class WordIndex /*extends ArrayList<WordModel>*/ implements Comparable<WordIndex> {
	
	//private static int count = 0;
	private int iD;
	private double weight;
	
	//public static void reset() {count = 0;}
	
	protected String word;
	private int nbOccurence = 0;
	protected Index<?> index;
	
	/**
	 * Tf et Idf par documents : Key = idCorpus
	 */
	protected HashMap<Integer, Integer> docOccurences = new HashMap<Integer, Integer>();
	protected HashMap<Integer, Integer> corpusOccurences = new HashMap<Integer, Integer>();

	public WordIndex() {
		super();
		index = null;
		//iD = count++;
	}
	
	public WordIndex(Index<?> index) {
		super();
		this.index = index;
		//iD = count++;
	}
	
	public WordIndex(String word, Index<?> index) {
		super();
		this.word = word;
		this.index = index;
		//iD = count++;
	}

	public Integer getiD() {
		return iD;
	}

	public void setiD(int iD) {
		this.iD = iD;
	}

	public HashMap<Integer, Integer> getDocOccurences() {
		return docOccurences;
	}

	public HashMap<Integer, Integer> getCorpusOccurences() {
		return corpusOccurences;
	}
	
	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	/*public void setId(int iD) {
		this.iD = iD;
	}*/

	public Index<?> getIndex() {
		return index;
	}

	public void setIndex(Index<?> index) {
		this.index = index;
	}
	
	public void addDocumentOccurence(int idCorpus, int idDoc) {
		nbOccurence++;
		if (!corpusOccurences.containsKey(idCorpus))
			corpusOccurences.put(idCorpus, 1);
		else
			corpusOccurences.put(idCorpus, corpusOccurences.get(idCorpus)+1);
		if (!docOccurences.containsKey(idDoc))
			docOccurences.put(idDoc, 1);
		else
			docOccurences.put(idDoc, docOccurences.get(idDoc)+1);
	}
	
	public double getTfDocument(int idDoc) {
		return (double)docOccurences.get(idDoc);
	}
	
	public double getTfCorpus(int idCorpus) {
		return (double)corpusOccurences.get(idCorpus);
	}
	
	public int getNbOccurence() {
		return nbOccurence;
	}
	
	public double getIdf() {
		/**
		 * Smooth IDF (1+log à la place de log simple) si rencontre de mot inconnu du dictionnaire
		 */
		return Math.log(index.getNbDocument()/getNbDocumentWithWordSeen());
	}
	
	public double getTf() {
		return (double)getNbOccurence();
	}
	
	public int getNbDocumentWithWordSeen() {
		return docOccurences.size();
	}
	
	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass())
			return false;
		WordIndex wi = (WordIndex) o;
		return iD == wi.getiD() ;
	}
	
	@Override
	public String toString() {
		return word;
	}

	@Override
	public int compareTo(WordIndex o) {
		return this.getiD().compareTo(o.getiD());
	}
}
