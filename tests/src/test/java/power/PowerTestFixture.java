package power;

import io.anuke.arc.Core;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Time;
import io.anuke.mindustry.Vars;
import io.anuke.mindustry.content.Blocks;
import io.anuke.mindustry.core.ContentLoader;
import io.anuke.mindustry.world.Block;
import io.anuke.mindustry.world.Tile;
import io.anuke.mindustry.world.blocks.PowerBlock;
import io.anuke.mindustry.world.blocks.power.Battery;
import io.anuke.mindustry.world.blocks.power.PowerGenerator;
import io.anuke.mindustry.world.modules.ConsumeModule;
import io.anuke.mindustry.world.modules.ItemModule;
import io.anuke.mindustry.world.modules.LiquidModule;
import io.anuke.mindustry.world.modules.PowerModule;
import org.junit.jupiter.api.BeforeAll;

import java.lang.reflect.Field;

/** This class provides objects commonly used by power related unit tests.
 *  For now, this is a helper with static methods, but this might change.
 *
 *  Note: All tests which subclass this will run with a fixed delta of 0.5!
 * */
public class PowerTestFixture{
    public static final float smallRoundingTolerance = Mathf.FLOAT_ROUNDING_ERROR;
    public static final float mediumRoundingTolerance = Mathf.FLOAT_ROUNDING_ERROR * 10;
    public static final float highRoundingTolerance = Mathf.FLOAT_ROUNDING_ERROR * 100;

    @BeforeAll
    static void initializeDependencies(){
        Core.graphics = new FakeGraphics();
        Vars.content = new ContentLoader();
        Vars.content.load();
        Time.setDeltaProvider(() -> 0.5f);
    }

    protected static PowerGenerator createFakeProducerBlock(float producedPower){
        return new PowerGenerator("fakegen"){{
            powerProduction = producedPower;
        }};
    }

    protected static Battery createFakeBattery(float capacity, float ticksToFill){
        return new Battery("fakebattery"){{
            consumes.powerBuffered(capacity, ticksToFill);
        }};
    }

    protected static Block createFakeDirectConsumer(float powerPerTick){
        return new PowerBlock("fakedirectconsumer"){{
            consumes.power(powerPerTick);
        }};
    }

    protected static Block createFakeBufferedConsumer(float capacity, float ticksToFill){
        return new PowerBlock("fakebufferedconsumer"){{
            consumes.powerBuffered(capacity, ticksToFill);
        }};
    }
    /**
     * Creates a fake tile on the given location using the given block.
     * @param x     The X coordinate.
     * @param y     The y coordinate.
     * @param block The block on the tile.
     * @return The created tile or null in case of exceptions.
     */
    protected static Tile createFakeTile(int x, int y, Block block){
        try{
            Tile tile = new Tile(x, y);

            // Using the Tile(int, int, byte, byte) constructor would require us to register any fake block or tile we create
            // Since this part shall not be part of the test and would require more work anyway, we manually set the block and floor
            // through reflections and then simulate part of what the changed() method does.

            Field field = Tile.class.getDeclaredField("wall");
            field.setAccessible(true);
            field.set(tile, block);

            field = Tile.class.getDeclaredField("floor");
            field.setAccessible(true);
            field.set(tile, Blocks.sand);

            // Simulate the "changed" method. Calling it through reflections would require half the game to be initialized.
            tile.entity = block.newEntity().init(tile, false);
            tile.entity.cons = new ConsumeModule();
            if(block.hasItems) tile.entity.items = new ItemModule();
            if(block.hasLiquids) tile.entity.liquids = new LiquidModule();
            if(block.hasPower){
                tile.entity.power = new PowerModule();
                tile.entity.power.graph.add(tile);
            }

            // Assign incredibly high health so the block does not get destroyed on e.g. burning Blast Compound
            block.health = 100000;
            tile.entity.health = 100000.0f;

            return tile;
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
