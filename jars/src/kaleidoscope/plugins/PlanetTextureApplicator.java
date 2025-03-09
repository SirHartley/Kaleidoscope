package kaleidoscope.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import kaleidoscope.ids.Ids;
import kaleidoscope.loading.ImageDataEntry;
import kaleidoscope.loading.Importer;

import java.util.*;

public class PlanetTextureApplicator {

    List<ImageDataEntry> entries = new ArrayList<>();

    public void run(){
        entries = Importer.loadImageData();

        List<PlanetAPI> planetList = new ArrayList<>();
        for (LocationAPI loc : Global.getSector().getAllLocations()) {
            if (loc.getPlanets().isEmpty()
                    || !Misc.getMarketsInLocation(loc).isEmpty()
                    || loc.hasTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)
                    || loc.isHyperspace()
                    || loc.hasTag(Tags.THEME_HIDDEN)
                    || loc.hasTag(Tags.THEME_SPECIAL)
                    || loc.hasTag(Tags.SYSTEM_ABYSSAL)) continue;

            planetList.addAll(loc.getPlanets());
        }

        for (PlanetAPI p : planetList){

            ModPlugin.log(p.getTypeId() + ";" + p.getTypeNameWithWorld() + ";" + p.getSpec().getTexture());

            List<ImageDataEntry> fittingEntries = new ArrayList<>();
            for (ImageDataEntry e : entries) if (e.matches(p)) fittingEntries.add(e);

            WeightedRandomPicker<ImageDataEntry> dataPicker = new WeightedRandomPicker<>();
            dataPicker.addAll(fittingEntries);
            dataPicker.add(null);

            ImageDataEntry e = dataPicker.pick();
            if (e != null) e.applyToPlanet(p);
        }

        setMemoryKey();
    }

    public void setMemoryKey(){
        Global.getSector().getMemoryWithoutUpdate().set(Ids.KEY_SETUP_COMPLETE, true);
    }
    
    public static void addResourceCondition(PlanetAPI planet, String conditionToAdd){
        HashMap<String, List<String>> resourceConditionMap = new HashMap<>();

        resourceConditionMap.put("ore", new ArrayList<>(Arrays.asList(Conditions.ORE_SPARSE, Conditions.ORE_MODERATE, Conditions.ORE_ABUNDANT, Conditions.ORE_RICH, Conditions.ORE_ULTRARICH)));
        resourceConditionMap.put("rare", new ArrayList<>(Arrays.asList(Conditions.RARE_ORE_SPARSE, Conditions.RARE_ORE_MODERATE, Conditions.RARE_ORE_ABUNDANT, Conditions.RARE_ORE_RICH, Conditions.RARE_ORE_ULTRARICH)));
        resourceConditionMap.put("organics", new ArrayList<>(Arrays.asList(Conditions.ORGANICS_TRACE, Conditions.ORGANICS_COMMON, Conditions.ORGANICS_ABUNDANT, Conditions.ORGANICS_PLENTIFUL)));
        resourceConditionMap.put("volatiles", new ArrayList<>(Arrays.asList(Conditions.VOLATILES_TRACE, Conditions.VOLATILES_DIFFUSE, Conditions.VOLATILES_ABUNDANT, Conditions.VOLATILES_PLENTIFUL)));
        resourceConditionMap.put("farmland", new ArrayList<>(Arrays.asList(Conditions.FARMLAND_POOR, Conditions.FARMLAND_ADEQUATE, Conditions.FARMLAND_RICH, Conditions.FARMLAND_BOUNTIFUL)));

        for (Map.Entry<String, List<String>> e : resourceConditionMap.entrySet()){
            if (conditionToAdd.startsWith(e.getKey())) {
                MarketAPI m = planet.getMarket();
                for (String s : e.getValue()) m.removeCondition(s);
                m.addCondition(conditionToAdd);
                break;
            }
        }
    }
}
