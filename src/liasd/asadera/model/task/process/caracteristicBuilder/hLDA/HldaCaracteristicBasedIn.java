package liasd.asadera.model.task.process.caracteristicBuilder.hLDA;

import java.util.Map;

import liasd.asadera.model.task.process.caracteristicBuilder.hLDA.HierarchicalLDA.NCRPNode;
import liasd.asadera.textModeling.SentenceModel;

public interface HldaCaracteristicBasedIn {

	public void setSentenceLevelDistribution(Map<SentenceModel, double[]> sentenceLevelDistribution);
	
	public void setTopicWordDistribution(Map<NCRPNode, double[]> topicWordDistribution);
}
