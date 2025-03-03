package io.github.aivruu.teams.shared.infrastructure.adapter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.NotNull;

public interface JsonCodecAdapterContract<T> extends JsonDeserializer<T>, JsonSerializer<T> {
  @NotNull Class<T> forClass();
}
