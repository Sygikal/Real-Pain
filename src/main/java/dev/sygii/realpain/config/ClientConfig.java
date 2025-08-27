package dev.sygii.realpain.config;


import dev.sygii.realpain.PainMain;
import me.fzzyhmstrs.fzzy_config.annotations.Action;
import me.fzzyhmstrs.fzzy_config.annotations.Comment;
import me.fzzyhmstrs.fzzy_config.annotations.RequiresAction;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedFloat;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedInt;
import me.fzzyhmstrs.fzzy_config.validation.number.ValidatedNumber;

public class ClientConfig extends Config {

    public ClientConfig() {
        super(PainMain.id("client_config"));
    }

    @Comment("Whether the GUI should render player model rather than boxes")
    public boolean renderModel = false;

    @Comment("Whether the GUI should render debug part info")
    public boolean debug = false;

    public ValidatedInt guiX = new ValidatedInt(20, 1024, 0);


    public ValidatedInt guiY = new ValidatedInt(26, 1024, 0);

    public ValidatedInt guiScale = new ValidatedInt(3, 32, 0);

}
