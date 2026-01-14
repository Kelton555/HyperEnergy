# Hyper Energy
An attempt at a Hytale Energy Library mod

I have no idea how this engine functions, I just copied enough from the ones who do to where now this *technically* works in a testing environment

please help me

# How to use
Import "net.kelton555.hyperenergy.EnergyRegistry" and use the .register method to register your own energy type
<br>This requires a String id and a base energy ([where i stole the idea from](https://github.com/UnderMybrella/ForgeEnergy2) proposes that this base energy be the power from a unit of coal in your energy system; will need to figure out an effective baseline for hytale)
<br>You can also provide a baseline conversion efficiency from other energy types to yours (i.e. 1.00 means they convert perfectly efficiently according to the ratio of base energies, 0.5 means that the conversion trashes half the energy, and 0.00 is intended to mean that it can't convert in the first place)
<br>You can specify specific conversion factors from other energy types if you like as well with the setEfficiency method in EnergyRegistry

To do energy operations yourself, you can get an Energy object from the registry and call the helper calculation functions, or do your own calculations with the contained information

HyperGenerators which i also have posted on this github account shows an example of the current usage (NOTE THAT REGISTRY HAS CHANGED LOCATIONS, USE THE ONE IN THE PARAGRAPH ABOVE) but that's likely to change and i probably will work on polishing this before improving or expanding other things (or someone else will make a better library)

## Basic Todo
- Figure out a directional energy interface (Java interface, not UI)
- Figure out UI for a power bar that can be put in menus (like a green line filling a darker green line by energyFillRatio)
  - Basic full battery UI? Customizable I/O in UI?
- Probably improve current implementations of interfaces
- Decide how library a mod this is; should it include cables for the energy it registers? if so how on earth are those gonna work
