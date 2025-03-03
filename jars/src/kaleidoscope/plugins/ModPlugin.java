package kaleidoscope.plugins;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import kaleidoscope.ids.Ids;
import kaleidoscope.loading.ImageDataEntry;
import kaleidoscope.loading.Importer;
import org.apache.log4j.Logger;

import java.io.IOException;

public class ModPlugin extends BaseModPlugin {
    public static Logger log = Global.getLogger(ModPlugin.class);

    public static final String CSV_PATH = "data/config/planet_texture_data.csv";
    public static final String MOD_ID = "kaleidoscope";

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
    }

    @Override
    public void onNewGameAfterProcGen() {
        super.onNewGameAfterProcGen();

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
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
