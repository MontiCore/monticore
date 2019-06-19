/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.prettyprint.CDPrettyPrinter;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.codegen.GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;

public final class TransformationHelper {

  public static final String DEFAULT_FILE_EXTENSION = ".java";

  public static final String AST_PREFIX = "AST";

  public static final String AST_PACKAGE_SUFFIX = "_ast";

  public static final String LIST_SUFFIX = "s";

  private TransformationHelper() {
    // noninstantiable
  }

  public static String getClassProdName(ASTClassProd classProd) {
    return classProd.getName();
  }

  public static String typeToString(ASTMCType type) {
    if (type instanceof ASTMCObjectType) {
      return Names.getQualifiedName(
          ((ASTMCObjectType) type).getNameList());
    }
    else if (type instanceof ASTMCPrimitiveType) {
      return type.toString();
    }
    return "";
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
      if (ancestor instanceof ASTConstantGroup) {
        return ((ASTConstantGroup) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTNonTerminal) {
        return ((ASTNonTerminal) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTNonTerminalSeparator) {
        return ((ASTNonTerminalSeparator) ancestor)
            .getUsageNameOpt();
      }
      if (ancestor instanceof ASTTerminal) {
        return ((ASTTerminal) ancestor).getUsageNameOpt();
      }
      if (ancestor instanceof ASTAdditionalAttribute) {
        return ((ASTAdditionalAttribute) ancestor).getNameOpt();
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
    if (node instanceof ASTAdditionalAttribute) {
      return ((ASTAdditionalAttribute) node).getNameOpt();
    }
    return Optional.empty();
  }

  public static ASTModifier createFinalModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setFinal(true);
    return modifier;
  }

  public static ASTCDParameter createParameter(String typeName,
                                               String parameterName) {
    ASTCDParameter parameter = CD4AnalysisNodeFactory
        .createASTCDParameter();
    parameter.setMCType(TransformationHelper.createType(typeName));
    parameter.setName(parameterName);
    return parameter;
  }

  public static ASTModifier createPrivateModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setPrivate(true);
    return modifier;
  }

  public static ASTModifier createStaticModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setStatic(true);
    return modifier;
  }

  public static ASTModifier createAbstractModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setAbstract(true);
    return modifier;
  }

  public static ASTModifier createProtectedModifier() {
    ASTModifier modifier = CD4AnalysisNodeFactory.createASTModifier();
    modifier.setProtected(true);
    return modifier;
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
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCObjectType createType(String typeName) {
    CD4AnalysisParser parser = new CD4AnalysisParser();
    Optional<ASTMCObjectType> optType = null;
    try {
      optType = parser.parse_StringMCObjectType(typeName);
    } catch (IOException e) {
      Log.error("0xA4036 Cannot create ASTType " + typeName + " during transformation from MC4 to CD4Analysis");
    }
    return optType.get();
  }

  public static ASTMCVoidType createVoidType() {
    return MCBasicTypesNodeFactory.createASTMCVoidType();
  }

  public static String grammarName2PackageName(MCGrammarSymbol grammar) {
    return grammar.getFullName() + ".";
  }

  public static String getPackageName(ProdSymbol symbol) {
    // return grammar.getName().toLowerCase() + AST_DOT_PACKAGE_SUFFIX_DOT;
    return getGrammarName(symbol) + ".";
  }

  public static String getAstPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getPackageList());
    if (!packageName.isEmpty()) {
      packageName = packageName + ".";
    }
    return packageName + cdCompilationUnit.getCDDefinition().getName().toLowerCase()
        + AST_DOT_PACKAGE_SUFFIX_DOT;
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

  public static String getBaseNodeName(
      ASTCDCompilationUnit cdCompilationUnit) {
    return getAstPackageName(cdCompilationUnit)
        + GeneratorHelper.getASTNodeBaseType(cdCompilationUnit.getCDDefinition().getName());
  }

  public static Set<String> getAllGrammarConstants(ASTMCGrammar grammar) {
    Set<String> constants = new HashSet<>();
    MCGrammarSymbol grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(grammar).get();
    Preconditions.checkState(grammarSymbol != null);
    for (RuleComponentSymbol component : grammarSymbol.getProds().stream()
        .flatMap(p -> p.getProdComponents().stream()).collect(Collectors.toSet())) {
      if (component.isConstantGroup()) {
        for (RuleComponentSymbol subComponent : component.getSubProdComponents()) {
          if (subComponent.isConstant()) {
            constants.add(subComponent.getName());
          }
        }
      }
    }
    for (ProdSymbol type : grammarSymbol.getProds()) {
      if (type.isEnum() && type.getAstNode().isPresent()
          && type.getAstNode().get() instanceof ASTEnumProd) {
        for (ASTConstant enumValue : ((ASTEnumProd) type.getAstNode().get()).getConstantList()) {
          String humanName = enumValue.isPresentHumanName()
              ? enumValue.getHumanName()
              : enumValue.getName();
          constants.add(humanName);
        }
      }
    }
    return constants;
  }

  public static java.util.Optional<ASTCDAttribute> createAttributeUsingCdParser(
      String toParse) throws IOException {
    checkArgument(!Strings.isNullOrEmpty(toParse));
    java.util.Optional<ASTCDAttribute> astCDAttribute = (new CD4AnalysisParser())
        .parseCDAttribute(new StringReader(toParse));
    return astCDAttribute;
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
  public static Optional<ASTCDCompilationUnit> getCDforGrammar(GlobalScope globalScope,
      ASTMCGrammar ast) {
    final String qualifiedCDName = Names.getQualifiedName(ast.getPackageList(), ast.getName());

    Optional<CDDefinitionSymbol> cdSymbol = globalScope.<CDDefinitionSymbol>resolveDown(
        qualifiedCDName, CDDefinitionSymbol.KIND);

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      Log.debug("Got existed symbol table for " + cdSymbol.get().getFullName(),
          TransformationHelper.class.getName());
      return Optional
          .of((ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get());
    }

    return Optional.empty();
  }

  public static String getQualifiedTypeNameAndMarkIfExternal(ASTMCType ruleReference,
      ASTMCGrammar grammar, ASTCDClass cdClass) {

    Optional<ProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);

    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference, grammar);

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
        typeSymbol, ruleReference, grammar);

    if (!typeSymbol.isPresent()) {
      addStereoType(interf,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }

    return qualifiedRuleName;
  }

  public static Optional<ProdSymbol> resolveAstRuleType(ASTNode node, ASTMCType type) {
    if (!type.getNameList().isEmpty()) {
      String simpleName = type.getNameList().get(type.getNameList().size() - 1);
      if (!simpleName.startsWith(AST_PREFIX)) {
        return Optional.empty();
      }
      Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(node,
          simpleName
              .substring(AST_PREFIX.length()));
      if (ruleSymbol.isPresent() && istPartOfGrammar(ruleSymbol.get())) {
        return ruleSymbol;
      }
    }
    return Optional.empty();
  }

  // TODO GV, PN: change it
  public static boolean istPartOfGrammar(ProdSymbol rule) {
    return rule.getEnclosingScope().getAstNode().isPresent()
        && rule.getEnclosingScope().getAstNode().get() instanceof ASTMCGrammar;
  }

  public static String getAstPackage(ProdSymbol rule) {
    return AstGeneratorHelper.getAstPackage(Names.getQualifier(rule.getFullName()).toLowerCase());
  }

  public static String getGrammarName(ProdSymbol rule) {
    return Names.getQualifier(rule.getFullName());
  }

  public static String getGrammarNameAsPackage(ProdSymbol rule) {
    return getGrammarName(rule) + ".";
  }

  public static boolean checkIfExternal(ASTNode node, ASTMCType type) {
    return !resolveAstRuleType(node, type).isPresent();
  }

  public static String getQualifiedAstName(
          Optional<ProdSymbol> typeSymbol, ASTMCType type,
          ASTMCGrammar grammar) {
    if (!typeSymbol.isPresent()) {
      return FullGenericTypesPrinter.printType(type);
    }
    if (type.getNameList().size() > 1) {
      return FullGenericTypesPrinter.printType(type);
    }
    String refGrammarName = getGrammarName(typeSymbol.get());
    if (grammar.isPresentSymbol()
        && grammar.getSymbol().getFullName().equals(refGrammarName)) {
      return FullGenericTypesPrinter.printType(type);
    }
    return refGrammarName + "." + FullGenericTypesPrinter.printType(type);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName,
      String stereotypeValue) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName, stereotypeValue);
  }

  public static void addStereoType(ASTCDType type, String stereotypeName) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
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

  public static void addStereoType(ASTCDMethod method,
      String stereotypeName,
      String stereotypeValue) {
    method.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    addStereotypeValue(method.getModifier(),
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
}
