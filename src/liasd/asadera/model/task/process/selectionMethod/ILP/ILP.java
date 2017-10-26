package liasd.asadera.model.task.process.selectionMethod.ILP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.model.task.process.scoringMethod.FileNameBasedIn;
import liasd.asadera.model.task.process.selectionMethod.AbstractSelectionMethod;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;

public class ILP extends AbstractSelectionMethod implements FileNameBasedIn {

	private String fileName = "";
	
	public ILP(int id) throws SupportADNException {
		super(id);
		
		listParameterIn.add(new ParametrizedType(null, String.class, FileNameBasedIn.class));
	}
	
	@Override
	public AbstractSelectionMethod makeCopy() throws Exception {
		ILP p = new ILP(id);
		initCopy(p);
		return p;
	}
	
	@Override
	public void initADN() throws Exception {
	}
	
	@Override
	public ArrayList<SentenceModel> calculateSummary(List<Corpus> listCorpus) {
		List<SentenceModel> listSentence = new ArrayList<SentenceModel>();
		for (Corpus c : listCorpus)
			listSentence.addAll(c.getAllSentence());
		
		runGLPK();
		
		ArrayList <Integer> ind_selected_sentences;
		System.out.println("Summary computation");
		ind_selected_sentences = this.getSentencesFromGLPKSol();
		ArrayList<SentenceModel> summary = new ArrayList <SentenceModel>();
		
		
		for (Integer i : ind_selected_sentences)
		{
			if (i < listSentence.size())
				summary.add(listSentence.get(i));
		}
		
		this.eraseTmpFiles();
		return summary;
	}
	
	private ArrayList<Integer> getSentencesFromGLPKSol()
	{
		ArrayList <Integer> ind_sentences = new ArrayList <Integer> ();
		String line = "";
		int ind;
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName + "_out"), "ASCII");
			BufferedReader br = new BufferedReader(isr);
			while ( (line = br.readLine() ) != null)
			{
				if ( (ind = this.decodeLine (line)) != -1 )
				{
					ind_sentences.add(ind);
				}
			}
			br.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return ind_sentences;
	}
	
	/**
	 * Decode a line from glpk sol
	 * @param line the line to decode
	 * @return -1 if not a line containing a selected sentence, the index of the selected sentence elsewhere
	 */
	private int decodeLine (String line)
	{
		int i = -1;
		Pattern pattern = Pattern.compile ("^[\\s]+[0-9]+[\\s]+s([0-9]+)[ \\*]+1");
		Matcher match = pattern.matcher(line);
		
		if (match.find())
		{
			return new Integer(match.group(1)).intValue();
		}

		return i;
	}
	
	private void eraseTmpFiles()
	{
		File file = new File(fileName + "_out");
		if(!file.delete())
			System.err.println(file.getName() + " not deleted!");
		
		file = new File(fileName);
		if(!file.delete())
			System.err.println(file.getName() + " not deleted!");
	}
	
	private void runGLPK()
	{	
		GLPLauncher glp = new GLPLauncher(fileName);
		glp.runGLP(fileName + "_out");
	}

	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return false;
	}

	@Override
	public void setCompatibility(ParametrizedMethod compMethod) {
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
