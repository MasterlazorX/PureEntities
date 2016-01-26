package milk.entitymanager.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.Projectile;
import cn.nukkit.entity.Snowball;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityShootBowEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.sound.LaunchSound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.Player;
import cn.nukkit.entity.Creature;
import milk.entitymanager.util.Utils;

public class SnowGolem extends Monster{
    public static final int NETWORK_ID = 21;

    public SnowGolem(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);
    }

    @Override
    public int getNetworkId(){
        return NETWORK_ID;
    }

    @Override
    public float getWidth(){
        return 0.65f;
    }

    @Override
    public float getHeight(){
        return 2.1f;
    }

    @Override
    public float getEyeHeight(){
        return 1.92f;
    }

    @Override
    public void initEntity(){
        super.initEntity();

        this.setFriendly(true);
    }

    @Override
    public String getName(){
        return "SnowGolem";
    }

    @Override
    public void attackEntity(Entity player){
        if(this.attackDelay > 23  && Utils.rand(1, 32) < 4 && this.distanceSquared(player) <= 55){
            this.attackDelay = 0;

            double f = 1.2;
            double yaw = this.yaw + Utils.rand(-220, 220) / 10;
            double pitch = this.pitch + Utils.rand(-120, 120) / 10;
            CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                    .add(new DoubleTag("", this.x + (-Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5)))
                    .add(new DoubleTag("", this.getEyeHeight()))
                    .add(new DoubleTag("", this.z +(Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * 0.5))))
                .putList(new ListTag<DoubleTag>("Motion")
                    .add(new DoubleTag("", -Math.sin(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f))
                    .add(new DoubleTag("", -Math.sin(pitch / 180 * Math.PI) * f))
                    .add(new DoubleTag("", Math.cos(yaw / 180 * Math.PI) * Math.cos(pitch / 180 * Math.PI) * f)))
                .putList(new ListTag<FloatTag>("Rotation")
                    .add(new FloatTag("", (float) yaw))
                    .add(new FloatTag("", (float) pitch)));

            Entity k = Entity.createEntity("Snowball", this.chunk, nbt, this);
            if(k == null){
                return;
            }

            Snowball snowball = (Snowball) k;
            snowball.setMotion(snowball.getMotion().multiply(f));

            //TODO: I can't change Snowball's damage
            /*property = (new \ReflectionClass(snowball)).getProperty("damage");
            property.setAccessible(true);
            property.setValue ( snowball, 2 );*/

            EntityShootBowEvent ev = new EntityShootBowEvent(this, Item.get(Item.ARROW, 0, 1), snowball, f);
            this.server.getPluginManager().callEvent(ev);

            Projectile projectile = ev.getProjectile();
            if(ev.isCancelled()){
                projectile.kill();
            }else if(projectile != null){
                ProjectileLaunchEvent launch = new ProjectileLaunchEvent(projectile);
                this.server.getPluginManager().callEvent(launch);
                if(launch.isCancelled()){
                    projectile.kill();
                }else{
                    projectile.spawnToAll();
                    this.level.addSound(new LaunchSound(this), this.getViewers().values());
                }
            }
        }
    }

    @Override
    public Item[] getDrops(){
        if(this.lastDamageCause instanceof EntityDamageByEntityEvent){
            return new Item[]{Item.get(Item.SNOWBALL, 0, 15)};
        }
        return new Item[0];
    }

    @Override
    public boolean targetOption(Creature creature, double distance){
        return !(creature instanceof Player) && creature.isAlive() && distance <= 60;
    }
}