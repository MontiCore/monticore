/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import com.google.common.base.Preconditions;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.MCBasicTypesHelper;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.JavaNamesHelper;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java.methods.AccessAsSupplierTypes.SUPPLIER_TYPE;

public class DecorationHelper extends MCBasicTypesHelper {

  protected static DecorationHelper decorationHelper;

  private DecorationHelper() {
  }

  public static DecorationHelper getInstance() {
    if (decorationHelper == null) {
      decorationHelper = new DecorationHelper();
    }

    return decorationHelper;
  }

  public static final String GET_PREFIX_BOOLEAN = "is";

  public static final String GET_SUFFIX_LIST = "List";

  public static final String GET_PREFIX = "get";

  public static final String SET_PREFIX = "set";

  /**
   * methods which check if the Type is of a special kind
   * e.g. Optional, List, ASTNode, Map
   */
  public boolean isAstNode(ASTCDAttribute attr) {
    if (attr.getModifier().isPresentStereotype()) {
      return attr.getModifier().getStereotype().getValuesList().stream().anyMatch(v -> v.getName().equals(MC2CDStereotypes.AST_TYPE.toString()));
    }
    return false;
  }

  public boolean isSimpleAstNode(ASTCDAttribute attr) {
    return !isOptional(attr.getMCType()) && !isList(attr.getMCType()) && isAstNode(attr);
  }

  public boolean isOptionalAstNode(ASTCDAttribute attr) {
    return isOptional(attr.getMCType()) && isAstNode(attr);
  }

  public boolean isListAstNode(ASTCDAttribute attribute) {
    return isList(attribute.getMCType()) && isAstNode(attribute);
  }

  public boolean isListType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "List".equals(type) || "java.util.List".equals(type)
        || "ArrayList".equals(type) || "java.util.ArrayList".equals(type);
  }

  public boolean isMapType(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Map".equals(type) || "java.util.Map".equals(type);
  }

  public boolean isOptional(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Optional".equals(Names.getSimpleName(type));
  }

  public boolean isSupplier(String type) {
    int index = type.indexOf('<');
    if (index != -1) {
      type = type.substring(0, index);
    }
    return "Supplier".equals(Names.getSimpleName(type));
  }

  // The ASTMCType overloads intentionally delegate to the String checks above instead of using
  // instanceof: a single implementation, and no reliance on the concrete AST node (see PR #372 review).
  public boolean isOptional(ASTMCType type) {
    return isOptional(type.printType());
  }

  public boolean isList(ASTMCType type) {
    return isListType(type.printType());
  }

  public boolean isSupplier(ASTMCType type) {
    return isSupplier(type.printType());
  }


  public boolean isString(String type) {
    return "String".equals(type) || "java.lang.String".equals(type);
  }

  public boolean isPrimitive(ASTMCType type) {
    return type instanceof ASTMCPrimitiveType;
  }

  /**
   * if mcType is not generic -> returns simply printed type
   * if mcType is generic -> returns only printed type argument
   */
  public String getNativeTypeName(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns automaton._ast.ASTState
    // if not generic returns simple Type like 'int'
    if (astType instanceof ASTMCGenericType && ((ASTMCGenericType) astType).getMCTypeArgumentList().size() == 1) {
      return CD4CodeMill.prettyPrint(((ASTMCGenericType) astType).getMCTypeArgumentList().get(0).getMCTypeOpt().get(), false);

    }
    return CD4CodeMill.prettyPrint(astType, false);
  }

  public String getSimpleNativeType(ASTMCType astType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    String nativeAttributeType = getNativeTypeName(astType);
    return getSimpleNativeType(nativeAttributeType);
  }

  public String getSimpleNativeType(String nativeAttributeType) {
    // check if type is Generic type like 'List<automaton._ast.ASTState>' -> returns ASTState
    // if not generic returns simple Type like 'int'
    if (nativeAttributeType.contains(".")) {
      nativeAttributeType = nativeAttributeType.substring(nativeAttributeType.lastIndexOf(".") + 1);
    }
    if (nativeAttributeType.contains(">")) {
      nativeAttributeType = nativeAttributeType.replaceAll(">", "");
    }
    return nativeAttributeType;
  }

  /**
   * adds default declaration to an attribute, by replacing the VALUE template
   * important for Optional and List types
   */
  public void addAttributeDefaultValues(ASTCDAttribute attribute, GlobalExtensionManagement glex) {
    // For a wrapped attribute (Supplier<X>) the default is derived from the unwrapped type X.
    ASTMCType type = attribute.getMCType();
    boolean isSupplier = isSupplier(type);
    if (isSupplier) {
      type = getReferenceTypeOfSupplier(type).getMCTypeOpt().get();
    }

    boolean isList = isList(type);
    String inner;
    if (isList) {
      inner = "new java.util.ArrayList<>()";
    } else if (isOptional(type)) {
      inner = "Optional.empty()";
    } else if (isSupplier) {
      inner = "null";
    } else {
      return;
    }

    // the initialization expression that is placed after the '=' of the field declaration
    String defaultValue;
    if (!isSupplier) {
        defaultValue = inner;
    } else if (isList) {
      // A list must expose a stable instance, otherwise we would create a new one every get and entries would be lost.
        defaultValue = "com.google.common.base.Suppliers.memoize(() -> " + inner + ")";
    } else {
        defaultValue = "() -> " + inner;
    }
    glex.replaceTemplate(VALUE, attribute, new StringHookPoint("= " + defaultValue));
  }

  /**
   * gets attribute Name without the 'r__' prefix
   * this prefix get all attribute names which are also keywords in java e.g. 'final', 'static'
   * remove this prefix to still get setters and getters without the 'r__' prefix
   */
  public String getNativeAttributeName(String attributeName) {
    if (!attributeName.startsWith(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED)) {
      return attributeName;
    }
    return attributeName.substring(JavaNamesHelper.PREFIX_WHEN_WORD_IS_RESERVED.length());
  }

  public ASTMCTypeArgument getReferenceTypeOfOptional(ASTMCType type) {
    Preconditions.checkArgument(isOptional(type));
    return ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
  }

  public ASTMCTypeArgument getReferenceTypeOfSupplier(ASTMCType type) {
    Preconditions.checkArgument(isSupplier(type));
    return ((ASTMCGenericType) type).getMCTypeArgumentList().get(0);
  }

  /**
   * Wraps {@code inner} into {@code Supplier<inner>}
   *
   * A copy of this is already in the MCTypeFacade. This can be deleted, and references rerouted to MCTypeFacade after release ...
   */
  public ASTMCType createSupplierTypeOf(ASTMCType inner) {
    ASTMCTypeArgument arg = MCSimpleGenericTypesMill
        .mCCustomTypeArgumentBuilder().setMCType(inner.deepClone()).build();
    return MCTypeFacade.getInstance().createBasicGenericTypeOf(SUPPLIER_TYPE, arg);
  }

  /**
   * methods only used in templates
   */
  public boolean hasOnlyAstAttributes(ASTCDClass type) {
    for (ASTCDAttribute attr : type.getCDAttributeList()) {
      if (!isAstNode(attr)) {
        return false;
      }
    }
    return true;
  }

  public boolean isAttributeOfTypeEnum(ASTCDAttribute attr) {
    if (!attr.isPresentSymbol()) {
      return false;
    }
    
    String typeName = CD4CodeMill.prettyPrint(attr.getMCType(), false);
    
    if (!typeName.contains(".") && !typeName.startsWith(AST_PREFIX)) {
      return false;
    }

    List<String> listName = Arrays.asList(typeName.split("\\."));
    if (!listName.get(listName.size() - 1).startsWith(AST_PREFIX)) {
      return false;
    }

    Optional<? extends ISymbol> type = attr.getMCType().getDefiningSymbol();
    if (!type.isPresent() || type.get() instanceof CDTypeSymbol) {
      return false;
    }
    return ((CDTypeSymbol) type.get()).isIsEnum();
  }

  /**
   * methods return correct getters or setters for a special attribut
   * needed in templates
   */
  public String getPlainGetter(ASTCDAttribute ast) {
    String astType = CD4CodeMill.prettyPrint(ast.getMCType(), false);
    StringBuilder sb = new StringBuilder();
    // Do not use CDTypes.isBoolean() because only primitive boolean uses GET_PREFIX_BOOLEAN
    if (astType.equals("boolean")) {
      sb.append(GET_PREFIX_BOOLEAN);
    } else {
      sb.append(GET_PREFIX);
    }
    sb.append(StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    if (isListType(astType)) {
      if (hasDerivedAttributeName(ast) && ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }
    }
    return sb.toString();
  }

  public String getPlainSetter(ASTCDAttribute ast) {
    StringBuilder sb = new StringBuilder(SET_PREFIX).append(
        StringTransformations.capitalize(getNativeAttributeName(ast.getName())));
    String astType = CD4CodeMill.prettyPrint(ast.getMCType(), false);
    if (isListType(astType)) {
      if (hasDerivedAttributeName(ast) && ast.getName().endsWith(TransformationHelper.LIST_SUFFIX)) {
        sb.replace(sb.length() - TransformationHelper.LIST_SUFFIX.length(),
            sb.length(), GET_SUFFIX_LIST);
      } else {
        sb.append(GET_SUFFIX_LIST);
      }    }
    return sb.toString();
  }

  public boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getModifier().isPresentStereotype()
        && astcdAttribute.getModifier().getStereotype().sizeValues() > 0 &&
        astcdAttribute.getModifier().getStereotype().getValuesList()
            .stream()
            .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
  }

  /**
   * only needed for templates, so that no instance of the PrettyPrinter has to be created in the template
   */
  public String printType(ASTMCType type) {
    return CD4CodeMill.prettyPrint(type, false);
  }

  public HookPoint createPackageHookPoint(final String... packageName) {
    return createPackageHookPoint(Arrays.asList(packageName));
  }

  public HookPoint createPackageHookPoint(final List<String> packageName) {
    return new StringHookPoint("package " + String.join(".", packageName) + ";");
  }

  public HookPoint createAnnotationsHookPoint(final ASTModifier modifier) {
    String anno = "";
    if (modifier.isPresentStereotype()) {
      ASTStereotype stereo = modifier.getStereotype();
      for (ASTStereoValue stereoValue : stereo.getValuesList()) {
        if (MC2CDStereotypes.DEPRECATED.toString().equals(stereoValue.getName())) {
          if (!stereoValue.getValue().isEmpty()) {
            // Append tag for java api
            anno = "/**\n * @deprecated " + stereoValue.getValue() + "\n **/\n";
          }
          anno += "@Deprecated";
        }
      }
    }
    return new StringHookPoint(anno);
  }
}
