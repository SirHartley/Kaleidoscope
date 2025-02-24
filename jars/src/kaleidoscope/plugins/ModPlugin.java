package kaleidoscope.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.campaign.rules.Memory;
import kaleidoscope.ids.Ids;
import kaleidoscope.listener.CargoStackAvailabilityIconProvider;

public class ModPlugin extends BaseModPlugin {
    @Override
    public void onGameLoad(boolean newGame) {
        super.onGameLoad(newGame);

        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        if (!memory.getBoolean(Ids.KEY_SETUP_COMPLETE)){
            PlanetTextureApplicator applicator = new PlanetTextureApplicator();
            applicator.run();
        }
    }

    @Override
    public void onNewGameAfterProcGen() {
        super.onNewGameAfterProcGen();

        PlanetTextureApplicator applicator = new PlanetTextureApplicator();
        applicator.run();
    }
}
