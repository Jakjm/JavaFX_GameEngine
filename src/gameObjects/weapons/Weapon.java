package gameObjects.weapons;

import gameObjects.SolidObject;
import hitshape.Hitshape;

public abstract class Weapon extends SolidObject{
	int fireTimer = RATE_OF_FIRE();
	public Weapon(double x,double y,Hitshape shape) {
		super(x,y,shape);
	}
	/**Tries to launch an attack with the weapon, if it has been enough time from the previous attack.*/
	private boolean pullTrigger() {
		if(fireTimer >= RATE_OF_FIRE()) {
			return true;
		}
		else {
			++fireTimer;
			return false;
		}
	}
	public abstract int RATE_OF_FIRE();
	public abstract void shoot();
	
	/**Fires the weapon, if the fire timer has recharged.*/
	public void fire() {
		if(pullTrigger()) {
			fireTimer = 0;
			shoot();
		}
	}
}
