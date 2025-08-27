package dev.sygii.realpain.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.sygii.realpain.PainMain;
import dev.sygii.realpain.body.damage.DamageDistribution;
import dev.sygii.realpain.body.damage.distribution.MeleeDamageDistribution;
import dev.sygii.realpain.body.damage.distribution.ProjectileDamageDistribution;
import dev.sygii.realpain.body.debuff.TickedDebuff;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class DamageDistributionLoader implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return PainMain.id("damage_distribution_loader");
    }

    @Override
    public void reload(ResourceManager manager) {
        PainMain.damageDistributionMap.clear();
        PainMain.damageDistributionMap.add(new ProjectileDamageDistribution());
        manager.findResources("distribution", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = null;
                stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                String fileName = id.getPath().replace("distribution/", "").replace(".json", "");
                Identifier distFileId = Identifier.of(id.getNamespace(), fileName);

                //Identifier damageSourceId = Identifier.tryParse(data.get("source").getAsString());

                //JsonObject distribution = data.get("distribution").getAsJsonObject();

                Identifier distributionType = Identifier.tryParse(data.get("type").getAsString());
                DamageDistribution damageDistribution = PainMain.damageDistributionCreator.create(distributionType, data);
                damageDistribution.setDamageSource(data.get("source").getAsString());
                if (data.has("parts")) {
                    for (JsonElement part : data.getAsJsonArray("parts")) {
                        damageDistribution.addBodyPart(Identifier.tryParse(part.getAsString()));
                    }
                }

                PainMain.damageDistributionMap.add(damageDistribution);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        PainMain.damageDistributionMap.add(new MeleeDamageDistribution());
    }
}
