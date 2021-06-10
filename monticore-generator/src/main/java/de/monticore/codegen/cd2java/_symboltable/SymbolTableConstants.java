/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

public class SymbolTableConstants {

  private SymbolTableConstants() {
  }

  /**
   * packages
   */

  public static final String SYMBOL_TABLE_PACKAGE = "_symboltable";

  /**
   * suffixes, prefixes
   */

  public static final String SYMBOL_SUFFIX = "Symbol";

  public static final String SCOPE_SUFFIX = "Scope";

  public static final String INTERFACE_PREFIX = "I";

  public static final String PHASED_SUFFIX = "Phased";

  public static final String SYMBOL_TABLE_CREATOR_SUFFIX = "SymbolTableCreator";

  public static final String SCOPES_GENITOR_SUFFIX = "ScopesGenitor";

  public static final String SYM_TAB_MILL_SUFFIX = "SymTabMill";

  public static final String ARTIFACT_PREFIX = "Artifact";

  public static final String GLOBAL_SUFFIX = "Global";

  public static final String COMMON_PREFIX = "Common";

  public static final String RESOLVER_SUFFIX = "Resolver";

  public static final String DELEGATOR_SUFFIX = "Delegator";

  public static final String STC_FOR = "%sSTCFor%s";

  public static final String SURROGATE_SUFFIX = "Surrogate";

  public static final String DE_SER_SUFFIX = "DeSer";

  public static final String SYMBOLS_2_JSON_SUFFIX = "Symbols2Json";

  /**
   * runtime classes
   */

  public static final String I_SYMBOL = "de.monticore.symboltable.ISymbol";

  public static final String I_SCOPE = "de.monticore.symboltable.IScope";

  public static final String I_ARTIFACT_SCOPE_TYPE = "de.monticore.symboltable.IArtifactScope";

  public static final String I_GLOBAL_SCOPE_TYPE = "de.monticore.symboltable.IGlobalScope";

  public static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  public static final String PATH = "java.nio.file.Path";

  public static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  public static final String PREDICATE = "java.util.function.Predicate";

  public static final String SYMBOL_MULTI_MAP = "com.google.common.collect.LinkedListMultimap";

  public static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  public static final String JSON_PRINTER = "de.monticore.symboltable.serialization.JsonPrinter";

  public static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  public static final String ACCESS_MODIFIER_ALL_INCLUSION = "de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION";

  public static final String DEQUE_TYPE = "Deque";

  public static final String SYMBOL_PATH_TYPE = "de.monticore.io.paths.MCPath";

  public static final String I_DE_SER = "de.monticore.symboltable.serialization.IDeSer";

  public static final String I_SYMBOL_DE_SER = "de.monticore.symboltable.serialization.ISymbolDeSer";

  /**
   * attribute names
   */

  public static final String ENCLOSING_SCOPE_VAR = "enclosingScope";

  public static final String SPANNED_SCOPE_VAR = "spannedScope";

  public static final String PACKAGE_NAME_VAR = "packageName";

  public static final String FULL_NAME_VAR = "fullName";

  public static final String AST_NODE_VAR = "astNode";

  public static final String SHADOWING_VAR = "shadowing";

  public static final String ORDERED_VAR = "ordered";

  public static final String SYMBOL_PATH_VAR = "symbolPath";

  public static final String FILE_EXTENSION_VAR = "fileExt";

  public static final String FOUND_SYMBOLS_VAR = "foundSymbols";

  public static final String PREDICATE_VAR = "predicate";

  public static final String NAME_VAR = "name";

  public static final String SYMBOL_VAR = "symbol";

  public static final String SCOPE_VAR = "scope";

  public static final String MODIFIER_VAR = "modifier";

  public static final String SCOPE_STACK_VAR = "scopeStack";

  public static final String SYMBOL_JSON_VAR = "symbolJson";

  public static final String SCOPE_JSON_VAR = "scopeJson";

  public static final String SYM_DESERS_VAR = "symbolDeSers";

  public static final String DESER_VAR = "deSer";

  public static final String FOUND_SYMBOL_DELEGATE = "false";

  public static final String PREDICATE_DELEGATE = "x -> true";

  /**
   * method names
   */

  public static final String ALREADY_RESOLVED = "AlreadyResolved";

  public static final String RESOLVE_MANY = "resolve%sMany";

  public static final String RESOLVE = "resolve%s";

  public static final String RESOLVE_ADAPTED = "resolveAdapted%s";

  public static final String CONTINUE_WITH_ENCLOSING_SCOPE = "continue%sWithEnclosingScope";

  public static final String DESERIALIZE = "deserialize";

}
