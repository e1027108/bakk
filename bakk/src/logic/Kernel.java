package logic;

import java.util.ArrayList;

import exceptions.InvalidInputException;
import interactor.Interactor;

public class Kernel extends Framework {

	private Type type;
	private Framework parent;

	//TODO interactor shall say something about kernel computation

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
			computeStableKernel();
			break;
		case ad:
			computeAdmissibleKernel(false);
			break;
		case gr:
			computeGroundedKernel();
			break;
		case co:
			computeCompleteKernel();
			break;
		case adstar: //TODO for all 
			computeAdmissibleKernel(true);
			break;
		default: //TODO don't enable checkbutton for other semantics?
			throw new InvalidInputException("No kernel possible for the chosen semantic!"); //TODO check whether this is actually true for the others
		}
	}

	//TODO test
	private void computeStableKernel() {
		//all attacks except the attacks from self-attacking arguments (the self-attack stays)

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		toRemove = getSelfAttackingRemovalList(selfAttacking);

		//testing --> TODO interactor statement?
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

	}

	//TODO test
	private void computeAdmissibleKernel(boolean star) {
		// all attacks except the attacks from self-attacking arguments (the self-attack stays),
		// if the attacked attackes back or also attacks itself

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> toNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		//build first two conditions
		toRemove = getSelfAttackingRemovalList(selfAttacking);

		//don't remove:
		boolean unRemove;

		for(Attack a: toRemove){
			Argument attacked = a.getAttacked();
			unRemove = false;
			for(Attack b: attacks){
				if(a!=b){
					// if attacked self-attacks
					if(b.getAttacker() == attacked && b.getAttacked() == attacked){
						unRemove = true;
						break;
					}
					// if attacked attacks back
					else if(b.getAttacker() == attacked && b.getAttacked() == a.getAttacker()){
						unRemove = true;
						break;
					}
				}
			}
			if(unRemove){
				toNotRemove.add(a);
			}
		}

		toRemove.removeAll(toNotRemove);

		//testing
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		//adstar:
		//retain all attacks except:
		//((a,b) where a != b and a self-attacks + (b self-attacks or b attacks a back) or) --> see above
		//	b self-attacks + for every c attacked by b:
		//		c is attacked by a or
		//		c attacks a or
		//		c is self-attacking or
		//		c attacks b back
		
		if(star){
			//check the second part
			ArrayList<Attack> toAlsoNotRemove = new ArrayList<Attack>();
			
			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b)){
					//check whether for all c the conditions hold
					ArrayList<Argument> thirdArguments = getAttackedBy(b.getName());
					for(Argument c: thirdArguments){
						ArrayList<Argument> attackedByThird = getAttackedBy(c.getName());
						if(getAttackedBy(a.getName()).contains(c) || attackedByThird.contains(a) || attackedByThird.contains(b) || selfAttacking.contains(c)){
							toAlsoNotRemove.add(att);
							break;
						}
					}
				}
			}
			
			toRemove.removeAll(toAlsoNotRemove);
			
		}
		
		attacks.removeAll(toRemove);
	}

	//TODO test very much! (reversed admissible code)
	private void computeGroundedKernel() {
		// all attacks except the attacks on self-attacking arguments (a self-attack stays),
		// but only if the attacker doesn't attack itself or gets attacked back

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> toNotRemove = new ArrayList<Attack>();

		for(Attack a: attacks){
			if(a.getAttacked() == a.getAttacker()){
				selfAttacking.add(a.getAttacked()); //not the attacker but the attacked
			}
		}

		for(Attack a: attacks){
			if(selfAttacking.contains(a.getAttacked())){
				if(a.getAttacked() != a.getAttacker()){
					toRemove.add(a);
				}
			}
		}

		//don't remove:
		boolean unRemove;

		for(Attack a: toRemove){
			Argument attacker = a.getAttacker();
			unRemove = false;
			for(Attack b: attacks){
				if(a!=b){
					// if attacked self-attacks
					if(b.getAttacker() == attacker && b.getAttacked() == attacker){
						unRemove = true;
						break;
					}
					// if attacked attacks back
					else if(b.getAttacked() == attacker && b.getAttacker() == a.getAttacked()){
						unRemove = true;
						break;
					}
				}
			}
			if(unRemove){
				toNotRemove.add(a);
			}
		}

		toRemove.removeAll(toNotRemove);

		//testing
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

	}

	//TODO test
	private void computeCompleteKernel() {
		// all attacks except the attacks of self-attacking arguments (a) (the self-attack stays)
		// but only if the attacked (b) also attacks itself

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> toNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		toRemove = getSelfAttackingRemovalList(selfAttacking); //now we have checked that a attacks itself

		boolean unRemove;

		//now weed out if b also attacks itself
		for(Attack a: toRemove){
			unRemove = true;
			for(Attack b: attacks){
				if(a.getAttacked() == b.getAttacked() && b.getAttacked() == b.getAttacker()){ // if b attacks itself, we really want to remove it
					unRemove = false;
					break;
				}
			}
			if(unRemove){
				toNotRemove.add(a);
			}
		}

		toRemove.removeAll(toNotRemove);

		//testing
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);

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

}
