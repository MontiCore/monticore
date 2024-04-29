/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.google.common.collect.Lists;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDClassBuilder;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolsurrogatemutator.MandatoryMutatorSymbolSurrogateDecorator;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.umlmodifier._ast.ASTModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.NAME_VAR;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * creates a SymbolLoader class from a grammar
 */
@Deprecated
public class SymbolSurrogateDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {
  
  protected static final String TEMPLATE_PATH = "_symboltable.symbolsurrogate.";
  protected SymbolTableService symbolTableService;
  protected VisitorService visitorService;
  protected MethodDecorator methodDecorator;
  protected MandatoryMutatorSymbolSurrogateDecorator symbolSurrogateMethodDecorator;
  
  public SymbolSurrogateDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final VisitorService visitorService,
                                  final MethodDecorator methodDecorator,
                                  final MandatoryMutatorSymbolSurrogateDecorator symbolSurrogateMethodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.symbolSurrogateMethodDecorator = symbolSurrogateMethodDecorator;
  }
  
  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolSurrogateSimpleName = symbolTableService.getSymbolSurrogateSimpleName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String simpleName = symbolInput.getName();
    ASTModifier modifier = symbolTableService.createModifierPublicModifier(symbolInput.getModifier());
    
    // symbol rule methods and attributes
    List<ASTCDMethod> symbolRuleAttributeMethods = symbolInput.getCDAttributeList()
      .stream()
      .map(methodDecorator.getMutatorDecorator()::decorate)
      .flatMap(List::stream)
      .collect(Collectors.toList());
    symbolRuleAttributeMethods.addAll(symbolInput.getCDAttributeList()
      .stream()
      .map(methodDecorator.getAccessorDecorator()::decorate)
      .flatMap(List::stream)
      .collect(Collectors.toList()));
    //name and enclosing scope methods do not delegate to the symbol
    List<ASTCDMethod> delegateMethods = symbolRuleAttributeMethods.stream()
      .filter(m -> !m.getName().equals("setName"))
      .filter(m -> !m.getName().equals("getName"))
      .filter(m -> !m.getName().equals("setEnclosingScope"))
      .filter(m -> !m.getName().equals("getEnclosingScope"))
      .collect(Collectors.toList());
    List<ASTCDMethod> delegateSymbolRuleAttributeMethods = createOverriddenMethodDelegates(delegateMethods);
    List<ASTCDMethod> symbolRuleMethods = symbolInput.getCDMethodList().stream()
            .map(a -> a.deepClone())
            .collect(Collectors.toList());
    List<ASTCDMethod> delegateSymbolRuleMethods = createOverriddenMethodDelegates(symbolRuleMethods);

    List<ASTCDMethod> delegateAccecptMethods = createOverriddenMethodDelegates(createAcceptTraverserMethods(symbolInput));

    ASTCDAttribute delegateAttribute = createDelegateAttribute(symbolFullName);
    
    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = Lists.newArrayList(createGetNameMethod());
    nameMethods.addAll(symbolSurrogateMethodDecorator.decorate(nameAttribute));
    
    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceType);
    List<ASTCDMethod> enclosingScopeMethods = Lists.newArrayList(createSetEnclosingScopeMethod(enclosingScopeAttribute, symbolTableService.getScopeInterfaceSimpleName()));
    enclosingScopeMethods.add(createGetEnclosingScopeMethod(enclosingScopeAttribute));
    
    ASTCDClassBuilder builder = CD4AnalysisMill.cDClassBuilder()
      .setName(symbolSurrogateSimpleName)
      .setModifier(modifier)
      .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(getMCTypeFacade().createQualifiedType(symbolTableService.getSymbolFullName(symbolInput))).build())
      .addCDMember(createConstructor(symbolSurrogateSimpleName))
      .addAllCDMembers(nameMethods)
      .addAllCDMembers(delegateSymbolRuleAttributeMethods)
      .addAllCDMembers(delegateAccecptMethods)
      .addCDMember(createGetFullNameMethod())
      .addCDMember(createOverridenDeterminePackageName())
      .addCDMember(createOverridenDetermineFullName())
      .addAllCDMembers(delegateSymbolRuleMethods);
    return builder
      .addCDMember(delegateAttribute)
      .addAllCDMembers(enclosingScopeMethods)
      .addCDMember(createCheckLazyLoadDelegateMethod(symbolSurrogateSimpleName, symbolFullName, simpleName, scopeInterfaceType))
      .addCDMember(createLazyLoadDelegateMethod(symbolSurrogateSimpleName, symbolFullName, simpleName, scopeInterfaceType))
      .build();
  }

  protected ASTCDMethod createSetEnclosingScopeMethod(ASTCDAttribute enclosingScopeAttribute, String scopeName) {
    ASTCDParameter param = getCDParameterFacade().createParameter(enclosingScopeAttribute.getMCType(), "enclosingScope");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "setEnclosingScope", param);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "SetEnclosingScope4SymbolSurrogate", enclosingScopeAttribute, scopeName));
    return method;
  }
  
  protected ASTCDConstructor createConstructor(String symbolSurrogateClass) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolSurrogateClass, nameParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorSymbolSurrogate"));
    return constructor;
  }
  
  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), "String", "name");
  }
  
  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeType) {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeType, "enclosingScope");
  }

  protected ASTCDMethod createGetNameMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "String", "getName");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        "  if (!checkLazyLoadDelegate()) {\n" +
            "    return name;\n" +
            "  }\n" +
            "  return lazyLoadDelegate().getName();"));
    return method;
  }

  protected ASTCDMethod createGetEnclosingScopeMethod(ASTCDAttribute enclosingScopeAttribute) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), enclosingScopeAttribute.getMCType(), "getEnclosingScope");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetEnclosingScopeSymbolSurrogate", symbolTableService.getScopeInterfaceSimpleName()));
    return method;
  }
  
  protected ASTCDAttribute createDelegateAttribute(String symbolType) {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createOptionalTypeOf(symbolType), "delegate");
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }
  
  protected ASTCDMethod createLazyLoadDelegateMethod(String symbolSurrogateName, String symbolName, String simpleName, String scopeName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(symbolName), "lazyLoadDelegate");
    String generatedError1 = symbolTableService.getGeneratedErrorCode("lazyLoadDelegate1");
    String generatedError2 = symbolTableService.getGeneratedErrorCode("lazyLoadDelegate2");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LazyLoadDelegate", symbolSurrogateName,
      symbolName, simpleName, scopeName, generatedError1, generatedError2));
    return method;
  }
  
  protected ASTCDMethod createCheckLazyLoadDelegateMethod(String symbolSurrogateName, String symbolName, String simpleName, String scopeName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), "checkLazyLoadDelegate");
    String generatedError = symbolTableService.getGeneratedErrorCode("checkLazyLoadDelegate");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "CheckLazyLoadDelegate", symbolSurrogateName,
      symbolName, simpleName, scopeName, generatedError));
    return method;
  }
  
  protected ASTCDMethod createGetFullNameMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "getFullName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetFullName"));
    return method;
  }
  
  protected List<ASTCDMethod> createOverriddenMethodDelegates(List<ASTCDMethod> inheritedMethods) {
    List<ASTCDMethod> overriddenDelegates = new ArrayList<>();
    for (ASTCDMethod inherited : inheritedMethods) {
      ASTCDMethod method = getCDMethodFacade().createMethod(inherited.getModifier(), inherited.getMCReturnType(), inherited.getName(), inherited.getCDParameterList());
      StringBuilder message = new StringBuilder();
      
      message.append("if (!checkLazyLoadDelegate()) {\n");
      
      if (method.getMCReturnType().isPresentMCVoidType()) {
        message.append("return;\n}\n");
      } else {
        if (method.getMCReturnType().getMCType() instanceof ASTMCListType) {
          message.append("return new ArrayList<>();\n}\n");
        } else if (method.getMCReturnType().getMCType() instanceof ASTMCArrayType) {
          String typeOfMethod = ((ASTMCArrayType) method.getMCReturnType().getMCType()).getMCType().printType();
          message.append("return new " + typeOfMethod + "[0];\n}\n");
        } else if (method.getMCReturnType().getMCType() instanceof ASTMCGenericType) {
          String typeOfList = ((ASTMCGenericType)method.getMCReturnType().getMCType()).getMCTypeArgument(0).printType();
          if (("Iterator").equals(((ASTMCGenericType) method.getMCReturnType().getMCType()).getName(0))) {
            message.append("return new ArrayList<" + typeOfList + ">().iterator();\n}\n");
          } else if (("ListIterator").equals(((ASTMCGenericType) method.getMCReturnType().getMCType()).getName(0))) {
            message.append("return new ArrayList<" + typeOfList + ">().listIterator();\n}\n");
          } else if (("Spliterator").equals(((ASTMCGenericType) method.getMCReturnType().getMCType()).getName(0))) {
            message.append("return new ArrayList<" + typeOfList + ">().spliterator();\n}\n");
          } else if (("Stream").equals(((ASTMCGenericType) method.getMCReturnType().getMCType()).getName(0))) {
            message.append("return new ArrayList<" + typeOfList + ">().stream();\n}\n");
          } else {
            message.append("}\n");
          }
        } else if (("boolean").equals(method.getMCReturnType().printType())) {
          message.append("return false;\n}\n");
        } else if (("int").equals(method.getMCReturnType().printType())) {
          message.append("return 0;\n}\n");
        } else if (("String").equals(method.getMCReturnType().printType())) {
          message.append("return \"\";\n}\n");
        } else if (("de.monticore.types.check.SymTypeExpression").equals(method.getMCReturnType().printType())) {
          message.append("return new de.monticore.types.check.SymTypeOfNull();\n}\n");
        } else {
          message.append("}\n");
        }
      }
      if (!method.getMCReturnType().isPresentMCVoidType()) {
        message.append("\treturn ");
      }
      message.append("lazyLoadDelegate().").append(method.getName()).append("(");
      String seperator = "";
      for (ASTCDParameter parameter : method.getCDParameterList()) {
        message.append(seperator).append(parameter.getName());
        seperator = ",";
      }
      message.append(");");
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(message.toString()));
      overriddenDelegates.add(method);
    }
    return overriddenDelegates;
  }

  protected ASTCDMethod createOverridenDeterminePackageName() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createStringType(), "determinePackageName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "DeterminePackageName"));
    return method;
  }

  protected ASTCDMethod createOverridenDetermineFullName() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createStringType(), "determineFullName");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "DetermineFullName"));
    return method;
  }


  protected List<ASTCDMethod> createAcceptTraverserMethods(ASTCDClass symbolInput) {
    List<ASTCDMethod> result = new ArrayList<>();
    ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(visitorService.getTraverserInterfaceFullName());
    ASTCDParameter parameter = getCDParameterFacade().createParameter(visitorType, VISITOR_PREFIX);
    result.add(getCDMethodFacade().createMethod(PUBLIC.build(), ACCEPT_METHOD, parameter));

    // accept methods for super visitors
    List<ASTMCQualifiedType> l = this.visitorService.getAllTraverserInterfacesTypesInHierarchy();
    l.add(getMCTypeFacade().createQualifiedType(VisitorConstants.ITRAVERSER_FULL_NAME));
    for (ASTMCType superVisitorType : l) {
      ASTCDParameter superVisitorParameter = this.getCDParameterFacade().createParameter(superVisitorType, VISITOR_PREFIX);
      result.add(this.getCDMethodFacade().createMethod(PUBLIC.build(), ASTConstants.ACCEPT_METHOD, superVisitorParameter));
    }
    return result;
  }
}
