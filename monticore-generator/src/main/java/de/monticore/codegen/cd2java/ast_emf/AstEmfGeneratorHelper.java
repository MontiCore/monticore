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
    // Run over classdiagramm and collects external emf types
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
    List<EmfAttribute> attributes = new ArrayList<>();
    attributes.addAll(getEReferences(type));
    attributes.addAll(getEAttributes(type));
    return attributes;
  }
  
  public List<EmfAttribute> getEAttributes(ASTCDType type) {
    if (!emfAttributes.containsKey(type)) {
      return new ArrayList<>();
    }
    return emfAttributes.get(type).stream().filter(e -> e.isEAttribute())
        .collect(Collectors.toList());
  }
  
  public List<EmfAttribute> getEReferences(ASTCDType type) {
    if (!emfAttributes.containsKey(type)) {
      return new ArrayList<>();
    }
    return emfAttributes.get(type).stream().filter(e -> e.isEReference())
        .collect(Collectors.toList());
  }
  
  public List<EmfAttribute> getAllEmfAttributes() {
    List<EmfAttribute> attributes = new ArrayList<>();
    emfAttributes.keySet().stream().forEach(t -> attributes.addAll(getEReferences(t)));
    emfAttributes.keySet().stream().forEach(t -> attributes.addAll(getEAttributes(t)));
    return attributes;
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
  
  public static boolean istJavaList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType())
        .equals(JAVA_LIST);
  }
  
  public boolean isExternalType(ASTCDAttribute cdAtttribute) {
    return isExternalType(getNativeTypeName(cdAtttribute));
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
  
  public void createEmfAttribute(ASTCDType ast, ASTCDAttribute cdAttribute) {
    String attributeName = getPlainName(ast) + "_"
        + StringTransformations.capitalize(GeneratorHelper.getNativeAttributeName(cdAttribute
            .getName()));
    boolean isAstNode = isAstNode(cdAttribute)
        || isOptionalAstNode(cdAttribute);
    boolean isAstList = isListAstNode(cdAttribute);
    boolean isOptional = AstGeneratorHelper.isOptional(cdAttribute);
    boolean isInherited = attributeDefinedInOtherCd(cdAttribute);
    boolean isEnum = !isAstNode && isAttributeOfTypeEnum(cdAttribute);
    addEmfAttribute(ast,
        new EmfAttribute(cdAttribute, ast, attributeName, getDefinedGrammarName(cdAttribute),
            isAstNode, isAstList, isOptional, isInherited, isExternal(cdAttribute), isEnum, this));
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
    if (isAstNode(attribute) || isListAstNode(attribute)) {
      return Names.getQualifier(Names.getQualifier(type));
    }
    return type;
  }
  
  // TODO GV: fix me
  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute).endsWith("Ext");
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
    }
    
  }
  
  public String getIdentifierName(String qualifiedName) {
    return Names.getSimpleName(qualifiedName) + "_"
        + Names.getQualifier(qualifiedName).replace('.', '_');
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
