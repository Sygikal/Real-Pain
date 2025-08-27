package dev.sygii.realpain;

import com.google.gson.JsonObject;
import dev.sygii.realpain.body.BodyPart;
import dev.sygii.realpain.body.damage.DamageDistribution;
import dev.sygii.realpain.body.damage.distribution.RandomAllDamageDistribution;
import dev.sygii.realpain.body.damage.distribution.RandomDamageDistribution;
import dev.sygii.realpain.body.damage.distribution.SplitAllDamageDistribution;
import dev.sygii.realpain.body.damage.distribution.SplitDamageDistribution;
import dev.sygii.realpain.body.entity.PlayerBody;
import dev.sygii.realpain.config.MainConfig;
import dev.sygii.realpain.content.*;
import dev.sygii.realpain.content.effect.MorphineStatusEffect;
import dev.sygii.realpain.content.effect.OverdoseStatusEffect;
import dev.sygii.realpain.content.item.BandageItem;
import dev.sygii.realpain.content.item.MedkitItem;
import dev.sygii.realpain.content.item.MorphineItem;
import dev.sygii.realpain.data.DamageDistributionLoader;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainMain implements ModInitializer {
	public static final String MOD_ID = "realpain";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Map<DamageSource, List<BodyPart>> sourceToPartMap = new HashMap<>();

	public static final List<DamageDistribution> damageDistributionMap = new ArrayList<>();
	//public static final Map<Identifier, DamageDistribution> damageDistributionMap = new HashMap<>();

	public static final Creator<JsonObject, DamageDistribution> damageDistributionCreator = new Creator<JsonObject, DamageDistribution>();


	public static MainConfig MAIN = ConfigApiJava.registerAndLoadConfig(MainConfig::new);

	public static final RegistryKey<ItemGroup> PAIN_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, id("items"));

	public static final Item MORPHINE_ITEM = new MorphineItem(new FabricItemSettings());
	public static final Item BANDAGE_ITEM = new BandageItem(new FabricItemSettings());
	public static final Item MEDICINAL_HERB = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(1).saturationModifier(0.1F).snack().alwaysEdible().build()));
	public static final Item MEDKIT_ITEM = new MedkitItem(new FabricItemSettings().maxCount(1));

	public static final StatusEffect MORPHINE_EFFECT = new MorphineStatusEffect();
	public static final StatusEffect OVERDOSE_EFFECT = new OverdoseStatusEffect();
	public static final OverdoseCriterion OVERDOSE_CRITERION = Criteria.register(new OverdoseCriterion());
	public static final MorphineCriterion MORPHINE_CRITERION = Criteria.register(new MorphineCriterion());

	public static final RegistryKey<DamageType> OVERDOSE_DAMAGE_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, id("overdose"));

	public static DamageSource of(World world, RegistryKey<DamageType> key) {
		return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
	}

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new DamageDistributionLoader());

		Registry.register(Registries.ITEM_GROUP, PAIN_GROUP, FabricItemGroup.builder()
				.icon(() -> new ItemStack(MORPHINE_ITEM))
				.displayName(Text.translatable("realpain.items"))
				.entries((features, entries) -> {
					entries.add(MORPHINE_ITEM);
					entries.add(BANDAGE_ITEM);
					entries.add(MEDICINAL_HERB);
					entries.add(MEDKIT_ITEM);
				})
				.build());

		damageDistributionCreator.registerCreator(SplitDamageDistribution.ID, data -> new SplitDamageDistribution());
		damageDistributionCreator.registerCreator(RandomDamageDistribution.ID, data -> new RandomDamageDistribution());
		damageDistributionCreator.registerCreator(RandomAllDamageDistribution.ID, data -> new RandomAllDamageDistribution());
		damageDistributionCreator.registerCreator(SplitAllDamageDistribution.ID, data -> new SplitAllDamageDistribution());

		Registry.register(Registries.ITEM, id("morphine"), MORPHINE_ITEM);
		Registry.register(Registries.ITEM, id("bandage"), BANDAGE_ITEM);
		Registry.register(Registries.ITEM, id("medicinal_herb"), MEDICINAL_HERB);
		Registry.register(Registries.ITEM, id("medkit"), MEDKIT_ITEM);


		Registry.register(Registries.STATUS_EFFECT, id("morphine"), MORPHINE_EFFECT);
		Registry.register(Registries.STATUS_EFFECT, id("overdosed"), OVERDOSE_EFFECT);

		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			PlayerBody body = PainAttachments.body.get(newPlayer);
			PainAttachments.body.syncFromServer(newPlayer, newPlayer, body);
		});

		/*ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerBody body = PainAttachments.body.get(handler.getPlayer());
			PainAttachments.body.syncFromServer(handler.getPlayer(), handler.getPlayer(), body);
		});*/


		/*Registry.register(Registries.ITEM_GROUP, PAIN_GROUP, FabricItemGroup.builder()
				.icon(() -> new ItemStack(MORPHINE_ITEM))
				.displayName(Text.translatable("realpain.items"))
						.entries((features, entries) -> {
							entries.add(MORPHINE_ITEM);
						})
				.build());*/

		//Registry.register(Registries.ITEM, id("bandage"), MORPHINE_ITEM);
		//Registry.register(Registries.ITEM, id("bandage"), MEDKIT_ITEM);

		/*ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerBody body = PainAttachments.body.get(handler.player);
			PainAttachments.body.syncFromServer(handler.player, handler.player, body);
		});*/
	}

	public static void log(String s) {
		LOGGER.info(s);
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	public static int incrementEffect(LivingEntity entity, StatusEffect effect, int duration) {
		int overdoseAmp = 0;
		if (entity.hasStatusEffect(effect)) {
			overdoseAmp = entity.getStatusEffect(effect).getAmplifier() + 1;
		}
		entity.addStatusEffect(new StatusEffectInstance(effect, duration, overdoseAmp));
		return overdoseAmp;
	}

	public static int incrementEffect(LivingEntity entity, StatusEffect effect, int duration, int overdoseAmp) {
		if (overdoseAmp >= 0) {
			if (entity.hasStatusEffect(effect)) {
				overdoseAmp += entity.getStatusEffect(effect).getAmplifier() + 1;
				if (entity.getStatusEffect(effect).getDuration() < 5) {
					entity.addStatusEffect(new StatusEffectInstance(effect, duration, overdoseAmp));
				}
			}else {
				entity.addStatusEffect(new StatusEffectInstance(effect, duration, overdoseAmp));
			}
			return overdoseAmp;
		}
		return 0;
	}
}
