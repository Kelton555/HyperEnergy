package net.kelton555.hyperenergy.energy;

// Consider implementing this for something like a conduit or phantomface that doesn't have internal power storage
public interface IEnergyHandler {
    /** Inserts an amount of energy of the given type, converting for this container if applicable
     * @param energyType The energy id of the type of energy being provided
     * @param amount Amount of energy available for the transfer
     * @param simulate If true, only simulate the insertion (energy will not actually be accepted)
     * @return The amount of energy accepted IN THE TYPE PROVIDED AS energyType (may differ from amount of energy added to container)
     */
    long insertEnergy(String energyType, long amount, boolean simulate);

    /** Extracts an amount of energy of the given type, converting if applicable
     * @param energyType The energy id of the type of energy being provided
     * @param amount Amount of energy desired
     * @param simulate If true, only simulate the extraction (energy will not be removed from this container)
     * @return The amount of energy provided IN THE TYPE PROVIDED AS energyType (may differ from amount of energy removed from container)
     */
    long extractEnergy(String energyType, long amount, boolean simulate);

    /** Directly adds an amount of energy to this container, neglecting type checks and insertion limits
     * @param amount The amount to be added
     * @param simulate If true, only simulate the adding
     * @return The amount of energy added (maxEnergy cap still applies)
     */
    long addEnergy(long amount, boolean simulate);

    /** Directly removes an amount of energy from this container, neglecting type checks and extraction limits
     * @param amount The amount to be removed
     * @param simulate If true, only simulate the removal
     * @return The amount of energy removed (container will not go into negative energy)
     */
    long removeEnergy(long amount, boolean simulate);
}
