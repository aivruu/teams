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
