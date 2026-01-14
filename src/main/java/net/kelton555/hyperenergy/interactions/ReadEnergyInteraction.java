package net.kelton555.hyperenergy.interactions;

import com.hypixel.hytale.builtin.beds.interactions.BedInteraction;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.kelton555.hyperenergy.block.state.EnergyStorageState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

public class ReadEnergyInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<ReadEnergyInteraction> CODEC = BuilderCodec.builder(ReadEnergyInteraction.class, ReadEnergyInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Interact with an energy containing block to view energy details.")
            .build();

    @Override
    protected void interactWithBlock(
            @NonNullDecl World world,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl InteractionType type,
            @NonNullDecl InteractionContext context,
            @NullableDecl ItemStack itemInHand,
            @NonNullDecl Vector3i pos,
            @NonNullDecl CooldownHandler cooldownHandler
    ) {
        Ref<EntityStore> ref = context.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (player != null) {
            if (world.getState(pos.x, pos.y, pos.z, true) instanceof EnergyStorageState energyStorage) {
                String message = String.format("Energy Type: %s\nCurrentEnergy: %d\nMaxEnergy: %d\nMaxInput/Output: %d/%d",
                        energyStorage.getEnergyType(),
                        energyStorage.getEnergy(),
                        energyStorage.getMaxEnergy(),
                        energyStorage.getMaxInsert(),
                        energyStorage.getMaxExtract());

                player.sendMessage(Message.raw(message));
            } else {
                player.sendMessage(Message.raw("failed to find energy block"));
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNull InteractionType interactionType, @NonNull InteractionContext interactionContext, @Nullable ItemStack itemStack, @NonNull World world, @NonNull Vector3i vector3i) {

    }
}
