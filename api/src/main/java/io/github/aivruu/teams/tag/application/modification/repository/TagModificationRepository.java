package io.github.aivruu.teams.tag.application.modification.repository;

import io.github.aivruu.teams.repository.domain.DomainRepository;
import io.github.aivruu.teams.tag.application.modification.ModificationInProgressValueObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface TagModificationRepository extends DomainRepository<ModificationInProgressValueObject> {
  @Override
  default @NotNull Collection<ModificationInProgressValueObject> findAllSync() {
    throw NOT_IMPLEMENTED_EXCEPTION;
  }
}
