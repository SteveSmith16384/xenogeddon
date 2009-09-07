package xenogeddon.effects;

import xenogeddon.GameObject;
import xenogeddon.Main;
import xenogeddon.models.CreatureObject;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

public final class WeaponFireEffect extends GameObject {

	private static final long DURATION = 500;

	public ParticleMesh mesh;
	protected long remove_at;
	private boolean shooting = false;

	public WeaponFireEffect(Main m) {
		super(m, "WeaponFire", false, 0f, 0f, 0f);

		BlendState as1 = main.getDisplay().getRenderer().createBlendState();
		as1.setBlendEnabled( true );
		as1.setSourceFunction( BlendState.SourceFunction.SourceAlpha );
		as1.setDestinationFunction( BlendState.DestinationFunction.One );
		as1.setTestEnabled( true );
		as1.setTestFunction( BlendState.TestFunction.GreaterThan );
		as1.setEnabled( true );

		TextureState ts = main.getDisplay().getRenderer().createTextureState();
		Texture t = TextureManager.loadTexture("./data/textures/spark.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor);
		ts.setTexture(t);
		ts.setEnabled(true);

		mesh = ParticleFactory.buildParticles("particles", 10);
		mesh.setRepeatType(Controller.RT_CYCLE);
		mesh.setEmissionDirection(new Vector3f(0f, 0f, 1f));
		mesh.setMaximumAngle(0.0f);
		mesh.setSpeed(.8f);
		mesh.setMinimumLifeTime(45.0f);
		mesh.setMaximumLifeTime(55.0f);
		mesh.setStartSize(.1f);
		mesh.setEndSize(.1f);
		mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 0.3f, 1.0f ) );
		mesh.setEndColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 0.0f ) );
		mesh.setInitialVelocity(0.01f);
		mesh.setRotateWithScene(true);
		mesh.setReleaseRate(10); // Particles per second
		mesh.setReleaseVariance(0.0f); // % / 100
		mesh.setParticleSpinSpeed(0.08f);

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

		mesh.setLocalTranslation(-0.2f, 0.2f, 0.3f);

		this.setLocalTranslation(main.player.getLocalTranslation());
		this.setLocalRotation(main.player.getLocalRotation());
		
		main.addObjectForUpdate(this);
		
		main.attachToRootNode(this);
	}

	
	public void shoot(float interpolation) {
		if (!shooting) {
			shooting = true;
			this.attachChild(mesh);
			//mesh.updateGeometricState(interpolation, true);
			mesh.updateRenderState();
			remove_at = System.currentTimeMillis() + DURATION;
		}
	}

	
	public void damage(int amt, CreatureObject cre, Vector3f pos, Vector3f dir) {
		// Do nothing
	}

	
	public void update(float interpolation) {
		if (shooting) {
			if (System.currentTimeMillis() > remove_at) {
				//Main.p("stopped shooting");
				//this.mesh.stopEmitting();
				mesh.removeFromParent();
				this.shooting = false;
			}
		}

	}

	
}
