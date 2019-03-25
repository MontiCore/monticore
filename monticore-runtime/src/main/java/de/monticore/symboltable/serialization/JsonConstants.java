/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface JsonConstants {
  
  public static final String PACKAGE = "package";
  
  public static final String IMPORTS = "imports";
  
  public static final String SUBSCOPES = "subScopes";
  
  @Deprecated // there will be lists of symbols per individual kind
  public static final String SYMBOLS = "symbols";
  
  public static final String EXPORTS_SYMBOLS = "exportsSymbols";
  
  public static final String IS_SHADOWING_SCOPE = "isShadowingScope";
  
  public static final String NAME = "name";
  
  public static final String KIND = "kind";
  
  public static final String SCOPE_SPANNING_SYMBOL = "spanningSymbol";
  
}
