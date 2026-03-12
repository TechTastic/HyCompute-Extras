package io.github.techtastic.hycomputeextras;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import dev.cozygalvinism.hycompute.computer.Computer;
import dev.cozygalvinism.hycompute.computer.LuaExecutor;
import dev.cozygalvinism.hycompute.shaded.cobalt.LuaTable;
import io.github.techtastic.hycomputeextras.api.ILuaAPI;
import io.github.techtastic.hycomputeextras.api.annotations.LuaFunction;
import io.github.techtastic.hycomputeextras.api.factory.LuaAPIFactory;
import io.github.techtastic.hycomputeextras.commands.ExampleCommand;
import io.github.techtastic.hycomputeextras.events.ExampleEvent;
import io.github.techtastic.hycomputeextras.processor.LuaFunctionProcessor;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class HyComputeExtras extends JavaPlugin {
    private static HyComputeExtras plugin;
    private Set<LuaAPIFactory> factories = new HashSet<>();

    public HyComputeExtras(@Nonnull JavaPluginInit init) {
        super(init);
        plugin = this;
    }

    public static HyComputeExtras get() {
        return plugin;
    }

    protected void addAPIs(Ref<ChunkStore> ref, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer, Computer computer) {
        LuaExecutor executor = computer.getLuaExecutor();
        if (executor != null) {
            for (LuaAPIFactory factory : this.factories) {
                ILuaAPI api = factory.getAPI(ref, store, commandBuffer, computer);
                LuaTable lua = new LuaTable();
                if (LuaFunctionProcessor.process(lua, api, api.getClass()))
                    executor.getGlobals().rawset(api.getType(), lua);
                else
                    this.getLogger().atSevere().log("Invalid API: " + api.getType() + " is being skipped!");
            }
        }
    }

    public void registerAPI(LuaAPIFactory factory) {
        this.factories.add(factory);
    }

    @Override
    protected void setup() {
        this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);

        this.getChunkStoreRegistry().registerSystem(new TempSystems.OnComputerPlaced());

        this.registerAPI((ref, store, commandBuffer, computer) -> new TempAPI());
    }
}