package logic;

import interactor.Interactor;
import logic.Framework.Type;

public class Expansion {

	Framework framework, expansion;
	Interactor interactor;
	
	public Expansion(Framework framework, Framework expansion){
		this.framework = framework;
		this.expansion = expansion;
		this.interactor = framework.getInteractor();
	}
	
	//TODO return local, weak, strong, normal expansion
	public Framework.Type determineExpansionType(){
		return null;
	}
	
}
