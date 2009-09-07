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
import com.jmex.effects.particles.SimpleParticleInfluenceFactory;


public class CeilingSpark extends AbstractEffect {

	public CeilingSpark(Main m, float x, float y, float z, Vector3f dir) {
		super(m, "CeilingSpark", x + 0.5f, y, z + 0.5f, true, -1);

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

		ParticleMesh mesh = ParticleFactory.buildParticles("particles", 20);
		mesh.getParticleController().setActive(false);
		mesh.addInfluence(SimpleParticleInfluenceFactory.createBasicGravity(new Vector3f(0, -.04f, 0f), true));
		mesh.setEmissionDirection(dir);
		mesh.setMaximumAngle(0.5f);
		mesh.setSpeed(0.2f);
		mesh.setMinimumLifeTime(70.0f);
		mesh.setMaximumLifeTime(100.0f);
		mesh.setStartSize(.1f);
		mesh.setEndSize(.1f);
		mesh.setStartColor( new ColorRGBA( 1.0f, 1.0f, 0.0f, 1.0f ) );
		mesh.setEndColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 0.0f ) );
		mesh.setInitialVelocity(0.01f);
		mesh.setRotateWithScene(true);
		mesh.setReleaseRate(1); // Particles per second
		mesh.setReleaseVariance(0.5f); // % / 100
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
