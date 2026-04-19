// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024-2026 aivruu
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
package io.github.aivruu.teams.packet.infrastructure;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.aivruu.teams.packet.application.PacketAdaptationContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PacketEventsAdapter implements PacketAdaptationContract {
  private final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
  private final Object2ObjectMap<String, WrapperPlayServerTeams.ScoreBoardTeamInfo> internalTeams = new Object2ObjectOpenHashMap<>();
  private final Object2ObjectMap<String, ObjectList<UUID>> usersByTeam = new Object2ObjectOpenHashMap<>();

  @Override
  public void createTeam(final @NotNull String team, final @NotNull TagPropertiesValueObject properties) {
    if (this.internalTeams.containsKey(team)) return;

    this.internalTeams.put(team, new WrapperPlayServerTeams.ScoreBoardTeamInfo(Component.empty(),
       properties.prefix(), properties.suffix(),
       WrapperPlayServerTeams.NameTagVisibility.ALWAYS, WrapperPlayServerTeams.CollisionRule.NEVER,
       properties.color(),
       WrapperPlayServerTeams.OptionData.NONE)
    );
    this.usersByTeam.put(team, new ObjectArrayList<>());
  }

  @Override
  public void deleteTeam(final @NotNull String team) {
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.internalTeams.remove(team);
    if (teamInfo == null) {
      return;
    }
    final ObjectList<UUID> teamUsers = this.usersByTeam.remove(team);
    this.notifyToTeamMembers(team, teamUsers, WrapperPlayServerTeams.TeamMode.REMOVE, teamInfo);
  }

  private void notifyToTeamMembers(
     final String team,
     final ObjectList<UUID> members,
     final WrapperPlayServerTeams.TeamMode mode,
     final WrapperPlayServerTeams.ScoreBoardTeamInfo info,
     final String... entities) {
    final WrapperPlayServerTeams packet = new WrapperPlayServerTeams(team, mode, info, entities);
    Player player;
    for (final UUID userId : members) {
      player = Bukkit.getPlayer(userId);
      if (player == null) {
        continue;
      }
      this.playerManager.getUser(player).sendPacket(packet);
    }
  }

  @Override
  public void addPlayerToTeam(final @NotNull Player player, final @NotNull String team) {
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.internalTeams.get(team);
    if (teamInfo == null) {
      return;
    }
    final ObjectList<UUID> teamUsers = this.usersByTeam.get(team);
    final UUID playerId = player.getUniqueId();
    teamUsers.add(playerId);
    this.notifyToTeamMembers(team, teamUsers, WrapperPlayServerTeams.TeamMode.ADD_ENTITIES, teamInfo, playerId.toString());
  }

  @Override
  @Deprecated
  public void removePlayerFromTeam(final @NotNull Player player) {}

  @Override
  public void removePlayerFromTeam(final @NotNull Player player, final @NotNull String team) {
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.internalTeams.get(team);
    if (teamInfo == null) {
      return;
    }
    final ObjectList<UUID> teamUsers = this.usersByTeam.get(team);
    final UUID playerId = player.getUniqueId();
    teamUsers.remove(playerId);
    this.notifyToTeamMembers(team, teamUsers, WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES, teamInfo, playerId.toString());
  }

  private void applyTeamChange(final @NotNull String team, final Consumer<WrapperPlayServerTeams.ScoreBoardTeamInfo> action) {
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.internalTeams.get(team);
    if (teamInfo == null) {
      return;
    }
    action.accept(teamInfo);
    this.notifyToTeamMembers(team, this.usersByTeam.get(team), WrapperPlayServerTeams.TeamMode.UPDATE, teamInfo);
  }

  @Override
  public void updateTeamPrefix(final @NotNull String team, final @Nullable Component prefix) {
    this.applyTeamChange(team, teamInfo -> teamInfo.setPrefix(prefix == null ? Component.empty() : prefix));
  }

  @Override
  public void updateTeamSuffix(final @NotNull String team, final @Nullable Component suffix) {
    this.applyTeamChange(team, teamInfo -> teamInfo.setSuffix(suffix == null ? Component.empty() : suffix));
  }

  @Override
  public void updateTeamColor(final @NotNull String team, final @NotNull NamedTextColor namedTextColor) {
    this.applyTeamChange(team, teamInfo -> teamInfo.setColor(namedTextColor));
  }

  @Override
  public void updateTeamAttributes(final @NotNull String team, final @NotNull TagPropertiesValueObject properties) {
    this.applyTeamChange(team, teamInfo -> {
      teamInfo.setPrefix(properties.prefix());
      teamInfo.setSuffix(properties.suffix());
      teamInfo.setColor(properties.color());
    });
  }

  @Override
  @Deprecated
  public @Nullable Component prefixOf(final @NotNull String team) {
    return this.extractProperty(team, PropertyType.PREFIX);
  }

  @Override
  @Deprecated
  public @Nullable Component suffixOf(final @NotNull String team) {
    return this.extractProperty(team, PropertyType.SUFFIX);
  }

  @Override
  @Deprecated
  public @NotNull NamedTextColor colorOf(final @NotNull String team) {
    return this.extractProperty(team, PropertyType.COLOR);
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NotNull <T> T extractProperty(final @NotNull String team, final @NotNull PropertyType type) {
    final WrapperPlayServerTeams.ScoreBoardTeamInfo teamInfo = this.internalTeams.get(team);
    return switch (type) {
      case PREFIX -> (T) teamInfo.getPrefix();
      case SUFFIX -> (T) teamInfo.getSuffix();
      case COLOR -> (T) teamInfo.getColor();
    };
  }
}
