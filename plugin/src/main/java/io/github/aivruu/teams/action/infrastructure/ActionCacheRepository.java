package io.github.aivruu.teams.action.infrastructure;

import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.action.application.repository.ActionRepository;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public final class ActionCacheRepository implements ActionRepository {
  private final Object2ObjectMap<String, ActionModelContract> cache = new Object2ObjectOpenHashMap<>();
  private @Nullable Collection<ActionModelContract> valuesView;

  @Override
  public @Nullable ActionModelContract findSync(final @NotNull String id) {
    return this.cache.get(id);
  }

  @Override
  public boolean existsSync(final @NotNull String id) {
    return this.cache.containsKey(id);
  }

  @Override
  public @NotNull Collection<ActionModelContract> findAllSync() {
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    return this.valuesView;
  }

  @Override
  public void saveSync(final @NotNull String id, final @NotNull ActionModelContract object) {
    this.cache.put(id, object);
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    this.valuesView.add(object);
  }

  @Override
  public @Nullable ActionModelContract deleteSync(final @NotNull String id) {
    final ActionModelContract action = this.cache.remove(id);
    if (this.valuesView == null) {
      this.valuesView = List.copyOf(this.cache.values());
    }
    if (action != null) {
      this.valuesView.remove(action);
    }
    return action;
  }

  @Override
  public void clearSync() {
    this.cache.clear();
    if (this.valuesView != null) {
      this.valuesView.clear();
    }
  }
}
