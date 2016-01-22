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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.symboltable.GlobalScope;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._visitor.CD4AnalysisInheritanceVisitor;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  private Map<ASTCDType, List<EmfAttribute>> emfAttributes = new LinkedHashMap<>();
  
  private Map<String, String> externalTypes = Maps.newHashMap();
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
    // Run over classdiagramm and converts cd types to mc-java types
    ETypeCollector emfCollector = new ETypeCollector(this);
    emfCollector.handle(topAst.getCDDefinition());
    System.err.println(emfCollector.getTypes());
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
  
  /**
   * @return externalTypes
   */
  public Collection<String> getExternalTypes() {
    return this.externalTypes.values();
  }
  
  public boolean isExternalType(String nativeType) {
    return externalTypes.containsKey(nativeType);
  }
  
  public Optional<String> getExternalType(String nativeType) {
    return Optional.ofNullable(externalTypes.get(nativeType));
  }
  
  private void addExternalType(String extType) {
    if (externalTypes.containsKey(extType)) {
      return;
    }
    int i = 0;
    String typeName = "E" + Names.getSimpleName(extType);
    while (externalTypes.values().contains(typeName)) {
      typeName = typeName + i;
      i++;
    }
    externalTypes.put(extType, typeName);
  }
  
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
  
  // TODO GV: rename
  public String getTypeNameEliminOptional(ASTCDAttribute attribute) {
    if (isOptional(attribute)) {
      return TypesHelper
          .printType(TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType()));
          
    }
    return attribute.printType();
  }
  /* public String getModelName(String qualifiedName) { String qualifier =
   * Names.getQualifier(qualifiedName); if
   * (!qualifier.endsWith(AST_PACKAGE_SUFFIX)) { return ""; } return
   * Names.getSimpleName(Names.getQualifier(qualifier)); } */
  
  public String getType(CDTypeSymbol type) {
    return type.getFullName();
  }
  
  // TODO GV:
  public static boolean istAstENodeList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType())
        .equals(JAVA_LIST);
  }
  
  public void createEmfAttribute(ASTCDType ast, ASTCDAttribute cdAttribute) {
    // TODO GV: interfaces, enums
    String attributeName = getPlainName(ast) + "_"
        + StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(cdAttribute
            .getName()));
    boolean isAstNode = isAstNode(cdAttribute)
        || isOptionalAstNode(cdAttribute);
    boolean isAstList = isListAstNode(cdAttribute);
    boolean isOptional = AstGeneratorHelper.isOptional(cdAttribute);
    addEmfAttribute(ast, new EmfAttribute(cdAttribute, ast, attributeName,
        isAstNode, isAstList, isOptional, this));
        
  }
  
  /**
   * Converts CD type to Java type using the given package suffix.
   * 
   * @param type
   * @param packageSuffix
   * @return converted type or original type if type is java type already
   */
  public void collectExternalTypes(ASTSimpleReferenceType astType) {
    String genericType = "";
    ASTSimpleReferenceType convertedType = astType;
    if (AstGeneratorHelper.isOptional(astType)) {
      Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
          .getFirstTypeArgumentOfOptional(astType);
      if (!typeArgument.isPresent()) {
        return;
      }
      convertedType = typeArgument.get();
      genericType = AstGeneratorHelper.OPTIONAL;
    }
    else if (TypesHelper.isGenericTypeWithOneTypeArgument(astType,
        AstGeneratorHelper.JAVA_LIST)) {
      Optional<ASTSimpleReferenceType> typeArgument = TypesHelper
          .getFirstTypeArgumentOfGenericType(astType, AstGeneratorHelper.JAVA_LIST);
      if (!typeArgument.isPresent()) {
        return;
      }
      convertedType = typeArgument.get();
      genericType = AstGeneratorHelper.JAVA_LIST;
    }
    
    String convertedTypeName = TypesPrinter.printType(convertedType);
    // Resolve only qualified types
    if (!convertedTypeName.contains(".")) {
      return;
    }
    
    // TODO: GV, PN: path converter by resolving
    // TODO GV: typeArgs!
    if (convertedTypeName.contains("<")) {
      return;
    }
    
    String newType = "";
    Optional<CDTypeSymbol> symbol = resolveCdType(convertedTypeName);
    if (!symbol.isPresent() || (symbol.isPresent() && symbol.get().isEnum())) {
      if (!genericType.isEmpty()) {
        newType = genericType + "<" + convertedTypeName + ">";
      }
      else {
        newType = convertedTypeName;
      }
      addExternalType(newType);
      System.err.println("NewtYPE " + newType);
    }
    
  }
  
  public String getIdentifierName(String qualifiedName) {
    return Names.getSimpleName(qualifiedName) + "_" + Names.getQualifier(qualifiedName).replace('.', '_');
  }
  public class ETypeCollector implements CD4AnalysisInheritanceVisitor {
    
    private AstEmfGeneratorHelper astHelper;
    
    /**
     * @return types
     */
    public Collection<String> getTypes() {
      return externalTypes.values();
    }
    
    public ETypeCollector(AstEmfGeneratorHelper astHelper) {
      this.astHelper = astHelper;
    }
    
    @Override
    public void visit(ASTSimpleReferenceType ast) {
      astHelper.collectExternalTypes(ast);
    }
    
  }
  
}
