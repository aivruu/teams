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

import io.github.aivruu.teams.Constants;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class UpdateChecker {
  private static final String PROJECT_VERSION_ACCESS_API_URL = "https://api.modrinth.com/v2/project/RKRkter9/version";
  private static boolean runningLatest;
  private static String latestVersion;

  public static boolean isRunningLatest() {
    return runningLatest;
  }

  public static @NotNull String getLatestVersion() {
    return latestVersion;
  }

  public static void searchUpdates() {
    final String latest = retrieveVersionNumberFormatted(requestBodyResponse(PROJECT_VERSION_ACCESS_API_URL));
    latestVersion = latest; // Assign latest-version for later use.
    runningLatest = Integer.parseInt(Constants.VERSION.replace(".", "")) == Integer.parseInt(latest.replace(".", ""));
  }

  private static @NotNull String retrieveVersionNumberFormatted(final @NotNull String json) {
    // Basically, from the json-text, we start from the "version_number" field's index, until the
    // first ':'
    final int desiredFieldIndex = json.indexOf("version_number") + 16;
    // from there, we check string's character until found a ',' (which indicates field's end)
    int delimiterCharIndex = desiredFieldIndex + 1;
    while (json.charAt(delimiterCharIndex) != ',') {
      ++delimiterCharIndex;
    }
    // it should be something like, e.g. ["version_number":"4.0.0"] -> "4.0.0"
    // output -> 400
    return json.substring(desiredFieldIndex, delimiterCharIndex).replace("\"", "");
  }

  private static @NotNull String requestBodyResponse(final @NotNull String url) {
    final HttpClient client = HttpClient.newBuilder()
       .executor(PluginExecutor.get())
       .connectTimeout(Duration.ofSeconds(30))
       .build();
    final HttpResponse<String> response = client.sendAsync(HttpRequest.newBuilder()
          .GET()
          .uri(URI.create(url))
          .build(), HttpResponse.BodyHandlers.ofString())
       .join();
    client.close();
    return response.body();
  }
}
