/* (c) https://github.com/MontiCore/monticore */

package de.monticore.sourcemap;

import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.se_rwth.commons.logging.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.sourcemap.Base64VLQUtil.base64Encoding;

/**
 * A utility class for decoding source maps from JSON strings and processing the associated data,
 * including mappings, sources, and metadata.
 */
public class Decoding {

  /**
   * Parses a JSON string representing a source map and returns a corresponding
   * {@code DecodedSourceMap} object.
   *
   * @param jsonSourceMap the JSON string containing the source map data, expected to be a valid
   *                      JSON object as per the source map specification
   * @param baseURL       the base URL used to resolve relative paths or URLs in the source map
   * @return a {@code DecodedSourceMap} object containing the parsed information, including
   * mappings, sources, and other relevant metadata
   */
  public static DecodedSourceMap parseSourceMapJson(String jsonSourceMap, URL baseURL) {

    JsonElement jsonParsed = JsonParser.parse(jsonSourceMap);
    if (!jsonParsed.isJsonObject()) {
      Log.error("0xA0720 Expected source map as JsonObject but got " + jsonParsed.getClass());
    }

    JsonObject jsonObject = jsonParsed.getAsJsonObject();

    verifyVersion(jsonObject);
    String mappings = getMappings(jsonObject);

    List<String> sources = getStringListOfMember("sources", jsonObject);

    List<DecodedSource> decodedSources = decodeSourceMapSources(
        baseURL,
        getSourceRoot(jsonObject),
        sources,
        getStringListOfMember("sourcesContent", jsonObject),
        getNumberListOfMember("ignoreList", jsonObject));

    return new DecodedSourceMap(
        getFile(jsonObject),
        decodedSources,
        decodeMappings(mappings, getStringListOfMember("names", jsonObject), decodedSources));
  }


  private static String getSourceRoot(JsonObject object) {
    if (object.hasMember("sourceRoot")) {
      if (object.getStringMemberOpt("sourceRoot").isPresent()) {
        return object.getStringMember("sourceRoot");
      }
    }
    return null;
  }

  private static String getMappings(JsonObject object) {
    if (object.hasMember("mappings")) {
      if (object.getStringMemberOpt("mappings").isPresent()) {
        return object.getStringMember("mappings");
      }
    }
    Log.error("0xA0724 No \"mappings\" member found.");
    return null;
  }

  private static List<Integer> getNumberListOfMember(String memberName, JsonObject object) {
    List<Integer> integers = new ArrayList<>();
    if (object.hasMember(memberName)) {
      if (object.getMember(memberName).isJsonArray()) {
        JsonArray arr = object.getMember(memberName).getAsJsonArray();
        for (JsonElement e : arr.getValues()) {
          if (e.isJsonNumber()) {
            integers.add(e.getAsJsonNumber().getNumberAsInteger());
          } else {
            Log.error("0xA0726 The member contains non-integer elements");
          }
        }
        return integers;
      }
    }
    return integers;
  }

  private static List<String> getStringListOfMember(String memberName, JsonObject object) {
    List<String> stringList = new ArrayList<>();
    if (object.hasMember(memberName)) {
      if (object.getMember(memberName).isJsonArray()) {
        JsonArray arr = object.getMember(memberName).getAsJsonArray();
        for (JsonElement e : arr.getValues()) {
          if (e.isJsonString()) {
            stringList.add(e.getAsJsonString().getValue());
          } else if (e.isJsonNull()) {
            stringList.add(null);
          } else {
            Log.error("0xA0727 The member contains non-string, non-null elements");
          }
        }
        return stringList;
      }
    }
    return stringList;
  }

  private static String getFile(JsonObject object) {
    if (object.hasMember("file")) {
      if (object.getStringMemberOpt("file").isPresent()) {
        return object.getStringMember("file");
      }
    }
    Log.error("0xA0725 No \"file\" member found.");
    return null;
  }

  private static List<DecodedSource> decodeSourceMapSources(
      URL baseURL,
      String sourceRoot,
      List<String> sources,
      List<String> sourcesContent,
      List<Integer> ignoreList) {
    List<DecodedSource> decodedSources = new ArrayList();
    String sourceURLPrefix = "";
    if (sourceRoot != null) {
      if (sourceRoot.contains("/")) {
        sourceURLPrefix = sourceRoot.substring(sourceRoot.lastIndexOf("/") + 1);
      }
    }

    for (int i = 0; i < sources.size(); i++) {
      String source = sources.get(i);
      if (source != null) {
        source = sourceURLPrefix + source;
      }
      URL sourceURL = null;
      try {
        sourceURL = new URL(baseURL, source);
      } catch (MalformedURLException e) {
        Log.error("0xA0728 Error while parsing the provided URL: " + baseURL, e);
      }

      boolean ignored = false;
      for (int index : ignoreList) {
        if (i == index) {
          ignored = true;
          break;
        }
      }

      String content = (sourcesContent.size() > i) ? sourcesContent.get(i) : null;
      decodedSources.add(new DecodedSource(sourceURL, content, ignored));
    }
    return decodedSources;
  }

  private static List<DecodedMapping> decodeMappings(String mappings, List<String> names, List<DecodedSource> decodedSources) {
    if (!isValidBase64VLQGroupings(mappings)) {
      Log.error("0xA0729 The provided mappings are not valid Base64 VLQ groupings.");
    }

    List<DecodedMapping> decodedMappings = new ArrayList<>();

    String[] groups = mappings.split(";");

    int originalLine = 0;
    int originalColumn = 0;
    int sourceIndex = 0;
    int nameIndex = 0;

    /*
      Lines are separated by ';'
     */
    for (int generatedLine = 0; generatedLine < groups.length; generatedLine++) {
      // Skip
      if (groups[generatedLine].equals("")) {
        continue;
      }
      String[] segments = groups[generatedLine].split(",");

      // The column position is relative
      int generatedColumn = 0;

      /*
        Each segment (separated by ',') represents a relative position of a single line in the generated code
       */
      for (String segment : segments) {
        int position = 0;
        // Decode the segment with the given position and also change the position as a side effect

        /*
         Field 1 zero-based starting column; if this is not the first segment, the column is seen
         relative to the one before
        */
        DecodingSegmentResult decodingSegmentResultField1 = decodeBase64VLQ(segment, position);
        int relativeGeneratedColumn = decodingSegmentResultField1.decoding;
        position = decodingSegmentResultField1.positionUpdate;

        if (relativeGeneratedColumn < 0) {
          Log.error("0xA0730 The provided segment is not valid base 64 VLQ (column less then 0).");
          continue;
        }
        generatedColumn += relativeGeneratedColumn;

        DecodedSource currentSource = null;
        int currentOriginalLine = 0;
        int currentOriginalColumn = 0;
        String currentName = null;

        // Optional Field 2, 3 and 4 of the segment
        {
          // Field 2 relative index of the (original) sources list this reference is pointing to
          DecodingSegmentResult decodingSegmentResultField2 = decodeBase64VLQ(segment, position);

          // The segment can either have 1, 4 or 5 fields
          // TODO: assuming the 5th field is fully optional and 2nd requires 3rd and 4th
          if (decodingSegmentResultField2 == null) {
            continue;
          }
          int relativeSourceIndex = decodingSegmentResultField2.decoding;
          position = decodingSegmentResultField2.positionUpdate;

          // Field 3 relative starting line of an original source
          DecodingSegmentResult decodingSegmentResultField3 = decodeBase64VLQ(segment, position);
          int relativeOriginalLine = decodingSegmentResultField3.decoding;
          position = decodingSegmentResultField3.positionUpdate;

          // Field 4 relative starting column of an original source
          DecodingSegmentResult decodingSegmentResultField4 = decodeBase64VLQ(segment, position);
          int relativeOriginalColumn = decodingSegmentResultField4.decoding;
          position = decodingSegmentResultField4.positionUpdate;

          sourceIndex += relativeSourceIndex;
          originalLine += relativeOriginalLine;
          originalColumn += relativeOriginalColumn;
          if (sourceIndex < 0 || sourceIndex >= decodedSources.size()) {
            Log.error("0xA0731 Mapping refers to an invalid source index.");
          } else {
            currentSource = decodedSources.get(sourceIndex);
          }
          if (originalLine < 0) {
            Log.error("0xA0732 Mapping refers to an invalid original line.");
          } else if (originalColumn < 0) {
            Log.error("0xA0733 Mapping refers to an invalid original column.");
          } else {
            currentOriginalLine = originalLine;
            currentOriginalColumn = originalColumn;
          }
        }


        // Optional Field 5 of the segment: index of the name referenced to in names list
        DecodingSegmentResult decodingSegmentResultField5 = decodeBase64VLQ(segment, position);
        if (decodingSegmentResultField5 != null) {
          int relativeNameIndex = decodingSegmentResultField5.decoding;
          position = decodingSegmentResultField5.positionUpdate;

          nameIndex += relativeNameIndex;

          if (nameIndex < 0 || nameIndex >= names.size()) {
            Log.error("0xA0734 Mapping refers to an invalid name index.");
          } else {
            currentName = names.get(nameIndex);
          }
        }

        if (position != segment.length()) {
          Log.error("0xA0735 The provided segment is not valid base 64 VLQ (invalid length).");
        }

        DecodedMapping decodedMapping = new DecodedMapping(
            generatedLine,
            generatedColumn,
            currentSource,
            currentOriginalLine,
            currentOriginalColumn,
            currentName
        );
        decodedMappings.add(decodedMapping);
      }
    }
    return decodedMappings;
  }

  private static DecodingSegmentResult decodeBase64VLQ(String base64VLQ, int position) {
    char[] base64VLQCharArr = base64VLQ.toCharArray();

    if (position == base64VLQCharArr.length) {
      return null;
    }

    byte first = getByteOfPosInStringOfBase64(base64VLQCharArr, position);

    int sign = ((first & 0x01) == 0x00) ? 1 : -1;
    int value = (first >> 1) & 0x0F;
    int nextShift = 16;
    byte currentByte = first;

    // Checking for the continuation bit
    while ((currentByte & 0x20) == 0x20) {
      position += 1;
      if (position == base64VLQCharArr.length) {
        Log.error("0xA0736 The provided segment is not valid base 64 VLQ (unexpected end of segment).");
        return null;
      }
      currentByte = getByteOfPosInStringOfBase64(base64VLQCharArr, position);
      int chunk = currentByte & 0x1F;
      value += chunk * nextShift;
      nextShift *= 32;
    }

    position += 1;
    if (value == 0 && sign == -1) {
      return new DecodingSegmentResult(Integer.MIN_VALUE, position);
    }
    return new DecodingSegmentResult(value * sign, position);
  }

  private static byte getByteOfPosInStringOfBase64(char[] base64String, int pos) {
    for (byte i = 0x00; i < base64Encoding.length; i++) {
      char character = base64Encoding[i];
      if (base64String[pos] == character) {
        return i;
      }
    }
    Log.error("0xA0737 Not a valid Base64 string");
    return 0;
  }

  private record DecodingSegmentResult(
      int decoding,
      int positionUpdate
  ) {

  }

  private static boolean isValidBase64VLQGroupings(String inputString) {

    // Null strings are not ASCII
    if (inputString == null) {
      return false;
    }
    // Check each character to ensure it is within the ASCII range
    for (char c : inputString.toCharArray()) {
      // Ascii 0-9
      if (c >= 48 && c <= 57) {
        return true;
      }
      // Ascii A-Z
      if (c >= 65 && c <= 90) {
        return true;
      }
      // Ascii a-z
      if (c >= 97 && c <= 122) {
        return true;
      }
      // Ascii ;
      if (c == 59) {
        return true;
      }
      // Ascii ,
      if (c == 44) {
        return true;
      }
      // Ascii /
      if (c == 47) {
        return true;
      }
      // Ascii /
      if (c == 43) {
        return true;
      }
    }
    return false;
  }

  private static void verifyVersion(JsonObject object) {
    if (!object.hasMember("version")) {
      Log.error("0xA0721 Source map must contain a 'version' member.");
      return;
    }
    Optional<Integer> versionOpt = object.getIntegerMemberOpt("version");
    if (versionOpt.isEmpty()) {
      Log.error("0xA0722 Source map 'version' member must be an integer.");
      return;
    }
    int version = versionOpt.get();
    if (version != 3) {
      Log.error("0xA0723 Unsupported source map version: " + version + ". " +
          "Only version 3 is supported.");
    }
  }
}
