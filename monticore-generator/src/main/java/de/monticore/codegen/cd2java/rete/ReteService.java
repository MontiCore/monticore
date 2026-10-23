/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java.rete.ReteConstants.*;

public class ReteService extends AbstractService<ReteService> {

  public ReteService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public ReteService(DiagramSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return RETE_PACKAGE;
  }

  @Override
  protected ReteService createService(DiagramSymbol cdSymbol) {
    return createReteService(cdSymbol);
  }

  public static ReteService createReteService(DiagramSymbol cdSymbol) {
    return new ReteService(cdSymbol);
  }

  /*
   * names of the generated classes
   */

  /**
   * @return the simple name of the class containing the features of the language,
   * e.g. {@code AutomataReteFeatures}
   */
  public String getFeaturesSimpleName() {
    return getCDName() + FEATURES_SUFFIX;
  }

  public String getFeaturesFullName() {
    return getFeaturesFullName(getCDSymbol());
  }

  /**
   * @return the full name of the class containing the features of the given
   * (e.g. super) language
   */
  public String getFeaturesFullName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), cdSymbol.getName() + FEATURES_SUFFIX);
  }

  /**
   * @return the simple name of the fact emitter of the language,
   * e.g. {@code AutomataReteFactEmitter}
   */
  public String getFactEmitterSimpleName() {
    return getCDName() + FACT_EMITTER_SUFFIX;
  }

  /**
   * @return the full name of the fact emitter of the given (e.g. super) language
   */
  public String getFactEmitterFullName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), cdSymbol.getName() + FACT_EMITTER_SUFFIX);
  }

  /**
   * @return the simple name of the fact emitter combining the emitters of the
   * language and its super languages, e.g. {@code AutomataFullReteFactEmitter}
   */
  public String getFullFactEmitterSimpleName() {
    return getCDName() + FULL_FACT_EMITTER_SUFFIX;
  }

  /*
   * attributes that get a feature
   */

  /**
   * @return the AST types of the class diagram in the order their features
   * are generated: interfaces first, then classes
   */
  public List<ASTCDType> getFeatureTypes(ASTCDDefinition cdDefinition) {
    List<ASTCDType> types = new ArrayList<>(cdDefinition.getCDInterfacesList());
    types.addAll(cdDefinition.getCDClassesList());
    return types;
  }

  /**
   * @return the attributes of the type that get a feature of their own: all
   * attributes with a feature value type that do not repeat an attribute of a
   * super type (whose feature applies instead)
   */
  public List<ASTCDAttribute> getFeatureAttributes(ASTCDType type) {
    return type.getCDAttributeList().stream()
        .filter(attribute -> getFeatureValueType(attribute).isPresent())
        .filter(attribute -> !isDeclaredInSuperType(type, attribute))
        .toList();
  }

  /**
   * @return the type of the feature values, i.e. the element type of list and
   * optional attributes and the wrapper type of primitive attributes; empty if
   * the attribute has another generic type or an array type, which cannot be
   * expressed as a class literal
   */
  public Optional<String> getFeatureValueType(ASTCDAttribute attribute) {
    ASTMCType type = attribute.getMCType();
    if (type instanceof ASTMCGenericType genericType) {
      if (!isListOrOptional(attribute) || genericType.getMCTypeArgumentList().size() != 1
          || genericType.getMCTypeArgumentList().get(0).getMCTypeOpt().isEmpty()) {
        return Optional.empty();
      }
      type = genericType.getMCTypeArgumentList().get(0).getMCTypeOpt().get();
    }
    if (type instanceof ASTMCPrimitiveType primitiveType) {
      return Optional.of(getWrapperType(primitiveType));
    }
    if (type instanceof ASTMCQualifiedType) {
      return Optional.of(CD4CodeMill.prettyPrint(type, false));
    }
    return Optional.empty();
  }

  public boolean isListAttribute(ASTCDAttribute attribute) {
    return getDecorationHelper().isListType(attribute.printType());
  }

  public boolean isOptionalAttribute(ASTCDAttribute attribute) {
    return getDecorationHelper().isOptional(attribute.getMCType());
  }

  protected boolean isListOrOptional(ASTCDAttribute attribute) {
    return isListAttribute(attribute) || isOptionalAttribute(attribute);
  }

  protected String getWrapperType(ASTMCPrimitiveType primitiveType) {
    return switch (CD4CodeMill.prettyPrint(primitiveType, false)) {
      case "boolean" -> "Boolean";
      case "byte" -> "Byte";
      case "short" -> "Short";
      case "char" -> "Character";
      case "long" -> "Long";
      case "float" -> "Float";
      case "double" -> "Double";
      default -> "Integer";
    };
  }

  /**
   * @return whether a (transitive) super type declares an attribute of the same
   * name, so that the feature of the super type applies
   */
  public boolean isDeclaredInSuperType(ASTCDType type, ASTCDAttribute attribute) {
    return getSuperTypesTransitive(type.getSymbol()).stream()
        .anyMatch(superType -> getAttributeNames(superType).contains(attribute.getName()));
  }

  protected Set<String> getAttributeNames(TypeSymbol typeSymbol) {
    Set<String> names = typeSymbol.getVariableList().stream()
        .map(VariableSymbol::getName)
        .collect(Collectors.toSet());
    if (typeSymbol instanceof OOTypeSymbol ooTypeSymbol) {
      ooTypeSymbol.getFieldList().forEach(field -> names.add(field.getName()));
    }
    return names;
  }

  protected List<TypeSymbol> getSuperTypesTransitive(TypeSymbol typeSymbol) {
    List<TypeSymbol> superTypes = new ArrayList<>();
    Deque<TypeSymbol> toVisit = new ArrayDeque<>(List.of(typeSymbol));
    Set<String> visited = new HashSet<>();
    while (!toVisit.isEmpty()) {
      for (SymTypeExpression superTypeExpression : toVisit.poll().getSuperTypesList()) {
        TypeSymbol superType = superTypeExpression.getTypeInfo();
        if (superType instanceof TypeSymbolSurrogate surrogate) {
          if (!surrogate.checkLazyLoadDelegate()) {
            continue;
          }
          superType = surrogate.lazyLoadDelegate();
        }
        if (visited.add(superType.getFullName())) {
          superTypes.add(superType);
          toVisit.add(superType);
        }
      }
    }
    return superTypes;
  }

  /**
   * @return the name of the feature constant, e.g. STATE_NAME for the attribute
   * name of ASTState and FOO_BAR_BAZ_LIST for the attribute bazList of ASTFooBar
   */
  public String getFeatureConstantName(ASTCDType type, ASTCDAttribute attribute) {
    String typeName = type.getName().startsWith(AST_PREFIX)
        ? type.getName().substring(AST_PREFIX.length())
        : type.getName();
    String attributeName = getDecorationHelper().getNativeAttributeName(attribute.getName());
    return toConstantName(typeName) + "_" + toConstantName(attributeName);
  }

  protected String toConstantName(String camelCaseName) {
    return camelCaseName.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toUpperCase();
  }
}
