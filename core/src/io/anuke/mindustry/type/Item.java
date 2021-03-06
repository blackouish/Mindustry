package io.anuke.mindustry.type;

import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.scene.ui.layout.Table;
import io.anuke.arc.util.Log;
import io.anuke.arc.util.Strings;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.game.UnlockableContent;
import io.anuke.mindustry.graphics.Pal;
import io.anuke.mindustry.ui.ContentDisplay;

public class Item extends UnlockableContent implements Comparable<Item>{
    public final String name;
    public final String description;
    public final Color color;
    public TextureRegion region;

    /**type of the item; used for tabs and core acceptance. default value is {@link ItemType#resource}.*/
    public ItemType type = ItemType.resource;
    /**how explosive this item is.*/
    public float explosiveness = 0f;
    /**flammability above 0.3 makes this eleigible for item burners.*/
    public float flammability = 0f;
    /**how radioactive this item is. 0=none, 1=chernobyl ground zero*/
    public float radioactivity;
    /**drill hardness of the item*/
    public int hardness = 0;
    /**the burning color of this item. TODO unused; implement*/
    public Color flameColor = Pal.darkFlame.cpy();
    /**
     * base material cost of this item, used for calculating place times
     * 1 cost = 1 tick added to build time
     */
    public float cost = 3f;
    /**Whether this item has ores generated for it.*/
    public boolean genOre = false;
    /**If true, item is always unlocked.*/
    public boolean alwaysUnlocked = false;

    public Item(String name, Color color){
        this.name = name;
        this.color = color;
        this.description = Core.bundle.getOrNull("item." + this.name + ".description");

        if(!Core.bundle.has("item." + this.name + ".name")){
            Log.err("Warning: item '" + name + "' is missing a localized name. Add the following to bundle.properties:");
            Log.err("item." + this.name + ".name = " + Strings.capitalize(name.replace('-', '_')));
        }
    }

    public void load(){
        this.region = Core.atlas.find("item-" + name);
    }

    @Override
    public boolean alwaysUnlocked() {
        return alwaysUnlocked;
    }

    @Override
    public void displayInfo(Table table){
        ContentDisplay.displayItem(table, this);
    }

    @Override
    public String localizedName(){
        return Core.bundle.get("item." + this.name + ".name");
    }

    @Override
    public TextureRegion getContentIcon(){
        return region;
    }

    @Override
    public String toString(){
        return localizedName();
    }

    @Override
    public int compareTo(Item item){
        return Integer.compare(id, item.id);
    }

    @Override
    public String getContentName(){
        return name;
    }

    @Override
    public ContentType getContentType(){
        return ContentType.item;
    }

    /**Allocates a new array containing all items the generate ores.*/
    public static Array<Item> getAllOres(){
        return Vars.content.items().select(i -> i.genOre);
    }
}
