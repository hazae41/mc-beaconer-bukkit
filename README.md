# Beaconer for Bukkit

⚠️This plugin requires PaperSpigot

⚠️This plugin requires Kotlin

### [Download](https://github.com/saurusmc/beaconer-bukkit/raw/master/build/libs/beaconer-1.0.jar)

### Features

- When a player enters a beacon area:
  - call a `BeaconEnterEvent`
  - send him a title with the beacon custom name
- When a player places a beacon:
  - store the player, can be accessed later with `beacon.getOwner()`
  