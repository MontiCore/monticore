/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class TransformationHelper {

  public static final String DEFAULT_FILE_EXTENSION = ".java";

  public static final String AST_PREFIX = "AST";

  public static final String LIST_SUFFIX = "s";

  public static final int STAR = -1;

  private static List<String> reservedCdNames = Arrays.asList(
      // CD4A
      "derived",
      "association",
      "composition",
      // Common.mc4
      "local",
      "readonly");

  private TransformationHelper() {
    // noninstantiable
  }

  public static String getClassProdName(ASTClassProd classProd) {
    return classProd.getName();
  }

  public static String typeToString(ASTMCType type) {
    if (type instanceof ASTMCGenericType) {
      return ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      return ((ASTMCArrayType) type).printTypeWithoutBrackets();
    }
    return type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

  public static String simpleName(ASTMCType type) {
    String name;
    if (type instanceof ASTMCGenericType) {
      name = ((ASTMCGenericType) type).printWithoutTypeArguments();
    } else if (type instanceof ASTMCArrayType) {
      name = ((ASTMCArrayType) type).printTypeWithoutBrackets();
    } else {
      name = type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
    return Names.getSimpleName(name);
  }

  /**
   * Pretty prints a CD AST to a String object.
   *
   * @param astNode the top node of the CD AST to be pretty printed
   */
  // TODO: should be placed somewhere in the UML/P CD project
  public static String prettyPrint(ASTCD4AnalysisNode astNode) {
    // set up objects
    CDPrettyPrinter prettyPrinter = new CDPrettyPrinter(
        new IndentPrinter());

    // run, check result and return
    String prettyPrintedAst = prettyPrinter.prettyprint(astNode);
    checkArgument(!isNullOrEmpty(prettyPrintedAst));
    return prettyPrintedAst;
  }

  public static Optional<String> getUsageName(ASTNode root,
                                              ASTNode successor) {
    List<ASTNode> intermediates = ASTNodes
        .getIntermediates(root, successor);
    for (ASTNode ancestor : Lists.reverse(intermediates)) {
      if (ancestor instanceof ASTConstantGroup && ((ASTConstantGroup) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTConstantGroup) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTNonTerminal && ((ASTNonTerminal) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTNonTerminal) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTNonTerminalSeparator) {
        return Optional.of(((ASTNonTerminalSeparator) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTTerminal && ((ASTTerminal) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTTerminal) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTKeyTerminal && ((ASTKeyTerminal) ancestor).isPresentUsageName()) {
        return Optional.of(((ASTKeyTerminal) ancestor).getUsageName());
      }
      if (ancestor instanceof ASTAdditionalAttribute && ((ASTAdditionalAttribute) ancestor).isPresentName()) {
        return Optional.of(((ASTAdditionalAttribute) ancestor).getName());
      }
    }
    return Optional.empty();
  }

  public static Optional<String> getName(ASTNode node) {
    if (node instanceof ASTNonTerminal) {
      return Optional.of(((ASTNonTerminal) node)
          .getName());
    }
    if (node instanceof ASTConstant) {
      return Optional.of(((ASTConstant) node)
          .getName());
    }
    if (node instanceof ASTNonTerminalSeparator) {
      return Optional.of(((ASTNonTerminalSeparator) node)
          .getName());
    }
    if (node instanceof ASTTerminal) {
      return Optional.of(((ASTTerminal) node).getName());
    }
    if (node instanceof ASTAdditionalAttribute && ((ASTAdditionalAttribute) node).isPresentName()) {
      return Optional.of(((ASTAdditionalAttribute) node).getName());
    }
    return Optional.empty();
  }

  public static ASTCDParameter createParameter(String typeName,
                                               String parameterName) {
    ASTCDParameter parameter = CD4AnalysisNodeFactory
        .createASTCDParameter();
    parameter.setMCType(TransformationHelper.createType(typeName));
    parameter.setName(parameterName);
    return parameter;
  }

  public static ASTModifier createPublicModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setPublic(true);
    return modifier;
  }

  public static ASTMCGenericType createType(
      String typeName, String generics) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCGenericType> optType = null;
    try {
      optType = parser.parse_StringMCGenericType(typeName + "<" + generics + ">");
    } catch (IOException e) {
      Log.error("0xA4103 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCType createType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCType> optType = null;
    try {
      optType = parser.parse_StringMCType(typeName);
    } catch (IOException e) {
      Log.error("0xA4104 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCReturnType createReturnType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCReturnType> optType = null;
    try {
      optType = parser.parse_StringMCReturnType(typeName);
    } catch (IOException e) {
      Log.error("0xA4105 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCObjectType createObjectType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCObjectType> optType = null;
    try {
      optType = parser.parse_StringMCObjectType(typeName);
    } catch (IOException e) {
      Log.error("0xA4106 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static String getPackageName(ProdSymbol symbol) {
    // return grammar.getName().toLowerCase() + AST_DOT_PACKAGE_SUFFIX_DOT;
    return getGrammarName(symbol) + ".";
  }

  public static String getPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getPackageList());
    if (!packageName.isEmpty()) {
      packageName = packageName + ".";
    }
    return packageName + cdCompilationUnit.getCDDefinition().getName() + ".";
  }

  public static Set<String> getAllGrammarConstants(ASTMCGrammar grammar) {
    Set<String> constants = new HashSet<>();
    MCGrammarSymbol grammarSymbol = grammar.getSymbol();
    Preconditions.checkState(grammarSymbol != null);
    for (RuleComponentSymbol component : grammarSymbol.getProds().stream()
        .flatMap(p -> p.getProdComponents().stream()).collect(Collectors.toSet())) {
      if (component.isIsConstantGroup()) {
        for (String subComponent : component.getSubProdList()) {
          constants.add(subComponent);
        }
      }
    }
    for (ProdSymbol type : grammarSymbol.getProds()) {
      if (type.isIsEnum() && type.isPresentAstNode()
          && type.getAstNode() instanceof ASTEnumProd) {
        for (ASTConstant enumValue : ((ASTEnumProd) type.getAstNode()).getConstantList()) {
          String humanName = enumValue.isPresentHumanName()
              ? enumValue.getHumanName()
              : enumValue.getName();
          constants.add(humanName);
        }
      }
    }
    return constants;
  }


  /**
   * Checks if a handwritten class with the given qualifiedName (dot-separated)
   * exists on the target path
   *
   * @param qualifiedName name of the class to search for
   * @return true if a handwritten class with the qualifiedName exists
   */
  public static boolean existsHandwrittenClass(IterablePath targetPath,
                                               String qualifiedName) {
    Path handwrittenFile = Paths.get(Names
        .getPathFromPackage(qualifiedName)
        + DEFAULT_FILE_EXTENSION);
    Log.debug("Checking existence of handwritten class " + qualifiedName
        + " by searching for "
        + handwrittenFile.toString(), TransformationHelper.class.getName());
    Optional<Path> handwrittenFilePath = targetPath.getResolvedPath(handwrittenFile);
    boolean result = handwrittenFilePath.isPresent();
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(handwrittenFilePath.get(),
          handwrittenFile);
    }
    Reporting.reportHWCExistenceCheck(targetPath,
        handwrittenFile, handwrittenFilePath);
    return result;
  }

  /**
   * Get the corresponding CD for MC grammar if exists
   *
   * @param ast
   * @return
   */
  public static Optional<ASTCDCompilationUnit> getCDforGrammar(CD4AnalysisGlobalScope globalScope,
                                                               ASTMCGrammar ast) {
    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getName());

    Optional<CDDefinitionSymbol> cdSymbol = globalScope.resolveCDDefinitionDown(
        qualifiedCDName);

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().isPresentAstNode()) {
      Log.debug("Got existed symbol table for " + cdSymbol.get().getFullName(),
          TransformationHelper.class.getName());
      return Optional
          .of((ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode());
    }

    return Optional.empty();
  }

  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
                                                             ASTMCGrammar grammar, ASTCDClass cdClass) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference);

    if (!typeSymbol.isPresent()) {
      addStereoType(cdClass,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  // TODO GV: remove this if CDInterface and CDClass have a common type CDType
  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
                                                             ASTMCGrammar grammar, ASTCDInterface interf) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference);

    if (!typeSymbol.isPresent()) {
      addStereoType(interf,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  public static Optional<ProdSymbol> resolveAstRuleType(ASTMCGrammar node, ASTMCType type) {
    String simpleName = Names.getSimpleName(typeToString(type));
    if (!simpleName.startsWith(AST_PREFIX)) {
      return Optional.empty();
    }
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(node,
        simpleName
            .substring(AST_PREFIX.length()));
    if (ruleSymbol.isPresent() && istPartOfGrammar(ruleSymbol.get())) {
      return ruleSymbol;
    }
    return Optional.empty();
  }

  // TODO GV, PN: change it
  public static boolean istPartOfGrammar(ProdSymbol rule) {
    return rule.getEnclosingScope().isPresentSpanningSymbol()
        && rule.getEnclosingScope().getSpanningSymbol() instanceof MCGrammarSymbol;
  }

  public static String getGrammarName(ProdSymbol rule) {
    return Names.getQualifier(rule.getFullName());
  }

  public static String getGrammarNameAsPackage(ProdSymbol rule) {
    return getGrammarName(rule) + ".";
  }

  public static String getQualifiedAstName(Optional<ProdSymbol> typeSymbol, ASTMCType type) {
    if (typeSymbol.isPresent()) {
      return Names.getQualifier(typeSymbol.get().getFullName()) + "." + AST_PREFIX + typeSymbol.get().getName();
    } else {
      return type.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    }
  }

  public static void addStereoType(ASTCDType type, String stereotypeName,
                                   String stereotypeValue) {
    if (!type.isPresentModifier()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName) {
    if (!type.isPresentModifier()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier(),
        stereotypeName);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName) {
    if (!type.isPresentModifier()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier(),
        stereotypeName);
  }

  public static void addStereoType(ASTCDAttribute attribute,
                                   String stereotypeName,
                                   String stereotypeValue) {
    if (!attribute.isPresentModifier()) {
      attribute.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(attribute.getModifier(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDDefinition type, String stereotypeName,
                                   String stereotypeValue) {
    if (!type.isPresentModifier()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName,
                                        String stereotypeValue) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4AnalysisNodeFactory
          .createASTCDStereotype());
    }
    List<ASTCDStereoValue> stereoValueList = astModifier.getStereotype()
        .getValueList();
    ASTCDStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTCDStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValue.setValue(stereotypeValue);
    stereoValueList.add(stereoValue);
  }

  public static void addStereotypeValue(ASTModifier astModifier,
                                        String stereotypeName) {
    if (!astModifier.isPresentStereotype()) {
      astModifier.setStereotype(CD4AnalysisNodeFactory
          .createASTCDStereotype());
    }
    List<ASTCDStereoValue> stereoValueList = astModifier.getStereotype()
        .getValueList();
    ASTCDStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTCDStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValueList.add(stereoValue);
  }

  /**
   * Checks whether the given attribute is a collection type written as String
   * (e.g., List<...>).
   *
   * @param attribute The input attribute
   * @return true if the input attribute is a collection type, false otherwise
   */
  public static boolean isCollectionType(ASTCDAttribute attribute) {
    String type = attribute.printType();
    if (type.startsWith("Collection<") || type.startsWith("List<") || type.startsWith("Set<") || type.startsWith("java.util.Collection<") || type.startsWith("java.util.List<") || type.startsWith("java.util.Set<")) {
      return true;
    }
    return false;
  }

  /**
   * Computes the simple type of an attribute from a collection type.
   *
   * @param attribute The input attribute
   * @return The simple type contained by a collection
   */
  public static String getSimpleTypeFromCollection(ASTCDAttribute attribute) {
    String simpleType = attribute.printType();
    if (simpleType.startsWith("Collection<")) {
      simpleType = simpleType.replaceFirst("Collection<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("List<")) {
      simpleType = simpleType.replaceFirst("List<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("Set<")) {
      simpleType = simpleType.replaceFirst("Set<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.Collection<")) {
      simpleType = simpleType.replaceFirst("java.util.Collection<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.List<")) {
      simpleType = simpleType.replaceFirst("java.util.List<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    } else if (simpleType.startsWith("java.util.Set<")) {
      simpleType = simpleType.replaceFirst("java.util.Set<", "");
      simpleType = simpleType.substring(0, simpleType.length() - 1);
    }
    return Names.getSimpleName(simpleType);
  }

  public static String getJavaAndCdConformName(String name) {
    Log.errorIfNull(name);
    return getCdLanguageConformName(getJavaConformName(name));
  }

  public static String getCdLanguageConformName(String name) {
    if (reservedCdNames.contains(name)) {
      return (JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED + name).intern();
    }
    return name.intern();
  }

  public static String getJavaConformName(String name) {
    return JavaNamesHelper.javaAttribute(name);
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
        superRuleNodes.add(symbol.get().getAstNode());
      }
    }
    return superRuleNodes;
  }
}
