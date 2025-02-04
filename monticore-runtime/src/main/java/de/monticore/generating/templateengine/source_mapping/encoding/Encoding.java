package de.monticore.generating.templateengine.source_mapping.encoding;

import de.monticore.generating.templateengine.source_mapping.DecodedMapping;
import de.monticore.generating.templateengine.source_mapping.DecodedSource;
import de.monticore.generating.templateengine.source_mapping.DecodedSourceMap;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.json.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static de.monticore.generating.templateengine.source_mapping.encoding.Base64VLQUtil.encodeBase64;

public class Encoding {

  // For Debugging purposes
  public static boolean USE_BASE_64_ENCODING = true;

  public static String encodDecodedSourceMapToString(DecodedSourceMap decodedSourceMap) {
    return encodeDecodedSourceMap(decodedSourceMap).print(new IndentPrinter());
  }

  public static JsonObject encodeDecodedSourceMap(DecodedSourceMap decodedSourceMap) {
    JsonObject jsonObject = serializeDefaultSourceMapJsonObject();
    jsonObject.putMember("file", new UserJsonString(decodedSourceMap.file));

    // All fields in mappings are interpreted relative to each other -> thats why all fields are dependent on the mappings

    List<DecodedMapping> sortedDecodedMappings = decodedSourceMap.mappings;
    Comparator<DecodedMapping> comparator = Comparator.comparingInt(d -> d.generatedLine);
    comparator = comparator.thenComparingInt(d -> d.generatedColumn);
    comparator = comparator.thenComparingInt(d -> d.originalLine);
    comparator = comparator.thenComparingInt(d -> d.originalColumn);
    sortedDecodedMappings.sort(comparator);

    List<String> names = serializeNamesFromDecodedMappings(sortedDecodedMappings);

    // Prefix of every source to avoid redundancy
    jsonObject.putMember("sourceRoot", new UserJsonString(""));

    jsonObject.putMember("sources", serializeSourcesArrayFromDecodedSources(decodedSourceMap.getSources()));
    jsonObject.putMember("sourcesContent", serializeSourcesContentArrayFromDecodedSources(decodedSourceMap.getSources()));
    jsonObject.putMember("names", serializeNamesArrayFromMappings(names));
    jsonObject.putMember("mappings",serializeMappings(sortedDecodedMappings, decodedSourceMap.getSources(), names));
    jsonObject.putMember("ignoreList", serializeIgnoreIndexListFromSources(decodedSourceMap.getSources()));

    return jsonObject;
  }

  private static JsonArray serializeIgnoreIndexListFromSources(List<DecodedSource> sources) {
    JsonArray jsonArray = new JsonArray();
    for (int i = 0; i < sources.size(); i++) {
      if(sources.get(i).ignored) {
        jsonArray.add(new JsonNumber(String.valueOf(i)));
      }
    }
    return jsonArray;
  }

  private static List<String> serializeNamesFromDecodedMappings(List<DecodedMapping> decodedMappings) {
    List<String> names = new ArrayList<>();
    decodedMappings.forEach(mapping ->
        {
          if(mapping.name != null && (names.isEmpty() || (names.lastIndexOf(mapping.name)!= names.size()-1))) {
            names.add(mapping.name);
          }
        }
    );
    return names;
  }

  private static JsonString serializeMappings(List<DecodedMapping> decodedMappings, List<DecodedSource> sources, List<String> names) {
    if(!USE_BASE_64_ENCODING) {
      return new UserJsonString(decodedMappings.toString());
    }
    StringBuilder result = new StringBuilder();

    int previousGeneratedLine = 0;
    int previousSource = 0;
    int previousGeneratedColumn = 0;
    int previousSourceLine = 0;
    int previousSourceColumn = 0;
    int previousName = 0;
    for (int i = 0;i < decodedMappings.size(); i++) {
      DecodedMapping mapping = decodedMappings.get(i);
      if(previousGeneratedLine != mapping.generatedLine) {
        previousGeneratedColumn = 0;
        // For each new line between last mapping and current mapping add a ';'
        for (int j = previousGeneratedLine; j < mapping.generatedLine; j++) {
          previousGeneratedLine++;
          result.append(";");
        }
      } else if(i > 0) {
        // If equal continue
        /*if(){
          continue;
        }*/
        result.append(",");
      }

      result.append(encodeBase64(mapping.generatedColumn - previousGeneratedColumn));
      previousGeneratedColumn = mapping.generatedColumn;

      if(mapping.originalSource != null) {
        int sourceIndex = sources.indexOf(mapping.originalSource);

        // Source index
        result.append(encodeBase64(sourceIndex - previousSource));
        previousSource = sourceIndex;

        // Source Line Number
        result.append(encodeBase64(
            mapping.originalLine - previousSourceLine
        ));
        previousSourceLine = mapping.originalLine;

        // Column Line Number
        result.append(encodeBase64(
            mapping.originalColumn - previousSourceColumn
        ));
        previousSourceColumn = mapping.originalColumn;


        if (mapping.name != null) {
          int nameIdx = names.indexOf(mapping.name);
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

  private static JsonArray serializeSourcesArrayFromDecodedSources(List<DecodedSource> decodedSources) {
    JsonArray jsonArray = new JsonArray();
    decodedSources.forEach(s ->
        {
          if(s.url.getFile() != null) {
            jsonArray.add(new UserJsonString(s.url.getFile()));
          } else {
            jsonArray.add(new UserJsonString(s.url.toString()));
          }
        }
    );
    return jsonArray;
  }

  private static JsonArray serializeSourcesContentArrayFromDecodedSources(List<DecodedSource> decodedSources) {
    JsonArray jsonArray = new JsonArray();
    decodedSources.forEach(s ->
        {
          if(s.content == null || s.content.isEmpty()) {
            // Empty String without quotes
            jsonArray.add(new JsonNull());
          } else {
            jsonArray.add(new UserJsonString(s.content));
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
