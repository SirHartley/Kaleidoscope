package kaleidoscope.plugins;

import com.fs.starfarer.api.Global;
import kaleidoscope.ids.Ids;

public class PlanetTextureApplicator {

    public void run(){
        // load textures ect
        // apply to relevant planets, skip core
        // add in support for US
            // for terran tidally locked, adjust the orbit
        // apply relevant conditions if needed
    }

    public void setMemoryKey(){
        Global.getSector().getMemoryWithoutUpdate().set(Ids.KEY_SETUP_COMPLETE, true);
    }
}
