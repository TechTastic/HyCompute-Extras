package io.github.techtastic.hycomputeextras;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import dev.cozygalvinism.hycompute.ComputerSystems;
import dev.cozygalvinism.hycompute.components.ComputerBlock;
import dev.cozygalvinism.hycompute.components.ComputerOn;
import dev.cozygalvinism.hycompute.computer.Computer;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class TempSystems {
    static class OnComputerPlaced extends RefChangeSystem<ChunkStore, ComputerOn> {
        @NullableDecl
        @Override
        public Query<ChunkStore> getQuery() {
            return Query.and(ComputerBlock.getComponentType(), BlockModule.BlockStateInfo.getComponentType());
        }

        @NonNullDecl
        @Override
        public Set<Dependency<ChunkStore>> getDependencies() {
            return Set.of(new SystemDependency<>(Order.AFTER, ComputerSystems.ComputerStateSystem.class));
        }

        @NonNullDecl
        @Override
        public ComponentType<ChunkStore, ComputerOn> componentType() {
            return ComputerOn.getComponentType();
        }

        @Override
        public void onComponentAdded(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl ComputerOn computerOn, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
            ComputerBlock block = commandBuffer.getComponent(ref, ComputerBlock.getComponentType());
            if (block != null) {
                commandBuffer.getExternalData().getWorld().execute(() -> {
                    Computer computer = block.getComputer();
                    HyComputeExtras.get().addAPIs(ref, store, commandBuffer, computer);
                });
            }
        }

        @Override
        public void onComponentSet(@NonNullDecl Ref<ChunkStore> ref, @NullableDecl ComputerOn computerOn, @NonNullDecl ComputerOn t1, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {
        }

        @Override
        public void onComponentRemoved(@NonNullDecl Ref<ChunkStore> ref, @NonNullDecl ComputerOn computerOn, @NonNullDecl Store<ChunkStore> store, @NonNullDecl CommandBuffer<ChunkStore> commandBuffer) {

        }
    }
}
