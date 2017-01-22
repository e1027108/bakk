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
			computeGroundedKernel(false);
			break;
		case co:
			computeCompleteKernel(false);
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

	//TODO test, check whether what's in or out is understood correctly
	private void computeAdmissibleKernel(boolean star) {
		// all attacks except the attacks from self-attacking arguments (the self-attack stays),
		// if the attacked attackes back or also attacks itself

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		//build first two conditions
		toRemove = getSelfAttackingRemovalList(selfAttacking);

		for(Attack a: toRemove){
			Argument attacker = a.getAttacker();
			Argument attacked = a.getAttacked();
			if(!(getAttackedBy(attacked.getName()).contains(attacker) || selfAttacking.contains(attacked))){
				maybeNotRemove.add(a);
			}
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
			ArrayList<Attack> maybeAlsoNotRemove = new ArrayList<Attack>();

			for(Attack att: toRemove){
				Argument a = att.getAttacker();
				Argument b = att.getAttacked();
				if(selfAttacking.contains(b)){
					//check whether for all c the conditions hold
					ArrayList<Argument> thirdArguments = getAttackedBy(b.getName());
					for(Argument c: thirdArguments){
						ArrayList<Argument> attackedByThird = getAttackedBy(c.getName());
						if(!(getAttackedBy(a.getName()).contains(c) || attackedByThird.contains(a) ||
								attackedByThird.contains(b) || selfAttacking.contains(c))){
							maybeAlsoNotRemove.add(att);
							break;
						}
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
		for(Attack a: toRemove){
			System.out.println(a.getAttacker().getName() + " attacking " + a.getAttacked().getName() + " was removed.");
		}

		attacks.removeAll(toRemove);
	}

	//TODO test very much! (reversed admissible code)
	private void computeGroundedKernel(boolean star) {
		// all attacks except the attacks on self-attacking arguments (a self-attack stays),
		// but only if the attacker doesn't attack itself or gets attacked back

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		for(Attack a: attacks){
			if(selfAttacking.contains(a.getAttacked())){
				if(a.getAttacked() != a.getAttacker()){
					toRemove.add(a);
				}
			}
		}

		for(Attack a: toRemove){
			Argument attacker = a.getAttacker();
			Argument attacked = a.getAttacked();
			if(!(selfAttacking.contains(attacker) || getAttackedBy(attacked.getName()).contains(attacker))){
				maybeNotRemove.add(a);
			}
		}

		//TODO now grstar
		// also do remove if:
		// for a self-attacking b has for all c:
		//		b attacks c and either:
		//			a attacks c
		//			c attacks a
		//			c is self-attacking

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

	}

	//TODO test
	private void computeCompleteKernel(boolean star) {
		// all attacks except the attacks of self-attacking arguments (a) (the self-attack stays)
		// but only if the attacked (b) also attacks itself

		ArrayList<Argument> selfAttacking = new ArrayList<Argument>();
		ArrayList<Attack> toRemove = new ArrayList<Attack>();
		ArrayList<Attack> maybeNotRemove = new ArrayList<Attack>();

		selfAttacking = getSelfAttacking();

		toRemove = getSelfAttackingRemovalList(selfAttacking); //now we have checked that a attacks itself

		//now weed out if b also attacks itself
		for(Attack att: toRemove){
			Argument b = att.getAttacked();
			if(!selfAttacking.contains(b)){
				maybeNotRemove.add(att);
			}
		}

		//TODO now costar
		// also do remove if:
		// for a self-attacking b:
		//		b doesn't attack a and for all c:
		//		that b attacks:
		//			a attacks c or
		//			c attacks a or
		//			c is self-attacking

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
