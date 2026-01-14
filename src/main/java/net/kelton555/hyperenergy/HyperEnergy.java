package net.kelton555.hyperenergy;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateRegistry;
import net.kelton555.hyperenergy.block.state.EnergyStorageState;
import net.kelton555.hyperenergy.block.state.SimpleEnergyProviderState;
import net.kelton555.hyperenergy.energy.EnergyRegistry;
import net.kelton555.hyperenergy.interactions.ReadEnergyInteraction;

import javax.annotation.Nonnull;

public class HyperEnergy extends JavaPlugin {
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final EnergyRegistry ENERGY_REGISTRY = new EnergyRegistry();

    public HyperEnergy(@Nonnull JavaPluginInit init) {
        super(init);
        ENERGY_REGISTRY.wipeRegistry();
    }

    @Override
    protected void setup() {
        EnergyRegistry.register("HyperEnergy", 50000);

        final BlockStateRegistry blockStateRegistry = this.getBlockStateRegistry();
        blockStateRegistry.registerBlockState(EnergyStorageState.class, "Kelton555_HyperEnergy_EnergyStorageState", EnergyStorageState.CODEC, EnergyStorageState.Data.class, EnergyStorageState.Data.CODEC);
        blockStateRegistry.registerBlockState(SimpleEnergyProviderState.class, "Kelton555_HyperEnergy_SimpleEnergyProviderState", SimpleEnergyProviderState.CODEC, SimpleEnergyProviderState.Data.class, SimpleEnergyProviderState.Data.CODEC);

        Interaction.CODEC.register("Kelton555_HyperEnergy_ReadEnergy", ReadEnergyInteraction.class, ReadEnergyInteraction.CODEC);
    }

    @Override
    protected void start() {}
}