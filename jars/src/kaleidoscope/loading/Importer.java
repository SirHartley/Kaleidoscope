package kaleidoscope.loading;

import com.fs.starfarer.api.Global;
import kaleidoscope.plugins.ModPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Importer {

    public static List<ImageDataEntry> loadImageData(){
        List<ImageDataEntry> dataMap = new ArrayList<>();

        try {
            JSONArray config = Global.getSettings().getMergedSpreadsheetDataForMod("id", ModPlugin.CSV_PATH, ModPlugin.MOD_ID);
            for (int i = 0; i < config.length(); i++) {

                JSONObject row = config.getJSONObject(i);
                int id = row.getInt("id");
                String imageName = row.getString("image_name").replaceAll("\\s", "");;
                String glowName = row.getString("glow_name").replaceAll("\\s", "");;
                String planetType = row.getString("planet_type").replaceAll("\\s", "");;
                String typeName = row.getString("type_name");
                String descId = row.getString("desc").replaceAll("\\s", "");;

                List<String> conditionsToAdd = new ArrayList<>();

                for (String s : row.getString("force_condition").split("\\s+")){
                    s = s.replaceAll("\\s", "");
                    if (s.length() < 3) continue;
                    conditionsToAdd.add(s);
                }
                ImageDataEntry imageEntry = new ImageDataEntry(
                        id,
                        conditionsToAdd,
                        imageName,
                        glowName,
                        planetType,
                        typeName,
                        descId);

                dataMap.add(imageEntry);
            }
        } catch (IOException | JSONException ex) {
            ModPlugin.log.error("Could not find Kaleidoscope planet_texture_data, or something is wrong with the data format.", ex);
        }

       return dataMap;
    }
}
