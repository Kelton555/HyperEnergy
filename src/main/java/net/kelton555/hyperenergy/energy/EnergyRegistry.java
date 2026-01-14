package net.kelton555.hyperenergy.energy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EnergyRegistry {
    private static final Map<String, Energy> REGISTRATIONS = new HashMap<>();
    private static EnergyRegistry INSTANCE = null;

    // DO NOT USE.
    public EnergyRegistry() {
        if (INSTANCE != null) {
            throw new RuntimeException("Second instantiation of EnergyRegistry attempted");
        } else {
            INSTANCE = this;
        }
    }

    public void wipeRegistry() {
        REGISTRATIONS.clear();
    }

    public static boolean register(String energyID, long baseEnergy) {
        return register(energyID, baseEnergy, 1.00d);
    }

    public static boolean register(String energyID, long baseEnergy, double conversionEfficiency) {
        if (REGISTRATIONS.containsKey(energyID)) {
            return false;
        } else {
            REGISTRATIONS.put(energyID, new Energy(energyID, baseEnergy, conversionEfficiency));
            return true;
        }
    }

    @Nullable
    public static Energy getRegisteredEnergy(String energyID) {
        return REGISTRATIONS.get(energyID);
    }

    public static boolean energyRegistered(String energyID) {
        return REGISTRATIONS.containsKey(energyID);
    }

    public static boolean setEfficiency(String energyIDFrom, String energyIDTo, double conversionEfficiency) {
        Energy energyTo = getRegisteredEnergy(energyIDTo);

        if (energyTo != null) {
            energyTo.setEfficiency(energyIDFrom, conversionEfficiency);
            return true;
        } else {
            return false;
        }
    }

    public static class Energy {
        public final String ENERGY_ID;
        public final long BASE_ENERGY;
        public final double DEFAULT_CONVERSION_EFFICIENCY;
        private final Map<String, Double> CONVERSION_EFFICIENCIES = new HashMap<>();

        private Energy(String energyID, long baseEnergy, double conversionEfficiency) {
            ENERGY_ID = energyID;
            BASE_ENERGY = baseEnergy;
            DEFAULT_CONVERSION_EFFICIENCY = conversionEfficiency;
        }

        private void setEfficiency(String energyIDFrom, double efficiency) {
            CONVERSION_EFFICIENCIES.put(energyIDFrom, efficiency);
        }

        private void clearEfficiency(String energyIDFrom) {
            CONVERSION_EFFICIENCIES.remove(energyIDFrom);
        }

        private double getFullRatio(String energyIDFrom) {
            Energy other = EnergyRegistry.getRegisteredEnergy(energyIDFrom);
            assert(other != null);

            return ((double) this.BASE_ENERGY / (double) other.BASE_ENERGY)
                    * CONVERSION_EFFICIENCIES.getOrDefault(energyIDFrom, DEFAULT_CONVERSION_EFFICIENCY);
        }

        /** Convert from a different type of energy to this type
         * @param energyIDFrom The energy type to convert from
         * @param energy The amount of energy of the type converting from
         * @return The amount of energy that the input conditions represent in this type
         */
        public long getConvertedAmount(String energyIDFrom, long energy) {
            if (energyIDFrom.equals(this.ENERGY_ID)) {
                return energy;
            } else {
                return (long) Math.floor(energy * getFullRatio(energyIDFrom));
            }
        }

        /** Gets the amount of some other type of energy required to convert to a given amount of this type of energy
         * The inverse of getConvertedAmount()
         * @param energyIDFrom The energy type to find a conversion from
         * @param energy The amount of energy that the conversion should result in
         * @return The amount of energy of type energyIDFrom that converts to the desired amount of this energy type.
         */
        public long getConvertibleAmount(String energyIDFrom, long energy) {
            if (energyIDFrom.equals(this.ENERGY_ID)) {
                return energy;
            } else {
                double ratio = getFullRatio(energyIDFrom);
                if (ratio != 0.00d) {
                    return (long) Math.ceil(energy / ratio);
                } else {
                    return 0;
                }
            }
        }
    }
}