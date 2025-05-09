// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024-2025 aivruu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <https://www.gnu.org/licenses/>.
package io.github.aivruu.teams.player.application;

import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.registry.PlayerAggregateRootRegistry;
import io.github.aivruu.teams.tag.domain.event.TagSelectEvent;
import io.github.aivruu.teams.tag.domain.event.TagUnselectEvent;
import io.github.aivruu.teams.tag.domain.registry.TagAggregateRootRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used as tag-selector manager for players.
 *
 * @since 0.0.1
 */
public final class PlayerTagSelectorManager {
  /** The player is not connected to server. */
  public static final byte PLAYER_IS_NOT_ONLINE = -1;
  /** The specified tag does not exist. */
  public static final byte TAG_SPECIFIED_NOT_EXIST = -2;
  /** There is no tag selected by the player. */
  public static final byte THERE_IS_NO_TAG_SELECTED = -3;
  private final PlayerAggregateRootRegistry playerAggregateRootRegistry;
  private final TagAggregateRootRegistry tagAggregateRootRegistry;
  private final PacketAdaptationContract packetAdaptation;

  /**
   * Creates a new {@link PlayerTagSelectorManager} with the provided parameters.
   *
   * @param playerAggregateRootRegistry the {@link PlayerAggregateRootRegistry}.
   * @param packetAdaptation the {@link PacketAdaptationContract} for teams update.
   * @since 0.0.1
   */
  public PlayerTagSelectorManager(
    final @NotNull PlayerAggregateRootRegistry playerAggregateRootRegistry,
    final @NotNull TagAggregateRootRegistry tagAggregateRootRegistry,
    final @NotNull PacketAdaptationContract packetAdaptation) {
    this.playerAggregateRootRegistry = playerAggregateRootRegistry;
    this.tagAggregateRootRegistry = tagAggregateRootRegistry;
    this.packetAdaptation = packetAdaptation;
  }

  /**
   * Defines the given tag as the new selection for the player, and returns a status-code
   * for the selection.
   *
   * @param player the player who selected the tag.
   * @param tag the tag's id.
   * @return A status-code which can be:
   * <ul>
   * <li>{@link PlayerAggregateRoot#TAG_HAS_BEEN_CHANGED} if the player's tag-selection was made correctly.</li>
   * <li>{@link PlayerAggregateRoot#TAG_IS_ALREADY_SELECTED} if the player already has the tag selected.</li>
   * <li>{@link #PLAYER_IS_NOT_ONLINE} if the player is not connected to the server.</li>
   * <li>{@link #TAG_SPECIFIED_NOT_EXIST} if the tag specified doesn't exist.</li>
   * </ul>
   * @see io.github.aivruu.teams.aggregate.domain.registry.AggregateRootRegistry#existsInInfrastructure(String)
   * @see PlayerAggregateRootRegistry#findInCache(String)
   * @see PlayerAggregateRoot#tagWithStatus(String)
   * @since 0.0.1
   */
  public byte select(final @NotNull Player player, final @NotNull String tag) {
    if (!this.tagAggregateRootRegistry.existsInInfrastructure(tag)) {
      return TAG_SPECIFIED_NOT_EXIST;
    }
    final PlayerAggregateRoot playerAggregateRoot = this.playerAggregateRootRegistry.findInCache(player.getUniqueId().toString());
    if (playerAggregateRoot == null) {
      return PLAYER_IS_NOT_ONLINE;
    }
    // Tag-id provided isn't null, so we shouldn't expect a [TAG_HAS_BEEN_CLEARED] status.
    if (playerAggregateRoot.tagWithStatus(tag) == PlayerAggregateRoot.TAG_IS_ALREADY_SELECTED) {
      return PlayerAggregateRoot.TAG_IS_ALREADY_SELECTED;
    }
    this.packetAdaptation.addPlayerToTeam(player, tag);
    Bukkit.getPluginManager().callEvent(new TagSelectEvent(player, tag));
    return PlayerAggregateRoot.TAG_HAS_BEEN_CHANGED;
  }

  /**
   * Clears the player's current tag-selection if it has one, and returns a status-coded
   * for the operation.
   * .
   * @param player the player.
   * @return A status-code which can be:
   * <ul>
   * <li>{@link PlayerAggregateRoot#TAG_HAS_BEEN_CLEARED} if the tag was unselected correctly.</li>
   * <li>{@link #THERE_IS_NO_TAG_SELECTED} if the player has no tag selected.</li>
   * <li>{@link #PLAYER_IS_NOT_ONLINE} if the player is not connected to the server.</li>
   * </ul>
   * @see PlayerAggregateRootRegistry#findInCache(String)
   * @see io.github.aivruu.teams.player.domain.PlayerModelEntity#tag()
   * @since 0.0.1
   */
  public byte unselect(final @NotNull Player player) {
    final PlayerAggregateRoot playerAggregateRoot = this.playerAggregateRootRegistry.findInCache(player.getUniqueId().toString());
    if (playerAggregateRoot == null) {
      return PLAYER_IS_NOT_ONLINE;
    }
    final String tag = playerAggregateRoot.playerModel().tag();
    if (tag == null) {
      return THERE_IS_NO_TAG_SELECTED;
    }
    this.packetAdaptation.removePlayerFromTeam(player);
    Bukkit.getPluginManager().callEvent(new TagUnselectEvent(player, tag));
    // We should expect a [TAG_HAS_BEEN_CLEARED] status.
    return playerAggregateRoot.tagWithStatus(null);
  }
}
