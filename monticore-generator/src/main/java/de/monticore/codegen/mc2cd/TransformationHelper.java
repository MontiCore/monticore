/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.mc2cd;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.codegen.GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.GeneratorHelper;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.io.paths.IterablePath;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.languages.grammar.MCTypeSymbol.KindType;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCD4AnalysisNode;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTStereoValue;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;
import de.monticore.umlcd4a.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.umlcd4a.prettyprint.CDPrettyPrinterConcreteVisitor;
import de.monticore.utils.ASTNodes;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

public final class TransformationHelper {
  
  public static final String DEFAULT_FILE_EXTENSION = ".java";
  
  public final static String GENERATED_CLASS_SUFFIX = "TOP";
  
  public static final String AST_PREFIX = "AST";
  
  public static final String AST_PACKAGE_SUFFIX = "_ast";
  
  private TransformationHelper() {
    // noninstantiable
  }
  
  public static String getClassProdName(ASTClassProd classProd) {
    return classProd.getName();
  }
  
  public static String typeToString(ASTType type) {
    if (type instanceof ASTSimpleReferenceType) {
      return Names.getQualifiedName(
          ((ASTSimpleReferenceType) type).getNames());
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
        return Optional.ofNullable(((ASTConstantGroup) ancestor)
            .getUsageName().orElse(null));
      }
      if (ancestor instanceof ASTNonTerminal) {
        return Optional.ofNullable(((ASTNonTerminal) ancestor)
            .getUsageName().orElse(null));
      }
      if (ancestor instanceof ASTNonTerminalSeparator) {
        return Optional.ofNullable(((ASTNonTerminalSeparator) ancestor)
            .getUsageName().orElse(null));
      }
      if (ancestor instanceof ASTTerminal) {
        return Optional.ofNullable(((ASTTerminal) ancestor).getUsageName()
            .orElse(null));
      }
      if (ancestor instanceof ASTAttributeInAST){
        return ((ASTAttributeInAST) ancestor).getName();
      }
    }
    return Optional.empty();
  }
  
  public static Optional<String> getName(ASTNode node) {
    try {
      Method getNameMethod = node.getClass().getMethod("getName");
      String name = (String) getNameMethod.invoke(node);
      return Optional.ofNullable(name);
    }
    catch (ClassCastException
           | InvocationTargetException
           | IllegalAccessException
           | IllegalArgumentException
           | NoSuchMethodException
           | SecurityException e) {
      return Optional.empty();
    }
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
    reference.setNames(name);
    
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
  
  public static String getAstPackageName(MCGrammarSymbol grammar) {
    // return grammar.getName().toLowerCase() + AST_DOT_PACKAGE_SUFFIX_DOT;
    return getPackageName(grammar);
  }
  
  public static String getPackageName(MCGrammarSymbol grammar) {
    return grammar.getFullName() + ".";
  }
  
  public static String getAstPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getPackage());
    if (!packageName.isEmpty()) {
      packageName = packageName + ".";
    }
    return packageName + cdCompilationUnit.getCDDefinition().getName().toLowerCase()
        + AST_DOT_PACKAGE_SUFFIX_DOT;
  }
  
  public static String getPackageName(
      ASTCDCompilationUnit cdCompilationUnit) {
    String packageName = Names
        .getQualifiedName(cdCompilationUnit.getPackage());
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
  
  // TODO SO <- GV: change
  public static List<String> getAllGrammarConstants(ASTMCGrammar grammar) {
    List<String> constants = new ArrayList<>();
    MCGrammarSymbol grammarSymbol = MCGrammarSymbolTableHelper.getMCGrammarSymbol(grammar).get();
    Preconditions.checkState(grammarSymbol != null);
    for (MCTypeSymbol type : grammarSymbol.getTypes()) {
      if (type.getKindOfType().equals(KindType.ENUM)
          || type.getKindOfType().equals(KindType.CONST)) {
        for (String enumValue : type.getEnumValues()) {
          if (!constants.contains(enumValue)) {
            constants.add(enumValue);
          }
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
    else {
      Reporting.reportUseHandwrittenCodeFile(null, handwrittenFile);
    }
    return result;
  }
  
  public static String getQualifiedTypeNameAndMarkIfExternal(
      ASTGenericType ruleReference,
      ASTMCGrammar grammar, ASTCDClass cdClass) {
      
    Optional<MCRuleSymbol> typeSymbol = resolveAstRuleType(ruleReference);
    
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
      
    Optional<MCRuleSymbol> typeSymbol = resolveAstRuleType(ruleReference);
    
    String qualifiedRuleName = getQualifiedAstName(
        typeSymbol, ruleReference, grammar);
        
    if (!typeSymbol.isPresent()) {
      addStereoType(interf,
          MC2CDStereotypes.EXTERNAL_TYPE.toString(), qualifiedRuleName);
    }
    
    return qualifiedRuleName;
  }
  
  public static Optional<MCRuleSymbol> resolveAstRuleType(ASTGenericType type) {
    if (!type.getNames().isEmpty()) {
      String simpleName = type.getNames().get(type.getNames().size() - 1);
      if (!simpleName.startsWith(AST_PREFIX)) {
        return Optional.empty();
      }
      Optional<MCRuleSymbol> ruleSymbol = MCGrammarSymbolTableHelper.resolveRule(type, simpleName
          .substring(AST_PREFIX.length()));
      if (ruleSymbol.isPresent() && ruleSymbol.get().getGrammarSymbol() != null) {
        return ruleSymbol;
      }
    }
    return Optional.empty();
  }
  
  public static boolean checkIfExternal(ASTGenericType type) {
    return !resolveAstRuleType(type).isPresent();
  }
  
  public static String getQualifiedAstName(
      Optional<MCRuleSymbol> typeSymbol, ASTGenericType type,
      ASTMCGrammar grammar) {
    if (!typeSymbol.isPresent()) {
      return type.getTypeName();
    }
    if (type.getNames().size() > 1) {
      return type.getTypeName();
    }
    MCGrammarSymbol refGrammar = typeSymbol.get().getGrammarSymbol();
    if (grammar.getSymbol().isPresent()
        && grammar.getSymbol().get().equals(refGrammar)) {
      return type.getTypeName();
    }
    return getAstPackageName(
        typeSymbol.get().getGrammarSymbol()) + type.getTypeName();
  }
  
  public static void addStereoType(ASTCDClass type, String stereotypeName,
      String stereotypeValue) {
    if (!type.getModifier().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier().get(),
        stereotypeName, stereotypeValue);
  }
  
  public static void addStereoType(ASTCDInterface type,
      String stereotypeName,
      String stereotypeValue) {
    if (!type.getModifier().isPresent()) {
      type.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(type.getModifier().get(),
        stereotypeName, stereotypeValue);
  }
  
  public static void addStereoType(ASTCDAttribute attribute,
      String stereotypeName,
      String stereotypeValue) {
    if (!attribute.getModifier().isPresent()) {
      attribute.setModifier(CD4AnalysisNodeFactory.createASTModifier());
    }
    addStereotypeValue(attribute.getModifier().get(),
        stereotypeName, stereotypeValue);
  }
  
  public static void addStereotypeValue(ASTModifier astModifier,
      String stereotypeName,
      String stereotypeValue) {
    if (!astModifier.getStereotype().isPresent()) {
      astModifier.setStereotype(CD4AnalysisNodeFactory
          .createASTStereotype());
    }
    List<ASTStereoValue> stereoValueList = astModifier.getStereotype().get()
        .getValues();
    ASTStereoValue stereoValue = CD4AnalysisNodeFactory
        .createASTStereoValue();
    stereoValue.setName(stereotypeName);
    stereoValue.setValue(stereotypeValue);
    stereoValueList.add(stereoValue);
  }
  
}
