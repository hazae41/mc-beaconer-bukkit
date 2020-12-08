package hazae41.beaconer

import com.destroystokyo.paper.event.block.BeaconEffectEvent
import org.bukkit.block.Beacon
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class BeaconEnterEvent(
  val parent: BeaconEffectEvent
) : Event(), Cancellable {
  val player = parent.player
  val block = parent.block
  val effect = parent.effect

  val beacon
    get() = block.state as Beacon

  var name = beacon.customName

  private var cancelled = false

  override fun isCancelled() = cancelled

  override fun setCancelled(cancel: Boolean) {
    cancelled = cancel
  }

  override fun getHandlers() = BeaconEnterEvent.handlers

  companion object {
    @JvmStatic
    fun getHandlerList() = handlers
    private val handlers = HandlerList()
  }
}