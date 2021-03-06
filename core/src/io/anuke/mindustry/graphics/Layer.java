package io.anuke.mindustry.graphics;

public enum Layer{
    /**Drawn under everything.*/
    shadow,
    /**Base block layer.*/
    block,
    /**for placement*/
    placement,
    /**First overlay. Stuff like conveyor items.*/
    overlay,
    /**"High" blocks, like turrets.*/
    turret,
    /**Power lasers.*/
    power,
    /**Extra lasers, like healing turrets.*/
    laser
}
