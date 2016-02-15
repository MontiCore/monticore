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

package de.monticore.codegen.cd2java.ast_emf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.emf._ast.ASTENodePackage;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public static String getEmfRuntimePackage() {
    return "de.monticore.emf._ast";
  }
  
  public static String getSuperClass(ASTCDClass clazz) {
    if (!clazz.getSuperclass().isPresent()) {
      return "de.monticore.emf._ast.ASTECNode";
    }
    return clazz.printSuperClass();
  }
  
  /**
   * @return externalTypes
   */
  @Override
  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    if (attribute.getValue().isPresent()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = TypesPrinter.printType(attribute.getType());
    if (isListType(typeName)) {
      String attributeName = getPlainName(clazz) + "_"
          + StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(attribute
              .getName()));
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(attribute.getType(), JAVA_LIST);
      if (typeArg.isPresent()) {
        String typeArgName = TypesHelper.printType(typeArg.get());
        if (Names.getQualifier(typeArgName).equals(getAstPackage())) {
          typeName = Names.getSimpleName(typeArgName);
          return "new EObjectContainmentEList<" + typeName + ">(" + typeName + ".class, this, "
              + this.getCdName() + "Package." + attributeName + ")";
        }
        else {
          typeName = typeArgName;
          return "new EDataTypeEList<" + typeName + ">(" + typeName + ".class, this, "
              + this.getCdName() + "Package." + attributeName + ")";
        }
      }
    }
    if (isMapType(typeName)) {
      return "new java.util.HashMap<>()";
    }
    return "";
  }
  
  public String getNativeTypeName(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return TypesHelper
          .printType(TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType()));
          
    }
    if (isListAstNode(attribute)) {
      Optional<ASTSimpleReferenceType> typeArg = TypesHelper
          .getFirstTypeArgumentOfGenericType(attribute.getType(), JAVA_LIST);
      if (typeArg.isPresent()) {
        return printType(typeArg.get());
      }
    }
    return attribute.printType();
  }
  
  public static boolean istJavaList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType())
        .equals(JAVA_LIST);
  }
  
  public List<String> getASTESuperPackages() {
    List<String> ePackages = new ArrayList<>();
    for (String superGrammar : getSuperGrammarCds()) {
      ePackages.add(getEPackageName(superGrammar));
    }
    if (ePackages.isEmpty()) {
      ePackages.add(ASTENodePackage.class.getName());
    }
    return ePackages;
  }
  
  public static String getEPackageName(String qualifiedSuperGrammar) {
    return qualifiedSuperGrammar.toLowerCase() + "._ast."
        + StringTransformations.capitalize(Names.getSimpleName(qualifiedSuperGrammar)) + "Package";
  }
  
  /**
   * Get all native (not created by decorators) cd types
   * 
   * @param cdDefinition
   * @return
   */
  public List<ASTCDType> getNativeTypes(ASTCDDefinition cdDefinition) {
    List<ASTCDType> types = new ArrayList<>(cdDefinition.getCDClasses());
    types.addAll(cdDefinition.getCDInterfaces());
    String genNode = BaseInterfaceAddingManipulation.getBaseInterfaceName(getCdDefinition());
    return types.stream().filter(c -> !c.getName().equals(genNode))
        .collect(Collectors.toList());
  }
  
  public boolean attributeDefinedInOtherCd(ASTCDAttribute attribute) {
    String definedGrammar = getDefinedGrammarName(attribute);
    return !definedGrammar.isEmpty()
        && !definedGrammar.equals(getQualifiedCdName().toLowerCase());
  }
  
  public String getDefinedGrammarName(ASTCDAttribute attribute) {
    String type = getNativeTypeName(attribute);
    if (isAstNode(attribute) || isListAstNode(attribute) || isOptional(attribute)) {
      return Names.getQualifier(Names.getQualifier(type));
    }
    return type;
  }
  
  // TODO GV: fix me
  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute).endsWith("Ext");
  }
  
  public String getIdentifierName(String qualifiedName) {
    return Names.getSimpleName(qualifiedName) + "_"
        + Names.getQualifier(qualifiedName).replace('.', '_');
  }
  
  
}
