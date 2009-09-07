package xenogeddon;

import xenogeddon.effects.Bullet;
import xenogeddon.effects.BulletSpark;
import xenogeddon.models.CreatureObject;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.Ray;
import com.jme.math.Vector3f;

public final class ShootAction extends InputAction {

	private Main main;
	private MyPickResults results;
	private CreatureObject shooter;

	public ShootAction(Main m, CreatureObject _shooter) {
		main = m;
		results = new MyPickResults(_shooter.collider);
		results.setCheckDistance(true);
		shooter = _shooter;
	}

	public void performAction(InputActionEvent evt) {
		if (evt.getTriggerPressed()) {
			if (main.gamestage == Main.MAIN_GAME) {
				if (main.player.ammo > 0) {
					main.weapon_effect.shoot(evt.getTime());
					main.player.playShootingSound();
					main.player.ammo--;
					main.updateStatHUD();
					main.lights.shot_flash = true;

					//Main.p("Shooting!");
					Ray r = new Ray(main.cam.getLocation(), main.cam.getDirection());

					results.clear();
					main.getRootNode().findPick(r, results);

					if (results.getNumber() > 0) {
						int i = 0; // Get the first thing the bullets hit
						
						/*if (Main.DEBUG) {
							Main.p("HIT: " + results.getGameObject(i) + " at " + results.getGameObject(i).getLocalTranslation());
						}*/
						
						Vector3f hit_point = main.cam.getLocation().add(main.cam.getDirection().mult(results.getPickData(i).getDistance()));
						
						//Vector3f bullet_start = main.weapon_effect.getWorldTranslation().add(main.weapon_effect.mesh.getLocalTranslation()); // Need to do it this way since FSR the mesh has zero world coords
						Vector3f bullet_start = main.weapon_effect.mesh.getOriginCenter(); // Need to do it this way since FSR the mesh has zero world coords
						new Bullet(main, bullet_start, hit_point);

						BulletSpark.AddBulletSpark(main, hit_point, r.getDirection().mult(-1f));

						results.getGameObject(i).damage(1, shooter, hit_point, r.getDirection().mult(-1));
					}
				}
			}
		}

	}
}
