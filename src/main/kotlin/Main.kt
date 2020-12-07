package hazae41.beaconer

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import org.bukkit.NamespacedKey
import org.bukkit.block.Beacon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY
import org.bukkit.persistence.PersistentDataType.LONG
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin(), Listener {
  override fun onEnable() {
    super.onEnable()

    server.pluginManager.registerEvents(this, this)
  }

  fun key(key: String) = NamespacedKey(this, key)
  fun now() = System.currentTimeMillis()

  fun Player.lastBeacon(): Beacon? {
    val (x, y, z) = persistentDataContainer
      .get(key("beacon.location"), INTEGER_ARRAY)
      ?: return null

    val time = persistentDataContainer
      .get(key("beacon.time"), LONG)
      ?: return null

    if (now() > time) return null

    val block = world.getBlockAt(x, y, z)
    return block.state as Beacon
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onbeacon(e: BeaconEffectEvent) {
    if (!e.isPrimary) return

    val beacon = e.block.state as Beacon

    val duration = e.effect.duration * 50
    val lastBeacon = e.player.lastBeacon()
    
    if (lastBeacon?.location == beacon.location) {
      e.player.persistentDataContainer
        .set(key("beacon.time"), LONG, now() + duration)
      return
    }

    val location = beacon.run { intArrayOf(x, y, z) }

    e.player.persistentDataContainer
      .set(key("beacon.location"), INTEGER_ARRAY, location)

    e.player.persistentDataContainer
      .set(key("beacon.time"), LONG, now() + duration)

    e.player.sendTitle(beacon.customName, "")
  }
}