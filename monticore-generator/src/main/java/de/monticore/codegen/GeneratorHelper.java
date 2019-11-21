/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd.CD4AnalysisHelper;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.cd.prettyprint.CDPrettyPrinterDelegator;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolLoader;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.prettyprint.Grammar_WithConceptsPrettyPrinter;
import de.monticore.io.FileReaderWriter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.monticore.grammar.Multiplicity.multiplicityByAlternative;
import static de.monticore.grammar.Multiplicity.multiplicityByIteration;
import static java.util.Collections.max;

public class GeneratorHelper {

  public static final String AST_PREFIX = "AST";

  public static final String AST_NODE_CLASS_NAME = "de.monticore.ast.ASTNode";

  public static final String AST_PACKAGE_SUFFIX = "_ast";

  public static final String VISITOR_PACKAGE_SUFFIX = "_visitor";

  public static final String AST_DOT_PACKAGE_SUFFIX_DOT = "._ast.";

  public static final String OPTIONAL = "Optional";

  public static final String SYMBOL = "Symbol";

  public static final String SCOPE = "Scope";

  public static final String BASE = "Node";

  public static final String CD_EXTENSION = ".cd";

  public static final String GET_SUFFIX_LIST = "List";

  public static final String JAVA_LIST = "java.util.List";

  public static final int STAR = -1;

  public static final String DEPRECATED = "@Deprecated";

  protected static final String LOG_NAME = "GeneratorHelper";

  public static final String GET_PREFIX_BOOLEAN = "is";

  public static final String GET_SUFFIX_OPTINAL = "Opt";

  public static final String GET_PREFIX = "get";

  // TODO: reserve names of the base grammars like CD4A, Types, Common ...
  private static List<String> reservedCdNames = Arrays.asList(new String[]{
      // CD4A
      "derived",
      "association",
      "composition",
      // Common.mc4
      "local",
      "readonly"});

  static Grammar_WithConceptsPrettyPrinter mcPrettyPrinter;

  static CDPrettyPrinterDelegator cdPrettyPrinter;

  protected static Collection<String> additionalAttributes = Lists.newArrayList(SYMBOL, SCOPE);

  protected ASTCDDefinition cdDefinition;

  protected String packageName;

  protected String qualifiedName;

  // preserves order of appearance in the extends list of the grammar
  protected List<String> superGrammarCds = new ArrayList<>();

  protected CD4AnalysisGlobalScope symbolTable;

  protected CDDefinitionSymbol cdSymbol;

  protected ASTCDCompilationUnit topAst;

  public GeneratorHelper(ASTCDCompilationUnit topAst, CD4AnalysisGlobalScope symbolTable) {
    Preconditions.checkArgument(topAst.getCDDefinition() != null);

    this.topAst = topAst;

    cdDefinition = topAst.getCDDefinition();

    this.symbolTable = symbolTable;

    // Qualified Name
    qualifiedName = Names.getQualifiedName(topAst.getPackageList(), getCdName());

    // CD package
    packageName = getCdPackage(qualifiedName);

    // CD symbol
    this.cdSymbol = getCd();

    // Create list of CDs for super grammars
    for (ASTMCImportStatement importSt : topAst.getMCImportStatementList()) {
      if (importSt.isStar()) {
        superGrammarCds.add(importSt.getQName());
      }
    }
  }


  /**
   * @return cdSymbol
   */
  public CDDefinitionSymbol getCdSymbol() {
    return this.cdSymbol;
  }


  // -------------- Static methods ------------

  public static String getSimpleName(List<String> nameAsList) {
    if (nameAsList == null || nameAsList.isEmpty()) {
      return "";
    }
    return nameAsList.get(nameAsList.size() - 1);
  }

  public static boolean isOptional(ASTCDAttribute attribute)
  {
    return isOptional(attribute.getMCType());
  }

  public static boolean isOptional(ASTMCType type) {
    return isGenericTypeWithOneTypeArgument(type, OPTIONAL);
  }

  public static boolean isGenericTypeWithOneTypeArgument(ASTMCType type, String simpleRefTypeName) {
    if (!(type instanceof ASTMCGenericType)) {
      return false;
    }
    ASTMCGenericType simpleRefType = (ASTMCGenericType) type;
    if (simpleRefType.getMCTypeArgumentList().isEmpty() || simpleRefType.getMCTypeArgumentList().size() != 1) {
      return false;
    }

    if (simpleRefType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter()).split("\\.").length == 1 && simpleRefTypeName.contains(".")) {
      if (simpleRefTypeName.endsWith("." + simpleRefType.printWithoutTypeArguments())) {
        return true;
      }
    }
    if (simpleRefType.printWithoutTypeArguments().equals(simpleRefTypeName)) {
      return true;
    }
    return false;
  }

  public static boolean isOptional(CDTypeSymbol type) {
    // TODO proper implementation
    if (OPTIONAL.equals(type.getName())) {
      return true;
    }
    return false;
  }

  public static boolean isOptional(CDFieldSymbol field) {
    return field.getType().isSymbolLoaded() && isOptional(field.getType().getLoadedSymbol());
  }

  public static boolean isBoolean(ASTCDAttribute attribute) {
    return "boolean".equals(attribute.printType());
  }

  public boolean isAstNode(CDTypeSymbolLoader type) {
    String typeName = type.getName();
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    } else {
      List<String> listName = Arrays.asList(typeName.split("\\."));
      if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
        return false;
      }
    }

    if (!type.getActualTypeArguments().isEmpty()) {
      return false;
    }
    return type.isSymbolLoaded() && !type.getLoadedSymbol().isIsEnum();
  }

  public boolean isOptionalAstNode(CDFieldSymbol field) {
    // TODO for default types (e.g. String) this field.getType() would try to
    // resolve the default type but fail
    // hence we currently use the ast methods instead of
    // "return isOptionalAstNode(field.getType())"
    return isOptionalAstNode(field.getType());
  }

  public boolean isListAstNode(CDFieldSymbol field) {
    // TODO for default types (e.g. String) this field.getType() would try to
    // resolve the default type but fail
    // hence we currently use the ast methods instead of
    // "return isOptionalAstNode(field.getType())"
    return isListAstNode(field.getType());
  }

  public boolean isAstNode(CDFieldSymbol field) {
    // TODO for default types (e.g. String) this field.getType() would try to
    // resolve the default type but fail
    // hence we currently use the ast methods instead of
    // "return isAstNode(field.getType())"
    return isAstNode(field.getType());
  }

  public static boolean isListType(String type) {
    // TODO : use symbol table
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }

  public static boolean isMapType(String type) {
    // TODO : use symbol table
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Map".equals(type) || "java.util.Map".equals(type)
        || "HashMap".equals(type) || "java.util.HashMap".equals(type);
  }

  public static boolean isAbstract(ASTCDMethod method, ASTCDClass type) {
    return CD4AnalysisHelper.isAbstract(method);
  }

  public boolean isEnum(String qualifiedName) {
    Optional<CDTypeSymbol> cdType = resolveCdType(qualifiedName);
    return cdType.isPresent() && cdType.get().isIsEnum();
  }

  public static Grammar_WithConceptsPrettyPrinter getMcPrettyPrinter() {
    if (mcPrettyPrinter == null) {
      mcPrettyPrinter = new Grammar_WithConceptsPrettyPrinter(new IndentPrinter());
    }
    return mcPrettyPrinter;
  }

  public static CDPrettyPrinterDelegator getCDPrettyPrinter() {
    if (cdPrettyPrinter == null) {
      cdPrettyPrinter = new CDPrettyPrinterDelegator(new IndentPrinter());
    }
    return cdPrettyPrinter;
  }

  public boolean isListAstNode(ASTCDAttribute attribute) {
    if (!attribute.isPresentSymbol()) {
      return false;
    }
    return isListAstNode(attribute.getSymbol().getType());
  }

  public boolean isListAstNode(CDTypeSymbolLoader type) {
    if (!isListType(type.getName())) {
      return false;
    }
    List<CDTypeSymbolLoader> typeArgs = type.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return false;
    }

    return isAstNode(typeArgs.get(0));
  }

  public boolean isList(CDTypeSymbolLoader type) {
    if (!type.getName().equals(JAVA_LIST)) {
      return false;
    }
    return type.getActualTypeArguments().size() == 1;
  }

  public boolean isOptionalAstNode(CDTypeSymbolLoader type) {
    if (!type.getName().equals(OPTIONAL)) {
      return false;
    }
    List<CDTypeSymbolLoader> typeArgs = type.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return false;
    }

    return isAstNode(typeArgs.get(0));
  }


  public static String getJavaConformName(String name) {
    return JavaNamesHelper.javaAttribute(name);
  }

  public static String getCdLanguageConformName(String name) {
    if (reservedCdNames.contains(name)) {
      return (JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED + name).intern();
    }
    return name.intern();
  }

  public static boolean isQualified(MCGrammarSymbolLoader grammarRef) {
    if (grammarRef.getName().contains(".")) {
      return true;
    }
    if (grammarRef.isSymbolLoaded()) {
      MCGrammarSymbol grammarSymbol = grammarRef.getLoadedSymbol();
      if (!grammarSymbol.getFullName().contains(".")) {
        // The complete name has no package name, therefore the grammarRefName
        // without "." is qualified
        return true;
      }
    }
    return false;
  }

  public static boolean isQualified(String name) {
    if (name.contains(".")) {
      return true;
    }
    return false;
  }

  public static String getJavaAndCdConformName(String name) {
    Log.errorIfNull(name);
    return getCdLanguageConformName(getJavaConformName(name));
  }


  public static String getDotPackageName(String packageName) {
    if (packageName.isEmpty() || packageName.endsWith(".")) {
      return packageName;
    }
    return packageName + ".";
  }


  public static String getPackageName(String packageName, String suffix) {
    return packageName.isEmpty() ? suffix : packageName
        + "." + suffix;
  }

  public static String getPackageName(ASTMCGrammar astGrammar, String suffix) {
    String qualifiedGrammarName = astGrammar.getPackageList().isEmpty()
        ? astGrammar.getName()
        : Joiner.on('.').join(Names.getQualifiedName(astGrammar.getPackageList()),
        astGrammar.getName());
    return Joiner.on('.').join(qualifiedGrammarName.toLowerCase(), suffix);
  }

   /**
   * TODO: Gets not transformed attribute name according to the original name in
   * MC grammar
   *
   * @param attributeName
   * @return
   */
  public static String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  /**
   * Prints Cd4Analysis AST to the file with the extension
   * {@link GeneratorHelper#CD_EXTENSION} in the given subdirectory
   *
   * @param astCd        - the top node of the Cd4Analysis AST
   * @param outputPath   - output path
   * @param subDirectory - sub directory of the output path
   */
  public static void prettyPrintAstCd(ASTCDCompilationUnit astCd, File outputPath,
                                      String subDirectory) {
    String fileName = Names.getSimpleName(astCd.getCDDefinition().getName());
    storeInFile(astCd, fileName, CD_EXTENSION, outputPath, subDirectory);
  }

  /**
   * Prints AST node to the file with the given name and extension in the given
   * subdirectory of the given output directory
   *
   * @param ast           - the AST node to print
   * @param fileName
   * @param fileExtension
   * @param outputPath
   * @param subDirectory
   */
  public static void storeInFile(ASTCD4AnalysisNode ast, String fileName, String fileExtension,
                                 File outputPath,
                                 String subDirectory) {
    Path path = createDestinationFile(fileName, fileExtension, outputPath, subDirectory);

    String output = getCDPrettyPrinter().prettyprint(ast);

    new FileReaderWriter().storeInFile(path, output);
  }

  private static Path createDestinationFile(String fileName, String fileExtension,
                                            File outputDirectory, String subDirectory) {
    final Path filePath = Paths.get(subDirectory, fileName + fileExtension);
    return Paths.get(outputDirectory.getAbsolutePath(), filePath.toString());
  }

  /**
   * This method gets all diagrams that participate in the given one by getting
   * (1) the class diagram itself and (2) all imported class diagrams (as well
   * as recursively their imported class diagrams as well). If a CD occurs twice
   * in this import-graph, the algorithm understands that this is exactly the
   * same diagram and thus <em>ignores the second</em> occurrence. Note that
   * this is different to the rules within a grammar - there the last occurrence
   * would be used, because it overrides the former declarations, but on a class
   * diagram level exists no overriding, because different references to a
   * diagram always mean the same diagram (i.e., the one on the model path with
   * <em>the</em> matching name).
   *
   * @param cd the class diagram to get all participating class diagrams for
   * @return the class diagrams starting with the current grammar and then in
   * order of appearance in the imports of the class diagram.
   */
  public List<CDDefinitionSymbol> getAllCds(CDDefinitionSymbol cd) {
    List<CDDefinitionSymbol> resolvedCds = new ArrayList<>();
    // the cd itself
    resolvedCds.add(cd);
    resolvedCds.addAll(getAllSuperCds(cd));
    return resolvedCds;
  }

  /**
   * This method gets all diagrams that participate in the given one by getting
   * (1) the class diagram itself and (2) all imported class diagrams (as well
   * as recursively their imported class diagrams as well). If a CD occurs twice
   * in this import-graph, the algorithm understands that this is exactly the
   * same diagram and thus <em>ignores the second</em> occurrence. Note that
   * this is different to the rules within a grammar - there the last occurrence
   * would be used, because it overrides the former declarations, but on a class
   * diagram level exists no overriding, because different references to a
   * diagram always mean the same diagram (i.e., the one on the model path with
   * <em>the</em> matching name).
   *
   * @param cd the class diagram to get all participating class diagrams for
   * @return the class diagrams starting with the current grammar and then in
   * order of appearance in the imports of the class diagram.
   */
  public List<CDDefinitionSymbol> getAllSuperCds(CDDefinitionSymbol cd) {
    List<CDDefinitionSymbol> resolvedCds = new ArrayList<>();
    // imported cds
    for (String importedCdName : cd.getImports()) {
      Log.trace("Resolving the CD: " + importedCdName, LOG_NAME);
      Optional<CDDefinitionSymbol> importedCd = resolveCd(importedCdName);
      if (!importedCd.isPresent()) {
        Log.error("0xA8451 The class diagram could not be resolved: " + importedCdName);
      } else {
        // recursively add imported cds
        /* ... and prevent duplicates. First occurrence of a grammar is used.
         * I.e., the algorithm understands that when one grammar is imported
         * multiple times by different diagrams, it is still the same diagram.
         * Note that this is independent from the rules within a grammar - there
         * the last occurrence would be used, because it overrides the former
         * declarations . */
        List<CDDefinitionSymbol> recursivImportedCds = getAllCds(importedCd.get());
        for (CDDefinitionSymbol recImport : recursivImportedCds) {
          if (!resolvedCds
              .stream()
              .filter(c -> c.getFullName().equals(recImport.getFullName()))
              .findAny()
              .isPresent()) {
            resolvedCds.add(recImport);
          }
        }
      }
    }
    return resolvedCds;
  }

  public CDDefinitionSymbol getCd() {
    Optional<CDDefinitionSymbol> cdOpt = resolveCd(getQualifiedCdName());
    if (!cdOpt.isPresent()) {
      Log.error("0xA0487 The class diagram could not be resolved: " + getQualifiedCdName());
    }
    return cdOpt.get();
  }

  /**
   * @return packageName
   */
  public String getPackageName() {
    return this.packageName;
  }

  public String getQualifiedCdName() {
    return qualifiedName;
  }

  /**
   * @return superGrammars in order of appearance in the grammars extends list.
   */
  public List<String> getSuperGrammarCds() {
    return this.superGrammarCds;
  }

  public String getCdName() {
    return cdDefinition.getName();
  }

  /**
   * Resolves the CD of the given qualified name
   *
   * @param qualifiedCdName full qualified name to resolve the CD for
   * @return the {@link CDDefinitionSymbol}
   */
  public Optional<CDDefinitionSymbol> resolveCd(String qualifiedCdName) {
    return symbolTable.resolveCDDefinition(qualifiedCdName);
  }

  public Optional<CDTypeSymbol> resolveCdType(String type) {
    // Log.trace("Resolve: " + type + " -> " + symbolTable.resolve(type,
    // CDTypeSymbol.KIND), LOG_NAME);
    return symbolTable.resolveCDType(type);
  }

  public static String getCdPackage(String qualifiedCdName) {
    return qualifiedCdName.toLowerCase();
  }

  public static Multiplicity getMultiplicity(ASTMCGrammar grammar,
                                             ASTNode nonTerminal) {
    Multiplicity byAlternative = multiplicityByAlternative(grammar,
        nonTerminal);
    Multiplicity byIteration = multiplicityByIteration(grammar,
        nonTerminal);
    return max(Lists.newArrayList(byIteration,
        byAlternative));
  }

  /**
   * @return the super productions defined in all super grammars (including
   * transitive super grammars)
   */
  public static List<ASTProd> getAllSuperProds(ASTProd astNode) {
    List<ASTProd> directSuperRules = getDirectSuperProds(astNode);
    List<ASTProd> allSuperRules = new ArrayList<>();
    for (ASTProd superRule : directSuperRules) {
      allSuperRules.addAll(getAllSuperProds(superRule));
    }
    allSuperRules.addAll(directSuperRules);
    return allSuperRules;
  }

  /**
   * @return the super productions defined in direct super grammars
   */
  public static List<ASTProd> getDirectSuperProds(ASTProd astNode) {
    if (astNode instanceof ASTClassProd) {
      List<ASTProd> directSuperProds = resolveRuleReferences(
          ((ASTClassProd) astNode).getSuperRuleList(), astNode);
      directSuperProds.addAll(
          resolveRuleReferences(((ASTClassProd) astNode).getSuperInterfaceRuleList(), astNode));
      return directSuperProds;
    } else if (astNode instanceof ASTInterfaceProd) {
      return resolveRuleReferences(((ASTInterfaceProd) astNode).getSuperInterfaceRuleList(), astNode);
    }
    return Collections.emptyList();
  }

  /**
   * @return the production definitions of B & C in "A extends B, C"
   */
  public static List<ASTProd> resolveRuleReferences(List<ASTRuleReference> ruleReferences,
                                                    ASTProd nodeWithSymbol) {
    List<ASTProd> superRuleNodes = new ArrayList<>();
    for (ASTRuleReference superRule : ruleReferences) {
      Optional<ProdSymbol> symbol = nodeWithSymbol.getEnclosingScope().resolveProd(superRule.getName());
      if (symbol.isPresent() && symbol.get().isPresentAstNode()) {
        superRuleNodes.add((ASTProd) symbol.get().getAstNode());
      }
    }
    return superRuleNodes;
  }

  public static Map<ASTProd, List<ASTNonTerminal>> getInheritedNonTerminals(ASTProd sourceNode) {
    return GeneratorHelper.getAllSuperProds(sourceNode).stream()
        .distinct()
        .collect(Collectors.toMap(Function.identity(),
            astProd -> ASTNodes.getSuccessors(astProd, ASTNonTerminal.class)));
  }

  /**
   * Generates an error code suffix in format "_ddd" where d is a decimal. If
   * there is an ast-name then always the same error code will be generated.
   *
   * @param ast
   * @return generated error code suffix in format "xddd" where d is a decimal.
   */
  public static String getGeneratedErrorCode(ASTNode ast) {
    int hashCode = 0;
    // If there is an ast-name then always generate the same error code.

    hashCode = Math.abs(ast.toString().hashCode());

    String errorCodeSuffix = String.valueOf(hashCode);
    return "x" + (hashCode < 1000 ? errorCodeSuffix : errorCodeSuffix
        .substring(errorCodeSuffix.length() - 3));
  }

  /**
   * @return name of the language's AST-Nodes marker interface
   * @see #getASTNodeBaseType(String)
   */
  public String getASTNodeBaseType() {
    return getASTNodeBaseType(getCdName());
  }

  /**
   * @return name of the language's AST-Nodes marker interface
   */
  public static String getASTNodeBaseType(String languageName) {
    return AST_PREFIX + languageName + BASE;
  }


  /**
   * Gets the qualified java AST type for the given type.
   *
   * @param type the type symbol
   * @return [astPackage of the type].[type.getName()]
   */
  public static String getJavaASTName(CDTypeSymbol type) {
    return Joiners.DOT.join(Names.getQualifier(type.getFullName()).toLowerCase(), AST_PACKAGE_SUFFIX, type.getName());
  }

  public Optional<String> getSymbolName(String name, MCGrammarSymbol grammarSymbol) {
    String astName = name.substring(name.lastIndexOf("AST") + 3);
    Optional<ProdSymbol> mcProdSymbol = grammarSymbol.getProd(astName);
    if (mcProdSymbol.isPresent() && mcProdSymbol.get().isIsSymbolDefinition()) {
      return Optional.of(mcProdSymbol.get().getName());
    }
    return Optional.empty();
  }

  public boolean isScopeClass(String name, MCGrammarSymbol grammarSymbol) {
    String astName = name.substring(name.lastIndexOf("AST") + 3);
    Optional<ProdSymbol> mcProdSymbol = grammarSymbol.getProd(astName);
    if (mcProdSymbol.isPresent() && mcProdSymbol.get().isIsScopeSpanning()) {
      return true;
    }
    return false;
  }

  public static String getPlainGetter(CDFieldSymbol field) {
    String astType = field.getType().getName();
    StringBuilder sb = new StringBuilder();
    if (CDTypes.isBoolean(astType)) {
      sb.append(GET_PREFIX_BOOLEAN);
    } else {
      sb.append(GET_PREFIX);
    }
    sb.append(StringTransformations.capitalize(getNativeAttributeName(field.getName())));
    if (isListType(astType)) {
      //list suffix `s` not added to field name
      sb.append(GET_SUFFIX_LIST);
    }
    return sb.toString();
  }

  public String getAstClassNameForASTLists(CDTypeSymbolLoader field) {
    List<CDTypeSymbolLoader> typeArgs = field.getActualTypeArguments();
    if (typeArgs.size() != 1) {
      return AST_NODE_CLASS_NAME;
    }
    String arg = typeArgs.get(0).getLoadedSymbol().getFullName();
    return Joiners.DOT.join(Names.getQualifier(arg).toLowerCase(), AST_PACKAGE_SUFFIX, Names.getSimpleName(arg));
  }

  public boolean isString(String type) {
    return "String".equals(type) || "java.lang.String".equals(type);
  }

  public boolean isString(ASTMCQualifiedType type) {
    String typeName = getSimpleName(type.getNameList());
    return "String".equals(typeName) || "java.lang.String".equals(typeName);
  }

  public String getVisitorPackage() {
    return getVisitorPackage(getPackageName());
  }

  public static String getVisitorPackage(String qualifiedLanguageName) {
    return getPackageName(qualifiedLanguageName.toLowerCase(),
            VISITOR_PACKAGE_SUFFIX);
  }


}
