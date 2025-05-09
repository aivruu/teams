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
package io.github.aivruu.teams.packet.application;

import io.github.aivruu.teams.packet.application.util.MinecraftColorParser;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import io.papermc.paper.adventure.AdventureComponent;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PacketAdaptationModule implements PacketAdaptationContract {
  private static final String PLUGIN_SCOREBOARD_TEAM_IDENTIFIER = "teams-player-team-";
  private final Scoreboard scoreboard = MinecraftServer.getServer().getScoreboard();

  @Override
  public void createTeam(final @NotNull String team, final @NotNull TagPropertiesValueObject properties) {
    final PlayerTeam playerTeam = this.scoreboard.addPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    // Update attributes for scoreboard-team.
    playerTeam.setCollisionRule(Team.CollisionRule.NEVER);
    // Shouldn't be null.
    playerTeam.setColor(MinecraftColorParser.minecraft(properties.color()));
    playerTeam.setPlayerPrefix((properties.prefix() == null) ? null : new AdventureComponent(properties.prefix()));
    playerTeam.setPlayerSuffix((properties.suffix() == null) ? null : new AdventureComponent(properties.suffix()));
  }

  @Override
  public void deleteTeam(final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return;
    }
    this.scoreboard.removePlayerTeam(playerTeam);
  }

  @Override
  public void addPlayerToTeam(final @NotNull Player player, final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return;
    }
    this.scoreboard.addPlayerToTeam(player.getName(), playerTeam);
  }

  @Override
  public void removePlayerFromTeam(final @NotNull Player player) {
    final String name = player.getName();
    final PlayerTeam playersCurrentTeam = this.scoreboard.getPlayersTeam(name);
    // May the team was deleted prior to this action.
    if (playersCurrentTeam == null) {
      return;
    }
    this.scoreboard.removePlayerFromTeam(name, playersCurrentTeam);
  }

  @Override
  public void updateTeamPrefix(final @NotNull String team, final @Nullable Component prefix) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return;
    }
    playerTeam.setPlayerPrefix((prefix == null) ? null : new AdventureComponent(prefix));
  }

  @Override
  public void updateTeamSuffix(final @NotNull String team, final @Nullable Component suffix) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return;
    }
    playerTeam.setPlayerSuffix((suffix == null) ? null : new AdventureComponent(suffix));
  }

  @Override
  public void updateTeamColor(final @NotNull String team, final @NotNull NamedTextColor namedTextColor) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return;
    }
    playerTeam.setColor(MinecraftColorParser.minecraft(namedTextColor));
  }

  @Override
  public @Nullable Component teamPrefix(final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    return (playerTeam == null) ? null : PaperAdventure.asAdventure(playerTeam.getPlayerPrefix());
  }

  @Override
  public @Nullable Component teamSuffix(final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    return (playerTeam == null) ? null : PaperAdventure.asAdventure(playerTeam.getPlayerSuffix());
  }

  @Override
  public @NotNull NamedTextColor teamColor(final @NotNull String team) {
    final PlayerTeam playerTeam = this.scoreboard.getPlayerTeam(PLUGIN_SCOREBOARD_TEAM_IDENTIFIER + team);
    if (playerTeam == null) {
      return NamedTextColor.WHITE;
    }
    final NamedTextColor color = MinecraftColorParser.modern(playerTeam.getColor());
    return (color == null) ? NamedTextColor.WHITE : color;
  }
}
