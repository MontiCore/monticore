/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java.ast_emf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.codegen.mc2cd.manipul.BaseInterfaceAddingManipulation;
import de.monticore.emf._ast.ASTECNode;
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
import de.monticore.umlcd4a.symboltable.CDFieldSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * A helper for emf-compatible generation
 */
public class AstEmfGeneratorHelper extends AstGeneratorHelper {
  
  public static final String JAVA_MAP = "java.util.Map";
  
  public AstEmfGeneratorHelper(ASTCDCompilationUnit topAst, GlobalScope symbolTable) {
    super(topAst, symbolTable);
  }
  
  public String getPackageURI() {
    return "http://" + getCdName() + "/1.0";
  }
  
  /**
   * @return externalTypes
   */
  @Override
  public String getAstAttributeValue(ASTCDAttribute attribute, ASTCDType clazz) {
    if (attribute.isPresentValue()) {
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
  
  /**
   * Get all native (not created by decorators) cd types
   * 
   * @param cdDefinition
   * @return
   */
  public List<ASTCDType> getNativeTypes(ASTCDDefinition cdDefinition) {
    List<ASTCDType> types = new ArrayList<>(cdDefinition.getCDClassList());
    types.addAll(cdDefinition.getCDInterfaceList());
    String genNode = BaseInterfaceAddingManipulation.getBaseInterfaceName(getCdDefinition());
    return types.stream().filter(c -> !c.getName().equals(genNode))
        .collect(Collectors.toList());
  }
  
  public boolean attributeDefinedInOtherCd(ASTCDAttribute attribute) {
    String definedGrammar = getDefinedGrammarName(attribute);
    return !definedGrammar.isEmpty()
        && !definedGrammar.equalsIgnoreCase(getQualifiedCdName());
  }
  
  public String getDefinedGrammarName(ASTCDAttribute attribute) {
    String type = getNativeTypeName(attribute);
    if (isAstNode(attribute) || isListAstNode(attribute) || isOptional(attribute)) {
      return Names.getQualifier(Names.getQualifier(type));
    }
    return type;
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllSuperTypesEmfOrder(ASTCDType type) {
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4097 Could not load symbol information for " + type.getName() + ".");
    }
    
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    return getAllSuperTypesEmfOrder(sym);
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllSuperTypesEmfOrder(CDTypeSymbol type) {
    List<CDTypeSymbol> allSuperTypes = new ArrayList<>();
    for (CDTypeSymbol s : type.getSuperTypes()) {
      List<CDTypeSymbol> supers = getAllSuperTypesEmfOrder(s);
      for (CDTypeSymbol sup : supers) {
        addIfNotContained(sup, allSuperTypes);
      }
      addIfNotContained(s, allSuperTypes);
    }
    return allSuperTypes;
  }
  
  /**
   * Gets super types recursively (without duplicates - the first occurrence in
   * the type hierarchy is used) in the order according to the EMF-generator
   * requirements
   * 
   * @param type
   * @return all supertypes (without the type itself)
   */
  public List<CDTypeSymbol> getAllTypesEmfOrder(ASTCDType type) {
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4098 Could not load symbol information for " + type.getName() + ".");
    }
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    List<CDTypeSymbol> types = getAllSuperTypesEmfOrder(sym);
    types.add(sym);
    return types;
  }
  
  /**
   *
   * @param cdType
   * @return
   */
  public Collection<CDFieldSymbol> getAllVisibleFields(ASTCDType type) {
    List<CDFieldSymbol> allSuperTypeFields = new ArrayList<>();
    if (!type.getSymbol().isPresent()) {
      Log.error("0xA4099 Could not load symbol information for " + type.getName() + ".");
      return new ArrayList<>();
    }
    CDTypeSymbol sym = (CDTypeSymbol) type.getSymbol().get();
    for (CDTypeSymbol sup : getAllSuperTypesEmfOrder(sym)) {
      sup.getFields().forEach(a -> addIfNotContained(a, allSuperTypeFields));
    }
    // filter-out all private fields
    List<CDFieldSymbol> allFields = allSuperTypeFields.stream()
        .filter(field -> !field.isPrivate()).collect(Collectors.toList());
    // add own fields if not inherited
    sym.getFields().stream()
        .filter(e -> !isAttributeOfSuperType(e, sym)).forEach(allFields::add);
    return allFields;
  }
  
  // TODO: fix me
  public boolean isExternal(ASTCDAttribute attribute) {
    return getNativeTypeName(attribute).endsWith("Ext");
  }
  
  public String getDefaultValue(CDFieldSymbol attribute) {
    if (isAstNode(attribute)) {
      return "null";
    }
    if (isOptional(attribute)) {
      return "Optional.empty()";
    }
    String typeName = attribute.getType().getName();
    switch (typeName) {
      case "boolean":
        return "false";
      case "int":
        return "0";
      case "short":
        return "(short) 0";
      case "long":
        return "0";
      case "float":
        return "0.0f";
      case "double":
        return "0.0";
      case "char":
        return "'\u0000'";
      default:
        return "null";
    }
  }
  
  public static String getEPackageName(String qualifiedSuperGrammar) {
    return qualifiedSuperGrammar.toLowerCase() + "._ast."
        + StringTransformations.capitalize(Names.getSimpleName(qualifiedSuperGrammar)) + "Package";
  }
  
  public String getIdentifierName(String qualifiedName) {
    return Names.getSimpleName(qualifiedName) + "_"
        + Names.getQualifier(qualifiedName).replace('.', '_');
  }
  
  public static boolean istJavaList(ASTCDAttribute attribute) {
    return TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(attribute.getType())
        .equals(JAVA_LIST);
  }
  
  public static String getEmfRuntimePackage() {
    return "de.monticore.emf._ast";
  }
  
  public static String getEDataType(String typeName) {
    switch (typeName) {
      case JAVA_LIST:
        return "Elist";
      case JAVA_MAP:
        return "EMap";
      case "boolean":
        return "EBoolean";
      case "Boolean":
        return "EBooleanObject";
      case "int":
        return "EInt";
      case "Integer":
        return "EIntegerObject";
      case "byte":
        return "EByte";
      case "Byte":
        return "EByteObject";
      case "short":
        return "EShort";
      case "Short":
        return "EShortObject";
      case "long":
        return "ELong";
      case "Long":
        return "ELongObject";
      case "double":
        return "EDouble";
      case "Double":
        return "EDoubleObject";
      case "float":
        return "EFloat";
      case "Float":
        return "EFloatObject";
      case "char":
        return "EChar";
      case "Character":
        return "ECharacterObject";
      default:
        return "E" + typeName;
    }
  }
  
  public static String getSuperClass(ASTCDClass clazz) {
    if (!clazz.isPresentSuperclass()) {
      return ASTECNode.class.getName();
    }
    return clazz.printSuperClass();
  }
  
  public static List<EmfAttribute> getSortedEmfAttributes(List<EmfAttribute> list) {
    List<EmfAttribute> sortedAttributes = new ArrayList<>(list);
    Collections.sort(sortedAttributes, new Comparator<EmfAttribute>() {
      public int compare(EmfAttribute attr1, EmfAttribute attr2) {
        return attr1.getAttributeName().compareTo(attr2.getAttributeName());
      }
    });
    return sortedAttributes;
  }
  
  public static void sortEmfAttributes(List<EmfAttribute> list) {
    Collections.sort(list, new Comparator<EmfAttribute>() {
      public int compare(EmfAttribute attr1, EmfAttribute attr2) {
        return attr1.getAttributeName().compareTo(attr2.getAttributeName());
      }
    });
  }
  
}
