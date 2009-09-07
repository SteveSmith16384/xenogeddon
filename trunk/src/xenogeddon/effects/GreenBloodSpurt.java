package xenogeddon.effects;

import java.util.ArrayList;
import xenogeddon.Main;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;

public final class GreenBloodSpurt extends AbstractEffect {

	private static final long DURATION = 300;

	private ParticleMesh mesh;
	private boolean active = false;
	private static ArrayList<GreenBloodSpurt> spurts = new ArrayList<GreenBloodSpurt>();

	public static void InitBloodSpurt(Main m) {
		for (int i=0 ; i<10 ; i++) {
			GreenBloodSpurt spark = new GreenBloodSpurt(m);
			spurts.add(spark);
			m.removeObjectForUpdate(spark); // Since it gets added automatically.
			spark.removeFromParent();
		}
	}

	public static void AddBloodSpurt(Main main, Vector3f pos, Vector3f dir) {
		for (int i=0 ; i<spurts.size() ; i++) {
			GreenBloodSpurt s = (GreenBloodSpurt)spurts.get(i);
			if (s.active == false) {
				//Main.p("Blood " + i);
				s.active = true;
				s.setLocalTranslation(pos);
				s.mesh.setEmissionDirection(dir);
				s.remove_at = System.currentTimeMillis() + DURATION;
				s.mesh.forceRespawn();
				main.addObjectForUpdate(s);
				main.attachToRootNode(s);
				s.mesh.updateRenderState();
				s.mesh.updateGeometricState(0, true);
				break;
			} 
		}
	}

	
	private GreenBloodSpurt(Main m) {
		super(m, "GreenBloodSpurt", 0f, 0f, 0f, false, -1);

		BlendState as1 = main.getDisplay().getRenderer().createBlendState();
		as1.setBlendEnabled( true );
		as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
		as1.setDestinationFunction( BlendState.DestinationFunction.One );
		as1.setTestEnabled( true );
		as1.setTestFunction( BlendState.TestFunction.GreaterThan );
		as1.setEnabled( true );

		TextureState ts = main.getDisplay().getRenderer().createTextureState();
		Texture t = TextureManager.loadTexture("./data/textures/flaresmall.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
		ts.setTexture(t);
		ts.setEnabled(true);

		mesh = ParticleFactory.buildParticles("particles", 4);
		mesh.getParticleController().setActive(false);
		mesh.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(new Vector3f(0, -.08f, 0f), true));
		//mesh.setRepeatType(Controller.RT_CLAMP);
		//mesh.setEmissionDirection(dir);
		mesh.setMaximumAngle((float)Math.PI*2);
		mesh.setSpeed(0.2f);
		mesh.setMinimumLifeTime(35.0f);
		mesh.setMaximumLifeTime(50.0f);
		mesh.setStartSize(.1f);
		mesh.setEndSize(.1f);
		mesh.setStartColor( new ColorRGBA( 0.0f, 1.0f, 0.0f, 1.0f ) );
		mesh.setEndColor( new ColorRGBA( 0.0f, 1.0f, 0.0f, 0.0f ) );
		mesh.setInitialVelocity(0.01f);
		mesh.setRotateWithScene(true);
		mesh.setReleaseRate(1); // Particles per second
		mesh.setReleaseVariance(0.0f); // % / 100
		mesh.setParticleSpinSpeed(0.08f);

		mesh.forceRespawn();
		//mesh.warmUp(60);

		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		mesh.setIsCollidable(false);

		ZBufferState zbuf = main.getDisplay().getRenderer().createZBufferState();
		zbuf.setWritable( false );
		zbuf.setEnabled( true );
		zbuf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );

		mesh.setRenderState(ts);
		mesh.setRenderState(as1);
		mesh.setRenderState(zbuf);

		this.attachChild(mesh);
	}

	public void update(float interpolation) {
		if (System.currentTimeMillis() > remove_at) {
			this.active = false;
			main.removeObjectForUpdate(this);
			this.removeFromParent();
		}
	}

}
