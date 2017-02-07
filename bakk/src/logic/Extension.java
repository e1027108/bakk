package logic;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.SingleInstruction;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class Extension {

	private ArrayList<Argument> arguments; //arguments of extension
	private Framework framework; //framework from which the extension is derived
	private ArrayList<Attack> outgoingAttacks;
	private ArrayList<Argument> extensionAttacks;
	private ArrayList<Attack> incomingAttacks;
	private boolean cf, adm;

	/**
	 * creates an extension object
	 * this contains arguments representing a candidate argument set for an extension
	 * it also reads the corresponding attacks from the framework
	 * @param arguments the arguments of the extension
	 * @param framework the framework the arguments are from
	 */
	public Extension(ArrayList<Argument> arguments, Framework framework) {
		this.arguments = new ArrayList<Argument>();
		this.framework = framework;
		this.arguments.addAll(arguments);

		outgoingAttacks = new ArrayList<Attack>();
		extensionAttacks = new ArrayList<Argument>();
		incomingAttacks = new ArrayList<Attack>();
		readAttacks(framework.getAttacks());
	}

	/**
	 * reads the attacks from a list and groups them into
	 * incoming and outgoing attacks with regards to the arguments of the extension
	 * @param attacks a list of attacks
	 */
	private void readAttacks(ArrayList<Attack> attacks) {
		for(Attack a: attacks){
			Argument attacker = a.getAttacker();
			Argument attacked = a.getAttacked();

			if(arguments.contains(a.getAttacked())){
				incomingAttacks.add(a);
			}
			if(arguments.contains(attacker)){
				outgoingAttacks.add(a);
				if(!extensionAttacks.contains(attacker)){
					extensionAttacks.add(attacked);
				}
			}
		}
	}

	/**
	 * checks whether the this extension candidate is conflict-free
	 * this means we check whether an argument in the set is attacked by
	 * another one, that is also in the set
	 * @param write whether to write certain messages to the interactor
	 * @param show whether to write anything to the interactor
	 * @return if the extension candidate is conflict-free
	 */
	public boolean isConflictFree(boolean write, boolean show) {
		ArrayList<Attack> violatingAttacks = new ArrayList<Attack>();
		
		for(Attack a: framework.getAttacks()){
			if(arguments.contains(a.getAttacker()) && arguments.contains(a.getAttacked())){
				violatingAttacks.add(a);
			}
		}

		if(!violatingAttacks.isEmpty()){
			if(write){
				String tmp = "";
				GraphInstruction instruction = toInstruction(Color.GREEN);
				ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

				for(Attack a: violatingAttacks){
					String attacked = "" + a.getAttacked().getName();
					if(!tmp.contains(attacked)){ //multiple arguments could attack the same one
						tmp += attacked;
					}
					edgeInstructions.add(new SingleInstruction((a.getAttacker().getName()+attacked),Color.RED));
				}

				tmp = Framework.formatNameList(tmp);
				instruction.setEdgeInstructions(edgeInstructions);

				framework.addToInteractor(new Command(this.format() + " attacks the arguments " + tmp + "; thus it is not a conflict-free set!", instruction, framework.getPane()),show);
			}
			return false;
		}

		if(write){
			framework.addToInteractor(new Command(this.format() + " is a conflict-free set, because it does not attack its own arguments", toInstruction(Color.GREEN), framework.getPane()),show);
		}
		cf = true;
		return true;
	}

	/**
	 * Checks whether this extension candidate really is an admissible extension
	 * first checks if it is conflict-free
	 * then checks if it defends itself from outside attacks
	 * @param write whether to write certain messages to the interactor
	 * @param show whether to write anything to the interactor
	 * @return if the extension candidate is admissible
	 */
	public boolean isAdmissible(boolean write,boolean show) {
		ArrayList<Attack> defeated = new ArrayList<Attack>();
		ArrayList<Attack> undefeated = new ArrayList<Attack>();
		GraphInstruction highlight = toInstruction(Color.GREEN);
		//ArrayList<SingleInstruction> defenceInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> attackerInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> undefeatedInstructions = new ArrayList<SingleInstruction>();

		if(!cf){
			framework.addToInteractor(new Command(format() + "is not conflict-free, so it can't be admissible.",null, framework.getPane()),show);
			return false;
		}

		for(Attack inc: incomingAttacks){			
			if(extensionAttacks.contains(inc.getAttacker())){
				defeated.add(inc);
			}
			else{
				undefeated.add(inc);
				attackerInstructions.add(new SingleInstruction(inc.getAttacker().getName(),Color.RED));
				undefeatedInstructions.add(new SingleInstruction("" + inc.getAttacker().getName() +
						inc.getAttacked().getName(),Color.RED));
			}
		}

		highlight.setEdgeInstructions(undefeatedInstructions);
		highlight.getNodeInstructions().addAll(attackerInstructions);

		if(undefeated.isEmpty()){
			if(write){
				framework.addToInteractor(new Command(format() + " defends all its arguments, so it is an admissible extension.", highlight, framework.getPane()),show);
			}
			adm = true;
			return true;
		}
		else{
			if(write){
				framework.addToInteractor(new Command(format() + " does not defend against " + Framework.formatAttackerList(undefeated,1) + ", so it is not an admissible extension.", highlight, framework.getPane()),show);
			}
			return false;
		}
	}

	/**
	 * checks if the extension is preferred with regards to a list of admissible extensions
	 * @param admissible a list of all admissible extension to check
	 * @param show whether to write anything to the interactor
	 * @return if the extension candidate is preferred
	 */
	public boolean isPreferred(ArrayList<Extension> admissible, boolean show) {
		if(!adm){
			framework.addToInteractor(new Command(format() + "is not admissible, so it can't be preferred.",null, framework.getPane()),show);
			return false;
		}

		for(Extension e: admissible){
			if(e.equals(this)){
				continue;
			}
			else if(isSubsetOf(e)){
				String format = format();
				GraphInstruction instruction = e.toInstruction(Color.GREEN);

				for(Argument a: e.getArguments()){
					if(!getArguments().contains(a)){
						instruction.getNodeInstructions().add(new SingleInstruction(""+a.getName(),Color.BLUE));
					}
				}

				framework.addToInteractor(new Command("Since " + format + " is a subset of " + e.format() + ", " + format + " is not preferred.", instruction, framework.getPane()),show);
				return false;
			}
		}

		framework.addToInteractor(new Command(format() + " is not the subset of another admissible extension, so it is a preferred extension.", toInstruction(Color.GREEN), framework.getPane()),show);
		return true;
	}

	/**
	 * checks whether the extension's arguments are a subset of another's
	 * @param e the extension to compare to
	 * @return whether a subset relation is true
	 */
	private boolean isSubsetOf(Extension e) {
		ArrayList<Argument> extArg = e.getArguments();

		for(Argument a: arguments){
			if(!extArg.contains(a)){
				return false;
			}
		}

		return true;
	}

	/**
	 * checks whether the extension is stable by checking
	 * whether it attacks all arguments it does not contain
	 * @param show whether to write anything to the interactor
	 * @return if the extension is stable
	 */
	public boolean isStable(boolean show){
		ArrayList<Argument> outside = new ArrayList<Argument>();
		ArrayList<Argument> unattacked = new ArrayList<Argument>();
		GraphInstruction highlight = toInstruction(Color.GREEN);

		if(!cf){
			framework.addToInteractor(new Command(format() + "is not conflict-free, so it can't be stable.",null, framework.getPane()),show);
			return false;
		}

		outside.addAll(framework.getArguments());
		outside.removeAll(arguments);

		for(Argument a: outside){
			if(!extensionAttacks.contains(a)){
				unattacked.add(a);
				highlight.getNodeInstructions().add(new SingleInstruction(""+a.getName(),Color.RED));
			}
		}

		if(unattacked.isEmpty()){
			framework.addToInteractor(new Command(format() + " attacks every argument outside itself, so it is a stable extension.", highlight, framework.getPane()),show);
			return true;
		}
		else{
			framework.addToInteractor(new Command(format() + " is not a stable extension because it doesn't attack " + Framework.formatArgumentList(unattacked) + ".", highlight, framework.getPane()),show);
			return false;
		}
	}

	/**
	 * checks if the extension is a complete extension
	 * by checking whether it contains all argument that it defends from other
	 * arguments
	 * @param write whether to write certain messages to the interactor
	 * @param show whether to write anything to the interactor
	 * @return if the extension is complete
	 */
	public boolean isComplete(boolean write,boolean show){
		ArrayList<Argument> outside = new ArrayList<Argument>();
		ArrayList<Argument> uselessDefences = new ArrayList<Argument>();
		GraphInstruction highlight = toInstruction(Color.GREEN);
		ArrayList<SingleInstruction> nodeIns = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> edgeIns = new ArrayList<SingleInstruction>();

		if(!adm){
			framework.addToInteractor(new Command(format() + "is not admissible, so it can't be complete.",null, framework.getPane()),show);
			return false;
		}

		outside.addAll(framework.getArguments());
		outside.removeAll(arguments);

		for(Argument a: outside){
			ArrayList<SingleInstruction> tmpNodeIns = new ArrayList<SingleInstruction>();
			ArrayList<SingleInstruction> tmpEdgeIns = new ArrayList<SingleInstruction>();

			if(!extensionAttacks.contains(a)){ //if extension doesn't attack it it needs to be checked if its defended
				ArrayList<Argument> argAttackers = new ArrayList<Argument>();

				for(Attack att: framework.getAttacks()){ //get arguments attacking the outside argument
					if(att.getAttacked().equals(a)){
						argAttackers.add(att.getAttacker());
					}
				}

				int defenses = 0;
				for(Argument att: argAttackers){
					if(extensionAttacks.contains(att)){
						defenses++;
						tmpNodeIns.add(new SingleInstruction(""+att.getName(),Color.RED));
						tmpEdgeIns.add(new SingleInstruction(""+att.getName()+a.getName(),Color.RED));
						for(Attack o: outgoingAttacks){ //highlights all attacks on the attacker the argument was defended from
							if(o.getAttacked().equals(att)){
								tmpEdgeIns.add(new SingleInstruction(""+o.getAttacker().getName()+att.getName(),Color.GREEN));
							}
						}
					}
				}

				if(defenses == argAttackers.size()){
					uselessDefences.add(a);
					highlight.getNodeInstructions().add(new SingleInstruction(""+a.getName(),Color.BLUE));
					nodeIns.addAll(tmpNodeIns);
					edgeIns.addAll(tmpEdgeIns);
				}
			}
		}

		if(uselessDefences.isEmpty()){
			if(write){
				framework.addToInteractor(new Command(format() + " contains all the arguments it defends and therefore is a complete extension.", highlight, framework.getPane()),show);
			}

			return true;
		}
		else{
			if(write){
				highlight.getNodeInstructions().addAll(nodeIns);

				if(highlight.getEdgeInstructions() != null){
					highlight.getEdgeInstructions().addAll(edgeIns);
				}
				else{
					highlight.setEdgeInstructions(edgeIns);
				}

				framework.addToInteractor(new Command(format() + " defends the argument(s) " + Framework.formatArgumentList(uselessDefences) + ", which it does not contain. " + format() + " is not a complete extension.",
						highlight, framework.getPane()),show);
			}

			return false;
		}
	}

	/**
	 * constructs an instruction containing all the arguments of the extension
	 * @param color which color the arguments should be
	 * @return an instruction passable to the interactor
	 */
	public GraphInstruction toInstruction(Color color) {
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: arguments){
			String argName = String.valueOf(a.getName());
			nodeInstructions.add(new SingleInstruction(argName, color));
		}

		return new GraphInstruction(nodeInstructions, null, framework.getPane());
	}

	/**
	 * checks whether the extension equals another
	 * therefore we check if the arguments of the extensions are the same
	 * (as defined by Argument.equals)
	 * @param other extension to compare this one to
	 * @return equality of the two extensions
	 */
	public boolean equals(Extension other){
		ArrayList<Argument> thisArguments = new ArrayList<Argument>();
		ArrayList<Argument> otherArguments = new ArrayList<Argument>();
		
		thisArguments.addAll(arguments);
		otherArguments.addAll(other.getArguments());
		
		boolean found;
		
		if(thisArguments.size() == 0 && otherArguments.size() == 0){
			return true;
		}
		else if(thisArguments.size() != otherArguments.size()){
			return false;
		}
		
		for(int t = thisArguments.size()-1; t >= 0; t--){
			found = false;
			
			for(int o = otherArguments.size()-1; o >= 0; o--){
				
				if(thisArguments.get(t).getName() == otherArguments.get(o).getName()){
					thisArguments.remove(t);
					otherArguments.remove(o);
					found = true;
					break;
				}
			}
			
			if(!found){
				return false;
			}
		}
		
		if(otherArguments.size() > 0){
			return false;
		}
		
		return true;
	}
	
	/**
	 * formats the extension as a list of arguments
	 * @return the formatted extension as a string
	 */
	public String format() {
		String formatted = "{";

		for(Argument a: arguments){
			formatted += a.getName() + ", ";
		}

		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}

		formatted += "}";

		return formatted;
	}

	public ArrayList<Argument> getArguments() {
		return arguments;
	}

}
