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

/**
 * Represents the result that the plugin return when a modification has been passed through the
 * {@link io.github.aivruu.teams.tag.application.modification.TagModificationProcessor}. This result
 * is represented by a status-code, and the modified-tag's {@link TagAggregateRoot}.
 *
 * @param status the status-code for the modification.
 * @param tagAggregateRoot the modified-tag's aggregate-root, or {@code null}.
 * @since 4.0.0
 */
public record ProcessedContextResultValueObject(
   byte status,
   @Nullable TagAggregateRoot tagAggregateRoot) {
  /** The tag specified does not exist. */
  public static final byte INVALID_TAG_AGGREGATE_ROOT = 0;
  /** The user cancelled the modification. */
  public static final byte MODIFICATION_PROCESS_CANCELLED = 1;
  /** The input-validating and checks were performed, delegated to implementation. */
  public static final byte PENDING_FOR_MODIFICATION_PROCESSING = 2;
  /** The modification couldn't be applied. */
  public static final byte FAILED_MODIFICATION_PROCESS = 3;
  /** The modification was done, and applied to the tag. */
  public static final byte MODIFIED_TAG_PROPERTIES = 4;

  /**
   * Creates a new {@link ProcessedContextResultValueObject} with the
   * {@link #INVALID_TAG_AGGREGATE_ROOT} status-code, no aggregate-root provided.
   *
   * @return A new {@link ProcessedContextResultValueObject}.
   * @since 4.0.0
   */
  public static @NotNull ProcessedContextResultValueObject asInvalid() {
    return new ProcessedContextResultValueObject(INVALID_TAG_AGGREGATE_ROOT, null);
  }

  /**
   * Creates a new {@link ProcessedContextResultValueObject} with the
   * {@link #MODIFICATION_PROCESS_CANCELLED} status-code, no aggregate-root provided.
   *
   * @return A new {@link ProcessedContextResultValueObject}.
   * @since 4.0.0
   */
  public static @NotNull ProcessedContextResultValueObject asCancelled() {
    return new ProcessedContextResultValueObject(MODIFICATION_PROCESS_CANCELLED, null);
  }

  /**
   * Creates a new {@link ProcessedContextResultValueObject} with the
   * {@link #PENDING_FOR_MODIFICATION_PROCESSING} status-code.
   *
   * @param tagAggregateRoot the tag's aggregate-root.
   * @return A new {@link ProcessedContextResultValueObject}.
   * @since 4.0.0
   */
  public static @NotNull ProcessedContextResultValueObject asPending(
     final @NotNull TagAggregateRoot tagAggregateRoot) {
    return new ProcessedContextResultValueObject(PENDING_FOR_MODIFICATION_PROCESSING, tagAggregateRoot);
  }

  /**
   * Creates a new {@link ProcessedContextResultValueObject} with the
   * {@link #FAILED_MODIFICATION_PROCESS} status-code, no aggregate-root provided.
   *
   * @return A new {@link ProcessedContextResultValueObject}.
   * @since 4.0.0
   */
  public static @NotNull ProcessedContextResultValueObject asFailed() {
    return new ProcessedContextResultValueObject(FAILED_MODIFICATION_PROCESS, null);
  }

  /**
   * Creates a new {@link ProcessedContextResultValueObject} with the
   * {@link #MODIFIED_TAG_PROPERTIES} status-code.
   *
   * @param tagAggregateRoot the tag's aggregate-root.
   * @return A new {@link ProcessedContextResultValueObject}.
   * @since 4.0.0
   */
  public static @NotNull ProcessedContextResultValueObject asModified(
     final @NotNull TagAggregateRoot tagAggregateRoot) {
    return new ProcessedContextResultValueObject(MODIFIED_TAG_PROPERTIES, tagAggregateRoot);
  }

  /**
   * Returns whether this context-result's status-code is {@link #INVALID_TAG_AGGREGATE_ROOT}.
   *
   * @return true if that's the status-code, otherwise false.
   * @since 4.0.0
   */
  public boolean invalid() {
    return this.status == INVALID_TAG_AGGREGATE_ROOT;
  }

  /**
   * Returns whether this context-result's status-code is {@link #MODIFICATION_PROCESS_CANCELLED}.
   *
   * @return true if that's the status-code, otherwise false.
   * @since 4.0.0
   */
  public boolean cancelled() {
    return this.status == MODIFICATION_PROCESS_CANCELLED;
  }

  /**
   * Returns whether this context-result's status-code is {@link #PENDING_FOR_MODIFICATION_PROCESSING}.
   *
   * @return true if that's the status-code, otherwise false.
   * @since 4.0.0
   */
  public boolean pending() {
    return this.status == PENDING_FOR_MODIFICATION_PROCESSING;
  }

  /**
   * Returns whether this context-result's status-code is {@link #FAILED_MODIFICATION_PROCESS}.
   *
   * @return true if that's the status-code, otherwise false.
   * @since 4.0.0
   */
  public boolean failed() {
    return this.status == FAILED_MODIFICATION_PROCESS;
  }

  /**
   * Returns whether this context-result's status-code is {@link #MODIFIED_TAG_PROPERTIES}.
   *
   * @return true if that's the status-code, otherwise false.
   * @since 4.0.0
   */
  public boolean modified() {
    return this.status == MODIFIED_TAG_PROPERTIES;
  }
}
