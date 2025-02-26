package kaleidoscope.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.loading.specs.PlanetSpec;
import kaleidoscope.ids.Ids;
import org.codehaus.janino.Mod;

import java.util.ArrayList;
import java.util.List;

public class PlanetTextureApplicator {

    public void run(){
        // load textures ect
        // apply to relevant planets, skip core
        // add in support for US
            // for terran tidally locked, adjust the orbit
        // apply relevant conditions if needed

        List<PlanetAPI> planetList = new ArrayList<>();
        for (LocationAPI loc : Global.getSector().getAllLocations()) planetList.addAll(loc.getPlanets());

        for (PlanetAPI p : planetList){

            ModPlugin.log(p.getTypeId() + ";" + p.getTypeNameWithWorld() + ";" + p.getSpec().getTexture());

            PlanetSpecAPI spec = p.getSpec();
            PlanetSpec obfSpec = (PlanetSpec) spec;
            obfSpec.name = "Test";
            p.applySpecChanges();
        }

        setMemoryKey();
    }

    public void setMemoryKey(){
        Global.getSector().getMemoryWithoutUpdate().set(Ids.KEY_SETUP_COMPLETE, true);
    }
}
