package net.kelton555.hyperenergy.block.state;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.StateData;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;

import net.kelton555.hyperenergy.energy.*;
import org.jspecify.annotations.NonNull;

import javax.annotation.Nonnull;

// Extend this class for more complicated energy using blocks
public class EnergyStorageState extends BlockState implements IEnergyStorage {
    public static final BuilderCodec<EnergyStorageState> CODEC = BuilderCodec.builder(EnergyStorageState.class, EnergyStorageState::new, BlockState.BASE_CODEC)
            .append(new KeyedCodec<>("MaxInsert", Codec.LONG), (i,v) -> i.maxInsert = v, i -> i.maxInsert)
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("Maximum energy inserted per operation")
            .add()

            .append(new KeyedCodec<>("MaxExtract", Codec.LONG), (i,v) -> i.maxExtract = v, i -> i.maxExtract)
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("Maximum energy extracted per operation")
            .add()

            .append(new KeyedCodec<>("Energy", Codec.LONG), (i,v) -> i.energy = v, i -> i.energy)
            .addValidator(Validators.greaterThanOrEqual(0L))
            .documentation("Current energy held in container")
            .add()

            .build();

    protected long maxExtract = 1000;
    protected long maxInsert = 1000;
    protected long energy = 0;

    protected Data data;

    @Override
    public boolean initialize(BlockType blockType) {
        if (super.initialize(blockType) && blockType.getState() instanceof Data data) {
            this.data = data;
            return true;
        }
        return false;
    }

    private void checkEnergiesExist(String otherEnergy) throws UnregisteredEnergyException {
        if (!EnergyRegistry.energyRegistered(this.data.energyType)) {
            throw new UnregisteredEnergyException(this.data.energyType);
        } else if (!EnergyRegistry.energyRegistered(otherEnergy)) {
            throw new UnregisteredEnergyException(otherEnergy);
        }
    }

    public long insertEnergy(String otherEnergy, long amount, boolean simulate) throws UnregisteredEnergyException {
        checkEnergiesExist(otherEnergy);

        EnergyRegistry.Energy self = EnergyRegistry.getRegisteredEnergy(data.energyType);
        assert(self != null);

        long energyReceived;

        if (otherEnergy.equals(data.energyType)) {
            energyReceived = amount;
        } else {
            energyReceived = self.getConvertedAmount(otherEnergy, amount);
        }

        // minimum of energy possibly received, energy until max, and maximum inserted energy
        long finalEnergyReceived = Math.min(energyReceived, Math.min(data.maxEnergy-energy, maxInsert));

        if (!simulate) {
            energy += finalEnergyReceived;
        }

        if (energyReceived == finalEnergyReceived) {
            return amount;
        } else {
            return self.getConvertibleAmount(otherEnergy, finalEnergyReceived);
        }
    }

    public long extractEnergy(String otherEnergy, long amount, boolean simulate) throws UnregisteredEnergyException {
        checkEnergiesExist(otherEnergy);

        EnergyRegistry.Energy other = EnergyRegistry.getRegisteredEnergy(otherEnergy);
        assert(other != null);

        long desiredEnergy = other.getConvertibleAmount(data.energyType, amount);

        // minimum of energy stored, maximum extraction, and amount of energy to convert to desired output
        long energyProvided = Math.min(maxExtract, Math.min(desiredEnergy, energy));

        if (!simulate) {
            energy -= energyProvided;
        }

        if (energyProvided == desiredEnergy) {
            return amount;
        } else {
            return other.getConvertedAmount(data.energyType, energyProvided);
        }
    }

    public long addEnergy(long amount, boolean simulate) {
        assert(amount >= 0);
        long amountToAdd = Math.min(amount, data.maxEnergy-energy);

        if (!simulate) {
            energy += amountToAdd;
        }

        return amountToAdd;
    }

    public long removeEnergy(long amount, boolean simulate) {
        assert(amount >= 0);
        long amountToRemove = Math.min(amount, energy);

        if (!simulate) {
            energy -= amountToRemove;
        }

        return amountToRemove;
    }

    public double getFillRatio() {
        return (double) energy / (double) data.maxEnergy;
    }

    public boolean isEmpty() {
        return energy == 0;
    }

    public boolean isFull() {
        return energy == data.maxEnergy;
    }

    public String getEnergyType() {
        return data.energyType;
    }

    public long getMaxEnergy() {
        return data.maxEnergy;
    }

    public long getEnergy() {
        return energy;
    }

    public long getMaxInsert() {
        return maxInsert;
    }

    public long getMaxExtract() {
        return maxExtract;
    }

    public static class Data extends StateData {
        @Nonnull
        public static final BuilderCodec<Data> CODEC = BuilderCodec.builder(Data.class, Data::new, StateData.DEFAULT_CODEC)
                .append(new KeyedCodec<>("EnergyType", Codec.STRING), (i,v) -> i.energyType = v, i -> i.energyType)
                .documentation("The String ID of energy type for this container")
                .add()

                .append(new KeyedCodec<>("MaxEnergy", Codec.LONG), (i,v) -> i.maxEnergy = v, i -> i.maxEnergy)
                .addValidator(Validators.greaterThanOrEqual(0L))
                .documentation("Maximum energy held in container")
                .add()
                .build();

        protected String energyType = "HyperEnergy";
        protected long maxEnergy = 1000000;
    }
}