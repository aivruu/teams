package io.github.aivruu.teams.menu.application;

import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public record ProcessedMenuItemValueObject(@NotNull String id, @NotNull ItemMeta meta) {}
