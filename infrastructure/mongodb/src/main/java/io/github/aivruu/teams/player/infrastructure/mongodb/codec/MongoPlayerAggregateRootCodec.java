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
package io.github.aivruu.teams.player.infrastructure.mongodb.codec;

import io.github.aivruu.teams.player.domain.PlayerAggregateRoot;
import io.github.aivruu.teams.player.domain.PlayerModelEntity;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public enum MongoPlayerAggregateRootCodec implements Codec<PlayerAggregateRoot> {
  INSTANCE;

  @Override
  public PlayerAggregateRoot decode(final BsonReader reader, final DecoderContext decoderContext) {
    reader.readStartDocument();
    final String id = reader.readString("id");
    final String tag = (reader.getCurrentBsonType() == null) ? null : reader.readString("selected-tag");
    reader.readEndDocument();
    return new PlayerAggregateRoot(id, new PlayerModelEntity(id, tag));
  }

  @Override
  public void encode(final BsonWriter writer, final PlayerAggregateRoot playerAggregateRoot, final EncoderContext encoderContext) {
    writer.writeStartDocument();
    writer.writeString("id", playerAggregateRoot.id());
    writer.writeString("selected-tag", playerAggregateRoot.playerModel().tag());
    writer.writeEndDocument();
  }

  @Override
  public Class<PlayerAggregateRoot> getEncoderClass() {
    return PlayerAggregateRoot.class;
  }
}
