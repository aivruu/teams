package io.github.aivruu.teams.action.application.repository;

import io.github.aivruu.teams.action.application.ActionModelContract;
import io.github.aivruu.teams.repository.domain.DomainRepository;
import org.jetbrains.annotations.NotNull;

public interface ActionRepository extends DomainRepository<ActionModelContract> {
  @Override
  default <V> void updateSync(final @NotNull String id, final @NotNull V value) {
    throw NOT_IMPLEMENTED_EXCEPTION;
  }
}
