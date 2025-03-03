package kaleidoscope.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import com.fs.starfarer.loading.specs.PlanetSpec;
import kaleidoscope.ids.Ids;
import kaleidoscope.loading.ImageDataEntry;
import kaleidoscope.loading.Importer;
import org.codehaus.janino.Mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlanetTextureApplicator {

    List<ImageDataEntry> entries = new ArrayList<>();

    public void run(){
        // load textures ect
        // apply to relevant planets, skip core
        // add in support for US
            // for terran tidally locked, adjust the orbit
        // apply relevant conditions if needed

        entries = Importer.loadImageData();

        List<PlanetAPI> planetList = new ArrayList<>();
        for (LocationAPI loc : Global.getSector().getAllLocations()) planetList.addAll(loc.getPlanets());

        for (PlanetAPI p : planetList){

            ModPlugin.log(p.getTypeId() + ";" + p.getTypeNameWithWorld() + ";" + p.getSpec().getTexture());

            //get all planets w/ types (remove num on end if num)
            //skip core
            //tally total textures
            //tally totals with this mod and assign chance based on total num of textures for equal distribution
            //assign tex, name, desc

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
}
