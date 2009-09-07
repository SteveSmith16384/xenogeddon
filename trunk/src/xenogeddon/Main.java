/**
 * Xenogeddon by Stephen Smith
 * 
 * stephen.carlylesmith@googlemail.com
 * 
 */

package xenogeddon;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import ssmith.audio.SoundCacheThread;
import ssmith.lang.Functions;
import ssmith.util.Interval;
import xenogeddon.effects.BulletSpark;
import xenogeddon.effects.Explosion;
import xenogeddon.effects.GreenBloodSpurt;
import xenogeddon.effects.RedBloodSpurt;
import xenogeddon.effects.WeaponFireEffect;
import xenogeddon.hud.HarmAlarm;
import xenogeddon.hud.StatsHUD;
import xenogeddon.hud.TextHUD;
import xenogeddon.maps.AbstractMapData;
import xenogeddon.missions.AbstractMission;
import xenogeddon.models.CreatureObject;
import xenogeddon.models.Terminator;
import com.jme.app.AbstractGame;
import com.jme.app.BaseGame;
import com.jme.app.BaseSimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.joystick.JoystickInput;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.WireframeState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Debug;
import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.Timer;
import com.jme.util.geom.Debugger;
import com.jme.util.resource.ResourceLocator;
import com.jme.util.resource.ResourceLocatorTool;
import com.jme.util.stat.StatCollector;
import com.jme.util.stat.StatType;
import com.jme.util.stat.graph.DefColorFadeController;
import com.jme.util.stat.graph.GraphFactory;
import com.jme.util.stat.graph.LineGrapher;
import com.jme.util.stat.graph.TabledLabelGrapher;

public abstract class Main extends BaseGame implements ResourceLocator {

	public static final boolean RELEASE_MODE = true;
	public static final boolean SHOW_COLLISION_BOXES = false;//!RELEASE_MODE;

	protected static final float CAM_HEIGHT = 0.7f;
	private static final float CAM_MOVE_SPEED = 2f; // Irrelevent really!
	private static final float CAM_TURN_SPEED = 0.3f;
	public static final float VIEW_DIST = 10f;
	private static final float MIN_VIEW_DIST = 0.1f;
	public static final float ENEMY_NON_VISIBLE_DIST = 2f;
	private static final float MAX_INTERPOLATION = 0.05f;
	public static boolean REMOVE_CORPSES, SHOW_FPS, HIGH_DETAIL, ENEMY_AI, CAMERA_LURCH;

	//private static final int PRE_GAME = -2;
	private static final int INTRO = -1;
	public static final int MAIN_GAME = 0;
	private static final int POST_GAME = 1;

	public AbstractMapData map;
	public CreatureObject player;
	public ArrayList<IUpdating> objects;

	private static final Logger logger = Logger.getLogger(BaseSimpleGame.class.getName());

	public Camera cam;
	protected Node rootNode;
	protected FirstPersonHandler input;
	public WeaponFireEffect weapon_effect;
	public int gamestage = INTRO;
	public AbstractMission mission;
	public HarmAlarm ha;
	private boolean cam_disconnected = false;
	public TextHUD text_hud;
	protected StatsHUD stats_hud;
	public AmbientLights lights;
	protected TorchLight torch;
	public ModelCache model_cache = new ModelCache();
	public float time_remaining;
	public SoundCacheThread sfx;
	public Hashtable<String, String> cfg = new Hashtable<String, String>();
	private CameraLurch lurch = new CameraLurch();
	protected Interval footstep_interval;
	protected Interval breath_interval;

	protected Timer timer;
	protected Node statNode;
	protected Node graphNode;

	protected int alphaBits = 0;
	protected int depthBits = 8;
	protected int stencilBits = 0;
	protected int samples = 0;

	/**
	 * Simply an easy way to get at timer.getTimePerFrame(). Also saves math cycles since
	 * you don't call getTimePerFrame more than once per frame.
	 */
	protected float tpf;

	protected boolean showDepth = false;
	protected boolean showBounds = false;
	protected boolean showNormals = false;
	protected boolean showGraphs = false;

	public WireframeState wireState;
	protected LightState lightState;

	/**
	 * boolean for toggling the simpleUpdate and geometric update parts of the
	 * game loop on and off.
	 */
	protected boolean pause;

	private TabledLabelGrapher tgrapher;
	private LineGrapher lgrapher;
	private Quad lineGraph, labGraph;

	public Main() {
		super();
		try {
			sfx = new SoundCacheThread(this);
			sfx.start();
			cfg = Functions.ConfigFile("config.txt", "=");
			REMOVE_CORPSES = cfg.get("remove_corpses").toString().equalsIgnoreCase("yes");
			SHOW_FPS = cfg.get("show_fps").toString().equalsIgnoreCase("yes");
			HIGH_DETAIL = cfg.get("high_detail").toString().equalsIgnoreCase("yes");
			ENEMY_AI = cfg.get("enemy_ai").toString().equalsIgnoreCase("yes");
			CAMERA_LURCH = cfg.get("camera_lurch").toString().equalsIgnoreCase("yes");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.setProperty("jme.stats", "set");
		ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, this);
	}


	public Node getRootNode() {
		return this.rootNode;
	}

	public void attachToRootNode(Spatial n) {
		this.rootNode.attachChild(n);
	}

	public DisplaySystem getDisplay() {
		return this.display;
	}


	public int getTimeRemaining() {
		return (int)this.time_remaining;
	}


	public void addObjectForUpdate(IUpdating obj) {
		objects.add(obj);
	}


	public void removeObjectForUpdate(IUpdating obj) {
		objects.remove(obj);
	}


	public void playerDied() {
		this.gamestage = POST_GAME;
		player.setIsCollidable(false);
		this.text_hud.clearText();
		this.text_hud.add("YOU HAVE BEEN KILLED");
		this.text_hud.add("Press R to Restart");
		this.text_hud.add("YOU HAVE BEEN KILLED");
	}

	public void missionCompleted() {
		//this.game_stage = 1;
		this.text_hud.clearText();
		this.text_hud.add("MISSON ACCOMPLISHED!");
		this.text_hud.add("Press R to Restart");
		this.text_hud.add("MISSON ACCOMPLISHED!");
		this.text_hud.add("Press R to Restart");
		this.text_hud.add("MISSON ACCOMPLISHED!");
	}

	public void timeExpired() {
		//this.game_stage = 1;
		this.text_hud.clearText();
		this.text_hud.add("TIME HAS EXPIRED!");
		this.text_hud.add("GAME OVER!");
		this.text_hud.add("Press R to Restart");
		this.text_hud.add("TIME HAS EXPIRED!");
		this.text_hud.add("GAME OVER!");
		this.text_hud.add("Press R to Restart");
	}

	public void updateStatHUD() {
		this.stats_hud.refresh();
	}

	public float getFPS() {
		return timer.getFrameRate();
	}

	protected final void update(float interpolation_dont_use) {
		/** Recalculate the framerate. */
		timer.update();

		/** Update tpf to time per frame according to the Timer. */
		tpf = timer.getTimePerFrame();

		/** Check for key/mouse updates. */
		updateInput();
		
		gameUpdate(tpf);

		if (gamestage == MAIN_GAME) {
			if (SHOW_FPS) {
				this.updateStatHUD(); // Update FPS
			}

			if (cam_disconnected == false) {
				cam.getLocation().y = player.getLocalTranslation().y; // Put cam at same height so we're only checking x and z diff

				float f = player.getDistTo(cam.getLocation());// - (Main.CAM_HEIGHT - Terminator.Y_OFF);
				//p("" + f);
				//if (player.getDistTo(cam.getLocation()) > (Main.CAM_HEIGHT - Terminator.Y_OFF)) {
				if (f != 0f) {
					if (CAMERA_LURCH) {
						lurch.update();
					}
					if (footstep_interval != null) {
						if (footstep_interval.hitInterval()) {
							sfx.playSound("./data/sounds/footstep.wav");
						}
					}
				}

				Vector3f move_dir = player.getDirTo(cam.getLocation());
				float dist = player.getSpeed() * tpf;
				player.move(move_dir, dist); // Player move slightly faster

				//Restore camera height - must be after we've checked for footsteps and after we've checked the diff between the cam location and the players location!
				cam.getLocation().y = CAM_HEIGHT + lurch.getCurrHeight(); // Restore cam height

				// Move the camera back in case we hit someone else and move the player back
				cam.getLocation().x = player.getLocalTranslation().x;
				cam.getLocation().z = player.getLocalTranslation().z;
				cam.update();

				player.getLocalTranslation().y = Terminator.Y_OFF; // position the model to be the right height
			}

			// Point us in the same direction as the camera
			player.getLocalRotation().lookAt(cam.getDirection(), Vector3f.UNIT_Y);

		} else {
			if (player != null) {
				if (this.cam_disconnected == false) {
					cam.getLocation().x = player.getLocalTranslation().x;
					cam.getLocation().y = MIN_VIEW_DIST;
					cam.getLocation().z = player.getLocalTranslation().z;
					cam.update();
				}
			}
		}

		IUpdating obj2;
		if (tpf > MAX_INTERPOLATION) {
			tpf = MAX_INTERPOLATION;
		}
		for (int i=0 ; i<objects.size() ; i++) {
			obj2 = (IUpdating)objects.get(i);
			obj2.update(tpf);
		}

		//ProjectedTextureUtil.updateProjectedTexture( projectedTexture1, 1.0f, 1.5f, 1.0f, VIEW_DIST, cam.getLocation(), cam.getDirection(), Vector3f.UNIT_Y );


		/** update stats, if enabled. */
		if (Debug.stats) {
			StatCollector.update();
		}

		// Execute updateQueue item
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.UPDATE).execute();

		/** If toggle_pause is a valid command (via key p), change pause. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_pause", false ) ) {
			pause = !pause;
		}

		/** If step is a valid command (via key ADD), update scenegraph one unit. */
		/*if ( KeyBindingManager.getKeyBindingManager().isValidCommand("step", true ) ) {
			simpleUpdate();
			rootNode.updateGeometricState(tpf, true);
		}*/

		/** If toggle_wire is a valid command (via key T), change wirestates. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_wire", false ) ) {
			wireState.setEnabled( !wireState.isEnabled() );
			rootNode.updateRenderState();
		}
		/** If toggle_lights is a valid command (via key L), change lightstate. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_lights", false ) ) {
			lightState.setEnabled( !lightState.isEnabled() );
			rootNode.updateRenderState();
		}
		/** If toggle_bounds is a valid command (via key B), change bounds. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_bounds", false ) ) {
			showBounds = !showBounds;
		}

		/** If toggle_depth is a valid command (via key F3), change depth. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_depth", false ) ) {
			showDepth = !showDepth;
		}

		if (Debug.stats) {
			/** handle toggle_stats command (key F4) */
			if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_stats", false ) ) {
				showGraphs = !showGraphs;
				Debug.updateGraphs = showGraphs;
				labGraph.clearControllers();
				lineGraph.clearControllers();
				labGraph.addController(new DefColorFadeController(labGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
				lineGraph.addController(new DefColorFadeController(lineGraph, showGraphs ? .6f : 0f, showGraphs ? .5f : -.5f));
			}
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("toggle_normals", false ) ) {
			showNormals = !showNormals;
		}
		/** If camera_out is a valid command (via key C), show camera location. */
		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("camera_out", false ) ) {
			logger.info( "Camera at: " + display.getRenderer().getCamera().getLocation() );
			if (cam_disconnected) { // About to reconnect!
				cam.getLocation().x = player.getLocalTranslation().x;
				cam.getLocation().y = player.getLocalTranslation().y;
				cam.getLocation().z = player.getLocalTranslation().z;
				player.detachChild(player.body_node); // Don't draw us!
				//player.detachChild(player.weapon_node); // Don't draw us!
			} else { // About to disconnect
				player.attachChild(player.body_node); // Draw us!
				//player.attachChild(player.weapon_node); // Draw us!
			}
			player.updateRenderState();
			player.updateGeometricState(0, true);
			cam_disconnected = !cam_disconnected;
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("screen_shot", false ) ) {
			display.getRenderer().takeScreenShot( "SimpleGameScreenShot" );
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand(
				"parallel_projection", false ) ) {
			if ( cam.isParallelProjection() ) {
				cameraPerspective();
			}
			else {
				cameraParallel();
			}
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("mem_report", false ) ) {
			long totMem = Runtime.getRuntime().totalMemory();
			long freeMem = Runtime.getRuntime().freeMemory();
			long maxMem = Runtime.getRuntime().maxMemory();

			logger.info("|*|*|  Memory Stats  |*|*|");
			logger.info("Total memory: "+(totMem>>10)+" kb");
			logger.info("Free memory: "+(freeMem>>10)+" kb");
			logger.info("Max memory: "+(maxMem>>10)+" kb");
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand( "exit", false ) ) {
			Main.p("Exiting");
			finish();
		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand( "restart", false ) ) {
			this.initGame();
		}

		/*if ( KeyBindingManager.getKeyBindingManager().isValidCommand("next_frame", false) ) {
			frame++;
			((AbstractMD2Model)player).animate(frame, frame, Controller.RT_CLAMP, 0);

		}

		if ( KeyBindingManager.getKeyBindingManager().isValidCommand("prev_frame", false) ) {
			frame--;
			((AbstractMD2Model)player).animate(frame, frame, Controller.RT_CLAMP, 0);
		}*/

		if (!pause) {
			/** Call simpleUpdate in any derived classes of SimpleGame. */
			//simpleUpdate();

			/** Update controllers/render states/transforms/bounds for rootNode. */
			rootNode.updateGeometricState(tpf, true);
			statNode.updateGeometricState(tpf, true);
		}
	}

	/**
	 * Check for key/mouse updates. Allow overriding this method to skip update in subclasses.
	 */
	protected void updateInput() {
		input.update(tpf);
	}
	
	protected abstract void gameUpdate(float tpf);

	/**
	 * This is called every frame in BaseGame.start(), after update()
	 * 
	 * @param interpolation
	 *            unused in this implementation
	 * @see AbstractGame#render(float interpolation)
	 */
	protected final void render(float interpolation) {
		Renderer r = display.getRenderer();
		/** Clears the previously rendered information. */
		r.clearBuffers();

		// Execute renderQueue item
		GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();

		//Renderer r = display.getRenderer();

		/** Draw the rootNode and all its children. */
		r.draw(rootNode);

		/** Call simpleRender() in any derived classes. */
		//simpleRender();

		/** Draw the stats node to show our stat charts. */
		r.draw(statNode);

		doDebug(r);
	}

	protected void doDebug(Renderer r) {
		/**
		 * If showing bounds, draw rootNode's bounds, and the bounds of all its
		 * children.
		 */
		if ( showBounds ) {
			Debugger.drawBounds( rootNode, r, true );
		}

		if ( showNormals ) {
			Debugger.drawNormals( rootNode, r );
			Debugger.drawTangents( rootNode, r );
		}

		if (showDepth) {
			r.renderQueue();
			Debugger.drawBuffer(Texture.RenderToTextureType.Depth, Debugger.NORTHEAST, r);
		}
	}

	/**
	 * Creates display, sets up camera, and binds keys. Called in
	 * BaseGame.start() directly after the dialog box.
	 *
	 * @see AbstractGame#initSystem()
	 */
	protected void initSystem() throws JmeException {
		logger.info(getVersion());
		try {
			/**
			 * Get a DisplaySystem according to the renderer selected in the
			 * startup box.
			 */
			display = DisplaySystem.getDisplaySystem(settings.getRenderer() );

			display.setMinDepthBits( depthBits );
			display.setMinStencilBits( stencilBits );
			display.setMinAlphaBits( alphaBits );
			display.setMinSamples( samples );

			/** Create a window with the startup box's information. */
			display.createWindow(settings.getWidth(), settings.getHeight(),
					settings.getDepth(), settings.getFrequency(),
					settings.isFullscreen() );
			logger.info("Running on: " + display.getAdapter()
					+ "\nDriver version: " + display.getDriverVersion() + "\n"
					+ display.getDisplayVendor() + " - "
					+ display.getDisplayRenderer() + " - "
					+ display.getDisplayAPIVersion());


			/**
			 * Create a camera specific to the DisplaySystem that works with the
			 * display's width and height
			 */
			cam = display.getRenderer().createCamera( display.getWidth(), display.getHeight() );

		} catch ( JmeException e ) {
			/**
			 * If the displaysystem can't be initialized correctly, exit
			 * instantly.
			 */
			logger.log(Level.SEVERE, "Could not create displaySystem", e);
			System.exit( 1 );
		}

		/** Set a black background. */
		display.getRenderer().setBackgroundColor( ColorRGBA.black.clone() );

		/** Set up how our camera sees. */
		cameraPerspective();
		Vector3f loc = new Vector3f( 0.0f, 0.0f, 25.0f );
		Vector3f left;
		Vector3f up;
		//if (Main.RELEASE_MODE) {
		up = new Vector3f( 0.0f, 1.0f, 0.0f );
		left = new Vector3f( -1.0f, 0.0f, 0.0f );
		/*} else {
			up = new Vector3f( 0.2f, 1.0f, 0.0f );
			left = new Vector3f( -1.0f, -0.2f, 0.0f );
		}*/
		Vector3f dir = new Vector3f( 0.0f, 0f, -1.0f );
		/** Move our camera to a correct place and orientation. */
		cam.setFrame( loc, left, up, dir );
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();
		/** Assign the camera to this renderer. */
		display.getRenderer().setCamera( cam );

		/** Create a basic input controller. */
		//FirstPersonHandler firstPersonHandler = 
		input = new FirstPersonHandler(cam, CAM_MOVE_SPEED, CAM_TURN_SPEED);//firstPersonHandler;

		/** Get a high resolution timer for FPS updates. */
		timer = Timer.getTimer();

		/** Sets the title of our display. */
		display.setTitle(this.getTitle());

		/** Assign key P to action "toggle_pause". */
		KeyBindingManager.getKeyBindingManager().set( "toggle_pause", KeyInput.KEY_P );
		/** Assign key ADD to action "step". */
		KeyBindingManager.getKeyBindingManager().set( "step", KeyInput.KEY_ADD );
		/** Assign key T to action "toggle_wire". */
		KeyBindingManager.getKeyBindingManager().set( "toggle_wire", KeyInput.KEY_T );
		/** Assign key L to action "toggle_lights". */
		KeyBindingManager.getKeyBindingManager().set( "toggle_lights", KeyInput.KEY_L );
		/** Assign key B to action "toggle_bounds". */
		KeyBindingManager.getKeyBindingManager().set( "toggle_bounds", KeyInput.KEY_B );
		/** Assign key N to action "toggle_normals". */
		KeyBindingManager.getKeyBindingManager().set( "toggle_normals", KeyInput.KEY_N );
		/** Assign key C to action "camera_out". */
		KeyBindingManager.getKeyBindingManager().set( "camera_out", KeyInput.KEY_C );
		/** Assign key R to action "mem_report". */
		if (Main.RELEASE_MODE) {
			KeyBindingManager.getKeyBindingManager().set("restart", KeyInput.KEY_R);
		} else {
			KeyBindingManager.getKeyBindingManager().set("mem_report", KeyInput.KEY_R);
		}

		KeyBindingManager.getKeyBindingManager().set( "exit", KeyInput.KEY_ESCAPE );

		KeyBindingManager.getKeyBindingManager().set( "screen_shot", KeyInput.KEY_F1 );
		KeyBindingManager.getKeyBindingManager().set( "parallel_projection", KeyInput.KEY_F2 );
		KeyBindingManager.getKeyBindingManager().set( "toggle_depth", KeyInput.KEY_F3 );
		KeyBindingManager.getKeyBindingManager().set( "toggle_stats", KeyInput.KEY_F4 );

		//KeyBindingManager.getKeyBindingManager().set( "next_frame", KeyInput.KEY_F5 );
		//KeyBindingManager.getKeyBindingManager().set( "prev_frame", KeyInput.KEY_F6 );

	}
	
	public abstract String getTitle();

	public void updateBreathInterval() {
		if (breath_interval != null) {
			breath_interval.setInterval(player.health * 400, false);
		}
	}



	protected void cameraPerspective() {
		cam.setFrustumPerspective( 45.0f, (float) display.getWidth() / (float) display.getHeight(), MIN_VIEW_DIST, VIEW_DIST );
		cam.setParallelProjection(false);
		cam.update();
	}


	protected void cameraParallel() {
		cam.setParallelProjection( true );
		float aspect = (float) display.getWidth() / display.getHeight();
		cam.setFrustum( -100, 1000, -50 * aspect, 50 * aspect, -50, 50 );
		cam.update();
	}

	/**
	 * Creates rootNode, lighting, statistic text, and other basic render
	 * states. Called in BaseGame.start() after initSystem().
	 *
	 * @see AbstractGame#initGame()
	 */
	protected void initGame() {
		/** Create rootNode */
		rootNode = new Node("rootNode");

		/**
		 * Create a wirestate to toggle on and off. Starts disabled with default
		 * width of 1 pixel.
		 */
		wireState = display.getRenderer().createWireframeState();
		wireState.setEnabled(true);
		//rootNode.setRenderState( wireState );

		/**
		 * Create a ZBuffer to display pixels closest to the camera above
		 * farther ones.
		 */
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled( true );
		buf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );
		rootNode.setRenderState( buf );

		// -- STATS, text node
		// Finally, a stand alone node (not attached to root on purpose)
		statNode = new Node("Stats node");
		statNode.setCullHint(Spatial.CullHint.Never);
		statNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

		if (Debug.stats) {
			graphNode = new Node( "Graph node" );
			graphNode.setCullHint( Spatial.CullHint.Never );
			statNode.attachChild(graphNode);

			setupStatGraphs();
			setupStats();
		}

		// ---- LIGHTS
		/** Set up a basic, default light. */
		lights = new AmbientLights(this);
		if (cfg.get("use_torch").toString().equalsIgnoreCase("yes")) {
			torch = new TorchLight(this);
		}

		/** Attach the light to a lightState and the lightState to rootNode. */
		lightState = display.getRenderer().createLightState();
		lightState.setEnabled(true);
		lightState.attach(lights);
		if (torch != null) {
			lightState.attach(torch);
		}
		rootNode.setRenderState(lightState);

		/** Let derived classes initialize. */
		//simpleInitGame();

		timer.reset();

		/**
		 * Update geometric and rendering information for both the rootNode and
		 * fpsNode.
		 */
		rootNode.updateGeometricState( 0.0f, true );
		rootNode.updateRenderState();

		statNode.updateGeometricState( 0.0f, true );
		statNode.updateRenderState();

		timer.reset();

		objects = new ArrayList<IUpdating>();

		text_hud = new TextHUD(this);
		this.statNode.attachChild(text_hud);
		this.addObjectForUpdate(text_hud);

		if (!RELEASE_MODE) {
			this.text_hud.add("RELEASE MODE OFF");
		}

		text_hud.add("Please wait...");
		text_hud.add("Teleporting...");
		text_hud.add("Stand by...");

		BulletSpark.InitBulletSpark(this);
		RedBloodSpurt.InitBloodSpurt(this);
		GreenBloodSpurt.InitBloodSpurt(this);
		Explosion.InitExplosion(this.getDisplay());

		this.addObjectForUpdate(lights);

	}
	

	/**
	 * Cleans up the keyboard.
	 *
	 * @see AbstractGame#cleanup()
	 */
	protected void cleanup() {
		logger.info( "Cleaning up resources." );

		TextureManager.doTextureCleanup();
		if (display != null && display.getRenderer() != null)
			display.getRenderer().cleanup();
		KeyInput.destroyIfInitalized();
		MouseInput.destroyIfInitalized();
		JoystickInput.destroyIfInitalized();
	}

	/**
	 * Calls the quit of BaseGame to clean up the display and then closes the JVM.
	 */
	protected void quit() {
		super.quit();
		System.exit( 0 );
	}

	/**
	 * Set up which stats to graph
	 *
	 */
	protected void setupStats() {
		lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.green);
		lgrapher.addConfig(StatType.STAT_FRAMES, LineGrapher.ConfigKeys.Stipple.name(), 0XFF0F);
		lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.cyan);
		lgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		lgrapher.addConfig(StatType.STAT_QUAD_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.lightGray);
		lgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		lgrapher.addConfig(StatType.STAT_LINE_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.red);
		lgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		lgrapher.addConfig(StatType.STAT_GEOM_COUNT, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.gray);
		lgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, LineGrapher.ConfigKeys.Color.name(), ColorRGBA.orange);
		lgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

		tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_FRAMES, TabledLabelGrapher.ConfigKeys.Name.name(), "Frames/s:");
		tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tris:");
		tgrapher.addConfig(StatType.STAT_TRIANGLE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Quads:");
		tgrapher.addConfig(StatType.STAT_QUAD_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Lines:");
		tgrapher.addConfig(StatType.STAT_LINE_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Objs:");
		tgrapher.addConfig(StatType.STAT_GEOM_COUNT, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
		tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Decimals.name(), 0);
		tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.Name.name(), "Avg.Tex binds:");
		tgrapher.addConfig(StatType.STAT_TEXTURE_BINDS, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

		// If you want to try out 
//		lgrapher.addConfig(StatType.STAT_RENDER_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.blue);
//		lgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.white);
//		lgrapher.addConfig(StatType.STAT_STATES_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.yellow);
//		lgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TimedAreaGrapher.ConfigKeys.Color.name(), ColorRGBA.red);

//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "Render:");
//		tgrapher.addConfig(StatType.STAT_RENDER_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "Other:");
//		tgrapher.addConfig(StatType.STAT_UNSPECIFIED_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "States:");
//		tgrapher.addConfig(StatType.STAT_STATES_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.Decimals.name(), 2);
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.Name.name(), "DisplaySwap:");
//		tgrapher.addConfig(StatType.STAT_DISPLAYSWAP_TIMER, TabledLabelGrapher.ConfigKeys.FrameAverage.name(), true);

//		StatCollector.addTimedStat(StatType.STAT_RENDER_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_STATES_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_UNSPECIFIED_TIMER);
//		StatCollector.addTimedStat(StatType.STAT_DISPLAYSWAP_TIMER);
	}

	/**
	 * Set up the graphers we will use and the quads we'll show the stats on.
	 *
	 */
	protected void setupStatGraphs() {
		StatCollector.setSampleRate(1000L);
		StatCollector.setMaxSamples(40);

		lineGraph = new Quad("lineGraph", display.getWidth(), display.getHeight()*.75f) {
			private static final long serialVersionUID = 1L;
			@Override
			public void draw(Renderer r) {
				StatCollector.pause();
				super.draw(r);
				StatCollector.resume();
			}
		};
		lgrapher = GraphFactory.makeLineGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
//		lgrapher = GraphFactory.makeTimedGraph((int)(lineGraph.getWidth()+.5f), (int)(lineGraph.getHeight()+.5f), lineGraph);
		lineGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.625f),0);
		lineGraph.setCullHint(CullHint.Always);
		lineGraph.getDefaultColor().a = 0;
		graphNode.attachChild(lineGraph);

		/*Text f4Hint = new Text("f4", "F4 - toggle stats") {
			private static final long serialVersionUID = 1L;
			@Override
			public void draw(Renderer r) {
				StatCollector.pause();
				super.draw(r);
				StatCollector.resume();
			}
		};
		f4Hint.setCullHint( Spatial.CullHint.Never );
		f4Hint.setRenderState( Text.getDefaultFontTextureState() );
		f4Hint.setRenderState( Text.getFontBlend() );
		f4Hint.setLocalScale(.8f);
		f4Hint.setTextColor(ColorRGBA.gray);
		f4Hint.setLocalTranslation(display.getRenderer().getWidth() - f4Hint.getWidth() - 15, display.getRenderer().getHeight() - f4Hint.getHeight() - 10, 0);
		graphNode.attachChild(f4Hint);*/

		labGraph = new Quad("labelGraph", display.getWidth(), display.getHeight()*.25f) {
			private static final long serialVersionUID = 1L;
			@Override
			public void draw(Renderer r) {
				StatCollector.pause();
				super.draw(r);
				StatCollector.resume();
			}
		};
		tgrapher = GraphFactory.makeTabledLabelGraph((int)(labGraph.getWidth()+.5f), (int)(labGraph.getHeight()+.5f), labGraph);
		tgrapher.setColumns(2);
		tgrapher.setMinimalBackground(false);
		tgrapher.linkTo(lgrapher);
		labGraph.setLocalTranslation((display.getWidth()*.5f), (display.getHeight()*.125f),0);
		labGraph.setCullHint(CullHint.Always);
		labGraph.getDefaultColor().a = 0;
		graphNode.attachChild(labGraph);

	}

	protected void reinit() {
		//do nothing
	}

	/*protected void simpleUpdate() {
	}

	protected void simpleRender() {
		//do nothing
	}*/

	public static void p(Object o) {
		System.out.println(System.currentTimeMillis() + ":" + o);
	}

	public URL locateResource(String resourceName) {
		try {
			return new File(resourceName).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
