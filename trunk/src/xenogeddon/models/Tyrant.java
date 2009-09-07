package xenogeddon.models;

import java.io.IOException;

import ssmith.lang.Functions;

import com.jme.math.Quaternion;
import com.jme.scene.Controller;

import xenogeddon.Main;
import xenogeddon.ai.EnemyAI;

public final class Tyrant extends AbstractMD2Model {

	private static final float WIDTH = 0.45f;
	private static final float X_OFF = 0.5f;
	public static final float Y_OFF = 0.4f;
	private static final float Z_OFF = 0.5f;
	private static final float SCALE = 0.016f;
	//private static final float SCALE = 0.008f;
	private static final float SPEED = 5f;
	private static final int HEALTH=8;
	private static final int ATTACK=10;

	public Tyrant(Main m, float x, float z) throws IOException { //Functions.rndFloat(0.002f, 0.016f)
		super(m, "Tyrant", "Tyrant", CreatureObject.SIDE_TYRANTS, x+X_OFF, z+Z_OFF, Y_OFF, WIDTH, "data/models/tyrant/", "tris.MD2", "hivetyrant.png", "", "", SCALE, SPEED, HEALTH, ATTACK, false);
		this.anim_standStill();
	}

	public void setupCeilingFall() {
		this.getLocalTranslation().y += main.map.getSq(this.getLocalTranslation().x, this.getLocalTranslation().z).ceiling_height;
		this.updateGeometricState(0, true);
		if (ai != null) {
			this.ai.setMode(EnemyAI.CEILING_WAITING);
		}
		this.invisible = true;
		setCullHint(CullHint.Always);
	}

	public void anim_shoot() {
		this.animate(1, 1, Controller.RT_CLAMP, 0);
	}

	public void playAttackSound() {
		main.sfx.playSound("./data/sounds/alien_sound/taunt.wav");
	}

	public void playDeathSound() {
		main.sfx.playSound("./data/sounds/alien_sound/death" + Functions.rnd(1, 2) + ".wav");
	}

	public void playHarmSound() {
		main.sfx.playSound("./data/sounds/alien_sound/pain75_1.wav");
	}

	public void playFallingSound() {
		main.sfx.playSound("./data/sounds/alien_sound/fall1.wav");
	}

	public void playShootingSound() {
		// Do nothing
	}

	public Quaternion getRecoilAngle() {
		return q_Recoil;
	}

}
