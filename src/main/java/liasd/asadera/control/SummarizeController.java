package main.java.liasd.asadera.control;

import java.util.ArrayList;
import java.util.List;

import main.java.liasd.asadera.model.AbstractModel;
import main.java.liasd.asadera.model.task.process.SummarizeProcess;
import main.java.liasd.asadera.model.task.process.caracteristicBuilder.AbstractCaracteristicBuilder;
import main.java.liasd.asadera.model.task.process.indexBuilder.AbstractIndexBuilder;
import main.java.liasd.asadera.model.task.process.scoringMethod.AbstractScoringMethod;
import main.java.liasd.asadera.model.task.process.selectionMethod.AbstractSelectionMethod;
import main.java.liasd.asadera.view.AbstractView;

@SuppressWarnings("rawtypes")
public class SummarizeController extends AbstractController {

	private SummarizeProcess currentProcess;

	public SummarizeController(AbstractModel model, AbstractView view) {
		super(model, view);
	}

	@Override
	public void notifyProcessChanged(String processName) throws ClassNotFoundException {
		Object o = dynamicConstructor("process.SummarizeProcess");
		currentProcess = (SummarizeProcess) o;
		getModel().getProcess().add(currentProcess);
	}

	@Override
	public void notifyIndexBuilderChanged(String processName, String indexBuilder) throws ClassNotFoundException {
		List<AbstractIndexBuilder> listIndexBuilders;
		if (currentProcess.getIndexBuilders() == null)
			listIndexBuilders = new ArrayList<AbstractIndexBuilder>();
		else
			listIndexBuilders = currentProcess.getIndexBuilders();
		Object o = dynamicConstructor("process.indexBuilder." + indexBuilder);
		listIndexBuilders.add((AbstractIndexBuilder) o);
		currentProcess.setIndexBuilders(listIndexBuilders);
	}

	@Override
	public void notifyCaracteristicBuilderChanged(String processName, String caracteristicBuilder) throws ClassNotFoundException {
		List<AbstractCaracteristicBuilder> listCaracBuilder;
		if (currentProcess.getCaracteristicBuilders() == null)
			listCaracBuilder = new ArrayList<AbstractCaracteristicBuilder>();
		else
			listCaracBuilder = currentProcess.getCaracteristicBuilders();
		Object o = dynamicConstructor("process.caracteristicBuilder." + caracteristicBuilder);
		listCaracBuilder.add((AbstractCaracteristicBuilder) o);
		currentProcess.setCaracteristicBuilders(listCaracBuilder);
	}

	@Override
	public void notifyScoringMethodChanged(String processName, String scoringMethod) throws ClassNotFoundException {
		List<AbstractScoringMethod> listScoringMethod;
		if (currentProcess.getScoringMethods() == null)
			listScoringMethod = new ArrayList<AbstractScoringMethod>();
		else
			listScoringMethod = currentProcess.getScoringMethods();
		Object o = dynamicConstructor("process.scoringMethod." + scoringMethod);
		listScoringMethod.add((AbstractScoringMethod) o);
		currentProcess.setScoringMethods(listScoringMethod);
	}

	@Override
	public void notifySelectionMethodChanged(String processName, String selectionMethod) throws ClassNotFoundException {
		Object o = dynamicConstructor("process.selectionMethod." + selectionMethod);
		currentProcess.setSelectionMethod((AbstractSelectionMethod) o);
	}

}
