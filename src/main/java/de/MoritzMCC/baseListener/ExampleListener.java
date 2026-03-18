package de.MoritzMCC.baseListener;

import de.MoritzMCC.Main;
import de.MoritzMCC.anntotations.Listen;
import de.MoritzMCC.anntotations.cancelIf;
import de.MoritzMCC.anntotations.impl.PlayerSneakCondition;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExampleListener extends BaseListener{

    public ExampleListener() {
        super();
        Main.getInstance().getLogger().info("Example Listener created");
    }

    @Listen
    public void onJoin(PlayerJoinEvent event) {
        Main.getInstance().getLogger().info("Example Listener joined");
        event.setJoinMessage("HALLO HALLO");
    }

    @Listen
    @cancelIf(condition = PlayerSneakCondition.class)
    public void onMove(PlayerMoveEvent event) {
        // something whatever you want
    }
}
