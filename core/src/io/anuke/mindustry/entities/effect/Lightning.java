package io.anuke.mindustry.entities.effect;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import io.anuke.mindustry.content.StatusEffects;
import io.anuke.mindustry.entities.Units;
import io.anuke.mindustry.game.Team;
import io.anuke.mindustry.graphics.Palette;
import io.anuke.ucore.core.Effects;
import io.anuke.ucore.core.Effects.Effect;
import io.anuke.ucore.entities.EntityGroup;
import io.anuke.ucore.entities.trait.DrawTrait;
import io.anuke.ucore.entities.trait.SolidTrait;
import io.anuke.ucore.entities.impl.TimedEntity;
import io.anuke.ucore.graphics.Draw;
import io.anuke.ucore.graphics.Lines;
import io.anuke.ucore.util.Angles;
import io.anuke.ucore.util.Mathf;

import static io.anuke.mindustry.Vars.bulletGroup;

public class Lightning extends TimedEntity implements Poolable, DrawTrait{
    private static Array<SolidTrait> entities = new Array<>();
    private static Rectangle rect = new Rectangle();
    private static Rectangle hitrect = new Rectangle();
    private static float angle;
    private static float wetDamageMultiplier = 2;

    private Array<Vector2> lines = new Array<>();

    public Color color = Palette.lancerLaser;

    /**Create a lighting branch at a location. Use Team.none to damage everyone.*/
    public static void create(Team team, Effect effect, Color color, float damage, float x, float y, float targetAngle, int length){
        Lightning l = Pools.obtain(Lightning.class);

        l.x = x;
        l.y = y;
        l.color = color;

        float step = 3f;
        float range = 6f;
        float attractRange = 20f;

        angle = targetAngle;
        entities.clear();

        Units.getNearbyEnemies(team, rect, entities::add);

        for(int i = 0; i < length; i ++){
            l.lines.add(new Vector2(x, y));

            float fx = x, fy = y;
            float x2 = x + Angles.trnsx(angle, step);
            float y2 = y + Angles.trnsy(angle, step);
            float fangle = angle;
            angle += Mathf.range(30f);

            rect.setSize(attractRange).setCenter(x, y);

            Units.getNearbyEnemies(team, rect, entity -> {
                float dst = entity.distanceTo(x2, y2);
                if(dst < attractRange) {
                    angle = Mathf.slerp(angle, Angles.angle(x2, y2, entity.x, entity.y), (attractRange - dst) / attractRange / 4f);
                }

                entity.getHitbox(hitrect);
                hitrect.x -= range/2f;
                hitrect.y -= range/2f;
                hitrect.width += range/2f;
                hitrect.height += range/2f;

                if(hitrect.contains(x2, y2) || hitrect.contains(fx, fy)){
                    float result = damage;

                    if(entity.getStatus() == StatusEffects.wet)
                        result = (result * wetDamageMultiplier);

                    entity.damage(result);
                    Effects.effect(effect, x2, y2, fangle);
                }
            });

            if(Mathf.chance(0.1)){
                Lightning.create(team, effect, color, damage, x2, y2, angle + Mathf.range(100f), length/3);
            }

            x = x2;
            y = y2;
        }

        l.lines.add(new Vector2(x, y));
        l.add();
    }

    /**For pooling use only. Do not call directly!*/
    public Lightning(){}

    @Override
    public float lifetime() {
        return 10;
    }

    @Override
    public void reset() {
        color = Palette.lancerLaser;
        lines.clear();
    }

    @Override
    public void draw() {
        float lx = x, ly = y;
        Draw.color(color, Color.WHITE, fin());
        for(int i = 0; i < lines.size; i ++){
            Vector2 v = lines.get(i);
            Lines.stroke(fout() * 3f + 1f-(float)i/lines.size);
            Lines.line(lx, ly, v.x, v.y);
            lx = v.x;
            ly = v.y;
        }
        Draw.color();
    }

    @Override
    public EntityGroup targetGroup() {
        return bulletGroup;
    }
}