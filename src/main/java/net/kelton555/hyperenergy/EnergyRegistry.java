package net.kelton555.hyperenergy;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class EnergyRegistry {
    private static final Map<String, Energy> REGISTRATIONS = new HashMap<>();

    // package access
    static void wipeRegistry() {
        REGISTRATIONS.clear();
    }

    /** Registers an energy type defaulting to a 1.00 conversion efficiency from all others
     * @param energyID The String ID to refer to the energy
     * @param baseEnergy The base energy of the type, perhaps an equivalent of burning a charcoal?
     *                      For reference, the default implemented energy has this as 50,000
     * @return True if the energy was registered from this call (was not already registered), false if it was not registered from this call (it already was)
     */
    public static boolean register(String energyID, long baseEnergy) {
        return register(energyID, baseEnergy, 1.00d);
    }

    /** Registers an energy type with a default conversion efficiency from all other energy types
     * @param energyID The String ID to refer to the energy
     * @param baseEnergy The base energy of the type, perhaps an equivalent of burning a charcoal?
     *                      For reference, the default implemented energy has this as 50,000
     * @param conversionEfficiency The efficiency with which other energies convert to this one, a double from 0.00 to 1.00 (inclusive)
     *                              0.00 is a special case that should disable energy transmission entirely
     *                              I am not responsible for what happens if you set this negative or >1.00
     * @return True if the energy was registered from this call (was not already registered), false if it was not registered from this call (it already was)
     */
    public static boolean register(String energyID, long baseEnergy, double conversionEfficiency) {
        if (REGISTRATIONS.containsKey(energyID)) {
            return false;
        } else {
            REGISTRATIONS.put(energyID, new Energy(energyID, baseEnergy, conversionEfficiency));
            return true;
        }
    }

    /** Returns the {@link Energy} registered with the given ID, null if one is not registered with that ID
     * @param energyID The ID of the energy to get
     * @return An Energy object registered with the provided ID, or null if nothing is registered with the ID
     */
    @Nullable
    public static Energy getRegisteredEnergy(String energyID) {
        return REGISTRATIONS.get(energyID);
    }

    /** Returns true if an energy is already registered with the given ID
     * @param energyID Energy ID to check
     * @return True if the energy is already registered, false if it is not
     */
    public static boolean energyRegistered(String energyID) {
        return REGISTRATIONS.containsKey(energyID);
    }

    /** Sets a specific conversion efficiency from one type of energy to another
     * energyIDTo MUST BE REGISTERED FOR THIS FUNCTION TO WORK, energyIDFrom does not have to be
     * @param energyIDFrom The ID of the energy being converted from for this efficiency to be applied
     * @param energyIDTo The ID of the energy being converted to for this efficiency to be applied
     * @param conversionEfficiency A number between 0.00 and 1.00 inclusive representing the efficiency of transmission
     *                              Warranty void on negative or >1.00 input
     * @return True if the efficiency is successfully added, false if it is not (energyIDTo is NOT registered)
     */
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

        /** Gets the conversion efficiency from a given energy type to this energy type
         * @param energyIDFrom The energy type being converted from
         * @return The conversion efficiency from the given energy type to this energy type (ignores ratio of base energies)
         */
        public double getEfficiencyFactor(String energyIDFrom) {
            if (energyIDFrom.equals(this.ENERGY_ID)) {
                return 1.00;
            } else {
                return CONVERSION_EFFICIENCIES.getOrDefault(energyIDFrom, DEFAULT_CONVERSION_EFFICIENCY);
            }
        }
    }
}