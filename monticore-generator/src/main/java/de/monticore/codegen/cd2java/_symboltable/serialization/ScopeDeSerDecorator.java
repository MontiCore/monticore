/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.utils.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a ScopeDeSer class from a grammar
 */
public class ScopeDeSerDecorator extends AbstractDecorator {

  //The following is a complete list of templates used by this decorator (i.e., all other templates
  // associated with scope desers can be removed):
  public static final String SERIALIZE_TEMPL = "_symboltable.serialization.scopeDeSer.Serialize4ScopeDeSer";
  public static final String SERIALIZE_AS_TEMPL = "_symboltable.serialization.scopeDeSer.SerializeAS4ScopeDeSer";

  protected final SymbolTableService symbolTableService;

  @Deprecated //Remove nach deser umbau
  protected static final String TEMPLATE_PATH = "_symboltable.serialization.scopeDeSer.";

  protected MethodDecorator methodDecorator;

  protected VisitorService visitorService;

  protected BITSer bitser;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
                             final SymbolTableService symbolTableService, final MethodDecorator methodDecorator,
                             VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.bitser = new BITSer();
  }

  /**
   * @param scopeInput  for scopeRule attributes and methods
   */
  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput) {
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    String symbols2JsonName = symbolTableService.getSymbols2JsonFullName();
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String artifactScopeInterfaceFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    String scopeClassFullName = symbolTableService.getScopeInterfaceFullName();
    String simpleName = symbolTableService.getCDName();
    String symTabMillFullName = symbolTableService.getMillFullName();

    ASTMCQualifiedType interfaceName = getMCTypeFacade().
            createQualifiedType(I_DE_SER+"<"+scopeInterfaceName+", "+ symbols2JsonName +">");

    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
            .createQualifiedType(scopeInterfaceName),"toSerialize");
    ASTCDParameter asParam = getCDParameterFacade().createParameter(getMCTypeFacade()
            .createQualifiedType(artifactScopeInterfaceFullName),"toSerialize");
    ASTCDParameter s2jParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(symbols2JsonName),"s2j");
    ASTCDParameter scopeJsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);

    // list of all scope rule attributes
    List<ASTCDAttribute> scopeRuleAttrList = scopeInput.deepClone().getCDDefinition()
        .getCDClassList()
        .stream()
        .map(ASTCDClass::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttrList
        .forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    return CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface(interfaceName)

        // serialization
        .addCDMethod(createSerializeMethod(scopeParam, s2jParam, scopeRuleAttrList))
        .addCDMethod(createSerializeASMethod(asParam, s2jParam, scopeRuleAttrList))
        .addAllCDMethods(createSerializeScopeRuleAttributesMethod(scopeRuleAttrList, scopeParam, s2jParam))
        .addCDMethod(createSerializeAddonsMethod(scopeParam, s2jParam))
        .addCDMethod(createSerializeAddonsMethod(asParam, s2jParam))

        //deserialization
        .addCDMethod(createDeserializeStringMethod(artifactScopeFullName, artifactScopeInterfaceFullName))
        .addCDMethod(createDeserializeScopeMethod(scopeClassFullName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttrList))
        .addCDMethod(createDeserializeArtifactScopeMethod(artifactScopeInterfaceFullName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttrList))
        .addAllCDMethods(createDeserializeAddonsMethods(scopeJsonParam))
        .addAllCDMethods(createDeserializeScopeRuleAttributesMethod(scopeRuleAttrList, scopeJsonParam))
        .build();
  }

  ////////////////////////////// SERIALIZATON //////////////////////////////////////////////////////
  protected ASTCDMethod createSerializeMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<ASTCDAttribute> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(SERIALIZE_TEMPL, scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeASMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<ASTCDAttribute> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(SERIALIZE_AS_TEMPL,  scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeAddonsMethod(ASTCDParameter toSerialize, ASTCDParameter s2j) {
    return getCDMethodFacade().createMethod(PUBLIC, "serializeAddons", toSerialize, s2j);
  }


  protected List<ASTCDMethod> createSerializeScopeRuleAttributesMethod(
      List<ASTCDAttribute> attributeList, ASTCDParameter toSerialize, ASTCDParameter s2j) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = "serialize" + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, toSerialize, s2j);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser.getSerialHook(attr.printType(), attr.getName());
      if(impl.isPresent()){
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else{
        method.getModifier().setAbstract(true);
      }
      methodList.add(method);
    }
    return methodList;
  }

  ////////////////////////////// DESERIALIZATON ////////////////////////////////////////////////////

  protected ASTCDMethod createDeserializeStringMethod(String artifactScopeName, String artifactScopeInterfaceName) {
    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName), DESERIALIZE,
            stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeString4ScopeDeSer",
            Names.getSimpleName(artifactScopeName)));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName, String simpleName,
                                                     String symTabMill,
                                                     ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeClassName),
            DESERIALIZE + simpleName + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeScope", symTabMill, scopeClassName,
        scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName,
                                                             String simpleName, String symTabMill,
                                                             ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(artifactScopeName),
            DESERIALIZE + simpleName + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeArtifactScope", symTabMill, artifactScopeName, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeAddonsMethods(ASTCDParameter jsonParam) {
    ASTCDParameter scopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    ASTCDMethod scopeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAddons", scopeParam, jsonParam);

    ASTCDParameter artifactScopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getArtifactScopeInterfaceType(), SCOPE_VAR);
    ASTCDMethod artifactScopeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAddons", artifactScopeParam,
            jsonParam);
    return Lists.newArrayList(scopeMethod, artifactScopeMethod);
  }

  protected List<ASTCDMethod> createDeserializeScopeRuleAttributesMethod(
      List<ASTCDAttribute> attributeList, ASTCDParameter scopeJsonParam) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PUBLIC, attr.getMCType(), methodName, scopeJsonParam);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser.getDeserialHook(attr.printType(), attr.getName(), "scopeJson");
      if(impl.isPresent()){
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else{
        method.getModifier().setAbstract(true);
      }
      methodList.add(method);
      methodList.add(method);
    }
    return methodList;
  }

}
