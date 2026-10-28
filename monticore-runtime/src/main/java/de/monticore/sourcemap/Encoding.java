/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import de.monticore.symboltable.serialization.json.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.sourcemap.Base64VLQUtil.encodeBase64;

/**
 * A utility class for encoding source maps into their string representation.
 */
public class Encoding {

  /**
   * Encodes a given {@code DecodedSourceMap} into its equivalent string representation.
   *
   * @param decodedSourceMap the {@link DecodedSourceMap} containing metadata about the file and its
   *                         mappings to be encoded. It must not be null.
   * @return a string representation of the encoded source map based on the provided
   * {@code DecodedSourceMap}.
   */
  public static String getEncodeSourceMap(DecodedSourceMap decodedSourceMap) {
    return encodeDecodedSourceMap(decodedSourceMap).toString();
  }

  private static JsonObject encodeDecodedSourceMap(DecodedSourceMap decodedSourceMap) {
    JsonObject jsonObject = serializeDefaultSourceMapJsonObject();
    jsonObject.putMember("file", new UserJsonString(decodedSourceMap.file()));

    // All fields in mappings are interpreted relative to each other -> that's why all fields are dependent on the mappings
    List<DecodedMapping> decodedMappings = new ArrayList<>(decodedSourceMap.mappings());

    List<String> names = serializeNamesFromDecodedMappings(decodedMappings);

    String sourcesRoot = "";

    // Prefix of every source to avoid redundancy
    jsonObject.putMember("sourceRoot", new UserJsonString(sourcesRoot));

    jsonObject.putMember("sources", serializeSourcesArrayFromDecodedSources(decodedSourceMap.sources(), sourcesRoot));
    jsonObject.putMember("sourcesContent", serializeSourcesContentArrayFromDecodedSources(decodedSourceMap.sources()));
    jsonObject.putMember("names", serializeNamesArrayFromMappings(names));
    jsonObject.putMember("mappings", serializeMappings(decodedMappings, decodedSourceMap.sources(), names));
    jsonObject.putMember("ignoreList", serializeIgnoreIndexListFromSources(decodedSourceMap.sources()));

    return jsonObject;
  }

  private static JsonArray serializeIgnoreIndexListFromSources(List<DecodedSource> sources) {
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < sources.size(); i++) {
      if (sources.get(i).ignored()) {
        jsonArray.add(new JsonNumber(String.valueOf(i)));
      }
    }
    return jsonArray;
  }

  private static List<String> serializeNamesFromDecodedMappings(List<DecodedMapping> decodedMappings) {
    List<String> names = new ArrayList<>();
    decodedMappings.forEach(mapping ->
        {
          if (mapping.name() != null && (names.isEmpty() || (names.lastIndexOf(mapping.name()) != names.size() - 1))) {
            names.add(mapping.name());
          }
        }
    );
    return names;
  }

  private static JsonString serializeMappings(List<DecodedMapping> decodedMappings, List<DecodedSource> sources, List<String> names) {
    StringBuilder result = new StringBuilder();

    int previousGeneratedLine = 0;
    int previousSource = 0;
    int previousGeneratedColumn = 0;
    int previousSourceLine = 0;
    int previousSourceColumn = 0;
    int previousName = 0;
    for (int i = 0; i < decodedMappings.size(); i++) {
      DecodedMapping mapping = decodedMappings.get(i);
      if (previousGeneratedLine != mapping.generatedLine()) {
        previousGeneratedColumn = 0;
        // For each new line between last mapping and current mapping add a ';'
        for (; previousGeneratedLine < mapping.generatedLine(); previousGeneratedLine++) {
          result.append(";");
        }
      } else if (i > 0) {
        result.append(",");
      }

      result.append(encodeBase64(mapping.generatedColumn() - previousGeneratedColumn));
      previousGeneratedColumn = mapping.generatedColumn();

      if (mapping.originalSource() != null) {
        int sourceIndex = sources.indexOf(mapping.originalSource());

        // Source index
        result.append(encodeBase64(sourceIndex - previousSource));
        previousSource = sourceIndex;

        // Source Line Number
        result.append(encodeBase64(mapping.originalLine() - previousSourceLine));
        previousSourceLine = mapping.originalLine();

        // Column Line Number
        result.append(encodeBase64(mapping.originalColumn() - previousSourceColumn));
        previousSourceColumn = mapping.originalColumn();


        if (mapping.name() != null) {
          int nameIdx = names.indexOf(mapping.name());
          result.append(encodeBase64(nameIdx - previousName));
          previousName = nameIdx;
        }
      }
    }

    return new UserJsonString(result.toString());
  }

  private static JsonArray serializeNamesArrayFromMappings(List<String> names) {
    JsonArray jsonArray = new JsonArray();
    names.forEach(name ->
        {
          jsonArray.add(new UserJsonString(name));
        }
    );
    return jsonArray;
  }

  private static JsonArray serializeSourcesArrayFromDecodedSources(
      List<DecodedSource> decodedSources,
      String sourcesRoot) {
    JsonArray jsonArray = new JsonArray();
    decodedSources.forEach(s -> jsonArray.add(new UserJsonString(s.url().toExternalForm().replace(sourcesRoot, ""))));
    return jsonArray;
  }

  private static JsonArray serializeSourcesContentArrayFromDecodedSources(List<DecodedSource> decodedSources) {
    JsonArray jsonArray = new JsonArray();
    decodedSources.forEach(s ->
        {
          if (s.content() == null) {
            jsonArray.add(new JsonNull());
          } else {
            jsonArray.add(new UserJsonString(s.content()));
          }
        }
    );
    return jsonArray;
  }

  private static JsonObject serializeDefaultSourceMapJsonObject() {
    JsonObject jsonObject = new JsonObject();
    jsonObject.putMember("version", new JsonNumber("3"));
    return jsonObject;
  }
}
