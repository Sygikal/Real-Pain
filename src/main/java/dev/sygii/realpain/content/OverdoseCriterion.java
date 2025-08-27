package dev.sygii.realpain.content;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.sygii.realpain.PainMain;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class OverdoseCriterion extends AbstractCriterion<OverdoseCriterion.Conditions> {
    private static final Identifier ID = PainMain.id("overdose_amount");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions conditionsFromJson(JsonObject jsonObject, LootContextPredicate lootContextPredicate, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        return new Conditions(lootContextPredicate, jsonObject.get("level"));
    }

    public void trigger(ServerPlayerEntity player, int amount) {
        this.trigger(player, conditions -> conditions.matches(amount));
    }

    public class Conditions extends AbstractCriterionConditions {

        private final JsonElement amount;

        public Conditions(LootContextPredicate lootContextPredicate, JsonElement amount) {
            super(ID, lootContextPredicate);
            this.amount = amount;
        }

        public boolean matches(int amount) {
            return amount >= JsonHelper.asInt(this.amount, "level");
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.add("level", this.amount);
            return jsonObject;
        }
    }

}
