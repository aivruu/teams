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
package io.github.aivruu.teams.util.application;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is used to proportionate a custom-size {@link Thread}-pool for the application.
 *
 * @since 4.0.0
 */
public final class PluginExecutor {
  private static ExecutorService threadPool;

  private PluginExecutor() {
    throw new UnsupportedOperationException("This class is for utility and cannot be instantiated.");
  }

  /**
   * Returns this helper's {@link ExecutorService}.
   * <p>
   * Don't try to call this method if the thread-pool is not initialized yet.
   *
   * @return The {@link ExecutorService}.
   * @throws IllegalStateException if the thread-pool has not been initialized yet.
   * @since 4.0.0
   */
  public static @NotNull ExecutorService get() {
    if (threadPool == null) {
      throw new IllegalStateException("The plugin's thread-pool has not been initialized yet.");
    }
    return threadPool;
  }

  /**
   * Creates a new thread-pool for the {@link ExecutorService} using the given threads number.
   *
   * @param threads the number of threads to assign.
   * @since 4.0.0
   */
  public static void build(final int threads) {
    if (threadPool != null) {
      Debugger.write("[WARNING] Requesting thread-pool initialization when already is initialized.");
      return;
    }
    threadPool = Executors.newFixedThreadPool(threads, r -> new Thread(r, "Teams-Thread-Pool"));
  }

  /**
   * Executes the given command (task) after the given delay-duration.
   * <p>
   * Don't try to call this method if the thread-pool is not initialized yet.
   *
   * @param task the logic to execute.
   * @param delay the duration-to-wait before run the task.
   * @throws IllegalStateException if the thread-pool has not been initialized yet.
   * @since 4.0.0
   */
  public static void runAfter(final @NotNull Runnable task, final @NotNull Duration delay) {
    if (threadPool == null) {
      Debugger.write("[WARNING] Requesting thread-pool initialization when already is initialized.");
      return;
    }
    threadPool.execute(() -> {
      try {
        Thread.sleep(delay);
      } catch (final InterruptedException exception) {
        Debugger.write("Unexpected exception when trying to sleep thread before task-execution.",
           exception);
        Thread.currentThread().interrupt();
      }
      task.run();
    });
  }

  /**
   * Executes the given command (task) immediately.
   * <p>
   * Don't try to call this method if the thread-pool is not initialized yet.
   *
   * @param task the logic to execute.
   * @throws IllegalStateException if the thread-pool has not been initialized yet.
   * @since 4.0.0
   */
  public static void runNow(final @NotNull Runnable task) {
    if (threadPool == null) {
      Debugger.write("[WARNING] Requesting thread-pool initialization when already is initialized.");
      return;
    }
    threadPool.execute(task);
  }
}
