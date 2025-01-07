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
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PacketAdaptationModule implements PacketAdaptationContract {
  private final Scoreboard scoreboard = MinecraftServer.getServer().getScoreboard();

  @Override
  public void createTeam(final @NotNull String team, final @NotNull TagPropertiesValueObject properties) {
    this.scoreboard.addPlayerTeam(team).setCollisionRule(Team.CollisionRule.NEVER);
    this.updateTeamPrefix(team, properties.prefix());
    this.updateTeamSuffix(team, properties.suffix());
  }

  @Override
  public boolean existsTeam(final @NotNull String team) {
    return this.scoreboard.getPlayerTeam(team) != null;
  }

  @Override
  public boolean deleteTeam(final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(team);
    if (playerTeam == null) {
      return false;
    }
    this.scoreboard.removePlayerTeam(playerTeam);
    return true;
  }

  @Override
  public boolean addPlayerToTeam(final @NotNull Player player, final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(team);
    return playerTeam != null && this.scoreboard.addPlayerToTeam(player.getName(), playerTeam);
  }

  @Override
  public boolean removePlayerFromTeam(final @NotNull Player player, final @NotNull String team) {
    final String playerName = player.getName();
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(team);
    if (playerTeam == null || !playerTeam.getPlayers().remove(playerName)) {
      return false;
    }
    this.scoreboard.removePlayerFromTeam(playerName, playerTeam);
    return true;
  }

  @Override
  public void updateTeamPrefix(final @NotNull String team, final @Nullable Component prefix) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(team);
    if (playerTeam == null) {
      return;
    }
    playerTeam.setPlayerPrefix((prefix == null) ? null : new AdventureComponent(prefix));
  }

  @Override
  public void updateTeamSuffix(final @NotNull String team, final @Nullable Component suffix) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(team);
    if (playerTeam == null) {
      return;
    }
    playerTeam.setPlayerSuffix((suffix == null) ? null : new AdventureComponent(suffix));
  }
}
