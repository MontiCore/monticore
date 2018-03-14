/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.codegen.GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.io.paths.IterablePath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.prettyprint.CDPrettyPrinterConcreteVisitor;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

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
  
  public static String typeToString(ASTType type) {
    if (type instanceof ASTSimpleReferenceType) {
      return Names.getQualifiedName(
          ((ASTSimpleReferenceType) type).getNameList());
    }
    else if (type instanceof ASTPrimitiveType) {
      return type.toString();
    }
    return "";
  }
  
  public static String typeReferenceToString(ASTGenericType typeReference) {
    return typeReference.getTypeName();
  }
  
  /**
   * Pretty prints a CD AST to a String object.
   * 
   * @param cdCompilationUnit the top node of the CD AST to be pretty printed
   */
  // TODO: should be placed somewhere in the UML/P CD project
  public static String prettyPrint(ASTCD4AnalysisNode astNode) {
    // set up objects
    CDPrettyPrinterConcreteVisitor prettyPrinter = new CDPrettyPrinterConcreteVisitor(
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
      if (ancestor instanceof ASTAttributeInAST) {
        return ((ASTAttributeInAST) ancestor).getNameOpt();
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
    if (node instanceof ASTAttributeInAST) {
      return ((ASTAttributeInAST) node).getNameOpt();
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
    parameter.setType(TransformationHelper.createSimpleReference(typeName));
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
  
  public static ASTSimpleReferenceType createSimpleReference(
      String typeName, String... generics) {
    ASTSimpleReferenceType reference = TypesNodeFactory
        .createASTSimpleReferenceType();
    
    // set the name of the type
    ArrayList<String> name = new ArrayList<String>();
    name.add(typeName);
    reference.setNameList(name);
    
    // set generics
    if (generics.length > 0) {
      List<ASTTypeArgument> typeArguments = new ArrayList<>();
      for (String generic : generics) {
        typeArguments.add(createSimpleReference(generic));
      }
      reference.setTypeArguments(TypesNodeFactory
          .createASTTypeArguments(typeArguments));
    }
    
    return reference;
  }
  
  public static ASTVoidType createVoidType() {
    return TypesNodeFactory.createASTVoidType();
  }
  
  public static String grammarName2PackageName(MCGrammarSymbol grammar) {
    return grammar.getFullName() + ".";
  }
  
  public static String getPackageName(MCProdSymbol symbol) {
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
    for (MCProdComponentSymbol component : grammarSymbol.getProds().stream()
        .flatMap(p -> p.getProdComponents().stream()).collect(Collectors.toSet())) {
      if (component.isConstantGroup()) {
        for (MCProdComponentSymbol subComponent : component.getSubProdComponents()) {
          if (subComponent.isConstant()) {
            constants.add(subComponent.getName());
          }
        }
      }
    }
    for (MCProdSymbol type : grammarSymbol.getProds()) {
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
    boolean result = targetPath.exists(handwrittenFile);
    if (result) {
      Reporting.reportUseHandwrittenCodeFile(targetPath.getResolvedPath(handwrittenFile).get(),
          handwrittenFile);
    }
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
    
    Optional<CDSymbol> cdSymbol = globalScope.<CDSymbol> resolveDown(
        qualifiedCDName, CDSymbol.KIND);

    if (cdSymbol.isPresent() && cdSymbol.get().getEnclosingScope().getAstNode().isPresent()) {
      Log.debug("Got existed symbol table for " + cdSymbol.get().getFullName(), TransformationHelper.class.getName());
      return Optional.of((ASTCDCompilationUnit) cdSymbol.get().getEnclosingScope().getAstNode().get());
    }
    
   return Optional.empty();
  }
  
  public static String getQualifiedTypeNameAndMarkIfExternal(
      ASTGenericType ruleReference,
      ASTMCGrammar grammar, ASTCDClass cdClass) {
    
    Optional<MCProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);
    
    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference, grammar);
    
    if (!typeSymbol.isPresent()) {
      addStereoType(cdClass,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }
    
    return qualifiedRuleName;
  }
  
  // TODO GV: remove this if CDInterface and CDClass have a common type CDType
  public static String getQualifiedTypeNameAndMarkIfExternal(
      ASTGenericType ruleReference,
      ASTMCGrammar grammar, ASTCDInterface interf) {
    
    Optional<MCProdSymbol> typeSymbol = resolveAstRuleType(grammar, ruleReference);
    
    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference, grammar);
    
    if (!typeSymbol.isPresent()) {
      addStereoType(interf,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }
    
    return qualifiedRuleName;
  }
  
  public static Optional<MCProdSymbol> resolveAstRuleType(ASTNode node, ASTGenericType type) {
    if (!type.getNameList().isEmpty()) {
      String simpleName = type.getNameList().get(type.getNameList().size() - 1);
      if (!simpleName.startsWith(AST_PREFIX)) {
        return Optional.empty();
      }
      Optional<MCProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(node,
          simpleName
              .substring(AST_PREFIX.length()));
      if (ruleSymbol.isPresent() && istPartOfGrammar(ruleSymbol.get())) {
        return ruleSymbol;
      }
    }
    return Optional.empty();
  }
  
  // TODO GV, PN: change it
  public static boolean istPartOfGrammar(MCProdSymbol rule) {
    return rule.getEnclosingScope().getAstNode().isPresent()
        && rule.getEnclosingScope().getAstNode().get() instanceof ASTMCGrammar;
  }
  
  public static String getAstPackage(MCProdSymbol rule) {
    return AstGeneratorHelper.getAstPackage(Names.getQualifier(rule.getFullName()).toLowerCase());
  }
  
  public static String getGrammarName(MCProdSymbol rule) {
    return Names.getQualifier(rule.getFullName());
  }
  
  public static String getGrammarNameAsPackage(MCProdSymbol rule) {
    return getGrammarName(rule) + ".";
  }
  
  public static boolean checkIfExternal(ASTNode node, ASTGenericType type) {
    return !resolveAstRuleType(node, type).isPresent();
  }
  
  public static String getQualifiedAstName(
      Optional<MCProdSymbol> typeSymbol, ASTGenericType type,
      ASTMCGrammar grammar) {
    if (!typeSymbol.isPresent()) {
      return type.getTypeName();
    }
    if (type.getNameList().size() > 1) {
      return type.getTypeName();
    }
    String refGrammarName = getGrammarName(typeSymbol.get());
    if (grammar.getSymbol().isPresent()
        && grammar.getSymbol().get().getFullName().equals(refGrammarName)) {
      return type.getTypeName();
    }
    return refGrammarName + "." + type.getTypeName();
  }
  
  public static void addStereoType(ASTCDClass type, String stereotypeName,
      String stereotypeValue) {
    if (!type.getModifierOpt().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifierOpt().get(),
        stereotypeName, stereotypeValue);
  }
  
  public static void addStereoType(ASTCDInterface type,
      String stereotypeName,
      String stereotypeValue) {
    if (!type.isPresentModifier()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier(),
        stereotypeName, stereotypeValue);
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
          .createASTStereotype());
    }
    List<ASTStereoValue> stereoValueList = astModifier.getStereotype()
        .getValueList();
    ASTStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValue.setValue(stereotypeValue);
    stereoValueList.add(stereoValue);
  }
  
}
