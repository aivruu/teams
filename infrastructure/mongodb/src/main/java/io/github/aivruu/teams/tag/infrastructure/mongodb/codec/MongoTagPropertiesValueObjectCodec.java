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
package io.github.aivruu.teams.tag.infrastructure.mongodb.codec;

import io.github.aivruu.teams.plain.application.PlainComponentHelper;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import net.kyori.adventure.text.Component;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public enum MongoTagPropertiesValueObjectCodec implements Codec<TagPropertiesValueObject> {
  INSTANCE;

  @Override
  public @NotNull TagPropertiesValueObject decode(final BsonReader reader, final DecoderContext decoderContext) {
    final String prefix = reader.readString("prefix");
    final String suffix = reader.readString("suffix");
    return new TagPropertiesValueObject(
      prefix.isEmpty() ? null : PlainComponentHelper.modern(prefix),
      suffix.isEmpty() ? null : PlainComponentHelper.modern(suffix)
    );
  }

  @Override
  public void encode(final BsonWriter writer, final TagPropertiesValueObject properties, final EncoderContext encoderContext) {
    final Component prefix = properties.prefix();
    writer.writeString("prefix", (prefix == null) ? "" : PlainComponentHelper.plain(prefix));
    final Component suffix = properties.suffix();
    writer.writeString("suffix", (suffix == null) ? "" : PlainComponentHelper.plain(suffix));
  }

  @Override
  public Class<TagPropertiesValueObject> getEncoderClass() {
    return TagPropertiesValueObject.class;
  }
}
