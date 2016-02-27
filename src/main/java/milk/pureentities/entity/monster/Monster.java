package milk.pureentities.entity.monster;

import cn.nukkit.entity.Entity;

public interface Monster{

    void attackEntity(Entity player);

    Entity getTarget();
    void setTarget(Entity target);

    int getDamage();
    int getDamage(Integer difficulty);

    int getMinDamage();
    int getMinDamage(Integer difficulty);

    int getMaxDamage();
    int getMaxDamage(Integer difficulty);

    void setDamage(int damage);
    void setDamage(int[] damage);
    void setDamage(int damage, int difficulty);

    void setMinDamage(int damage);
    void setMinDamage(int[] damage);
    void setMinDamage(int damage, int difficulty);

    void setMaxDamage(int damage);
    void setMaxDamage(int[] damage);
    void setMaxDamage(int damage, int difficulty);

}
