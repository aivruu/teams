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
package io.github.aivruu.teams.shared.infrastructure.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import io.github.aivruu.teams.aggregate.domain.AggregateRoot;
import io.github.aivruu.teams.shared.infrastructure.adapter.JsonCodecAdapterContract;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class JsonCoder {
  private static Gson gson;

  private JsonCoder() {
    throw new UnsupportedOperationException("This class is for utility.");
  }

  public static void buildWithAdapters(final @NotNull JsonCodecAdapterContract<?>... adapters) {
    if (gson != null) return;

    final GsonBuilder builder = new GsonBuilder();
    builder.serializeNulls().setPrettyPrinting();
    for (final JsonCodecAdapterContract<?> adapter : adapters) {
      builder.registerTypeAdapter(adapter.forClass(), adapter);
    }
    gson = builder.create();
  }

  public static <A extends AggregateRoot> @Nullable A read(final @NotNull Path file, final @NotNull Class<A> aggregateRootClass) {
    try (final Reader reader = Files.newBufferedReader(file)) {
      return gson.fromJson(reader, TypeToken.get(aggregateRootClass));
    } catch (final IOException exception) {
      return null;
    }
  }

  public static @Nullable TagPropertiesValueObject readProperties(final @NotNull String json) {
    try {
      return gson.fromJson(json, TagPropertiesValueObject.class);
    } catch (final JsonSyntaxException exception) {
      return null;
    }
  }

  public static @NotNull String writeProperties(final @NotNull TagPropertiesValueObject properties) {
    return gson.toJson(properties);
  }

  public static <A extends AggregateRoot> boolean write(final @NotNull Path file, final @NotNull A aggregateRoot) {
    try (final Writer writer = Files.newBufferedWriter(file)) {
      gson.toJson(aggregateRoot, writer);
      return true;
    } catch (final IOException | JsonIOException exception) {
      return false;
    }
  }
}
