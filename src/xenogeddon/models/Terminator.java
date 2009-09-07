package xenogeddon.models;

import java.io.IOException;

import ssmith.lang.Functions;

import com.jme.math.Quaternion;
import com.jme.scene.Controller;

import xenogeddon.Main;

public final class Terminator extends AbstractMD2Model {
	
	public static final int BLOOD_ANGELS = 1;
	public static final int ULTRA_MARINES = 2;
	public static final int DARK_ANGELS = 3;

    private static final float WIDTH = 0.6f;
    public static final float Y_OFF = 0.4f;
    private static final float SCALE = 0.016f;
    private static final float SPEED = 1f;
    private static final int HEALTH=10;
    private static final int ATTACK=1;
    
    public static Terminator TerminatorFactory(int chapter, Main m, String name, float x, float z) throws IOException {
    	switch (chapter) {
    	case BLOOD_ANGELS:
    		return new Terminator(m, name, x, z, "blood.png");
    	case ULTRA_MARINES:
    		return new Terminator(m, name, x, z, "ultra.png");
    	case DARK_ANGELS:
    		return new Terminator(m, name, x, z, "dark.png");
    	default:
    		throw new RuntimeException("Unknown Terminator chapter: " + chapter);
    	}
    	
    }

	private Terminator(Main m, String name, float x, float z, String skin) throws IOException {
		super(m, "Terminator_" + name, "Terminator", CreatureObject.SIDE_TERMINATORS, x, z, Y_OFF, WIDTH, "data/models/terminator/", "tris.md2", skin, "weapon.md2", "weapon.png", SCALE, SPEED, HEALTH, ATTACK, true);
		this.anim_standStill();
	}

	public void anim_shoot() {
		rotateChildSpatials(q_Shoot);
		int frame = 48; // Todo - choose better frame
		this.animate(frame, frame, Controller.RT_CLAMP, 0);
	}

	public void playAttackSound() {
		// Do nothing
	}

	public void playDeathSound() {
		main.sfx.playSound("./data/sounds/Spacemarine_sound/Death" + Functions.rnd(1, 3) + ".wav");
	}

	public void playHarmSound() {
		main.sfx.playSound("./data/sounds/Spacemarine_sound/Pain25_1.wav");
	}

	public void playShootingSound() {
		main.sfx.playSound("./data/sounds/machinepistol.wav");
	}

	public void playFallingSound() {
		// Do nothing
	}

	public Quaternion getRecoilAngle() {
		return q_Recoil;
	}

}
