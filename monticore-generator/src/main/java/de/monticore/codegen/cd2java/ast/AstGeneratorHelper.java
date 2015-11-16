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

package de.monticore.codegen.cd2java.ast;

import java.util.List;
import java.util.Optional;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstGeneratorHelper extends GeneratorHelper {
  
  protected static final String AST_BUILDER = "Builder_";
  
  public AstGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public String getAstAttributeValue(ASTCDAttribute attribute) {
    if (attribute.getValue().isPresent()) {
      return attribute.printValue();
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = TypesPrinter.printType(attribute.getType());
    if (isAstList(attribute)) {
      return new StringBuilder(getCdName()).append("NodeFactory.create")
          .append(Names.getSimpleName(typeName)).append("()").toString();
    }
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
  
  public String getAstPackage() {
    return getPackageName(getPackageName(), getAstPackageSuffix());
  }
  
  public Optional<ASTCDClass> getASTBuilder(ASTCDClass clazz) {
    return getCdDefinition().getCDClasses().stream()
        .filter(c -> c.getName().equals(getNameOfBuilderClass(clazz))).findAny();
  }
  
  /**
   * @param qualifiedName
   * @return The lower case qualifiedName + AST_PACKAGE_SUFFIX
   */
  public static String getAstPackage(String qualifiedName) {
    Log.errorIfNull(qualifiedName);
    return Joiners.DOT.join(qualifiedName.toLowerCase(), getAstPackageSuffix());
  }
  
  public static String getAstPackageSuffix() {
    return GeneratorHelper.AST_PACKAGE_SUFFIX;
  }
  
  public static String getNameOfBuilderClass(ASTCDClass astClass) {
    return AST_BUILDER + getPlainName(astClass);
  }

  
  
}
