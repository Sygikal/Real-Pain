package dev.sygii.realpain.config;


import dev.sygii.realpain.PainMain;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.config.ConfigSection;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;

public class MainConfig extends Config {

    public MainConfig() {
        super(PainMain.id("main_config"));
    }

    public HealthSection PARTS = new HealthSection();
    public static class HealthSection extends ConfigSection {
        public float headWeight = 5;
        public boolean headFatal = true;
        public float torsoWeight = 5;
        public boolean torsoFatal = true;
        public float armWeight = 1;
        public float legWeight = 2;
    }

    public ItemSection ITEMS = new ItemSection();
    public static class ItemSection extends ConfigSection {
        public int morphineUseTime = 32;
        public int morphineDuration = 3600;
        public boolean additionalMorphineTime = true;
        public int additionalMorphineTimeModifier = 1000;
        public boolean allowOverdosing = true;

        public int bandageUseTime = 36;
        public float bandageHealAmount = 3f;
        public ValidatedFloat bandageThreshold = new ValidatedFloat(0.5f, 1f, 0f);

        public int medkitMaxUses = 3;


    }

}
