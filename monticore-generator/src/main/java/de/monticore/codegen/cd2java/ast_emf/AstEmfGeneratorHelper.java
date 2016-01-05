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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  private Map<ASTCDType, List<EmfAttribute>> emfAttributes = new LinkedHashMap<>();
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public static String getQualifiedENodeName() {
    return "de.monticore.emf._ast.ASTEnode";
  }
  
  public static String getEmfRuntimePackage() {
    return "de.monticore.emf._ast";
  }
  
  public static String getSuperClass(ASTCDClass clazz) {
    if (!clazz.getSuperclass().isPresent()) {
      return "de.monticore.emf.ASTEObjectImplNode";
    }
    return clazz.printSuperClass();
  }
  
  public void addEmfAttribute(ASTCDType type, EmfAttribute attribute) {
    List<EmfAttribute> attributes = emfAttributes.get(type);
    if (attributes == null) {
      attributes = new ArrayList<>();
      emfAttributes.put(type, attributes);
    }
    attributes.add(attribute);
  }
  
  public List<EmfAttribute> getEmfAttributes(ASTCDType type) {
    if (!emfAttributes.containsKey(type)) {
      return new ArrayList<>();
    }
    return emfAttributes.get(type);
  }
  
  public List<EmfAttribute> getAllEmfAttributes() {
    return emfAttributes.keySet().stream()
        .flatMap(type -> getEmfAttributes(type).stream()).collect(Collectors.toList());
  }
  
  @Override
  // TODO GV
  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
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
  
  public static boolean istAstENodeList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType()).startsWith(
        "de.monticore.emf._ast.ASTENodeList");
  }
}
