package logic.old;

import interactor.Command;
import interactor.GraphInstruction;
import interactor.SingleInstruction;

import java.util.ArrayList;

import edu.uci.ics.jung.graph.util.Pair;
import exceptions.InvalidInputException;
import javafx.scene.paint.Color;

/**
 * The Extension class contains a set of arguments and is capable
 *  of computing if it is of (most of) the extension types of an abstract argumentation framework
 * @author Patrick Bellositz
 */
@Deprecated
public class OldExtension {

	private ArrayList<OldArgument> arguments; //arguments of extension
	private OldFramework framework; //framework from which the extension is derived

	/**
	 * creates a new Extension with a starting node
	 * @param framework is the framework from which the extension is derived
	 * @param a is the starting argument
	 * @throws InvalidInputException if argument that is not in framework should be added
	 */
	public OldExtension(OldArgument a, OldFramework framework) throws InvalidInputException{
		this.arguments = new ArrayList<OldArgument>();
		this.framework = framework;
		addArgument(a);
	}

	/**
	 * creates a whole Extension
	 * @param framework is the framework from which the extension is derived
	 * @param arguments is a set of Arguments
	 */
	public OldExtension(ArrayList<OldArgument> arguments, OldFramework framework){
		this.arguments = new ArrayList<OldArgument>();
		this.framework = framework;
		this.arguments.addAll(arguments);
	}

	/**
	 * adds an argument to the Extension
	 * @param a is the argument to be added
	 * @throws InvalidInputException if argument that is not in framework should be added
	 */
	public void addArgument(OldArgument a) throws InvalidInputException{
		if(!framework.getArguments().contains(a)){
			throw new InvalidInputException("Argument is not in framework!");
		}

		if(!arguments.contains(a)){
			arguments.add(a);
		}
	}

	/**
	 * checks if an extension is conflict-free
	 * @details checks if no argument in the extension attacks another
	 * @param write whether the results are to be handed to the Interactor
	 * @return if the extension is conflict-free
	 */
	public boolean isConflictFree(boolean write){
		ArrayList<Pair<String>> violatingAttacks = new ArrayList<Pair<String>>();

		for(OldArgument a: arguments){
			String attacks = a.getAttacks();
			for(OldArgument a2: arguments){
				String tmp = "" + a2.getName();
				if(attacks.contains(tmp)){
					violatingAttacks.add(new Pair<String>(String.valueOf(a.getName()),tmp));
				}
			}
		}

		if(!violatingAttacks.isEmpty()){
			if(write){
				String tmp = "";
				GraphInstruction instruction = toInstruction(Color.GREEN);
				ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

				for(Pair<String> p: violatingAttacks){
					if(!tmp.contains(p.getSecond())){ //multiple arguments could attack the same one
						tmp += p.getSecond();
					}
					edgeInstructions.add(new SingleInstruction((p.getFirst()+p.getSecond()),Color.RED));
				}

				tmp = framework.formatNameList(tmp);
				instruction.setEdgeInstructions(edgeInstructions);

				framework.addToInteractor(new Command(this.format() + " attacks the arguments " + tmp + "; thus it is not a conflict-free set!", instruction));
			}
			return false;
		}

		if(write){
			framework.addToInteractor(new Command(this.format() + " is a conflict-free set, because it does not attack its own arguments", toInstruction(Color.GREEN)));
		}
		return true;
	}

	/**
	 * checks if a set is admissible
	 * @param whether the results are to be handed to the Interactor
	 * @return if all the arguments are defended
	 */ //TODO is it better to if(write) everything to do with the interactor?
	public boolean isAdmissible(boolean write){
		if(!isConflictFree(false)){
			if(write){
				framework.addToInteractor(new Command(format() + " is not a conflict-free set, so it is not an admissible extension.", toInstruction(Color.RED)));
			}
			return false;
		}
		else{
			String undefended = "";
			GraphInstruction highlight = toInstruction(Color.GREEN);
			highlight.setEdgeInstructions(new ArrayList<SingleInstruction>());
			ArrayList<SingleInstruction> relevantDefenses = new ArrayList<SingleInstruction>();

			for(OldArgument a: arguments){
				ArrayList<String> defences = framework.getDefences(this, a);
				ArrayList<OldArgument> attackers = framework.getAttackers(a.getName());
				String argName = String.valueOf(a.getName());

				if(attackers.isEmpty()){ //if it doesn't get attacked, no defences are neccessary
					continue;
				}
				else{
					String defeatedAttackers = "";

					for(String p: defences){
						defeatedAttackers += p.charAt(1);
					}

					for(OldArgument att: attackers){
						String attName = String.valueOf(att.getName());

						if(!defeatedAttackers.contains(attName)){
							if(att.getAttacks().contains(argName)){
								highlight.getNodeInstructions().add(new SingleInstruction(attName, Color.RED));					
								highlight.getEdgeInstructions().add(new SingleInstruction(attName+argName, Color.RED));
								undefended += attName;
							}
						}
						else{
							if(a.getAttacks().contains(attName)){
								relevantDefenses.add(new SingleInstruction(argName+attName, Color.GREEN));
							}
						}
					}
				}
			}
			if(undefended.length() > 0){
				if(write){
					framework.addToInteractor(new Command(format() + " does not defend against " + framework.formatNameList(undefended) + ", so it is not an admissible extension.", highlight));
				}
				return false;
			}
			else{
				highlight.getEdgeInstructions().addAll(relevantDefenses);
				if(write){
					framework.addToInteractor(new Command(format() + " defends all its arguments, so it is an admissible extension.", highlight));
				}
				return true;
			}
		}
	}

	/**
	 * checks if the extension is preferred of the given ones
	 * @param admissible the set of admissible extensions of which the extension may be preferred
	 * @return if the extension is a true subset of another
	 */
	public boolean isPreferred(ArrayList<OldExtension> admissible){		
		for(OldExtension e: admissible){
			if(e.equals(this)){
				continue;
			}
			else if(isSubsetOf(e)){
				String format = format();
				GraphInstruction instruction = e.toInstruction(Color.GREEN);

				for(OldArgument a: e.getArguments()){
					if(!getArguments().contains(a)){
						instruction.getNodeInstructions().add(new SingleInstruction(""+a.getName(),Color.BLUE));
					}
				}

				framework.addToInteractor(new Command("Since " + format + " is a subset of " + e.format() + ", " + format + " is not preferred.", instruction));
				return false;
			}
		}

		framework.addToInteractor(new Command(format() + " is not the subset of another admissible extension, so it is a preferred extension.", toInstruction(Color.GREEN)));
		return true;
	}

	/**
	 * checks if the extension is a sub-extension of the given one
	 * @param e the given extension
	 * @return if all elements of the extension are also in the given extension
	 */
	private boolean isSubsetOf(OldExtension e) {
		ArrayList<OldArgument> extArg = e.getArguments();

		for(OldArgument a: arguments){
			if(!extArg.contains(a)){
				return false;
			}
		}

		return true;
	}

	/**
	 * checks if the extension in stable
	 * @return if every argument outside the extension is attacked
	 */
	public boolean isStable(){
		ArrayList<OldArgument> allArguments = framework.getArguments();
		String attacks = getAttacks();
		GraphInstruction instruction = toInstruction(Color.GREEN);
		instruction.setEdgeInstructions(new ArrayList<SingleInstruction>());

		if(!isConflictFree(false)){
			framework.addToInteractor(new Command(format() + " is not a conflict-free set, so it is not a stable extension.", toInstruction(Color.RED)));
			return false;
		}

		for(OldArgument a: getArguments()){
			String argName = String.valueOf(a.getName());
			String argAttacks = a.getAttacks();
			for(int i = 0; i < argAttacks.length(); i++){
				instruction.getEdgeInstructions().add(new SingleInstruction(argName+argAttacks.charAt(i), Color.GREEN));
			}
		}

		if(attacks.length() == (allArguments.size()-getArgumentNames().length())){
			framework.addToInteractor(new Command(format() + " attacks every argument outside itself, so it is a stable extension.", instruction));
			return true;
		}
		else{
			String notAttacked = "";

			for(OldArgument a: allArguments){
				String argName = String.valueOf(a.getName());

				if(!arguments.contains(a) && !attacks.contains(argName)){
					instruction.getNodeInstructions().add(new SingleInstruction(argName,Color.RED));
					notAttacked += argName;
				}
			}

			framework.addToInteractor(new Command(format() + " is not a stable extension because it does not attack " + framework.formatNameList(notAttacked) + ".", instruction));
			return false;
		}
	}

	/**
	 * computes all attacks of the extension
	 * @return a String list of arguments that the extension attacks
	 */
	public String getAttacks(){
		String tmp = "";
		String attacks = "";

		for(OldArgument a: arguments){
			tmp += a.getAttacks();
		}

		for(int i = 0; i<tmp.length(); i++){
			if(!attacks.contains(String.valueOf(tmp.charAt(i)))){
				attacks += tmp.charAt(i);
			}
		}

		return attacks;
	}

	/**
	 * computes a list of all arguments
	 * @return a String list of arguments in the extension
	 */
	public String getArgumentNames(){
		String names = "";

		for(OldArgument a: arguments){
			names += a.getName(); 
		}

		return names;
	}

	/**
	 * formats the extension to a user-friendly string format
	 * @return a list of argument names (separated by ',' between '{' and '}')
	 */
	public String format() {
		String formatted = "{";

		for(OldArgument a: arguments){
			formatted += a.getName() + ", ";
		}

		if(formatted.length() > 1){
			formatted = formatted.substring(0,formatted.length()-2);
		}

		formatted += "}";

		return formatted;
	}

	/**
	 * @return list of arguments of the extension
	 */
	public ArrayList<OldArgument> getArguments(){
		return arguments;
	}

	/**
	 * computes instruction that highlights exension's nodes in given color
	 * @param color the color of the highlighting
	 * @return the set of seperate instructions needed for to highlight the nodes
	 */
	public GraphInstruction toInstruction(Color color) {
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();

		for(OldArgument a: arguments){
			String argName = String.valueOf(a.getName());
			nodeInstructions.add(new SingleInstruction(argName, color));
		}

		return new GraphInstruction(nodeInstructions, null);
	}
}
