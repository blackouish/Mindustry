package io.anuke.mindustry.world.blocks;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.mindustry.graphics.CacheLayer;
import io.anuke.mindustry.world.Pos;
import io.anuke.mindustry.world.Tile;

import static io.anuke.mindustry.Vars.tilesize;
import static io.anuke.mindustry.Vars.world;

public class StaticWall extends Rock{
    TextureRegion large;

    public StaticWall(String name){
        super(name);
        breakable = alwaysReplace = false;
        solid = true;
        cacheLayer = CacheLayer.walls;
    }

    @Override
    public void draw(Tile tile){
        int rx = tile.x / 2 * 2;
        int ry = tile.y / 2 * 2;

        if(Core.atlas.isFound(large) && eq(rx, ry) && Mathf.randomSeed(Pos.get(rx, ry)) < 0.5){
            if(rx == tile.x && ry == tile.y){
                Draw.rect(large, tile.worldx() + tilesize/2f, tile.worldy() + tilesize/2f);
            }
        }else if(variants > 0){
            Draw.rect(regions[Mathf.randomSeed(tile.pos(), 0, Math.max(0, regions.length - 1))], tile.worldx(), tile.worldy());
        }else{
            Draw.rect(region, tile.worldx(), tile.worldy());
        }
    }

    @Override
    public void load(){
        super.load();
        large = Core.atlas.find(name + "-large");
    }

    //two functions for calculating 2x2 tile brightness
    int min(int rx, int ry){
        return Math.min(world.tile(rx + 1, ry).getRotation(), Math.min(world.tile(rx, ry).getRotation(), Math.min(world.tile(rx + 1, ry + 1).getRotation(), world.tile(rx, ry + 1).getRotation())));
    }

    int avg(int rx, int ry){
        return (world.tile(rx + 1, ry).getRotation() + world.tile(rx, ry).getRotation() + world.tile(rx + 1, ry + 1).getRotation() + world.tile(rx, ry + 1).getRotation()) / 4;
    }

    boolean eq(int rx, int ry){
        return world.tile(rx + 1, ry).block() == this
            && world.tile(rx, ry + 1).block() == this
            && world.tile(rx, ry).block() == this
            && world.tile(rx + 1, ry + 1).block() == this;
    }
}
