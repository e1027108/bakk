package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Command;
import interactor.GraphInstruction;
import interactor.Interactor;
import interactor.SingleInstruction;
import javafx.scene.paint.Color;

public class Kernel extends Framework {

	private Type type;
	private Framework parent;
	private int pane;

	public Kernel(Framework parent, Interactor interactor, Type type, int pane) throws InvalidInputException {
		super(parent.getArguments(), parent.getAttacks(), interactor, pane);
		this.parent = parent;
		this.type = type;
		this.pane = pane;
		computeKernel(this.type);
	}

	//all arguments stay in the kernel
	private void computeKernel(Type type) throws InvalidInputException {
		switch(type){
		case st:
			interactor.addToCommands(new Command("Computing stable kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeStableKernel();
			break;
		case ad:
			interactor.addToCommands(new Command("Computing admissible kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeAdmissibleKernel(false);
			break;
		case gr:
			interactor.addToCommands(new Command("Computing grounded kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeGroundedKernel(false);
			break;
		case co:
			interactor.addToCommands(new Command("Computing complete kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeCompleteKernel(false);
			break;
		case adstar:
			interactor.addToCommands(new Command("Computing admissible-*-kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeAdmissibleKernel(true);
			break;
		case costar:
			interactor.addToCommands(new Command("Computing complete-*-kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeCompleteKernel(true);
			break;
		case grstar:
			interactor.addToCommands(new Command("Computing grounded-*-kernel of framework with arguments " + 
					Framework.formatArgumentList(parent.getArguments()) + " and attacks " + Framework.formatAttackList(parent.getAttacks()),
					null,pane));
			computeGroundedKernel(true);
			break;
		default:
			throw new InvalidInputException("No kernel possible for the chosen semantic!");
		}
	}

	/**
	 * computes a stable kernel, removing all attacks except the attacks from self-attacking arguments (the self-attack stays)
	 */
	private void computeStableKernel() {
		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		if(selfAttacking.isEmpty()){
			interactor.addToCommands(new Command("No arguments attack themselves, the kernel is equivalent to the framework!",null,pane));
			return;
		}

		String formattedSelfAttacking = formatArgumentList(selfAttacking);
		GraphInstruction selfAttackInstruction = getSelfAttackInstruction(selfAttacking,Color.BLUE);

		toRemove = getSelfAttackingRemovalList(selfAttacking);

		for(Attack a: toRemove){
			selfAttackInstruction.getEdgeInstructions().add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		interactor.addToCommands(new Command("Since " + formattedSelfAttacking + " are attacking themselves, we do not include their attacks on other arguments in the stable kernel",
				selfAttackInstruction,pane));

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting stable kernel contains all arguments and the attacks " + formatAttackList(attacks) + ".", getKernelInstruction(Color.GREEN),pane));
	}

	/**
	 * computes admissible kernel removing all attacks except the attacks from self-attacking arguments (the self-attack stays), if the attacked attacks back or also attacks itself
	 * if star is true also removes attacks ((a,b) where a != b and a self-attacks + (b self-attacks or b attacks a back)) or (b self-attacks + for every c attacked by b: c is attacked by a or
	 * c attacks a or c is self-attacking or c attacks b back)
	 * @param star whether or not a star kernel is to be computed
	 */
	private void computeAdmissibleKernel(boolean star) {

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();
		String formattedSelfAttacking = formatArgumentList(selfAttacking);
		GraphInstruction selfAttackInstruction = getSelfAttackInstruction(selfAttacking,Color.BLUE);

		if(!selfAttacking.isEmpty()){
			interactor.addToCommands(new Command("We want to compute the " + getDescription("admissible",star) + " kernel. Therefore we look at self-attacking arguments " + formattedSelfAttacking + ".", 
					selfAttackInstruction,pane));
		}
		else{
			interactor.addToCommands(new Command("No arguments attack themselves, the kernel is equivalent to the framework!",null,pane));
			return;
		}

		//build first two conditions
		toRemove = getSelfAttackingRemovalList(selfAttacking);

		selfAttackInstruction.setEdgeInstructions(new ArrayList<SingleInstruction>()); //here we don't continue highlighting the self attacks, the arguments we continue highlighting
		for(Attack a: toRemove){
			selfAttackInstruction.getEdgeInstructions().add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		interactor.addToCommands(new Command("We look at all their attacks on other arguments to determine whether they belong in the kernel.",
				selfAttackInstruction,pane));

		ArrayList<Attack> BsAttackingBack = new ArrayList<Attack>();
		ArrayList<Attack> SelfAttackingBs = new ArrayList<Attack>();

		for(Attack a: toRemove){
			Argument attacker = a.getAttacker();
			Argument attacked = a.getAttacked();
			boolean aAttackedByb = getAttackedBy(attacked.getName()).contains(attacker);
			boolean bSelfAttacking = selfAttacking.contains(attacked);
			if(!(aAttackedByb || bSelfAttacking)){
				maybeNotRemove.add(a);
			}
			else{ //notes for interactor
				if(aAttackedByb){ 
					BsAttackingBack.add(a);
				}
				if(bSelfAttacking){
					SelfAttackingBs.add(a);
				}
			}
		}

		ArrayList<SingleInstruction> removeInstruction = new ArrayList<SingleInstruction>();

		for(Attack a: BsAttackingBack){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		if(!removeInstruction.isEmpty()){
			interactor.addToCommands(new Command("We want to remove the attacks " + formatAttackList(BsAttackingBack) + " from the kernel, since they are defences against self-attacking arguments.",
					new GraphInstruction(null,removeInstruction,pane),pane));
		}
		else{
			interactor.addToCommands(new Command("There are no attacks that are defences against self-attacking arguments.",null,pane));
		}

		removeInstruction = new ArrayList<SingleInstruction>();

		for(Attack a: SelfAttackingBs){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		if(!removeInstruction.isEmpty()){
			interactor.addToCommands(new Command("Additionally we want to remove the attacks " + formatAttackList(SelfAttackingBs) + " from the kernel, since they are attacks on self-attacking arguments.",
					new GraphInstruction(null,removeInstruction,pane),pane));
		}
		else{
			interactor.addToCommands(new Command("There are no attacks from other arguments on self-attacking arguments.",null,pane));
		}

		if(star){
			//check the second part
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();				
				if(selfAttacking.contains(b)){
					//check whether for all c the conditions hold

					boolean kernelCondition = true;

					ArrayList<Argument> thirdArguments = getAttackedBy(b.getName());
					for(Argument c: thirdArguments){
						ArrayList<Argument> attackedByThird = getAttackedBy(c.getName());
						boolean aAttacksc = getAttackedBy(a.getName()).contains(c);
						boolean aAttackedByc = attackedByThird.contains(a);
						boolean bAttackedByc = attackedByThird.contains(b);
						boolean selfAttackingc = selfAttacking.contains(c);
						if(!(aAttacksc || aAttackedByc || bAttackedByc || selfAttackingc)){
							maybeAlsoNotRemove.add(att);
							kernelCondition = false;
							break;
						}
					}

					if(kernelCondition){
						ArrayList<SingleInstruction> attackInstruction = new ArrayList<SingleInstruction>();
						attackInstruction.add(new SingleInstruction(""+a.getName()+b.getName(),Color.RED));
						interactor.addToCommands(new Command("The attack (" + a.getName() + "," + b.getName() + ") is removed from the kernel, since " + b.getName() + " attacks itself and, all arguments "
								+ b.getName() + " attacks, either: (1) are attacked by " + a.getName() + ", (2) attack " + a.getName() + ", (3) attack back or (4) attack themselves.", 
								new GraphInstruction(null,attackInstruction,pane),pane));
					}
				}
				else{
					maybeAlsoNotRemove.add(att);
				}
			}

			//if we don't get here maybenotremove is not removed
			//otherwise we check here what is really not removed
			maybeNotRemove.retainAll(maybeAlsoNotRemove);
		}

		toRemove.remove(maybeNotRemove);

		//testing
		/*for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}*/

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("admissible",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks) + ".", getKernelInstruction(Color.GREEN),pane));
	}

	/**
	 * computes grounded kernel removing all attacks except the attacks on self-attacking arguments (a self-attack stays), but only if the attacker doesn't attack itself or gets attacked back
	 * if star is true also remove attacks for self-attacking bs have for all c: b attacks c and (a attacks c, c attacks a, or c is self-attacking) 
	 * @param star whether it is a standard or star kernel
	 */
	private void computeGroundedKernel(boolean star) {
		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();
		String formattedSelfAttacking = formatArgumentList(selfAttacking);
		GraphInstruction selfAttackInstruction = getSelfAttackInstruction(selfAttacking,Color.BLUE);

		if(!selfAttacking.isEmpty()){
			interactor.addToCommands(new Command("We want to compute the " + getDescription("grounded",star) + " kernel. Therefore we look at self-attacking arguments " + formattedSelfAttacking + ".", selfAttackInstruction,pane));
		}
		else{
			interactor.addToCommands(new Command("No arguments attack themselves, the kernel is equivalent to the framework!",null,pane));
			return;
		}

		selfAttackInstruction.setEdgeInstructions(new ArrayList<SingleInstruction>()); //here we don't continue highlighting the self attacks, the arguments we continue highlighting

		for(Attack a: attacks){
			if(selfAttacking.contains(a.getAttacked())){
				if(a.getAttacked() != a.getAttacker()){
					toRemove.add(a);
					selfAttackInstruction.getEdgeInstructions().add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
				}
			}
		}

		interactor.addToCommands(new Command("We look at all the attacks on them by other arguments to determine whether they belong in the kernel.",
				selfAttackInstruction,pane));

		ArrayList<Attack> selfAttackingAs = new ArrayList<Attack>();
		ArrayList<Attack> AsAttackedByBs = new ArrayList<Attack>();

		for(Attack att: toRemove){
			Argument attacker = att.getAttacker();
			Argument attacked = att.getAttacked();
			boolean ASelfAttacks = selfAttacking.contains(attacker);
			boolean AGetsAttackedByB = getAttackedBy(attacked.getName()).contains(attacker);
			if(!(ASelfAttacks || AGetsAttackedByB)){
				maybeNotRemove.add(att);
			}
			else{
				if(ASelfAttacks){
					selfAttackingAs.add(att);
				}
				if(AGetsAttackedByB){
					AsAttackedByBs.add(att);
				}
			}
		}

		ArrayList<SingleInstruction> removeInstruction = new ArrayList<SingleInstruction>();

		for(Attack a: AsAttackedByBs){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		if(!removeInstruction.isEmpty()){
			interactor.addToCommands(new Command("We want to remove the attacks " + formatAttackList(AsAttackedByBs) + " from the kernel, since they are defences against self-attacking arguments.",
					new GraphInstruction(null,removeInstruction,pane),pane));
		}
		else{
			interactor.addToCommands(new Command("There are no attacks that are defences against self-attacking arguments.",null,pane));
		}

		removeInstruction = new ArrayList<SingleInstruction>();

		for(Attack a: selfAttackingAs){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		if(!removeInstruction.isEmpty()){
			interactor.addToCommands(new Command("Additionally we want to remove the attacks " + formatAttackList(selfAttackingAs) + " from the kernel, since they are attacks by self-attacking arguments.",
					new GraphInstruction(null,removeInstruction,pane),pane));
		}
		else{
			interactor.addToCommands(new Command("There are no attacks that are attacks against self-attacking arguments.",null,pane));
		}

		if(star){
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att:toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();

				if(selfAttacking.contains(b)){

					boolean kernelCondition = true;

					ArrayList<Argument> cs = getAttackedBy(b.getName());
					for(Argument c: cs){
						if(!(selfAttacking.contains(c) || getAttackedBy(c.getName()).contains(a) || getAttackedBy(a.getName()).contains(c)) ){
							maybeAlsoNotRemove.add(att);
							kernelCondition = false;
							break;
						}
					}

					if(kernelCondition){
						ArrayList<SingleInstruction> attackInstruction = new ArrayList<SingleInstruction>();
						attackInstruction.add(new SingleInstruction(""+a.getName()+b.getName(),Color.RED));
						interactor.addToCommands(new Command("The attack (" + a.getName() + "," + b.getName() + ") is removed from the kernel, since " + b.getName() + " attacks itself and, all arguments "
								+ b.getName() + " attacks, either: (1) are attacked by " + a.getName() + ", (2) attack " + a.getName() + " or (3) attack themselves.",
								new GraphInstruction(null,attackInstruction,pane),pane));
					}
				}
				else{
					maybeAlsoNotRemove.add(att);
				}
			}

			maybeNotRemove.retainAll(maybeAlsoNotRemove);
		}

		toRemove.remove(maybeNotRemove);

		//testing
		/*for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}*/

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("grounded",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks) + ".", getKernelInstruction(Color.GREEN),pane));
	}

	/**
	 * removes all attacks except the attacks of self-attacking arguments (a) (the self-attack stays), but only if the attacked (b) also attacks itself
	 * if star is set also removes attacks for self-attacking bs if	b doesn't attack a and for all c that b attacks: a attacks c or c attacks a or c is self-attacking
	 * @param star whether it is a standard or star kernel
	 */
	private void computeCompleteKernel(boolean star) {
		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		if(selfAttacking.isEmpty()){
			interactor.addToCommands(new Command("There are no self-attacking arguments, therefore the kernel is equal to the framework.",null,pane));
			return;
		}

		toRemove = getSelfAttackingRemovalList(selfAttacking); //now we have checked that a attacks itself

		ArrayList<Attack> definetlyRemove = new ArrayList<Attack>();
		ArrayList<SingleInstruction> removeInstruction = new ArrayList<SingleInstruction>();

		//now weed out if b also attacks itself
		for(Attack att: toRemove){
			Argument b = att.getAttacked();
			if(!selfAttacking.contains(b)){
				maybeNotRemove.add(att);
			}
			else{
				definetlyRemove.add(att);
				removeInstruction.add(new SingleInstruction(""+att.getAttacker().getName()+att.getAttacked().getName(),Color.RED));
			}
		}

		if(!definetlyRemove.isEmpty()){
			interactor.addToCommands(new Command("Since " + formatAttackList(definetlyRemove) + " are attacks between two self-attacking arguments, we do not want them in the "
					+ getDescription("complete",star) + " kernel.",
					new GraphInstruction(null,removeInstruction,pane),pane));
		}
		else{
			interactor.addToCommands(new Command("There are no attacks between two self-attacking arguments, hence no such attacks are removed from the kernel",null,pane));
		}

		if(star){
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b) && !getAttackedBy(b.getName()).contains(a)){
					
					boolean kernelCondition = true;
					
					ArrayList<Argument> cs = getAttackedBy(b.getName());
					for(Argument c: cs){
						if(!(getAttackedBy(a.getName()).contains(c) || selfAttacking.contains(c) || getAttackedBy(c.getName()).contains(a))){
							maybeAlsoNotRemove.add(att);
							kernelCondition = false;
							break;
						}
					}
					
					if(kernelCondition){
						ArrayList<SingleInstruction> attackInstruction = new ArrayList<SingleInstruction>();
						attackInstruction.add(new SingleInstruction(""+a.getName()+b.getName(),Color.RED));
						interactor.addToCommands(new Command("The attack (" + a.getName() + "," + b.getName() + ") is removed from the kernel, since " + b.getName() + " attacks itself, does not attack " +
							a.getName() + " and all arguments " + b.getName() + " attacks, either: (1) are attacked by " + a.getName() + ", (2) attack " + a.getName() + "or (3) attack themselves.",
							new GraphInstruction(null,attackInstruction,pane),pane));
					}

				}
				else{
					maybeAlsoNotRemove.add(att);
				}
			}

			maybeNotRemove.retainAll(maybeAlsoNotRemove);
		}

		toRemove.removeAll(maybeNotRemove);

		//testing
		/*for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}*/

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("complete",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks) + ".", getKernelInstruction(Color.GREEN),pane));
	}

	private ArrayList<Argument> getSelfAttacking(){
		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();

		for(Attack a: attacks){
			if(a.getAttacked() == a.getAttacker()){ //i think i want the exact same object
				selfAttacking.add(a.getAttacker());
			}
		}

		return selfAttacking;
	}

	private GraphInstruction getSelfAttackInstruction(ArrayList<Argument> selfAttackers, Color color){
		GraphInstruction selfAttackInstruction = getNodeInstructionsFromArgumentList(formatArgumentList(selfAttackers),color);
		ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: selfAttackers){
			edgeInstructions.add(getEdgeInstructionFromAttack(""+a.getName()+a.getName(),color));
		}

		selfAttackInstruction.setEdgeInstructions(edgeInstructions);

		return selfAttackInstruction;
	}

	public GraphInstruction getKernelInstruction(Color color){
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: arguments){
			nodeInstructions.add(new SingleInstruction(a.getName(),color));
		}
		for(Attack a: attacks){
			edgeInstructions.add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),color));
		}

		return new GraphInstruction(nodeInstructions,edgeInstructions,pane);
	}

	/**
	 * returns a list of attacks of self-attacking arguments, but not the self-attacks themselves
	 * @param selfAttacking a list of self-attacking arguments
	 * @return the list of other attacks
	 */
	private ArrayList<Attack> getSelfAttackingRemovalList(ArrayList<Argument> selfAttacking){
		ArrayList<Attack> toRemove = new ArrayList<Attack>();

		for(Attack a: attacks){
			if(selfAttacking.contains(a.getAttacker())){ //exact same object again
				if(a.getAttacked() != a.getAttacker()){
					toRemove.add(a);
				}
			}
		}

		return toRemove;
	}

	private String getDescription(String type, boolean star){
		if(star){
			return type;
		}
		else{
			return type+"-*";
		}
	}

}
