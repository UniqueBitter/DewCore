package ltd.mc233.util

import ltd.mc233.bate.Jobs
import ltd.mc233.bate.Race
import ltd.mc233.data.atts.PlAtt
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

object PlayerGet {
    var Player.job: Jobs?
        get() = this.persistentDataContainer.get(PlAtt.job, PersistentDataType.INTEGER)?.let { Jobs.getById(it) }
        set(value) {
            value?.let {
                this.persistentDataContainer.set(PlAtt.job, PersistentDataType.INTEGER, it.id)
            }
        }

    var Player.race: Race?
        get() = this.persistentDataContainer.get(PlAtt.race, PersistentDataType.INTEGER)?.let { Race.getById(it) }
        set(value) {
            value?.let {
                this.persistentDataContainer.set(PlAtt.race, PersistentDataType.INTEGER, it.id)
            }
        }
}