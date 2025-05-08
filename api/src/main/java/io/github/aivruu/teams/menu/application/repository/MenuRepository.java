package io.github.aivruu.teams.menu.application.repository;

import io.github.aivruu.teams.menu.application.AbstractMenuModel;
import io.github.aivruu.teams.repository.domain.DomainRepository;
import org.jetbrains.annotations.NotNull;

public interface MenuRepository extends DomainRepository<AbstractMenuModel> {
  @Override
  default <V> void updateSync(final @NotNull String id, @NotNull final V value) {
    throw NOT_IMPLEMENTED_EXCEPTION;
  }
}
