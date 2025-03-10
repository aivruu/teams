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
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public enum MongoTagPropertiesValueObjectCodec implements Codec<TagPropertiesValueObject> {
  INSTANCE;

  @Override
  public @NotNull TagPropertiesValueObject decode(final BsonReader reader, final DecoderContext decoderContext) {
    reader.readStartDocument();
    // Verify if values are available for reading and deserialization.
    final String prefix = (reader.getCurrentBsonType() == BsonType.NULL) ? null : reader.readString("prefix");
    final String suffix = (reader.getCurrentBsonType() == BsonType.NULL) ? null : reader.readString("suffix");
    final int colorValue = reader.readInt32("color-value");
    reader.readEndDocument();
    return new TagPropertiesValueObject(
      (prefix == null) ? null : PlainComponentHelper.modern(prefix),
      (suffix == null) ? null : PlainComponentHelper.modern(suffix),
      // This will never be null.
      NamedTextColor.namedColor(colorValue)
    );
  }

  @Override
  public void encode(final BsonWriter writer, final TagPropertiesValueObject properties, final EncoderContext encoderContext) {
    writer.writeStartDocument();
    final Component prefix = properties.prefix();
    if (prefix == null) {
      writer.writeNull("prefix");
    } else {
      writer.writeString("prefix", PlainComponentHelper.plain(prefix));
    }
    final Component suffix = properties.suffix();
    if (suffix == null) {
      writer.writeNull("suffix");
    } else {
      writer.writeString("suffix", PlainComponentHelper.plain(suffix));
    }
    writer.writeInt32("color-value", properties.color().value());
    writer.writeEndDocument();
  }

  @Override
  public Class<TagPropertiesValueObject> getEncoderClass() {
    return TagPropertiesValueObject.class;
  }
}
