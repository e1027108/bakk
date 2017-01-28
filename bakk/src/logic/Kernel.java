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

	public Kernel(Framework parent, Interactor interactor, Type type) throws InvalidInputException {
		super(parent.getArguments(), parent.getAttacks(), interactor);
		this.parent = parent;
		this.type = type;
		computeKernel(this.type);
	}

	//all arguments stay in the kernel
	private void computeKernel(Type type) throws InvalidInputException {
		switch(type){
		case st:
			interactor.addToCommands(new Command("Computing stable kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeStableKernel();
			break;
		case ad:
			interactor.addToCommands(new Command("Computing admissible kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeAdmissibleKernel(false);
			break;
		case gr:
			interactor.addToCommands(new Command("Computing grounded kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeGroundedKernel(false);
			break;
		case co:
			interactor.addToCommands(new Command("Computing complete kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeCompleteKernel(false);
			break;
		case adstar:
			interactor.addToCommands(new Command("Computing admissible-*-kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeAdmissibleKernel(true);
			break;
		case costar:
			interactor.addToCommands(new Command("Computing complete-*-kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
			computeCompleteKernel(true);
			break;
		case grstar:
			interactor.addToCommands(new Command("Computing grounded-*-kernel of framework with arguments " + 
					parent.formatArgumentList(parent.getArguments()) + " and attacks " + parent.formatAttackList(parent.getAttacks(), 1),
					null));
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
		String formattedSelfAttacking = formatArgumentList(selfAttacking);
		GraphInstruction selfAttackInstruction = getSelfAttackInstruction(selfAttacking,Color.BLUE);

		toRemove = getSelfAttackingRemovalList(selfAttacking);

		for(Attack a: toRemove){
			selfAttackInstruction.getEdgeInstructions().add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		interactor.addToCommands(new Command("Since " + formattedSelfAttacking + " are attacking themselves, we do not include their attacks on other arguments in the stable kernel",
				selfAttackInstruction));

		attacks.removeAll(toRemove);

		//TODO use framework names? just show the right one at any given time?
		interactor.addToCommands(new Command("The resulting stable kernel contains all arguments and the attacks " + formatAttackList(attacks,1) + ".", getKernelInstruction(Color.GREEN)));

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

		interactor.addToCommands(new Command("We want to compute the " + getDescription("admissible",star) + " kernel. Therefore we look at self-attacking arguments " + formattedSelfAttacking + ".", selfAttackInstruction));

		//build first two conditions
		toRemove = getSelfAttackingRemovalList(selfAttacking);

		selfAttackInstruction.setEdgeInstructions(new ArrayList<SingleInstruction>()); //here we don't continue highlighting the self attacks, the arguments we continue highlighting
		for(Attack a: toRemove){
			selfAttackInstruction.getEdgeInstructions().add(new SingleInstruction(""+a.getAttacker().getName()+a.getAttacked().getName(),Color.RED));
		}

		interactor.addToCommands(new Command("We look at all their attacks on other arguments to determine whether they belong in the kernel.",
				selfAttackInstruction));

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
			removeInstruction.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.RED));
		}
		
		interactor.addToCommands(new Command("We want to remove the attacks " + formatAttackList(BsAttackingBack,1) + " from the kernel, since they are defences against self-attacking arguments.",
				new GraphInstruction(null,removeInstruction)));
		
		removeInstruction = new ArrayList<SingleInstruction>();
		
		for(Attack a: SelfAttackingBs){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.RED));
		}
		
		interactor.addToCommands(new Command("Additionally we want to remove the attacks " + formatAttackList(SelfAttackingBs,1) + " from the kernel, since they are attacks on self-attacking arguments.",
				new GraphInstruction(null,removeInstruction)));

		if(star){
			//check the second part
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();
			ArrayList<Attack> AsAttackingAllCs = new ArrayList<Attack>();
			ArrayList<Attack> AsAttackedByAllCs = new ArrayList<Attack>();
			ArrayList<Attack> selfAttackingCs = new ArrayList<Attack>();
			ArrayList<Attack> BsAttackedByAllCs = new ArrayList<Attack>();

			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b)){
					//check whether for all c the conditions hold
					ArrayList<Argument> thirdArguments = getAttackedBy(b.getName());
					for(Argument c: thirdArguments){
						ArrayList<Argument> attackedByThird = getAttackedBy(c.getName());
						boolean aAttacksc = getAttackedBy(a.getName()).contains(c);
						boolean aAttackedByc = attackedByThird.contains(a);
						boolean bAttackedByc = attackedByThird.contains(b);
						boolean selfAttackingc = selfAttacking.contains(c);
						if(!(aAttacksc || aAttackedByc || bAttackedByc || selfAttackingc)){
							maybeAlsoNotRemove.add(att);
							break;
						}

						//notes for interactor
						if(aAttacksc){
							AsAttackingAllCs.add(att);
						}
						if(aAttackedByc){
							AsAttackedByAllCs.add(att);
						}
						if(bAttackedByc){
							BsAttackedByAllCs.add(att);
						}
						if(selfAttackingc){
							selfAttackingCs.add(att);
						}
					}
				}
				else{
					maybeAlsoNotRemove.add(att);
				}
			}

			//TODO star interactions (4 different)
			
			//if we don't get here maybenotremove is not removed
			//otherwise we check here what is really not removed

			maybeNotRemove.retainAll(maybeAlsoNotRemove);
		}

		toRemove.remove(maybeNotRemove);

		//testing
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("admissible",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks,1) + ".", getKernelInstruction(Color.GREEN)));
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

		interactor.addToCommands(new Command("We want to compute the " + getDescription("grounded",star) + " kernel. Therefore we look at self-attacking arguments " + formattedSelfAttacking + ".", selfAttackInstruction));

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
				selfAttackInstruction));

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
			removeInstruction.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.RED));
		}
		
		interactor.addToCommands(new Command("We want to remove the attacks " + formatAttackList(AsAttackedByBs,1) + " from the kernel, since they are defences against self-attacking arguments.",
				new GraphInstruction(null,removeInstruction)));
		
		removeInstruction = new ArrayList<SingleInstruction>();
		
		for(Attack a: selfAttackingAs){
			removeInstruction.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),Color.RED));
		}
		
		interactor.addToCommands(new Command("Additionally we want to remove the attacks " + formatAttackList(selfAttackingAs,1) + " from the kernel, since they are attacks by self-attacking arguments.",
				new GraphInstruction(null,removeInstruction)));

		//TODO interactor downwards for star
		if(star){
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att:toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b)){
					ArrayList<Argument> cs = getAttackedBy(b.getName());
					for(Argument c: cs){
						if(!(selfAttacking.contains(c) || getAttackedBy(c.getName()).contains(a) || getAttackedBy(a.getName()).contains(c)) ){
							maybeAlsoNotRemove.add(att);
							break;
						}
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
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("grounded",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks,1) + ".", getKernelInstruction(Color.GREEN)));
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
				removeInstruction.add(new SingleInstruction(""+att.getAttacker()+att.getAttacked(),Color.RED));
			}
		}
		
		interactor.addToCommands(new Command("Since " + formatAttackList(definetlyRemove,1) + " are attacks between two self-attacking arguments, we do not want them in the "
				+ getDescription("complete",star) + " kernel.",
				new GraphInstruction(null,removeInstruction)));

		//TODO interactor downwards for star
		if(star){
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b) && !getAttackedBy(b.getName()).contains(a)){
					ArrayList<Argument> cs = getAttackedBy(b.getName());
					for(Argument c: cs){
						if(!(getAttackedBy(a.getName()).contains(c) || selfAttacking.contains(c) || getAttackedBy(c.getName()).contains(a))){
							maybeAlsoNotRemove.add(att);
							break;
						}
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
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

		interactor.addToCommands(new Command("The resulting " + getDescription("complete",star) + " kernel contains all arguments and the attacks " + formatAttackList(attacks,1) + ".", getKernelInstruction(Color.GREEN)));
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

	private GraphInstruction getKernelInstruction(Color color){
		ArrayList<SingleInstruction> nodeInstructions = new ArrayList<SingleInstruction>();
		ArrayList<SingleInstruction> edgeInstructions = new ArrayList<SingleInstruction>();

		for(Argument a: arguments){
			nodeInstructions.add(new SingleInstruction(a.getName(),color));
		}
		for(Attack a: attacks){
			edgeInstructions.add(new SingleInstruction(""+a.getAttacker()+a.getAttacked(),color));
		}

		return new GraphInstruction(nodeInstructions,edgeInstructions);
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
