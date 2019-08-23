package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ScopeClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  public ScopeClassDecorator(final GlobalExtensionManagement glex,
                             final SymbolTableService symbolTableService,
                             final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String scopeClassName = input.getCDDefinition().getName() + SCOPE_SUFFIX;
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();

    List<ASTCDClass> symbolClasses = symbolTableService.getSymbolClasses(input.getCDDefinition().getCDClassList());
    List<ASTCDInterface> symbolInterfaces = symbolTableService.getScopeInterfaces(input.getCDDefinition().getCDInterfaceList());

    List<ASTCDAttribute> symbolAttributes = createSymbolAttributes(symbolClasses);
    symbolAttributes.addAll(createSymbolAttributes(symbolInterfaces));

    List<ASTCDMethod> symbolMethods = createSymbolMethods(symbolAttributes);

    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = createSymbolAlreadyResolvedAttributes(symbolAttributes);

    List<ASTCDMethod> symbolAlreadyResolvedMethods = symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream).collect(Collectors.toList());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(scopeClassName)
        .setModifier(PUBLIC.build())
        .addInterface(scopeInterfaceType)
        .addAllCDConstructors(createConstructors(scopeClassName))
        .addAllCDAttributes(symbolAttributes)
        .addAllCDMethods(symbolMethods)
        .addAllCDAttributes(symbolAlreadyResolvedAttributes)
        .addAllCDMethods(symbolAlreadyResolvedMethods)
        .build();
  }

  protected List<ASTCDConstructor> createConstructors(String scopeClassName) {
    List<ASTCDConstructor> constructors = new ArrayList<>();
    constructors.add(createDefaultConstructor(scopeClassName));
    constructors.add(createIsShadowingConstructor(scopeClassName));
    constructors.add(createEnclosingScopeConstructor(scopeClassName));
    constructors.add(createIsShadowingAndEnclosingScopeConstructor(scopeClassName));
    return constructors;
  }

  protected ASTCDConstructor createDefaultConstructor(String scopeClassName) {
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC, scopeClassName);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("    super();\n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this(enclosingScope, false);"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this." + SHADOWING + " = " + SHADOWING + ";\n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingAndEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING);
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this." + SHADOWING + " = " + SHADOWING + ";\n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected List<ASTCDAttribute> createSymbolAttributes(List<? extends ASTCDType> symbolClassList) {
    List<ASTCDAttribute> symbolAttributeList = new ArrayList<>();
    for (ASTCDType astcdClass : symbolClassList) {
      String symbolTypeName = symbolTableService.getSymbolTypeName(astcdClass);
      String attrName;
      if (symbolTypeName.contains(".")) {
        attrName = StringTransformations.uncapitalize(symbolTypeName.substring(symbolTypeName.lastIndexOf(".") + 1)) + "s";
      } else {
        attrName = StringTransformations.uncapitalize(symbolTypeName) + "s";
      }
      ASTMCType symbolMultiMap = getCDTypeFacade().createTypeByDefinition("com.google.common.collect.LinkedListMultimap<String, " + symbolTypeName + ">");
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, symbolMultiMap, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= new com.google.common.collect.LinkedListMultimap.create();"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  protected List<ASTCDAttribute> createSymbolAlreadyResolvedAttributes(List<ASTCDAttribute> symbolAttributes) {
    List<ASTCDAttribute> symbolAttributeList = new ArrayList<>();
    for (ASTCDAttribute attribute : symbolAttributes) {
      String attrName = attribute.getName() + ALREADY_RESOLVED;
      ASTMCType booleanType = getCDTypeFacade().createBooleanType();
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, booleanType, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false;"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  protected List<ASTCDMethod> createSymbolMethods(List<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> symbolMethodList = new ArrayList<>();
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (attribute.getMCType() instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) attribute.getMCType()).sizeMCTypeArguments() == 2) {
        Optional<ASTMCType> mcTypeArgument = ((ASTMCBasicGenericType) attribute.getMCType()).getMCTypeArgument(1).getMCTypeOpt();
        if (mcTypeArgument.isPresent()) {
          symbolMethodList.add(createAddSymbolMethod(mcTypeArgument.get(), attribute.getName()));
          symbolMethodList.add(createRemoveSymbolMethod(mcTypeArgument.get(), attribute.getName()));
          symbolMethodList.add(createGetSymbolListMethod(attribute));
        }
      }
    }
    return symbolMethodList;
  }

  protected ASTCDMethod createAddSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, "symbol");
    ASTCDMethod addMethod = getCDMethodFacade().createMethod(PUBLIC, "add", parameter);
    this.replaceTemplate(EMPTY_BODY, addMethod, new StringHookPoint("    this." + attributeName + ".put(symbol.getName(), symbol);\n" +
        "    symbol.setEnclosingScope(this);"));
    return addMethod;
  }

  protected ASTCDMethod createRemoveSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, StringTransformations.uncapitalize(SYMBOL_SUFFIX));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "remove", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + attributeName + ".remove(symbol.getName(), symbol);"));
    return method;
  }

  protected ASTCDMethod createGetSymbolListMethod(ASTCDAttribute astcdAttribute) {
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(astcdAttribute.getMCType()).build();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, returnType, "get" + StringTransformations.capitalize(astcdAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this." + astcdAttribute.getName() + ";"));
    return method;
  }


}
