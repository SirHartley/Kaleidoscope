package kaleidoscope.loading;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.InteractionDialogImageVisual;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.loading.specs.PlanetSpec;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageDataEntry {
    public int id;
    public List<String> conditionsToAdd;
    public String imageName;
    public String glowName;
    public String targetPlanetType;
    public String typename;
    public String typeDesc;

    public int tally = 0;

    public ImageDataEntry(int id, List<String> conditionsToAdd, String imageName, String glowName, String targetPlanetType, String typename, String typeDesc) {
        this.id = id;
        this.conditionsToAdd = conditionsToAdd;
        this.imageName = imageName;
        this.glowName = glowName;
        this.targetPlanetType = targetPlanetType;
        this.typename = typename;
        this.typeDesc = typeDesc;
    }

    public boolean matches(PlanetAPI planet){
        String type = planet.getTypeId();
        if (Character.isDigit(type.charAt(type.length()-1))) type = type.substring(0, type.length() - 2);

        return type.equals(targetPlanetType);
    }

    public void applyToPlanet(PlanetAPI planet){
        PlanetSpecAPI spec = planet.getSpec();
        PlanetSpec obfSpec = (PlanetSpec) spec;

        obfSpec.texture = imageName;
        if (glowName != null && !glowName.isEmpty()) {
            if (obfSpec.glowTexture == null) {
                obfSpec.setUseReverseLightForGlow(false);
                obfSpec.setGlowColor(new Color(255,255,255,255));
            }

            obfSpec.glowTexture = glowName;
        }
        if (typename != null && !typename.isEmpty()) obfSpec.name = typename;
        if (typeDesc != null && !typeDesc.isEmpty()) obfSpec.descriptionId = typeDesc;

        for (String s : conditionsToAdd) if (!planet.getMarket().hasCondition(s)) planet.getMarket().addCondition(s);

        planet.applySpecChanges();
    }
}
