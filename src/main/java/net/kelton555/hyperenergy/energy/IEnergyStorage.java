package net.kelton555.hyperenergy.energy;

// Implement this for something that has an internal buffer of energy; machines and batteries can both apply, and some implementations of cables may as well
public interface IEnergyStorage extends IEnergyHandler {
    /** Calculates the fill ratio of the container, a number from 0-1 (inclusive)
     * @return The ratio to which the container is filled with energy
     */
    double getFillRatio();

    /** Checks if the container is empty; i.e. it has 0 energy
     * @return True if the container has 0 energy, otherwise false
     */
    boolean isEmpty();

    /** Checks if the container is full; i.e. it has energy == maxEnergy
     * @return True if the container is completely full of energy, otherwise false
     */
    boolean isFull();

    String getEnergyType();

    long getMaxEnergy();

    long getEnergy();
}
