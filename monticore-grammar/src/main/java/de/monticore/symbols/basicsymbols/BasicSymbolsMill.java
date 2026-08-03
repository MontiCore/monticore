/*(c) https://github.com/MontiCore/monticore*/
package de.monticore.symbols.basicsymbols;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Collections;
import java.util.jar.JarFile;

public class BasicSymbolsMill extends BasicSymbolsMillTOP {

  public static final String INT = "int";

  public static final String DOUBLE = "double";

  public static final String FLOAT = "float";

  public static final String SHORT = "short";

  public static final String LONG = "long";

  public static final String BOOLEAN = "boolean";

  public static final String BYTE = "byte";

  public static final String CHAR = "char";

  public static final String NULL = "null";

  public static final String VOID = "void";

  public static final List<String> PRIMITIVE_LIST = Collections.unmodifiableList(Lists.newArrayList(INT, DOUBLE, FLOAT, SHORT, LONG, BOOLEAN, BYTE, CHAR, NULL, VOID));

  public static final String STRING = "String";

  public static final String OBJECT = "Object";

  public static void initializePrimitives(){
    getMill()._initializePrimitives();
  }

  public void _initializePrimitives(){
    IBasicSymbolsGlobalScope gs = globalScope();

    for(String primitive: PRIMITIVE_LIST){
      gs.add(createPrimitive(primitive));
    }
  }

  protected TypeSymbol createPrimitive(String name){
    return typeSymbolBuilder()
            .setName(name)
            .setEnclosingScope(globalScope())
            .setFullName(name)
            .setSpannedScope(scope())
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .build();
  }

  /**
   * This is only required if the String Symbol is not provided otherwise,
   * e.g., using Class2MC,
   * as it is required for, e.g., String literals.
   * It is deliberately a trivial Symbol without any fields / methods.
   */
  public static void initializeString() {
    // reusing the initializer
    getMill()._initializeString();
  }

  protected void _initializeString() {
    IBasicSymbolsGlobalScope gs = globalScope();
    gs.add(typeSymbolBuilder()
        .setName(STRING)
        // this is not Java's String
        .setFullName(STRING)
        .setEnclosingScope(gs)
        .setSpannedScope(scope())
        .setAccessModifier(AccessModifier.ALL_INCLUSION)
        .build()
    );
  }

  /**
   * This is only required if the Object Symbol is not provided otherwise,
   * e.g., using Class2MC,
   * It is deliberately a trivial Symbol without any fields / methods.
   */
  public static void initializeObject() {
    getMill()._initializeObject();
  }

  protected void _initializeObject() {
    IBasicSymbolsGlobalScope gs = globalScope();
    gs.add(typeSymbolBuilder()
      .setName(OBJECT)
      // this is not Java's Object
      .setFullName(OBJECT)
      .setEnclosingScope(gs)
      .setSpannedScope(scope())
      .setAccessModifier(AccessModifier.ALL_INCLUSION)
      .build()
    );
  }

  public static void initializeStreams() {
    getMill()._initializeStreams();
  }

  protected void _initializeStreams() {
    IBasicSymbolsGlobalScope gs = globalScope();
    URL streamURL = BasicSymbolsMill.class.getClassLoader().getResource("Stream.symtabdefinitionsym");
    if (streamURL == null) {
      Log.error("0xA7106 Resource Stream.symtabdefinitionsym not found");
    }
    else {
      try {
        JarURLConnection urlConnection = (JarURLConnection) streamURL.openConnection();
        JarFile jar = urlConnection.getJarFile();
        Path jarPath = Path.of(jar.getName());
        gs.getSymbolPath().addEntry(jarPath);
      } catch (IOException e) {
        Log.error("0xA7107 Unable to read Stream.symtabdefinitionsym from disk", e);
      }
    }
  }

}
