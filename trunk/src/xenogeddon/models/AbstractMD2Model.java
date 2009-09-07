package xenogeddon.models;

import java.io.IOException;
import ssmith.lang.Functions;
import xenogeddon.Main;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.model.animation.KeyframeController;

/**
Frames:
(subtract 1)
"stand" 1
"run" 41
"attack" 47
"pain1" 55
"pain2" 59
"pain3" 63
"jump" 67
"flip" 73
"salute" 85
"taunt" 96
"wave" 113
"point" 124
"crstnd" 136
"crwalk" 155
"crattack" 161
"crpain" 170
"crdeath" 174
"death1" 179
"death2" 185
"death3" 191


 * @author stevec
 *
 */
public abstract class AbstractMD2Model extends CreatureObject  {

	protected static Quaternion q_Walk = new Quaternion();
	protected static Quaternion q_90 = new Quaternion();
	protected static Quaternion q_StandStill = new Quaternion();
	protected static Quaternion q_Recoil = new Quaternion();
	protected static Quaternion q_Shoot = new Quaternion();
	protected static Quaternion q_Attack = new Quaternion();
	protected static Quaternion q_Die = new Quaternion();

	private String path, mdl1, skin1, mdl2, skin2;
	private float scale;
	//private float speed;
	public static int total_models = 0;
	
	static {
		q_Attack.fromAngleAxis(0, new Vector3f(0,0,0));
		q_Die.fromAngleAxis(0, new Vector3f(0,0,0));
		q_StandStill.fromAngleAxis(-35 * FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		q_Recoil.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		q_Shoot.fromAngleAxis(-45 * FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		q_Walk.fromAngleAxis(-110 * FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
		q_90.fromAngleAxis(-90 * FastMath.DEG_TO_RAD, new Vector3f(0,1,0));
	}

	public AbstractMD2Model(Main m, String node_name, String display_name, int side, float _x, float _z, float y_off, float _w, String _path, String _mdl1, String _skin1, String _mdl2, String _skin2, float _scale, float _speed, int hlth, int att, boolean load_models) throws IOException {
		super(m, node_name, display_name, side, _x, _z, y_off, _w, _speed, hlth, att);

		//speed = _speed;

		path = _path;
		mdl1 = _mdl1;
		skin1 =_skin1;
		mdl2 = _mdl2;
		skin2 =_skin2;
		scale = _scale;

		if (load_models) {
			this.loadModels();
		}
	}
	
	public abstract Quaternion getRecoilAngle();

	public void loadModels() {
		try {
			total_models++;
			//Main.p("Loading model " + total_models);

			body_node = (Node)main.model_cache.getNode(path + mdl1);
			if (body_node == null) {
				body_node = ModelLoaders.LoadMD2Model(main.getDisplay(), path, mdl1, skin1, scale);
				body_node.setName(this.getName() + "_body");
				main.model_cache.putNode(path + mdl1, body_node);
			}
			this.attachChild(body_node);

			if (mdl2.length() > 0) {
				weapon_node = (Node)main.model_cache.getNode(path + mdl2);
				if (weapon_node == null) {
					weapon_node = ModelLoaders.LoadMD2Model(main.getDisplay(), path, mdl2, skin2, scale);
					weapon_node.setName(this.getName() + "_weapon");
					main.model_cache.putNode(path + mdl2, weapon_node);
				}
				this.attachChild(weapon_node);
			}
			this.updateRenderState();
			
			this.updateGeometricState(0, true);

			this.updateModelBound();
			/*CollisionBox c = (CollisionBox)this.collider;
			BoundingBox b = (BoundingBox)this.worldBound;
			c.xExtent = b.xExtent; 
			c.yExtent = b.yExtent; 
			c.zExtent = b.zExtent;
			c.updateGeometricState(0, true);*/
			
			this.model_nodes_loaded = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void unloadModels() {
		if (body_node != null) {
			this.body_node.removeFromParent();
		}
		if (weapon_node != null) {
			this.weapon_node.removeFromParent();
		}
		this.model_nodes_loaded = false;
	}

	protected void rotateChildSpatials(Quaternion q) {
		if (this.getChildren() != null) {
			int ch = this.getChildren().size();
			for (int i=0 ; i<ch ; i++) {
				Spatial sp = this.getChild(i);
				if (sp instanceof Node) {
					Node n = (Node)sp;
					n.setLocalRotation(q);
				}
			}
		}
	}


	protected void animate(int s, int f, int type, int speed) {
		if (this.getChildren() != null) {
			int ch = this.getChildren().size();
			for (int i=0 ; i<ch ; i++) {
				Spatial sp = this.getChild(i);
				if (sp instanceof Node) {
					Node n = (Node)sp;
					KeyframeController kc = (KeyframeController) n.getChild(0).getController(0);
					kc.setSpeed(speed);
					kc.setRepeatType(type);
					kc.setNewAnimationTimes(s, f);
				}
			}
		}
	}

	public void anim_standStill() {
		if (this.children != null) {
			rotateChildSpatials(q_StandStill); // Works for Terminators!
			int s = Functions.rnd(0, 15);
			int f = Functions.rnd(s+10, 39);
			this.animate(s, f, Controller.RT_CYCLE, 2);
		}
	}

	public void anim_walk() {
		rotateChildSpatials(q_Walk); // Works for Terminators!
		this.animate(40, 45, Controller.RT_WRAP, 4 * (int)speed);
	}


	public void anim_attack() {
		rotateChildSpatials(q_Attack);
		this.animate(46, 53, Controller.RT_WRAP, 4);
	}


	public void anim_die() {
		// Remove weapon
		if (this.weapon_node != null) {
			if (this.weapon_node.getParent() != null) {
				this.weapon_node.removeFromParent();
				this.updateRenderState();
			}
		}

		rotateChildSpatials(q_Die);

		// Choose random die
		int x = (Functions.rnd(0, 2)*6) + 178;
		this.animate(x, x+5, Controller.RT_CLAMP, 6);
	}

	public void anim_recoil() {
		rotateChildSpatials(getRecoilAngle());
		//animate(54, 57, Controller.RT_CLAMP, 5);
		// Choose random recoil
		int x = (Functions.rnd(0, 2)*4) + 54;
		this.animate(x, x+3, Controller.RT_CLAMP, 6);
	}

}
