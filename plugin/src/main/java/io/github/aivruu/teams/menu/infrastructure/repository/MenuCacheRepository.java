package io.github.aivruu.teams.menu.infrastructure.repository;

import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.menu.application.repository.MenuRepository;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class MenuCacheRepository implements MenuRepository {
  private final Object2ObjectMap<String, AbstractMenuModel> cache = new Object2ObjectOpenHashMap<>();
  private Collection<AbstractMenuModel> valuesView;

  @Override
  public @Nullable AbstractMenuModel findSync(final @NotNull String id) {
    return this.cache.get(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.containsKey(id);
  }

  @Override
  public @NotNull Collection<AbstractMenuModel> findAllSync() {
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    return this.valuesView;
  }

  @Override
  public void saveSync(final @NotNull String id, final @NotNull AbstractMenuModel object) {
    this.cache.put(object.id(), object);
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    this.valuesView.add(object);
  }

  @Override
  public @Nullable AbstractMenuModel deleteSync(final @NotNull String id) {
    final AbstractMenuModel menuModel = this.cache.remove(id);
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    if (menuModel != null) {
      this.valuesView.remove(menuModel);
    }
    return menuModel;
  }

  @Override
  public void clearSync() {
    this.cache.clear();
    this.valuesView.clear();
  }
}
