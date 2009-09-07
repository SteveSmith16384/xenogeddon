package xenogeddon.effects;

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

public final class Smoke extends AbstractEffect {

	public Smoke(Main m, float x, float y, float z, Vector3f dir) {
		super(m, "Smoke", x + 0.5f, y, z + 0.5f, true, -1);

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

		ParticleMesh mesh = ParticleFactory.buildParticles("particles", 200);
		
		//mesh.getParticleController().setActive(false);
		//mesh.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(new Vector3f(0, -.1f, 0f), true));
		
		mesh.setEmissionDirection(dir);
		mesh.setMaximumAngle(0.3f);
		mesh.setSpeed(0.1f);
		mesh.setMinimumLifeTime(50.0f);
		mesh.setMaximumLifeTime(150.0f);
		mesh.setStartSize(.01f);
		mesh.setEndSize(.6f);
		mesh.setStartColor(new ColorRGBA(0.204f, 0.255f, 0.355f, 0.3f));
		mesh.setEndColor(new ColorRGBA(0.204f, 0.255f, 0.355f, 0.0f));

		mesh.setInitialVelocity(0.01f);
		mesh.setRotateWithScene(true);
		mesh.setReleaseRate(1); // Particles per second
		mesh.setReleaseVariance(0.6f); // % / 100
		mesh.setParticleSpinSpeed(0.08f);

		mesh.forceRespawn();
		mesh.warmUp(60);

		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		mesh.setIsCollidable(false);

		ZBufferState zbuf = main.getDisplay().getRenderer().createZBufferState();
		zbuf.setWritable( false );
		zbuf.setEnabled( true );
		zbuf.setFunction( ZBufferState.TestFunction.LessThanOrEqualTo );

		mesh.setRenderState( ts );
		mesh.setRenderState( as1 );
		mesh.setRenderState( zbuf );

		this.attachChild(mesh);

	}

}
