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
package io.github.aivruu.teams.tag.application.modification.context;

import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ProcessedContextResultValueObject(
   byte status,
   @Nullable TagAggregateRoot tagAggregateRoot) {
  public static final byte INVALID_TAG_AGGREGATE_ROOT = 0;
  public static final byte MODIFICATION_PROCESS_CANCELLED = 1;
  public static final byte PENDING_FOR_MODIFICATION_PROCESSING = 2;
  public static final byte FAILED_MODIFICATION_PROCESS = 3;
  public static final byte MODIFIED_TAG_PROPERTIES = 4;

  public static @NotNull ProcessedContextResultValueObject asInvalid() {
    return new ProcessedContextResultValueObject(INVALID_TAG_AGGREGATE_ROOT, null);
  }

  public static @NotNull ProcessedContextResultValueObject asCancelled() {
    return new ProcessedContextResultValueObject(MODIFICATION_PROCESS_CANCELLED, null);
  }

  public static @NotNull ProcessedContextResultValueObject asPending(
     final @NotNull TagAggregateRoot tagAggregateRoot) {
    return new ProcessedContextResultValueObject(PENDING_FOR_MODIFICATION_PROCESSING, tagAggregateRoot);
  }

  public static @NotNull ProcessedContextResultValueObject asFailed() {
    return new ProcessedContextResultValueObject(FAILED_MODIFICATION_PROCESS, null);
  }

  public static @NotNull ProcessedContextResultValueObject asModified(
     final @NotNull TagAggregateRoot tagAggregateRoot) {
    return new ProcessedContextResultValueObject(MODIFIED_TAG_PROPERTIES, tagAggregateRoot);
  }

  public boolean invalid() {
    return this.status == INVALID_TAG_AGGREGATE_ROOT;
  }

  public boolean cancelled() {
    return this.status == MODIFICATION_PROCESS_CANCELLED;
  }

  public boolean pending() {
    return this.status == PENDING_FOR_MODIFICATION_PROCESSING;
  }

  public boolean failed() {
    return this.status == FAILED_MODIFICATION_PROCESS;
  }

  public boolean modified() {
    return this.status == MODIFIED_TAG_PROPERTIES;
  }
}
