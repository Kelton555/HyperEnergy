package net.kelton555.hyperenergy.block.state;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class SimpleEnergyProviderState extends EnergyStorageState implements TickableBlockState {
    public static final BuilderCodec<SimpleEnergyProviderState> CODEC = BuilderCodec.builder(SimpleEnergyProviderState.class, SimpleEnergyProviderState::new, EnergyStorageState.CODEC)
            .build();

    @Override
    public boolean initialize(BlockType blockType) {
        if (super.initialize(blockType)) {
            if (!initialized) {
                this.energy = this.data.maxEnergy;

                // only set initialized if this is the highest level of initialization
                if (this.getClass() == SimpleEnergyProviderState.class) {
                    initialized = true;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private static final Vector3i[] OFFSETS = {
            new Vector3i(1,0,0),
            new Vector3i(-1,0,0),
            new Vector3i(0,1,0),
            new Vector3i(0,-1,0),
            new Vector3i(0,0,1),
            new Vector3i(0,0,-1)
    };

    public void tick(float dt, int index, ArchetypeChunk<ChunkStore> archeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        final World world = store.getExternalData().getWorld();
        final Vector3i providerPos = this.getBlockPosition();
        final BlockPosition pos = world.getBaseBlock(new BlockPosition(providerPos.x, providerPos.y, providerPos.z));

        for (Vector3i off : OFFSETS) {
            final Vector3i outputPos = new Vector3i(pos.x, pos.y, pos.z).add(off);
            final WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(outputPos.x, outputPos.z));

            if (chunk != null && chunk.getState(outputPos.x, outputPos.y, outputPos.z) instanceof EnergyStorageState energyState) {
                long removableEnergy = this.removeEnergy(this.maxExtract, true);
                long takenEnergy = energyState.insertEnergy(this.data.energyType, removableEnergy, false);
                this.removeEnergy(takenEnergy, false);
            }
        }
    }
}
