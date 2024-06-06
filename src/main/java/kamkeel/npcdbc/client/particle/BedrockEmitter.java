package kamkeel.npcdbc.client.particle;

import com.sun.org.apache.xpath.internal.operations.Variable;
import kamkeel.npcdbc.util.Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.*;

public class BedrockEmitter {

    public String locator;
    public int disableAfter = -1;
    public double lastTick = 0;
    public List<BedrockParticle> particles = new ArrayList<BedrockParticle>();
    public List<BedrockParticle> splitParticles = new ArrayList<BedrockParticle>();


    public EntityLivingBase target;
    public World world;
    public boolean lit;

    public boolean added;
    public int sanityTicks;
    public boolean running = true;
    private BedrockParticle guiParticle;

    /* Intermediate values */
    public Vector3d lastGlobal = new Vector3d();
    public Vector3d prevGlobal = new Vector3d();
    public Matrix3f rotation = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
    public Matrix3f prevRotation = new Matrix3f(1, 0, 0, 0, 1, 0, 0, 0, 1);
    public Vector3f angularVelocity = new Vector3f();
    public Vector3d translation = new Vector3d();

    /* Runtime properties */
    public int age;
    public int lifetime;
    public double spawnedParticles;
    public boolean playing = true;

    public float random1 = (float) Math.random();
    public float random2 = (float) Math.random();
    public float random3 = (float) Math.random();
    public float random4 = (float) Math.random();

    private BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    public double[] scale = {1, 1, 1};

    public boolean lastLoop = false;
    /* Camera properties */
    public int viewPerspective;
    public float cameraYaw;
    public float cameraPitch;

    public double cameraX;
    public double cameraY;
    public double cameraZ;


    public List<BedrockParticle> particles = new ArrayList<BedrockParticle>();


    public boolean isFinished() {
        return !this.running && this.particles.isEmpty();
    }

    public boolean isLooping() {
        for (BedrockComponentBase componentBase : scheme.components) {
            if (componentBase instanceof BedrockComponentLifetimeLooping) {
                return true;
            }
        }
        return false;
    }

    public void setLastLoop() {
        lastLoop = true;
    }

    public double getDistanceSq() {
        this.setupCameraProperties(0F);

        double dx = this.cameraX - this.lastGlobal.x;
        double dy = this.cameraY - this.lastGlobal.y;
        double dz = this.cameraZ - this.lastGlobal.z;

        return dx * dx + dy * dy + dz * dz;
    }

    public double getAge() {
        return this.getAge(0);
    }

    public double getAge(float partialTicks) {
        return (this.age + partialTicks) / 20.0;
    }


    public void setTarget(EntityLivingBase target) {
        this.target = target;
        this.world = target == null ? null : target.worldObj;
    }



    /* Variable related code */


    public void start() {
        if (this.playing) {
            return;
        }

        this.age = 0;
        this.spawnedParticles = 0;
        this.playing = true;
    }

    public void stop() {
        if (!this.playing) {
            return;
        }

        this.spawnedParticles = 0;
        this.playing = false;

        this.random1 = (float) Math.random();
        this.random2 = (float) Math.random();
        this.random3 = (float) Math.random();
        this.random4 = (float) Math.random();
    }

    /**
     * Update this current emitter
     */
    public void update() {
        if (this.scheme == null) {
            return;
        }

        this.updateParticles();

        this.age += 1;
        this.sanityTicks += 1;
    }

    /**
     * Update all particles
     */
    private void updateParticles() {
        Iterator<BedrockParticle> it = this.particles.iterator();

        while (it.hasNext()) {
            BedrockParticle particle = it.next();

            this.updateParticle(particle);

            if (particle.isDead) {
                it.remove();
            }
        }

        if (!this.splitParticles.isEmpty()) {
            this.particles.addAll(this.splitParticles);
            this.splitParticles.clear();
        }
    }

    private void updateParticlesCollision() {

        Iterator<BedrockParticle> it = this.particles.iterator();

        while (it.hasNext()) {
            BedrockParticle particle = it.next();

            //  collision.update(this, particle);

            if (particle.isDead) {
                it.remove();
            }
        }

        if (!this.splitParticles.isEmpty()) {
            this.particles.addAll(this.splitParticles);
            this.splitParticles.clear();
        }
    }

    /**
     * Update a single particle
     */
    private void updateParticle(BedrockParticle particle) {
        particle.update(this);

    }

    /**
     * Spawn a particle
     */
    public void spawnParticle() {
        if (!this.running) {
            return;
        }

        this.particles.add(this.createParticle(false));
    }

    /**
     * Create a new particle
     */
    public BedrockParticle createParticle(boolean forceRelative) {
        BedrockParticle particle = new BedrockParticle();

        particle.setupMatrix(this);

 

        if (particle.relativePosition && !particle.relativeRotation) {
            Vector3f vec = new Vector3f(particle.position);

            particle.matrix.transform(vec);

            particle.position.x = vec.x;
            particle.position.y = vec.y;
            particle.position.z = vec.z;
        }

        if (!(particle.relativePosition && particle.relativeRotation)) {
            particle.position.add(this.lastGlobal);
            particle.initialPosition.add(this.lastGlobal);
        }

        particle.prevPosition.set(particle.position);
        particle.rotation = particle.initialRotation;
        particle.prevRotation = particle.rotation;

        return particle;
    }

    /**
     * Render the particle on screen
     */
    public void renderOnScreen(int x, int y, float scale) {
        if (this.scheme == null) {
            return;
        }

        BedrockComponentParticleMorph particleMorphComponent = this.scheme.getOrCreate(BedrockComponentParticleMorph.class);
        float partialTicks = Minecraft.getMinecraft().timer.renderPartialTicks;

        List<IComponentParticleRender> listParticle = this.scheme.getComponents(IComponentParticleRender.class);
        List<IComponentParticleMorphRender> listMorph = this.scheme.getComponents(IComponentParticleMorphRender.class);

        Matrix3f rotation = this.rotation;

        this.rotation = new Matrix3f();

        if (!listParticle.isEmpty() && (!this.isMorphParticle() || particleMorphComponent.renderTexture)) {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.scheme.texture);

            this.scheme.material.beginGL();
            GlStateManager.disableCull();

            if (this.guiParticle == null || this.guiParticle.dead) {
                this.guiParticle = this.createParticle(true);
            }

            this.rotation.setIdentity();
            this.guiParticle.update(this);
            this.setEmitterVariables(partialTicks);
            this.setParticleVariables(this.guiParticle, partialTicks);

            for (IComponentParticleRender render : listParticle) {
                render.renderOnScreen(this.guiParticle, x, y, scale, partialTicks);
            }

            this.scheme.material.endGL();
            GlStateManager.enableCull();
        }

        if (!listMorph.isEmpty() && this.isMorphParticle()) {
            if (this.guiParticle == null || this.guiParticle.dead) {
                this.guiParticle = this.createParticle(true);
            }

            this.rotation.setIdentity();
            this.guiParticle.update(this);
            this.setEmitterVariables(partialTicks);
            this.setParticleVariables(this.guiParticle, partialTicks);

            for (IComponentParticleMorphRender render : listMorph) {
                render.renderOnScreen(this.guiParticle, x, y, scale, partialTicks);
            }
        }

        this.rotation = rotation;
    }

    /**
     * Render all the particles in this particle emitter
     */
    public void render(float partialTicks) {
        if (this.scheme == null) {
            return;
        }

        this.setupCameraProperties(partialTicks);
        BedrockComponentParticleMorph particleMorphComponent = this.scheme.getOrCreate(BedrockComponentParticleMorph.class);
        List<IComponentParticleRender> renders = this.scheme.particleRender;
        List<IComponentParticleMorphRender> morphRenders = this.scheme.particleMorphRender;

        boolean morphRendering = this.isMorphParticle();
        boolean particleRendering = !morphRendering || particleMorphComponent.renderTexture;
        updateParticlesCollision();
        /* particle rendering */
        if (particleRendering) {
            this.setupOpenGL(partialTicks);

            for (IComponentParticleRender component : renders) {
                component.preRender(this, partialTicks);
            }

            if (!this.particles.isEmpty()) {
                this.depthSort();

                this.renderParticles(this.scheme.texture, renders, false, partialTicks);

                BedrockComponentCollisionAppearance collisionAppearance = this.scheme.getOrCreate(BedrockComponentCollisionAppearance.class);

                /* rendering the collided particles with an extra component */
                if (collisionAppearance != null && collisionAppearance.texture != null) {
                    this.renderParticles(collisionAppearance.texture, renders, true, partialTicks);
                }
            }
            for (IComponentParticleRender component : renders) {
                component.postRender(this, partialTicks);
            }

            this.endOpenGL();
        }

        /* Morph rendering */
        if (morphRendering) {
            for (IComponentParticleMorphRender component : morphRenders) {
                component.preRender(this, partialTicks);
            }

            if (!this.particles.isEmpty()) {
                //only depth sort either in particle rendering or morph rendering
                if (!particleRendering) {
                    this.depthSort();
                }

                this.renderParticles(morphRenders, false, partialTicks);

                /*BedrockComponentCollisionParticleMorph collisionComponent = this.scheme.getOrCreate(BedrockComponentCollisionParticleMorph.class);

                if (collisionComponent != null && collisionComponent.morph != null)
                {
                    this.renderParticles(morphRenders, true, partialTicks);
                }*/
            }

            for (IComponentParticleMorphRender component : morphRenders) {
                if (component.getClass() == BedrockComponentRateSteady.class) {
                    if (!particleRendering) {
                        //only spawn particles either in particles or in morph rendering
                        component.postRender(this, partialTicks);
                    }
                } else {
                    component.postRender(this, partialTicks);
                }
            }
        }
    }

    /**
     * This method renders the particles using morphs
     *
     * @param renderComponents
     * @param collided
     * @param partialTicks
     */
    private void renderParticles(List<? extends IComponentParticleMorphRender> renderComponents, boolean collided, float partialTicks) {

        for (BedrockParticle particle : this.particles) {
            this.setEmitterVariables(partialTicks);
            this.setParticleVariables(particle, partialTicks);

            for (IComponentRenderBase component : renderComponents) {
                component.render(this, particle, Tessellator.instance, partialTicks);
            }
        }
    }

    /**
     * This method renders the particles using the default bedrock billboards
     *
     * @param texture          Ressource location of the texture to render
     * @param renderComponents
     * @param collided
     * @param partialTicks
     */
    private void renderParticles(ResourceLocation texture, List<? extends IComponentParticleRender> renderComponents, boolean collided, float partialTicks) {

        GifTexture.bindTexture(texture, this.age, partialTicks);
        Tessellator.instance.startDrawingQuads();
        //builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        for (BedrockParticle particle : this.particles) {
            boolean collisionStuff = particle.isCollisionTexture(this) || particle.isCollisionTinting(this);

            if (collisionStuff != collided) {
                continue;
            }

            this.setEmitterVariables(partialTicks);
            this.setParticleVariables(particle, partialTicks);
            for (IComponentRenderBase component : renderComponents) {
                /* if collisionTexture or collisionTinting is true - means that those options are enabled
                 * therefore the old Billboardappearance should not be called
                 * because collisionAppearance.class is rendering
                 */
                if (!(collisionStuff && component.getClass() == BedrockComponentAppearanceBillboard.class)) {
                    component.render(this, particle, Tessellator.instance, partialTicks);
                }
            }
        }

        Tessellator.instance.draw();
    }

    private void setupOpenGL(float partialTicks) {
        this.scheme.material.beginGL();

        if (!GuiModelRenderer.isRendering()) {
            Entity camera = Minecraft.getMinecraft().renderViewEntity;
            //double playerX = camera.prevPosX + (camera.posX - camera.prevPosX) * (double) partialTicks;
            //double playerY = camera.prevPosY + (camera.posY - camera.prevPosY) * (double) partialTicks;
            //double playerZ = camera.prevPosZ + (camera.posZ - camera.prevPosZ) * (double) partialTicks;

            //Tessellator.instance.setTranslation(-playerX, -playerY, -playerZ);
            //Tessellator.instance.setTranslation(-lastGlobal.x, -lastGlobal.y, -lastGlobal.z);
            GlStateManager.disableCull();
            GlStateManager.enableTexture2D();
        }
    }

    private void endOpenGL() {
        if (!GuiModelRenderer.isRendering()) {
            Tessellator.instance.setTranslation(0, 0, 0);
        }

        this.scheme.material.endGL();
    }


    private void depthSort() {
        if (true)//TODO //Blockbuster.snowstormDepthSorting.get())
        {
            this.particles.sort((a, b) ->
            {
                double ad = a.getDistanceSq(this);
                double bd = b.getDistanceSq(this);

                if (ad < bd) {
                    return 1;
                } else if (ad > bd) {
                    return -1;
                }

                return 0;
            });
        }
    }

    public void setupCameraProperties(float partialTicks) {
        if (this.world != null) {
            Entity camera = Minecraft.getMinecraft().renderViewEntity;

            this.viewPerspective = Minecraft.getMinecraft().gameSettings.thirdPersonView;
            this.cameraYaw = 180 - Utility.lerp(camera.prevRotationYaw, camera.rotationYaw, partialTicks);
            this.cameraPitch = 180 - Utility.lerp(camera.prevRotationPitch, camera.rotationPitch, partialTicks);

            this.cameraX = Utility.lerp(camera.prevPosX, camera.posX, partialTicks);
            this.cameraY = Utility.lerp(camera.prevPosY, camera.posY, partialTicks) + camera.getEyeHeight();
            this.cameraZ = Utility.lerp(camera.prevPosZ, camera.posZ, partialTicks);
        }
    }

    /**
     * Get brightness for the block
     */
    public int getBrightnessForRender(float partialTicks, double x, double y, double z) {
        if (this.lit || this.world == null) {
            return 15728880;
        }

        this.blockPos.set((int) x, (int) y, (int) z);

        return this.world.blockExists((int) x, (int) y, (int) z) ? this.world.getLightBrightnessForSkyBlocks((int) x, (int) y, (int) z, 0) : 0;
    }
}
