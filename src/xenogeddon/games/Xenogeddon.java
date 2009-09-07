package xenogeddon.games;

import java.awt.Point;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.Main;
import xenogeddon.ShootAction;
import xenogeddon.ai.EnemyAI;
import xenogeddon.ai.TeamMemberAI;
import xenogeddon.effects.BulletSpark;
import xenogeddon.effects.Explosion;
import xenogeddon.effects.GreenBloodSpurt;
import xenogeddon.effects.RedBloodSpurt;
import xenogeddon.effects.WeaponFireEffect;
import xenogeddon.hud.HarmAlarm;
import xenogeddon.hud.StatsHUD;
import xenogeddon.maps.MapSquare;
import xenogeddon.missions.AbstractMission;
import xenogeddon.missions.ActivateSelfDestructMission;
import xenogeddon.models.MapModel;
import xenogeddon.models.Terminator;
import xenogeddon.models.Tyrant;
import com.jme.input.InputHandler;
import com.jme.math.Quaternion;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.FogState;

public final class Xenogeddon extends Main {

	private static final String TITLE = "Xenogeddon v0.7";

	private Interval ambient_interval = new Interval(30000);

	public static void main(String[] args) {
		Logger.getLogger("").setLevel(Level.WARNING);

		Main app = new Xenogeddon();
		if (RELEASE_MODE) {
			app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		} else {
			app.setConfigShowMode(ConfigShowMode.ShowIfNoConfig);
		}
		app.start();
	}


	protected void initGame() {
		super.initGame();
		Main.p("Loading mission...");
		int chapter = Functions.rnd(1, 3);
		try {
			this.startMission(new ActivateSelfDestructMission(this), chapter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.p("Finished loading mission.");
	}


	public void startMission(AbstractMission m, int side) throws IOException {
		this.mission = m;
		m.initMission();

		//this.rootNode.detachAllChildren();

		/*ScannerHUD sh = new ScannerHUD(this);
		this.statNode.attachChild(sh);
		this.addObjectForUpdate(sh);*/

		ha = new HarmAlarm(this);
		this.statNode.attachChild(ha);		
		this.addObjectForUpdate(ha);

		Point start_pos = map.getPlayerStartPos();

		player = Terminator.TerminatorFactory(side, this, "Player", start_pos.x + 0.5f, start_pos.y + 0.5f);
		player.setLocalRotation(new Quaternion()); // Nee to give it a unique rotation since all others models use the same one.
		player.anim_shoot(); // Must be before we detach the body, so the body gets rotated correctly for if it is re-attached
		player.detachChild(player.body_node);
		player.setSpeed(1.4f);
		player.detachChild(player.weapon_node);
		//player.weapon_node.getLocalTranslation().y += (Main.CAM_HEIGHT - Terminator.Y_OFF);
		lights.setLocation(player.getLocalTranslation());
		if (torch != null) {
			torch.setLocation(player.getLocalTranslation());
			torch.setDirection(cam.getDirection());
		}

		stats_hud = new StatsHUD(this);
		this.statNode.attachChild(stats_hud);

		// Position the camera at the player
		cam.getLocation().x = player.getLocalTranslation().x;
		cam.getLocation().y = CAM_HEIGHT;
		cam.getLocation().z = player.getLocalTranslation().z;
		cam.update();

		this.addObjectForUpdate(player);
		rootNode.attachChild(player);

		weapon_effect = new WeaponFireEffect(this);

		while (true) {
			Point p = map.getGoodyStartPos();
			if (p != null) {
				Terminator terminator = Terminator.TerminatorFactory(Terminator.ULTRA_MARINES, this, "AI", p.x + 0.5f, p.y + 0.5f);
				this.addObjectForUpdate(terminator);
				rootNode.attachChild(terminator);
				terminator.ai = new TeamMemberAI(this, terminator);
			} else {
				break;
			}
		}


		while (true) {
			Point p = map.getBaddyStartPos();
			if (p != null) {
				Tyrant tyrant = new Tyrant(this, p.x, p.y);
				this.addObjectForUpdate(tyrant);
				rootNode.attachChild(tyrant);
				if (ENEMY_AI) {
					tyrant.ai = new EnemyAI(this, tyrant);
				}
				if (Functions.rnd(1, 2) == 1) {
					tyrant.setupCeilingFall();
					map.getSq(p.x, p.y).ceiling_type = MapSquare.FALLING;
				}
			} else {
				break;
			}
		}

		MapModel map_model = new MapModel(this, map); // Must be after we create Tyrants since if they fall from the ceiling, we remove the ceiling!
		this.rootNode.attachChild(map_model.getNode());

		// Next line must be after the player is created!
		input.addAction(new ShootAction(this, player), InputHandler.DEVICE_MOUSE, 0, InputHandler.AXIS_NONE, false ); // Add dummy action so that actions != null!
		input.removeAllActions();
		input.addAction(new ShootAction(this, player), InputHandler.DEVICE_MOUSE, 0, InputHandler.AXIS_NONE, false );

		// Fog makes the particles texture edges show!
		FogState fs = this.getDisplay().getRenderer().createFogState();
		fs.setEnabled(true);
		fs.setDensity(0.3f);
		fs.setColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.5f));
		fs.setStart(Main.VIEW_DIST/2);
		fs.setEnd(Main.VIEW_DIST);
		fs.setDensityFunction(FogState.DensityFunction.Linear);
		fs.setQuality(FogState.Quality.PerVertex);
		rootNode.setRenderState(fs);

		mission.modelsLoaded();

		rootNode.updateGeometricState(0, true);
		rootNode.updateRenderState();

		breath_interval = new Interval(4000);
		footstep_interval = new Interval(1000);

		gamestage = MAIN_GAME; // Wait until everything is loaded!
	}

	protected void gameUpdate(float tpf) {
		if (gamestage == MAIN_GAME) {
			mission.update(tpf);
			if (mission.showTime()) {
				if (this.time_remaining > 0) {
					this.time_remaining -= tpf;
					if (this.time_remaining < 0) {
						time_remaining = 0;
						this.timeExpired();
					}
				}
				this.updateStatHUD();
			}
			
				if (breath_interval != null) {
				if (breath_interval.hitInterval()) {
					sfx.playSound("./data/sounds/breath.wav");
				}
			}
			if (ambient_interval.hitInterval()) {
				sfx.playSound("./data/sounds/space_ambience.wav");
			}
		}
	}


	public String getTitle() {
		return TITLE;
	}



}
