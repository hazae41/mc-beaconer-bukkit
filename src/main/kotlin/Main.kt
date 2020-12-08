package hazae41.beaconer

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import org.bukkit.NamespacedKey
import org.bukkit.OfflinePlayer
import org.bukkit.block.Beacon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType.*
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class Main : JavaPlugin(), Listener {
  override fun onEnable() {
    super.onEnable()

    server.pluginManager.registerEvents(this, this)
  }

  fun key(key: String) = NamespacedKey(this, key)
  fun now() = System.currentTimeMillis()

  fun Player.getLastBeacon(): Beacon? {
    val (x, y, z) = persistentDataContainer
      .get(key("beacon.location"), INTEGER_ARRAY)
      ?: return null

    val time = persistentDataContainer
      .get(key("beacon.duration"), LONG)
      ?: return null

    if (now() > time) return null

    val block = world.getBlockAt(x, y, z)
    return block.state as Beacon
  }

  fun Player.setLastBeacon(beacon: Beacon) {
    setLastBeaconLocation(beacon)
    setLastBeaconTime(beacon)
  }

  fun Player.setLastBeaconLocation(beacon: Beacon) {
    val location = beacon.run { intArrayOf(x, y, z) }

    persistentDataContainer
      .set(key("beacon.location"), INTEGER_ARRAY, location)
  }

  fun Player.setLastBeaconTime(beacon: Beacon) {
    val effect = beacon.primaryEffect ?: return
    val duration = effect.duration * 50

    persistentDataContainer
      .set(key("beacon.time"), LONG, now() + duration)
  }

  fun Beacon.getOwner() = persistentDataContainer
    .get(key("owner"), STRING)
    ?.let { UUID.fromString(it) }
    ?.let { server.getOfflinePlayer(it) }

  fun Beacon.setOwner(player: OfflinePlayer) {
    val uuid = player.uniqueId.toString()

    persistentDataContainer
      .set(key("owner"), STRING, uuid)
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onplaced(e: BlockPlaceEvent) {
    val beacon = e.block.state as? Beacon ?: return
    beacon.setOwner(e.player)
  }

  @EventHandler(priority = EventPriority.MONITOR)
  fun onbeacon(e: BeaconEffectEvent) {
    if (!e.isPrimary) return

    val player = e.player
    val beacon = e.block.state as Beacon
    val lastBeacon = player.getLastBeacon()

    if (lastBeacon?.location == beacon.location) {
      player.setLastBeaconTime(beacon)
      return
    }

    player.setLastBeacon(beacon)

    val event = BeaconEnterEvent(e)
      .apply { callEvent() }
    if (event.isCancelled) return

    player.sendTitle(event.name, "")
  }
}