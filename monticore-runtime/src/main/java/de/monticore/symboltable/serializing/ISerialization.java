/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface ISerialization<T> extends JsonSerializer<T>, JsonDeserializer<T>{
  
  public Class<T> getSerializedClass();
  
  public static final String PACKAGE = "package";
  
  public static final String IMPORTS = "imports";
  
  public static final String SUBSCOPES = "subScopes";
  
  public static final String SYMBOLS = "symbols";
  
  public static final String EXPORTS_SYMBOLS = "exportsSymbols";
  
  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";
  
  public static final String NAME = "name";
  
  public static final String KIND = "kind";
  
}
