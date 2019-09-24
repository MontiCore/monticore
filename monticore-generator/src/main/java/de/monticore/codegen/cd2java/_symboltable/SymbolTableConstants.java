/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

public class SymbolTableConstants {

  /**
   * packages
   */

  public static final String SYMBOL_TABLE_PACKAGE = "_symboltable";

  public static final String SERIALIZATION_PACKAGE = "serialization";

  /**
   * suffixes, prefixes
   */

  public static final String SYMBOL_SUFFIX = "Symbol";

  public static final String SCOPE_SUFFIX = "Scope";

  public static final String INTERFACE_PREFIX = "I";

  public static final String SYMBOL_TABLE_CREATOR_SUFFIX = "SymbolTableCreator";

  public static final String SYM_TAB_MILL_SUFFIX = "SymTabMill";

  public static final String ARTIFACT_PREFIX = "Artifact";

  public static final String GLOBAL_SUFFIX = "Global";

  public static final String COMMON_PREFIX = "Common";

  public static final String RESOLVING_DELEGATE_SUFFIX = "ResolvingDelegate";

  public static final String DELEGATOR_SUFFIX = "Delegator";

  public static final String STC_FOR = "%sSTCFor%s";

  public static final String LANGUAGE_SUFFIX = "Language";

  public static final String REFERENCE_SUFFIX = "Reference";

  public static final String DE_SER_SUFFIX = "DeSer";

  public static final String SYMBOL_TABLE_PRINTER_SUFFIX = "SymbolTablePrinter";

  public static final String MODEL_LOADER_SUFFIX = "ModelLoader";

  /**
   * runtime classes
   */

  public static final String I_SYMBOL = "de.monticore.symboltable.ISymbol";

  public static final String I_SCOPE = "de.monticore.symboltable.IScope";

  public static final String IMPORT_STATEMENT = "de.monticore.symboltable.ImportStatement";

  public static final String I_SYMBOL_REFERENCE = "de.monticore.symboltable.references.ISymbolReference";

  public static final String QUALIFIED_NAMES_CALCULATOR = "de.monticore.symboltable.names.QualifiedNamesCalculator";

  public static final String I_MODELING_LANGUAGE = "de.monticore.IModelingLanguage";

  public static final String I_MODEL_LOADER = "de.monticore.modelloader.IModelLoader<%s, %s>";

  public static final String AST_PROVIDER = "de.monticore.modelloader.AstProvider<%s>";

  public static final String MODEL_COORDINATE = "de.monticore.io.paths.ModelCoordinate";

  public static final String PATH = "java.nio.file.Path";

  public static final String I_SCOPE_SPANNING_SYMBOL = "de.monticore.symboltable.IScopeSpanningSymbol";

  public static final String PREDICATE = "java.util.function.Predicate<%s>";

  public static final String SYMBOL_MULTI_MAP = "com.google.common.collect.LinkedListMultimap<String, %s>";

  public static final String JSON_OBJECT = "de.monticore.symboltable.serialization.json.JsonObject";

  public static final String JSON_PRINTER = "de.monticore.symboltable.serialization.JsonPrinter";

  public static final String ACCESS_MODIFIER = "de.monticore.symboltable.modifiers.AccessModifier";

  public static final String DEQUE_TYPE = "Deque<%s>";

  public static final String DEQUE_WILDCARD_TYPE = "Deque<? extends %s>";

  public static final String I_DE_SER_TYPE = "de.monticore.symboltable.serialization.IDeSer<%s>";

  public static final String MODEL_PATH_TYPE = "de.monticore.io.paths.ModelPath";

  /**
   * attribute names
   */

  public static final String ENCLOSING_SCOPE_VAR = "enclosingScope";

  public static final String SPANNED_SCOPE_VAR = "spanned%sScope";

  public static final String PACKAGE_NAME_VAR = "packageName";

  public static final String FULL_NAME_VAR = "fullName";

  public static final String AST_NODE_VAR = "astNode";

  public static final String SHADOWING_VAR = "shadowing";

  public static final String MODEL_PATH_VAR = "modelPath";

  public static final String MODEL_LOADER_VAR = "modelLoader";

  public static final String FOUND_SYMBOLS_VAR = "foundSymbols";

  public static final String PREDICATE_VAR = "predicate";

  public static final String NAME_VAR = "name";

  public static final String SYMBOL_VAR = "symbol";

  public static final String SCOPE_VAR = "scope";

  public static final String MODIFIER_VAR = "modifier";

  public static final String SCOPE_STACK_VAR = "scopeStack";

  public static final String SYMBOL_JSON_VAR = "symbolJson";

  public static final String SCOPE_JSON_VAR = "scopeJson";

  /**
   * method names
   */

  public static final String ALREADY_RESOLVED = "AlreadyResolved";

  public static final String RESOLVE_MANY = "resolve%sMany";

  public static final String RESOLVE_ADAPTED = "resolveAdapted%s";

  public static final String CONTINUE_WITH_ENCLOSING_SCOPE = "continue%sWithEnclosingScope";

  public static final String CALCULATED_MODEL_NAME = "calculatedModelName";
}
