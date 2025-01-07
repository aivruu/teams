// This file is part of teams, licensed under the GNU License.
//
// Copyright (c) 2024 aivruu
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
package io.github.aivruu.teams.result.domain;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the result (and status-code) of a mutation-operation made on a value-object.
 *
 * @param status the status-code.
 * @param result the operation's result.
 * @param <T> a type-parameter representing the result's type.
 * @since 0.0.1
 */
public record ValueObjectMutationResult<T>(byte status, @Nullable T result) {
  /** The value-object was mutated correctly. */
  public static final byte MUTATED_STATUS = 0;
  /** The value-object wasn't mutated due to some reason. */
  public static final byte UNCHANGED_STATUS = 1;
  /** Something went wrong during value-object's mutation. */
  public static final byte ERROR_STATUS = 2;

  /**
   * Creates a new {@link ValueObjectMutationResult} with the provided parameters.
   *
   * @param result the operation's result.
   * @param <T> a type-parameter representing the result's type.
   * @return A new {@link ValueObjectMutationResult} with the provided result and {@link #MUTATED_STATUS}.
   * @since 0.0.1
   */
  public static <T> @NotNull ValueObjectMutationResult<@NotNull T> mutated(final T result) {
    return new ValueObjectMutationResult<>(MUTATED_STATUS, result);
  }

  /**
   * Creates a new {@link ValueObjectMutationResult} with the provided parameters.
   *
   * @param <T> a type-parameter representing the result's type.
   * @return A new {@link ValueObjectMutationResult} with {@code null} result and {@link #UNCHANGED_STATUS}.
   * @since 0.0.1
   */
  public static <T> @NotNull ValueObjectMutationResult<@Nullable T> unchanged() {
    return new ValueObjectMutationResult<>(UNCHANGED_STATUS, null);
  }

  /**
   * Creates a new {@link ValueObjectMutationResult} with the provided parameters.
   *
   * @param <T> a type-parameter representing the result's type.
   * @return A new {@link ValueObjectMutationResult} with {@code null} result and {@link #ERROR_STATUS}.
   * @since 0.0.1
   */
  public static <T> @NotNull ValueObjectMutationResult<@Nullable T> error() {
    return new ValueObjectMutationResult<>(ERROR_STATUS, null);
  }

  /**
   * Creates a new {@link ValueObjectMutationResult} with the provided parameters.
   *
   * @param status the status-code.
   * @param result the result for this mutation.
   * @param <T> a type-parameter representing the result's type.
   * @return A new {@link ValueObjectMutationResult} with the given status-code and result.
   * @since 0.0.1
   */
  public static <T> @NotNull ValueObjectMutationResult<@Nullable T> custom(final byte status, final @Nullable T result) {
    return new ValueObjectMutationResult<>(status, result);
  }

  /**
   * Checks if the value-object was mutated correctly.
   *
   * @return Whether this {@link ValueObjectMutationResult}'s status-code is {@link #MUTATED_STATUS}.
   * @since 0.0.1
   */
  public boolean wasMutated() {
    return this.status == MUTATED_STATUS;
  }

  /**
   * Checks if the value-object wasn't mutated.
   *
   * @return Whether this {@link ValueObjectMutationResult}'s status-code is {@link #UNCHANGED_STATUS}.
   * @since 0.0.1
   */
  public boolean wasUnchanged() {
    return this.status == UNCHANGED_STATUS;
  }

  /**
   * Checks if an error occurred during the value-object's mutation.
   *
   * @return Whether this {@link ValueObjectMutationResult}'s status-code is {@link #ERROR_STATUS}.
   * @since 0.0.1
   */
  public boolean wasError() {
    return this.status == ERROR_STATUS;
  }

  /**
   * Checks if the status-code is equal to the provided one.
   *
   * @param status the status to check.
   * @return Whether this instance's status-code is equal to the provided one.
   * @since 0.0.1
   */
  public boolean statusIs(final byte status) {
    return this.status == status;
  }
}