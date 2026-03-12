package io.github.techtastic.hycomputeextras;

import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.techtastic.hycomputeextras.commands.ExampleCommand;
import io.github.techtastic.hycomputeextras.events.ExampleEvent;

import javax.annotation.Nonnull;

public class HyComputeExtras extends JavaPlugin {

    public HyComputeExtras(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);

        this.getChunkStoreRegistry().registerSystem(new TempSystems.OnComputerPlaced());
    }
}