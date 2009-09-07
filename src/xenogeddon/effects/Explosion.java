package xenogeddon.effects;

import java.util.ArrayList;
import xenogeddon.Main;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.scene.state.BlendState.DestinationFunction;
import com.jme.scene.state.BlendState.SourceFunction;
import com.jme.scene.state.BlendState.TestFunction;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;

public class Explosion extends AbstractEffect {

    private static ArrayList<ParticleMesh> explosions = new ArrayList<ParticleMesh>();

    ParticleMesh explosion;

    private static BlendState bs;
    private static TextureState ts;
    private static ZBufferState zstate;

	public Explosion(Main m, float x, float y, float z) {
		super(m, "Explosion", x, y, z, true, 10000);
		explosion = getSmallExplosion();
		explosion.forceRespawn();
		this.attachChild(explosion);
		
		this.updateGeometricState(0, true);
		this.updateRenderState();

		main.sfx.playSound("./data/sounds/fire explosion.wav");
	}

    private static ParticleMesh getSmallExplosion() {
        for (int x = 0, tSize = explosions.size(); x < tSize; x++) {
            ParticleMesh e = (ParticleMesh)explosions.get(x);
            if (!e.getParticleController().isActive()) {
                return e;
            }
        }
        return createSmallExplosion();
    }
    
    public static void InitExplosion(DisplaySystem display) {
        //DisplaySystem display = main.getDisplay();
        bs = display.getRenderer().createBlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.One);
        bs.setTestEnabled(true);
        bs.setTestFunction(TestFunction.GreaterThan);

        ts = display.getRenderer().createTextureState();
        ts.setTexture(TextureManager.loadTexture("./data/textures/flaresmall.jpg", Texture.MinificationFilter.NearestNeighborLinearMipMap, Texture.MagnificationFilter.NearestNeighbor));

        zstate = display.getRenderer().createZBufferState();
        zstate.setEnabled(false);

        for (int i = 0; i < 15; i++) {
            createSmallExplosion();
        }
    }

    private static ParticleMesh createSmallExplosion() {
        ParticleMesh explosion = ParticleFactory.buildParticles("small", 40);
        explosion.setEmissionDirection(new Vector3f(0.0f, 1.0f, 0.0f));
        explosion.setMaximumAngle(FastMath.PI);
        explosion.setSpeed(0.2f);
        explosion.setMinimumLifeTime(60.0f);
        explosion.setMinimumLifeTime(120.0f);
        explosion.setStartSize(1.0f);
        explosion.setEndSize(2.0f);
        explosion.setStartColor(new ColorRGBA(1.0f, 0.312f, 0.121f, 1.0f));
        explosion.setEndColor(new ColorRGBA(1.0f, 0.24313726f, 0.03137255f, 0.0f));
        explosion.setControlFlow(false);
        explosion.setInitialVelocity(0.01f);
        explosion.setParticleSpinSpeed(0.0f);
        explosion.setRepeatType(Controller.RT_CLAMP);

        explosion.warmUp(1000);

        explosion.setRenderState(ts);
        explosion.setRenderState(bs);
        explosion.setRenderState(zstate);
        explosion.updateRenderState();
        
        explosions.add(explosion);
        
        return explosion;
    }

}
