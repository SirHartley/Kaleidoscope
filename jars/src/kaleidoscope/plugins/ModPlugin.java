package kaleidoscope.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import kaleidoscope.ids.Ids;
import kaleidoscope.loading.ImageDataEntry;
import kaleidoscope.loading.Importer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModPlugin extends BaseModPlugin {
    public static Logger log = Global.getLogger(ModPlugin.class);

    public static void log(String Text) {
        if (Global.getSettings().isDevMode()) Global.getLogger(ModPlugin.class).info(Text);
    }

    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);

        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.getBoolean(Ids.KEY_SETUP_COMPLETE)){
            PlanetTextureApplicator applicator = new PlanetTextureApplicator();
            applicator.run();
        }

        if (!memory.getBoolean(Ids.KEY_1_0_B_FIX)){
            for (LocationAPI loc : Global.getSector().getAllLocations()){
                for (PlanetAPI p : loc.getPlanets()){
                    if (p.getSpec().getTexture().contains("liquidmetal01")) {
                        PlanetTextureApplicator.addResourceCondition(p, Conditions.ORE_ULTRARICH);
                        PlanetTextureApplicator.addResourceCondition(p, Conditions.RARE_ORE_ULTRARICH);
                    }
                }
            }
        }

        if (Global.getSettings().isDevMode()){
            spawnSuperSystem();
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        super.onNewGameAfterEconomyLoad();

        PlanetTextureApplicator applicator = new PlanetTextureApplicator();
        applicator.run();
    }

    @Override
    public void onApplicationLoad() throws Exception {
        super.onApplicationLoad();

        for (ImageDataEntry e : Importer.loadImageData()){
            try {
                Global.getSettings().loadTexture(e.imageName);
                if (e.glowName != null && !e.glowName.isEmpty()) Global.getSettings().loadTexture(e.glowName);
                if (e.cloudName != null && !e.cloudName.isEmpty()) Global.getSettings().loadTexture(e.cloudName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * All of this is just for debugging and polish - not used in a normal game
     */

    public void spawnSuperSystem(){
        List<PlanetAPI> planetList = new ArrayList<>();
        List<PlanetAPI> planetToMoveList = new ArrayList<>();
        List<ImageDataEntry> entries = Importer.loadImageData();

        for (LocationAPI loc : Global.getSector().getAllLocations()) {
            planetList.addAll(loc.getPlanets());
        }

        for (PlanetAPI p : planetList) {
            for (ImageDataEntry entry : entries) {
                if (entry.tally < 1 && entry.imageName.equals(p.getSpec().getTexture())){
                    planetToMoveList.add(p);
                    entry.tally++;
                }
            }
        }

        StarSystemAPI loc = Global.getSector().getStarSystem("Duzahk");

        float currentOrbitRadius = 3500;
        int planetIndex = 0;

        // Change condition to '<' so we never exceed the last index.
        while (planetIndex < planetToMoveList.size()) {
            float circumference = (float) (2 * Math.PI * currentOrbitRadius);
            int sections = (int) Math.floor(circumference / 800f);

            // Prevent division by zero.
            if (sections == 0) break;

            float angleIncrement = 360f / sections;
            float currentAngle = 0f;

            // Ensure that we do not go out of bounds in the inner loop.
            for (int i = 0; i < sections && planetIndex < planetToMoveList.size(); i++){
                PlanetAPI p = planetToMoveList.get(planetIndex);
                float orbitPeriod = 365f;

                PlanetAPI newPlanet = loc.addPlanet(p.getId(), loc.getCenter(), p.getName(), p.getTypeId(), currentAngle, p.getRadius(), currentOrbitRadius, orbitPeriod);

                //make planet look the same
                copySpecData(newPlanet.getSpec(), p.getSpec());
                newPlanet.applySpecChanges();

                for (ImageDataEntry entry : entries) if (entry.imageName.equals(p.getSpec().getTexture())) entry.applyToPlanet(newPlanet);

                //prepare new planet for player interactions
                newPlanet.getMemoryWithoutUpdate().set("$isSurveyed", true);
                newPlanet.getMemoryWithoutUpdate().set("$hasUnexploredRuins", false);
                newPlanet.getMemoryWithoutUpdate().set("$isPlanetConditionMarketOnly", false);

                planetIndex++;
                currentAngle += angleIncrement;
            }

            // Increase the orbit radius for the next circle.
            currentOrbitRadius += 2000f;
        }
    }

    private void copySpecData(PlanetSpecAPI newSpec, PlanetSpecAPI oldSpec) {
        newSpec.setAtmosphereColor(oldSpec.getAtmosphereColor());
        newSpec.setAtmosphereThickness(oldSpec.getAtmosphereThickness());
        newSpec.setAtmosphereThicknessMin(oldSpec.getAtmosphereThicknessMin());

        newSpec.setCloudColor(oldSpec.getCloudColor());
        newSpec.setCloudRotation(oldSpec.getCloudRotation());
        newSpec.setCloudTexture(oldSpec.getCloudTexture());

        newSpec.setCoronaColor(oldSpec.getCoronaColor());
        newSpec.setCoronaSize(oldSpec.getCoronaSize());
        newSpec.setCoronaTexture(oldSpec.getCoronaTexture());

        newSpec.setGlowColor(oldSpec.getGlowColor());
        newSpec.setGlowTexture(oldSpec.getGlowTexture());

        newSpec.setIconColor(oldSpec.getIconColor());

        newSpec.setPitch(oldSpec.getPitch());
        newSpec.setPlanetColor(oldSpec.getPlanetColor());
        newSpec.setRotation(oldSpec.getRotation());
        newSpec.setTexture(oldSpec.getTexture());
        newSpec.setTilt(oldSpec.getTilt());
        newSpec.setUseReverseLightForGlow(oldSpec.isUseReverseLightForGlow());

        newSpec.setScaleMultMapIcon(oldSpec.getScaleMultMapIcon());
        newSpec.setScaleMultStarscapeIcon(oldSpec.getScaleMultStarscapeIcon());
        newSpec.setStarscapeIcon(oldSpec.getStarscapeIcon());
    }

}
