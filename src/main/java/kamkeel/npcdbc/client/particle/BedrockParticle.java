package kamkeel.npcdbc.client.particle;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Matrix3f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.Map;

public class BedrockParticle {
    public float randomX = (float) Math.random();
    public float randomY = (float) Math.random();
    public float randomZ = (float) Math.random();

    public final ResourceLocation texture;
    public int u, v, width, height;

    public int age;
    public int lifetime;
    private int expireAge = -1;

    public boolean isDead;
    public boolean gravity;
    public boolean relativePosition;
    public boolean relativeRotation;
    public boolean relativeDirection;
    public boolean relativeScale;
    public boolean relativeScaleBillboard;
    public boolean relativeAcceleration;
    public boolean realisticCollisionDrag;
    public float linearVelocity;
    public float angularVelocity;
    public boolean manual;
    public Vector3f collisionTime = new Vector3f(-2f, -2f,-2f);
    public HashMap<Entity, Vector3f> entityCollisionTime = new HashMap<>();
    public boolean collided;
    public int bounces;
    
    public float rotation;
    public float initialRotation;
    public float prevRotation;

    public float rotationVelocity;
    public float rotationAcceleration;
    public float rotationDrag;

    public Vector3d offset = new Vector3d();
    public Vector3d position = new Vector3d();
    public Vector3d initialPosition = new Vector3d();
    public Vector3d prevPosition = new Vector3d();
    private Vector3d globalPosition = new Vector3d();

    public Vector3f speed = new Vector3f();
    public Vector3f acceleration = new Vector3f();
    public Vector3f accelerationFactor = new Vector3f(1, 1, 1);
    public float drag = 0;
    public float dragFactor = 0;
    public Matrix3f matrix = new Matrix3f();

    public float r, g, b, a;

    public BedrockParticle(ResourceLocation texture, int u, int v, int width, int height) {
        this.texture = texture;
        this.speed.set(randomX - 0.5F, randomY - 0.5F, randomZ - 0.5F);
        this.speed.normalize();
        
    }

    public BedrockParticle setColor(int hex, float alpha) {
        this.r = (hex >> 16 & 0xff) / 255f;
        this.g = (hex >> 8 & 0xff) / 255f;
        this.b = (hex & 0xff) / 255f;
        a = alpha;
        return this;
    }


    public void update(BedrockEmitter emitter) {
        this.prevRotation = this.rotation;
        this.prevPosition.set(this.position);

        this.setupMatrix(emitter);

        if (!this.manual) {
            //this.position.add(this.offset);

            if (this.realisticCollisionDrag && Math.round(this.speed.x * 10000) == 0 && Math.round(this.speed.y * 10000) == 0 && Math.round(this.speed.z * 10000) == 0) {
                this.dragFactor = 0;
                this.speed.scale(0);
            }

            /* lazy fix for transforming from moving intertial system back to global space */
            if (this.entityCollisionTime.isEmpty()) {
                transformOffsetToGlobal();
            } else {
                for (HashMap.Entry<Entity, Vector3f> entry : this.entityCollisionTime.entrySet()) {
                    if (entry.getValue().y != this.age) {
                        transformOffsetToGlobal();
                    }
                }
            }

            float rotationAcceleration = this.rotationAcceleration / 20F - this.rotationDrag * this.rotationVelocity;
            this.rotationVelocity += rotationAcceleration / 20F;
            this.rotation = this.initialRotation + this.rotationVelocity * this.age;

            /* Position */
            if (this.age == 0) {
                if (this.relativeDirection) {
                    emitter.rotation.transform(this.speed);
                }

                if (this.linearVelocity != 0) {
                    Vector3f v = new Vector3f(emitter.lastGlobal);
                    v.x -= emitter.prevGlobal.x;
                    v.y -= emitter.prevGlobal.y;
                    v.z -= emitter.prevGlobal.z;

                    this.speed.x += v.x * this.linearVelocity;
                    this.speed.y += v.y * this.linearVelocity;
                    this.speed.z += v.z * this.linearVelocity;
                }

                if (this.angularVelocity != 0) {
                    Matrix3f rotation1 = new Matrix3f(emitter.rotation);
                    Matrix3f identity = new Matrix3f();

                    identity.setIdentity();

                    try {
                        Matrix3f rotation0 = new Matrix3f(emitter.prevRotation);

                        rotation0.invert();
                        rotation1.mul(rotation0);

                        Vector3f angularV = MatrixUtils.getAngularVelocity(rotation1);

                        Vector3f radius = new Vector3f(emitter.translation);
                        radius.x += this.position.x - emitter.lastGlobal.x;
                        radius.y += this.position.y - emitter.lastGlobal.y;
                        radius.z += this.position.z - emitter.lastGlobal.z;

                        Vector3f v = new Vector3f();

                        v.cross(angularV, radius);

                        this.speed.x += v.x * this.angularVelocity;
                        this.speed.y += v.y * this.angularVelocity;
                        this.speed.z += v.z * this.angularVelocity;
                    } catch (SingularMatrixException e) {
                    } //maybe check if determinant is zero
                }
            }

            if (this.relativeAcceleration) {
                emitter.rotation.transform(this.acceleration);
            }

            Vector3f drag = new Vector3f(this.speed);

            drag.scale(-(this.drag + this.dragFactor));

            if (this.gravity) {
                this.acceleration.y -= 9.81;
            }

            this.acceleration.add(drag);
            this.acceleration.scale(1 / 20F);
            this.speed.add(this.acceleration);

            Vector3f speed0 = new Vector3f(this.speed);
            speed0.x *= this.accelerationFactor.x;
            speed0.y *= this.accelerationFactor.y;
            speed0.z *= this.accelerationFactor.z;

            if (this.relativePosition || this.relativeRotation) {
                this.matrix.transform(speed0);
            }

            this.position.x += speed0.x / 20F;
            this.position.y += speed0.y / 20F;
            this.position.z += speed0.z / 20F;
            

//            if (!this.morph.isEmpty())
//            {
//                EntityLivingBase dummy = this.getDummy(emitter);
//
//                this.morph.get().update(dummy);
//
//                dummy.ticksExisted += 1;
//            }
        }

        if (this.lifetime >= 0 &&
                (this.age >= this.lifetime || (this.age >= this.expireAge && this.expireAge != -1))) {
            this.isDead = true;
        }

        this.age++;
    }
    public void transformOffsetToGlobal()
    {
        this.offset.scale(6); //scale it up so it gets more noticeable (artistic choice)

        this.speed.x += this.offset.x;
        this.speed.y += this.offset.y;
        this.speed.z += this.offset.z;

        this.offset.scale(0);
    }
    public Vector3d getGlobalPosition(BedrockEmitter emitter, Vector3d vector)
    {
        double px = vector.x;
        double py = vector.y;
        double pz = vector.z;

        if (this.relativePosition && this.relativeRotation)
        {
            Vector3f v = new Vector3f((float) px, (float) py, (float) pz);
            emitter.rotation.transform(v);

            px = v.x;
            py = v.y;
            pz = v.z;

            px += emitter.lastGlobal.x;
            py += emitter.lastGlobal.y;
            pz += emitter.lastGlobal.z;
        }

        this.globalPosition.set(px, py, pz);

        return this.globalPosition;
    }
    public BedrockParticle softCopy(BedrockParticle to)
    {
        to.age = this.age;
        to.expireAge = this.expireAge;
        to.realisticCollisionDrag = this.realisticCollisionDrag;
        to.collisionTime = (Vector3f) this.collisionTime.clone();
        to.entityCollisionTime = new HashMap<>();

        for(Map.Entry<Entity, Vector3f> entry : this.entityCollisionTime.entrySet())
        {
            to.entityCollisionTime.put(entry.getKey(), (Vector3f) entry.getValue().clone());
        }

        to.bounces = this.bounces;
        to.offset = (Vector3d) this.offset.clone();
        to.position = (Vector3d) this.position.clone();
        to.initialPosition = (Vector3d) this.initialPosition.clone();
        to.prevPosition = (Vector3d) this.prevPosition.clone();
        to.matrix = (Matrix3f) this.matrix.clone();
        to.speed = (Vector3f) this.speed.clone();
        to.acceleration = (Vector3f) this.acceleration.clone();
        to.accelerationFactor = (Vector3f) this.accelerationFactor.clone();
        to.dragFactor = this.dragFactor;
        to.globalPosition = (Vector3d) this.globalPosition.clone();

        return to;
    }

    public double getDistanceSq(BedrockEmitter emitter)
    {
        Vector3d pos = this.getGlobalPosition(emitter);

        double dx = emitter.cameraX - pos.x;
        double dy = emitter.cameraY - pos.y;
        double dz = emitter.cameraZ - pos.z;

        return dx * dx + dy * dy + dz * dz;
    }
}
