package de.monticore.generating.templateengine.source_mapping.encoding;

import de.monticore.generating.templateengine.source_mapping.DecodedMapping;
import de.monticore.generating.templateengine.source_mapping.DecodedSource;
import de.monticore.generating.templateengine.source_mapping.DecodedSourceMap;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.generating.templateengine.source_mapping.encoding.Base64VLQUtil.base64Encoding;

public class Decoding {

  public static DecodedSourceMap parseSourceMapJson(String jsonSourceMap, URL baseURL) {

    JsonElement jsonParsed = JsonParser.parse(jsonSourceMap);
    if(!jsonParsed.isJsonObject()) {
      System.out.println("Error expected source map json");
    }

    JsonObject jsonObject = jsonParsed.getAsJsonObject();

    verifyVersion(jsonObject);
    String mappings = getMappings(jsonObject);

    List<String> sources = getStringListOfMember("sources",jsonObject);

    List<DecodedSource> decodedSources = decodeSourceMapSources(
        baseURL,
        getSourceRoot(jsonObject),
        sources,
        getStringListOfMember("sourcesContent", jsonObject),
        getNumberListOfMember("ignoredSources", jsonObject));

    return new DecodedSourceMap(
        getFile(jsonObject),
        decodedSources,
        decodeMappings(mappings, getStringListOfMember("names",jsonObject), decodedSources)
    );


  }

  public static String getSourceRoot(JsonObject object) {
    if(object.hasMember("sourceRoot")) {
      if(object.getStringMemberOpt("sourceRoot").isPresent()) {
        return object.getStringMember("sourceRoot");
      }
    }
    System.out.println("Error no sourceRoot given.");
    return null;
  }

  public static String getMappings(JsonObject object) {
    if(object.hasMember("mappings")) {
      if(object.getStringMemberOpt("mappings").isPresent()) {
        return object.getStringMember("mappings");
      }
    }
    throw new RuntimeException("Error no mapping given.");
  }

  public static List<Integer> getNumberListOfMember(String memberName,JsonObject object) {
    List<Integer> integers = new ArrayList<>();
    if(object.hasMember(memberName)) {
      if(object.getMember(memberName).isJsonArray()) {
        JsonArray arr = object.getMember(memberName).getAsJsonArray();
        for (JsonElement e : arr.getValues()) {
          if(e.isJsonNumber()) {
            integers.add(e.getAsJsonNumber().getNumberAsInteger());
          } else {
            throw new RuntimeException("Error source not given as JsonString");
          }
        }
        return integers;
      }
    }
    System.out.println("Error no "+memberName+" given");
    return integers;
  }

  public static List<String> getStringListOfMember(String memberName,JsonObject object) {
    List<String> stringList = new ArrayList<>();
    if(object.hasMember(memberName)) {
      if(object.getMember(memberName).isJsonArray()) {
        JsonArray arr = object.getMember(memberName).getAsJsonArray();
        for (JsonElement e : arr.getValues()) {
          if(e.isJsonString()) {
            stringList.add(e.getAsJsonString().getValue());
          } else if(e.isJsonNull()){
            stringList.add(null);
          } else {
            throw new RuntimeException("Error source not given as JsonString");
          }
        }
        return stringList;
      }
    }
    System.out.println("Error no "+memberName+" given");
    return stringList;
  }

  public static String getFile(JsonObject object) {
    if(object.hasMember("file")) {
      if(object.getStringMemberOpt("file").isPresent()) {
        return object.getStringMember("file");
      }
    }
    throw new RuntimeException("No file given");
  }

  public static List<DecodedSource> decodeSourceMapSources(URL baseURL,
                                                           String sourceRoot,
                                                           List<String> sources,
                                                           List<String> sourcesContent,
                                                           List<Integer> ignoredSources) {
    List<DecodedSource> decodedSources = new ArrayList();
    String sourceURLPrefix = "";
    if(sourceRoot != null) {
      if(sourceRoot.contains("/")) {
      sourceURLPrefix = sourceRoot.substring(sourceRoot.lastIndexOf("/")+1);
      } else {
        sourceURLPrefix = "/";
      }
    }

    for (int i = 0; i< sources.size(); i++) {
      String source = sources.get(i);
      if(source!=null) {
        source = sourceURLPrefix + source;
      }
      URL sourceURL = null;
      try {
        sourceURL = new URL(baseURL, source);
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }

      boolean ignored = false;
      for (int index : ignoredSources) {
        if(i == index) {
          ignored = true;
          break;
        }
      }

      String content = (sourcesContent.size() > i)? sourcesContent.get(i): null;
      decodedSources.add(new DecodedSource(sourceURL, content, ignored));
    }
    return decodedSources;
  }

  public static List<DecodedMapping> decodeMappings(String mappings, List<String> names, List<DecodedSource> decodedSources) {
    if(!validateBase64VLQGroupings(mappings)) {
      throw new RuntimeException("Mapping not a valid base64 VLQ grouping.");
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
      if(groups[generatedLine].equals("")) {
        continue;
      }
      String[] segments = groups[generatedLine].split(",");

      // The columns position is relative
      int generatedColumn = 0;

      /*
        Each segment (separated by ',') represents a relative position of a single line in the generated code
       */
      for (String segment : segments) {
        int position = 0;
        // Decode the segment with the given position and also change the position as a side effect

        /*
         Field 1 zero-based starting column; if this is not the first segment the column is seen relative to the one before
        */
        DecodingSegmentResult decodingSegmentResultField1 = decodeBase64VLQ(segment, position);
        int relativeGeneratedColumn = decodingSegmentResultField1.decoding;
        position = decodingSegmentResultField1.positionUpdate;

        if(relativeGeneratedColumn < 0) {
          System.out.println("Error in decoding base 64 VLQ.");
          continue;
        }
        generatedColumn += relativeGeneratedColumn;

        DecodedMapping decodedMapping =new DecodedMapping(generatedLine, generatedColumn);
        decodedMappings.add(decodedMapping);

        // Optional Field 2, 3 and 4 of the segment
        {
          // Field 2 relative index of the (original) sources list this reference is pointing to
          DecodingSegmentResult decodingSegmentResultField2 = decodeBase64VLQ(segment, position);

          // The segment can either have 1, 4 or 5 fields
          // TODO: assuming the 5th field is fully optional and 2nd requires 3rd and 4th
          if(decodingSegmentResultField2 == null) {
            continue;
          }
          int relativeSourceIndex = decodingSegmentResultField2.decoding;
          position = decodingSegmentResultField2.positionUpdate;

          // Field 3 relative starting line of an original source
          DecodingSegmentResult decodingSegmentResultField3 = decodeBase64VLQ(segment, position);
          int relativeOriginalLine = decodingSegmentResultField3.decoding;
          position = decodingSegmentResultField3.positionUpdate;

          // Field 4 relative starting columnn of an original source
          DecodingSegmentResult decodingSegmentResultField4 = decodeBase64VLQ(segment, position);
          int relativeOriginalColumn = decodingSegmentResultField4.decoding;
          position = decodingSegmentResultField4.positionUpdate;

          sourceIndex += relativeSourceIndex;
          originalLine += relativeOriginalLine;
          originalColumn += relativeOriginalColumn;
          if (sourceIndex < 0 || sourceIndex >= decodedSources.size()) {
            System.out.println("Source index out of bounds.");
          } else {
            decodedMapping.originalSource = decodedSources.get(sourceIndex);
          }
          if(originalLine < 0 ) {
            System.out.println("Error while decoding originalLine cannot be negative.");
            throw new RuntimeException("Orig line");
            // Runtime error?
          } else if(originalColumn < 0){
            System.out.println("Error while decoding originalColumn cannot be negative.");
            throw new RuntimeException("Orig column");
          } else {
            decodedMapping.originalLine = originalLine;
            decodedMapping.originalColumn = originalColumn;
          }
        }


        {
          // Optional Field 5 of the segment: index of the name referenced to in names list
          DecodingSegmentResult decodingSegmentResultField5 = decodeBase64VLQ(segment, position);
          if(decodingSegmentResultField5 == null) {
            continue;
          }

          int relativeNameIndex = decodingSegmentResultField5.decoding;
          position = decodingSegmentResultField5.positionUpdate;

          nameIndex += relativeNameIndex;

          if (nameIndex < 0 || nameIndex >= names.size()) {
            System.out.println("Error nameIndex out of bounds.");
          } else {
            decodedMapping.name= names.get(nameIndex);
          }
        }
        if(position != segment.length()) {
          System.out.println("Error ");
        }
      }
    }
    return decodedMappings;
  }

  /**
   * On error return -1
   * @param base64VLQ
   * @return
   */
  public static DecodingSegmentResult decodeBase64VLQ(String base64VLQ, int position) {
    char[] base64VLQCharArr = base64VLQ.toCharArray();

    if(position==base64VLQCharArr.length) {
      return null;
    }

    byte first = getByteOfPosInStringOfBase64(base64VLQCharArr, position);

    int sign = ((first & 0x01) == 0x00)? 1 : -1;
    int value = (first >> 1) & 0x0F;
    int nextShift = 16;
    byte currentByte = first;

    // Checking for the continuation bit
    while((currentByte & 0x20) == 0x20) {
      position+=1;
      if(position==base64VLQCharArr.length) {
        throw new RuntimeException("Position is end of segment.");
      }
      currentByte = getByteOfPosInStringOfBase64(base64VLQCharArr, position);
      int chunk = currentByte & 0x1F;
      value += chunk * nextShift;
      nextShift *= 32;
    }

    position+=1;
    if(value==0 && sign==-1) {
      return new DecodingSegmentResult(Integer.MIN_VALUE, position);
    }
    return new DecodingSegmentResult(value * sign, position);
  }

  public static byte getByteOfPosInStringOfBase64(char[] base64String, int pos) {
    //System.out.println(base64String[pos]);
    for (byte i = 0x00; i<base64Encoding.length; i++) {
      char character = base64Encoding[i];
      if(base64String[pos] == character) {
        //System.out.println(i);
        return i;
      }
    }
    throw new RuntimeException("Error while decoding Base64");
  }

  public static class DecodingSegmentResult {
    public final int decoding;
    public final int positionUpdate;

    public DecodingSegmentResult(int decoding, int positionUpdate){
      this.decoding = decoding;
      this.positionUpdate = positionUpdate;
    }
  }

  public static boolean validateBase64VLQGroupings(String inputString) {

    // Null strings are not ASCII
    if (inputString == null) {
      return false;
    }
    // Check each character to ensure it is within the ASCII range
    for (char c : inputString.toCharArray()) {
      // Ascii 0-9
      if(c >= 48 && c <= 57) {
        return true;
      }
      // Ascii A-Z
      if(c >= 65 && c <= 90) {
        return true;
      }
      // Ascii a-z
      if(c >= 97 && c <= 122) {
        return true;
      }
      // Ascii ;
      if(c == 59) {
        return true;
      }
      // Ascii ,
      if(c == 44) {
        return true;
      }
      // Ascii /
      if(c == 47) {
        return true;
      }
      // Ascii /
      if(c == 43) {
        return true;
      }
    }
    return false;
  }

  public static void verifyVersion(JsonObject object) {
    if(object.hasMember("version")) {
      object.getIntegerMemberOpt("version").ifPresentOrElse(i -> {
        if (i != 3) {
          System.out.println("Error version not 3");
        }
      }, () -> {
        System.out.println("Error version not a number");
      });
    } else {
      System.out.println("Error no version");
    }
  }
}

