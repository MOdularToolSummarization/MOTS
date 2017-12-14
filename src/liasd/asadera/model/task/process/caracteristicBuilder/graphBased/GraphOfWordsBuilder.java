package liasd.asadera.model.task.process.caracteristicBuilder.graphBased;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import liasd.asadera.model.task.process.caracteristicBuilder.AbstractCaracteristicBuilder;
import liasd.asadera.model.task.process.caracteristicBuilder.GraphBasedIn;
import liasd.asadera.model.task.process.caracteristicBuilder.GraphBasedOut;
import liasd.asadera.model.task.process.indexBuilder.IndexBasedIn;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedMethod;
import liasd.asadera.model.task.process.processCompatibility.ParametrizedType;
import liasd.asadera.optimize.SupportADNException;
import liasd.asadera.textModeling.Corpus;
import liasd.asadera.textModeling.SentenceModel;
import liasd.asadera.textModeling.TextModel;
import liasd.asadera.textModeling.wordIndex.Index;
import liasd.asadera.textModeling.wordIndex.NGram;
import liasd.asadera.textModeling.wordIndex.WordIndex;

public class GraphOfWordsBuilder extends AbstractCaracteristicBuilder implements IndexBasedIn<WordIndex>, GraphBasedOut<WordIndex, DefaultWeightedEdge> {

	private SimpleWeightedGraph<WordIndex, DefaultWeightedEdge> graph;
	private Index<WordIndex> index;
	private int slidingWindow = 2;
	
	public GraphOfWordsBuilder(int id) throws SupportADNException {
		super(id);

		listParameterIn.add(new ParametrizedType(NGram.class, Index.class, IndexBasedIn.class));
		listParameterIn.add(new ParametrizedType(WordIndex.class, Index.class, IndexBasedIn.class));
		listParameterOut.add(new ParametrizedType(DefaultWeightedEdge.class, WordIndex.class, GraphBasedOut.class));
		
		graph = new SimpleWeightedGraph<WordIndex, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	@Override
	public AbstractCaracteristicBuilder makeCopy() throws Exception {
		GraphOfWordsBuilder p = new GraphOfWordsBuilder(id);
		initCopy(p);
		return p;
	}

	@Override
	public void initADN() throws Exception {
		slidingWindow = Integer.parseInt(getCurrentProcess().getModel().getProcessOption(id, "Window"));
	}

	@Override
	public void processCaracteristics(List<Corpus> listCorpus) throws Exception {
		for (WordIndex word : index.values())
			graph.addVertex(word);

		for (Corpus corpus : listCorpus) {
			for (TextModel text : corpus)
				for (SentenceModel sen : text) {
					int n = sen.getN();
					for (int i=0; i<sen.size(); i++)
//						if (getCurrentProcess().getFilter().passFilter(sen.getListWordModel().get(i)))
							for (int j=i+n; j<Math.min(i + slidingWindow + 1, sen.size()); j++) {
//								if (getCurrentProcess().getFilter().passFilter(sen.getListWordModel().get(j))) {
									WordIndex w1 = sen.get(i); //index.get(sen.getListWordModel().get(i).getmLemma());
									WordIndex w2 = sen.get(j); //index.get(sen.getListWordModel().get(j).getmLemma());
						            if (i != j && w1 != w2) {
						            	if (!graph.containsVertex(w1))
						            		graph.addVertex(w1);
						            	if (!graph.containsVertex(w2))
						            		graph.addVertex(w2);
						            	if (!graph.containsEdge(w1, w2))
						            		graph.addEdge(w1, w2);
						            	else {
							            	DefaultWeightedEdge edge = graph.getEdge(w1, w2);
							            	graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
//							            	System.out.println(edge + " " + graph.getEdgeWeight(edge));
						            	}
						            }
//								}
							}
				}
		}
	}
	
	@Override
	public void finish() {
		graph.removeAllEdges(new ArrayList<DefaultWeightedEdge>(graph.edgeSet()));
		graph.removeAllVertices(new ArrayList<WordIndex>(graph.vertexSet()));
	}
	
	@Override
	public void setIndex(Index<WordIndex> index) {
		this.index = index;
	}
	
	@Override
	public Graph<WordIndex, DefaultWeightedEdge> getGraph() {
		return graph;
	}

	@Override
	public boolean isOutCompatible(ParametrizedMethod compatibleMethod) {
		return compatibleMethod.getParameterTypeIn().contains(new ParametrizedType(DefaultWeightedEdge.class, WordIndex.class, GraphBasedIn.class));
	}

	/**
	 * donne le/les paramètre(s) d'output en input à la class comp méthode
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setCompatibility(ParametrizedMethod compatibleMethod) {
		((GraphBasedIn<WordIndex, DefaultWeightedEdge>)compatibleMethod).setGraph(graph);
	}
}
