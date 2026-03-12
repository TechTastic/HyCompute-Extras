package io.github.techtastic.hycomputeextras.api.factory;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import dev.cozygalvinism.hycompute.computer.Computer;
import io.github.techtastic.hycomputeextras.api.ILuaAPI;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

@FunctionalInterface
public interface LuaAPIFactory {
    @NonNullDecl
    ILuaAPI getAPI(Ref<ChunkStore> ref, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer, Computer computer);
}
