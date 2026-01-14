package net.kelton555.hyperenergy.energy;

public class UnregisteredEnergyException extends Exception {
    public UnregisteredEnergyException() {
        super();
    }

    public UnregisteredEnergyException(String energyType) {
        super("Unregistered energy type: " + energyType);
    }
}
