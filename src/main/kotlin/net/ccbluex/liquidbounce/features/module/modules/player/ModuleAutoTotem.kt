/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2024 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.config.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.events.ScheduleInventoryActionEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.player.ModuleAutoTotem.Health.doesNotPassHealth
import net.ccbluex.liquidbounce.features.module.modules.player.invcleaner.OffHandSlot
import net.ccbluex.liquidbounce.utils.inventory.ClickInventoryAction
import net.ccbluex.liquidbounce.utils.inventory.PlayerInventoryConstraints
import net.ccbluex.liquidbounce.utils.item.findInventorySlot
import net.minecraft.item.Items

/**
 * AutoTotem module
 *
 * Automatically places a totem in off-hand.
 */
object ModuleAutoTotem : Module("AutoTotem", Category.PLAYER) {

    private val inventoryConstraints = tree(PlayerInventoryConstraints())

    private object Health : ToggleableConfigurable(this, "Health", true) {

        val targetHealth by int("Health", 18, 0..20)

        val doesNotPassHealth: Boolean
            get() = Health.enabled && player.health > targetHealth

    }

    init {
        tree(Health)
    }

    @Suppress("unused")
    private val autoTotemHandler = handler<ScheduleInventoryActionEvent> {
        if (player.isCreative || player.isSpectator || player.isDead || doesNotPassHealth) {
            return@handler
        }
        if (player.offHandStack.item == Items.TOTEM_OF_UNDYING) {
            return@handler
        }

        val slot = findInventorySlot { it.item == Items.TOTEM_OF_UNDYING } ?: return@handler
        it.schedule(inventoryConstraints, ClickInventoryAction.performSwap(from = slot, to = OffHandSlot))
    }
}
