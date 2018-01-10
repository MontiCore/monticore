/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.codegen.cd2java.ast;

import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTVoidType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisVisitor;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class AstGeneratorHelper extends GeneratorHelper {
  
  protected static final String AST_BUILDER = "Builder";
  
  public AstGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    return getAstAttributeValue(attribute);
  }
  
  public String getAstAttributeValue(ASTCDAttribute attribute) {
    if (attribute.getValue().isPresent()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = TypesPrinter.printType(attribute.getType());
    if (isListType(typeName)) {
      return "new java.util.ArrayList<>()";
    }
    if (isMapType(typeName)) {
      return "new java.util.HashMap<>()";
    }
    return "";
  }
  
  public String getAstAttributeValueForBuilder(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return "";
    }
    return getAstAttributeValue(attribute);
  }
  
  public static boolean isBuilderClass(ASTCDDefinition cdDefinition, ASTCDClass clazz) {
    if (!clazz.getName().endsWith(AST_BUILDER)) {
      return false;
    }
    String className = AST_PREFIX
        + clazz.getName().substring(0, clazz.getName().indexOf(AST_BUILDER));
    return cdDefinition.getCDClasses().stream()
        .filter(c -> className.equals(GeneratorHelper.getPlainName(c))).findAny()
        .isPresent();
  }
  
  public Optional<ASTCDClass> getASTBuilder(ASTCDClass clazz) {
    return getCdDefinition().getCDClasses().stream()
        .filter(c -> c.getName().equals(getNameOfBuilderClass(clazz))).findAny();
  }
  
  public static boolean compareAstTypes(String qualifiedType, String type) {
    if (type.indexOf('.') != -1) {
      return qualifiedType.equals(type);
    }
    String simpleName = Names.getSimpleName(qualifiedType);
    if (simpleName.startsWith(AST_PREFIX)) {
      return simpleName.equals(type);
    }
    return false;
  }
  
  public static boolean isSuperClassExternal(ASTCDClass clazz) {
    return clazz.superclassIsPresent()
        && hasStereotype(clazz, MC2CDStereotypes.EXTERNAL_TYPE.toString())
        && getStereotypeValues(clazz, MC2CDStereotypes.EXTERNAL_TYPE.toString())
            .contains(clazz.printSuperClass());
  }
  
  /**
   * @param qualifiedName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackage(String qualifiedName) {
    Log.errorIfNull(qualifiedName);
    return Joiners.DOT.join(qualifiedName.toLowerCase(), AST_PACKAGE_SUFFIX_DOT);
  }
  
  /**
   * @param qualifiedName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackageForCD(String qualifiedCdName) {
    Log.errorIfNull(qualifiedCdName);
    return Joiners.DOT.join(qualifiedCdName.toLowerCase(),
        Names.getSimpleName(qualifiedCdName).toLowerCase(), getAstPackageSuffix());
  }
  
  public static String getAstPackageSuffix() {
    return GeneratorHelper.AST_PACKAGE_SUFFIX;
  }
  
  public static String getNameOfBuilderClass(ASTCDClass astClass) {
    return getASTClassNameWithoutPrefix(astClass) + AST_BUILDER;
  }
  
  public static String getSuperClassForBuilder(ASTCDClass clazz) {
    if (!clazz.getSuperclass().isPresent()) {
      return "";
    }
    String superClassName = Names.getSimpleName(clazz.printSuperClass());
    return superClassName.startsWith(GeneratorHelper.AST_PREFIX)
        ? superClassName.substring(GeneratorHelper.AST_PREFIX.length())
        : superClassName;
  }
  
  public static boolean generateSetter(ASTCDClass clazz, ASTCDAttribute cdAttribute, String typeName) {
    if (GeneratorHelper.isInherited(cdAttribute)) {
      return false;
    }
    String methodName = GeneratorHelper.getPlainSetter(cdAttribute);
    if (clazz.getCDMethods().stream()
        .filter(m -> methodName.equals(m.getName()) && m.getCDParameters().size() == 1
            && compareAstTypes(typeName,
                TypesHelper.printSimpleRefType(m.getCDParameters().get(0).getType())))
        .findAny()
        .isPresent()) {
      return false;
    }
    return true;
  }
  
  public static String getConstantClassName(MCGrammarSymbol grammarSymbol) {
    return grammarSymbol.getFullName().toLowerCase() +
        GeneratorHelper.AST_DOT_PACKAGE_SUFFIX + "."
        + getConstantClassSimpleName(grammarSymbol);
    
  }
  
  public static String getConstantClassSimpleName(MCGrammarSymbol grammarSymbol) {
    return "ASTConstants" + grammarSymbol.getName();
  }
  
  public static String getASTClassNameWithoutPrefix(ASTCDType type) {
    if (!GeneratorHelper.getPlainName(type).startsWith(GeneratorHelper.AST_PREFIX)) {
      return type.getName();
    }
    return GeneratorHelper.getPlainName(type).substring(GeneratorHelper.AST_PREFIX.length());
  }
  
  public static String getASTClassNameWithoutPrefix(String type) {
    return type.startsWith(GeneratorHelper.AST_PREFIX)
        ? type.substring(GeneratorHelper.AST_PREFIX.length())
        : type;
  }
  
  public static boolean isBuilderClassAbstarct(ASTCDClass astType) {
    return (astType.getSuperclass().isPresent() && isSuperClassExternal(astType))
        || (astType.getModifier().isPresent() && astType.getModifier().get().isAbstract()
            && !isSupertypeOfHWType(astType.getName()));
  }
  
  public static boolean hasReturnTypeVoid(ASTCDMethod method) {
    return method.getReturnType() instanceof ASTVoidType;
  }
  
  /**
   * Transforms all CD types to Java types using the given package suffix.
   */
  public void transformCdTypes2Java() {
    new Cd2JavaTypeConverter() {
      @Override
      public void visit(ASTSimpleReferenceType node) {
        AstGeneratorHelper.this.transformTypeCd2Java(node, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
    }.handle(topAst);
    
  }
  
  /**
   * Clones the top ast and transforms CD types defined in this- or in one of the super CDs to simple CD types
   * @return cloned transformed ast
   */
  public ASTCDCompilationUnit getASTCDForReporting() {
    ASTCDCompilationUnit ast = topAst.deepClone();
    
    new Cd2JavaTypeConverter() {
      @Override
      public void visit(ASTSimpleReferenceType node) {
        AstGeneratorHelper.this.transformQualifiedToSimpleIfPossible(node, GeneratorHelper.AST_DOT_PACKAGE_SUFFIX_DOT);
      }
    }.handle(ast);
    
    return ast;
  }
  
  public class Cd2JavaTypeConverter implements CD4AnalysisVisitor {}

}
