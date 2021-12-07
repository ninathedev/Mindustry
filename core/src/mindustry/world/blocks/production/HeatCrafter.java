package mindustry.world.blocks.production;

import arc.*;
import arc.math.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/** A crafter that requires contact from heater blocks to craft. */
public class HeatCrafter extends GenericCrafter{
    /** Base heat requirement for 100% efficiency. */
    public float heatRequirement = 10f;
    /** After heat meets this requirement, excess heat will be scaled by this number. */
    public float overheatScale = 1f;
    /** Maximum possible efficiency after overheat. */
    public float maxEfficiency = 4f;

    public HeatCrafter(String name){
        super(name);
    }

    @Override
    public void setBars(){
        super.setBars();

        bars.add("heat", (HeatCrafterBuild entity) ->
            new Bar(() ->
            Core.bundle.format("bar.heatpercent", (int)entity.heat, (int)(entity.efficiencyScale() * 100)),
            () -> Pal.lightOrange,
            () -> entity.heat / heatRequirement));
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.input, heatRequirement, StatUnit.heatUnits);
    }

    public class HeatCrafterBuild extends GenericCrafterBuild{
        //TODO sideHeat could be smooth
        public float[] sideHeat = new float[4];
        public float heat = 0f;

        @Override
        public void updateTile(){
            heat = calculateHeat(sideHeat);

            super.updateTile();
        }

        public float heatRequirement(){
            return heatRequirement;
        }

        @Override
        public float warmupTarget(){
            return Mathf.clamp(heat / heatRequirement);
        }

        @Override
        public float getProgressIncrease(float base){
            return super.getProgressIncrease(base) * efficiencyScale();
        }

        public float efficiencyScale(){
            float over = Math.max(heat - heatRequirement, 0f);
            return Math.min(Mathf.clamp(heat / heatRequirement) + over / heatRequirement * overheatScale, maxEfficiency);
        }
    }
}
