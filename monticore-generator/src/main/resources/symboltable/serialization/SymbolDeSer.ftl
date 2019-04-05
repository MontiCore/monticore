<#-- (c) https://github.com/MontiCore/monticore -->
${signature("languageName","className","symbolName", "symbolRule")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign superClass = " extends de.monticore.symboltable.CommonScope ">
<#assign superInterfaces = "">

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()}.serialization;

import ${genHelper.getSymbolTablePackage()}.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import com.google.gson.stream.JsonReader;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonConstants;
import de.se_rwth.commons.logging.Log;


/**
 * Class for serializing and deserializing ${symbolName}Symbols
 */
public class ${className} implements IDeSer<${symbolName}Symbol> {
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(${symbolName}Symbol toSerialize) {
    ${languageName}SymbolTablePrinter ${className?lower_case}SymbolTablePrinter = new ${languageName}SymbolTablePrinter();
    toSerialize.accept(${className?lower_case}SymbolTablePrinter);
    return ${className?lower_case}SymbolTablePrinter.getSerializedString();
  }
  
  /**
   * @throws IOException
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<${symbolName}Symbol> deserialize(String serialized) {
    JsonReader reader = new JsonReader(new StringReader(serialized));
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (!kind.equals(getSerializedKind())) {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer " + this.getClass()
                  + " failed");
            }
            else {
              Optional<${symbolName}Symbol> deserializedSymbol = deserialize${symbolName}Symbol(reader);
              reader.endObject();
              return deserializedSymbol;
            }
            break;
          default:
            reader.skipValue();
            break;
        }
      }
      reader.endObject();
      reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
  public Optional<${symbolName}Symbol> deserialize${symbolName}Symbol(JsonReader reader) {
    // Part 1: Initialize all attributes with default values
    Optional<String> name = Optional.empty();
    
    // Part 2: Read all available values from the Json string
    try {
      while (reader.hasNext()) {
        String key = reader.nextName();
        switch (key) {
          case JsonConstants.KIND:
            String kind = reader.nextString();
            if (!kind.equals(getSerializedKind())) {
              Log.error("Deserialization of symbol kind " + kind + " with DeSer " + this.getClass()
                  + " failed, because KIND was not the first attribute");
            }
            break;
          case JsonConstants.NAME:
            name = Optional.ofNullable(reader.nextString());
            break;
          default:
            reader.skipValue();
            break;
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    
    // Part 3: Construct the symbol/scope object if all required information is available
    if (name.isPresent()) {
      ${symbolName}Symbol symbol = new ${symbolName}Symbol(name.get());
      return Optional.ofNullable(symbol);
    }
    return Optional.empty();
    
  }
  
  /**
  * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
  */
  @Override
  public String getSerializedKind() {
    return ${symbolName}Symbol.class.getName();
  }
}