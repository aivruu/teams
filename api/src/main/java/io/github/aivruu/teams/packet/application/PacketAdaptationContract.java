// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024 aivruu
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
package io.github.aivruu.teams.packet.application;

import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface-contract with the methods required by implementations to proportionate
 * packet-level operations for the plugin.
 *
 * @since 0.0.1
 */
public interface PacketAdaptationContract {
  /**
   * Creates a new player-team with the given id and properties.
   *
   * @param team the team's id.
   * @param properties the team's properties.
   * @since 0.0.1
   */
  void createTeam(final @NotNull String team, final @NotNull TagPropertiesValueObject properties);

  /**
   * Checks if a team with the given id exists.
   *
   * @param team the team's id.
   * @return Whether the team exists.
   * @since 0.0.1
   */
  boolean existsTeam(final @NotNull String team);

  /**
   * Deletes the team with the specified id.
   *
   * @param team the team's id.
   * @return Whether the team existed.
   * @since 0.0.1
   */
  boolean deleteTeam(final @NotNull String team);

  /**
   * Adds the given player to the specified team's players-registry.
   *
   * @param player the player to add.
   * @param team the team's id.
   * @return Whether the team exists and the player is not member of it.
   * @since 0.0.1
   */
  boolean addPlayerToTeam(final @NotNull Player player, final @NotNull String team);

  /**
   * Removes the player from the specified team's players-registry.
   *
   * @param player the player to remove.
   * @param team the team's id.
   * @return Whether the team exists and the player is member of it.
   * @since 0.0.1
   */
  boolean removePlayerFromTeam(final @NotNull Player player, final @NotNull String team);

  /**
   * Modifies the team's prefix by the given one.
   *
   * @param team the team's id.
   * @param prefix the new prefix.
   * @since 0.0.1
   */
  void updateTeamPrefix(final @NotNull String team, final @Nullable Component prefix);

  /**
   * Modifies the team's suffix by the given one.
   *
   * @param team the team's id.
   * @param suffix the new suffix.
   * @since 0.0.1
   */
  void updateTeamSuffix(final @NotNull String team, final @Nullable Component suffix);
}
