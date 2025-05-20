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
package io.github.aivruu.teams.tag.infrastructure.mongodb.codec;

import io.github.aivruu.teams.tag.domain.TagAggregateRoot;
import io.github.aivruu.teams.tag.domain.TagModelEntity;
import io.github.aivruu.teams.tag.domain.TagPropertiesValueObject;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.jetbrains.annotations.NotNull;

public enum MongoTagAggregateRootCodec implements Codec<TagAggregateRoot> {
  INSTANCE;

  @Override
  public @NotNull TagAggregateRoot decode(final BsonReader reader, final DecoderContext decoderContext) {
    reader.readStartDocument();
    final String id = reader.readString("id");
    final TagPropertiesValueObject properties = decoderContext.decodeWithChildContext(
       MongoTagPropertiesValueObjectCodec.INSTANCE, reader);
    reader.readEndDocument();
    return new TagAggregateRoot(id, new TagModelEntity(id, properties));
  }

  @Override
  public void encode(final BsonWriter writer, final TagAggregateRoot tagAggregateRoot, final EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString("id", tagAggregateRoot.id());
    encoderContext.encodeWithChildContext(MongoTagPropertiesValueObjectCodec.INSTANCE, writer, tagAggregateRoot.tagModel().tagComponentProperties());
    writer.writeEndDocument();
  }

  @Override
  public Class<TagAggregateRoot> getEncoderClass() {
    return TagAggregateRoot.class;
  }
}
