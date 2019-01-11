/*
 * Copyright (c) 2018 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serializing;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

/**
 * 
 * Interface for all Serialization classes for specific symbols and scopes that relies on Google's Gson API for json.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 * @param <T>
 */
public interface ISerialization<T> extends JsonSerializer<T>, JsonDeserializer<T>{
  
  public Class<T> getSerializedClass();
  
  public static final String PACKAGE = "package";
  
  public static final String IMPORTS = "imports";
  
  public static final String SUBSCOPES = "subScopes";
  
  public static final String SYMBOLS = "symbols";
  
  public static final String EXPORTS_SYMBOLS = "exportsSymbols";
  
  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";
  
  public static final String NAME = "name";
  
  public static final String CLASS = "class";
  
  @Deprecated //Can be removed after release 5.0.3
  public static final String KIND = "kind";
  
  // TODO: Can be removed if ScopeSpanningSymbols are removed
  public static final String SCOPESPANNING_SYMBOL = "spanningSymbol";
  
}
