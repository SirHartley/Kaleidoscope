package kaleidoscope.loading;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.loading.specs.PlanetSpec;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.List;

public class ImageDataEntry {
    public int id;
    public List<String> conditionsToAdd;
    public String imageName;
    public String glowName;
    public String cloudName;
    public String targetPlanetType;
    public String typeName;
    public String typeDesc;

    public int tally = 0;

    public ImageDataEntry(int id, List<String> conditionsToAdd, String imageName, String glowName, String cloudName, String targetPlanetType, String typename, String typeDesc) {
        this.id = id;
        this.conditionsToAdd = conditionsToAdd;
        this.imageName = imageName;
        this.glowName = glowName;
        this.cloudName = cloudName;
        this.targetPlanetType = targetPlanetType;
        this.typeName = typename;
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
        if (glowName != null && !glowName.isEmpty()) obfSpec.glowTexture = glowName;
        if (typeName != null && !typeName.isEmpty()) obfSpec.name = typeName;
        if (typeDesc != null && !typeDesc.isEmpty()) obfSpec.descriptionId = typeDesc;

        if (cloudName != null && !cloudName.isEmpty()) {
            if (obfSpec.cloudAlpha < 0.3f) obfSpec.cloudAlpha = 0.7f;
            if (obfSpec.cloudColor == null) obfSpec.cloudColor = new Color(255,255,255,255);
            obfSpec.cloudTexture = cloudName;
        }

        for (String s : conditionsToAdd) if (!planet.getMarket().hasCondition(s)) planet.getMarket().addCondition(s);

        obfSpec.pitch = MathUtils.getRandomNumberInRange(-15f, 15f);

        if (Global.getSettings().isDevMode()) obfSpec.name = id + " - " + obfSpec.name + " - t/p: " + obfSpec.tilt + "/" + obfSpec.pitch + " cloud: " + obfSpec.cloudTexture + " alpha / col" + obfSpec.cloudAlpha + " / " + obfSpec.cloudColor;

        planet.applySpecChanges();
    }
}
