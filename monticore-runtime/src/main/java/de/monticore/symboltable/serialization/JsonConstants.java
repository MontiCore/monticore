package de.monticore.symboltable.serialization;

/**
 * This class contains constants for Json keys commonly used for the serialization of symbol tables.
 * These constants avoid inconsistencies between the Json keys used for storing and those expected
 * while loading an object.
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
