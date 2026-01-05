/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public abstract class ListMethodDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected String capitalizedAttributeNameWithS;

  protected String capitalizedAttributeNameWithOutS;

  protected String attributeType;

  String ERROR_CODE = "0xA9091";

  public ListMethodDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    this.capitalizedAttributeNameWithS = getCapitalizedAttributeNameWithS(ast);
    // if the attributeName is set by itself then the s is not removed
    // this means capitalizedAttributeNameWithS == capitalizedAttributeNameWithOutS
    this.capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS;
    // but if the attributeName is derived then the s is removed
    if(capitalizedAttributeNameWithS.endsWith("s") && hasDerivedAttributeName(ast)) {
      this.capitalizedAttributeNameWithOutS = capitalizedAttributeNameWithS.substring(0, capitalizedAttributeNameWithS.length() - 1);
    }
    this.attributeType = getAttributeType(ast);

    List<ASTCDMethod> methods;
    List<String> methodTypes;
    boolean isGeneric = getDecorationHelper().isAstNode(ast);
    if (isGeneric) {
      methods = getMethodSignaturesGeneric().values().stream()
          .map(getCDMethodFacade()::createMethodByDefinition)
          .collect(Collectors.toList());
      methodTypes = getMethodSignaturesGeneric().keySet().stream().collect(Collectors.toList());
    } else {
      methods = getMethodSignatures().values().stream()
          .map(getCDMethodFacade()::createMethodByDefinition)
          .collect(Collectors.toList());
      methodTypes = getMethodSignatures().keySet().stream().collect(Collectors.toList());
    }

    for(int i = 0; i < methods.size(); i++) {
      String methodSignature = methodTypes.get(i);
      ASTCDMethod method = methods.get(i);
      this.replaceTemplate(EMPTY_BODY, method, createListImplementation(method,methodSignature,isGeneric));
    }
    return methods;
  }

  protected boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getModifier().isPresentStereotype()
        && astcdAttribute.getModifier().getStereotype().sizeValues() > 0 &&
        astcdAttribute.getModifier().getStereotype().getValuesList()
            .stream()
            .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
  }

  protected abstract Map<String, String> getMethodSignatures();

  protected abstract Map<String, String> getMethodSignaturesGeneric();

  protected String getTypeArgumentFromListType(ASTMCType type) {
    String typeString = CD4CodeMill.prettyPrint(type, false);
    int lastListIndex = typeString.lastIndexOf("List<") + 5;
    return typeString.substring(lastListIndex, typeString.length() - 1);
  }

  protected HookPoint createListImplementation(final ASTCDMethod method, final String methodSignature, boolean isGeneric) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeNameWithOutS);
    int attributeIndex = method.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
    String methodName = method.getName().substring(0, attributeIndex);
    String parameterCall = method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));

    String returnType = CD4CodeMill.prettyPrint(method.getMCReturnType(), false);
    if(isGeneric) {
      return new TemplateHookPoint("mc.methods.MethodDelegateGeneric", attributeName, methodName, parameterCall, returnType, attributeType, methodSignature, ERROR_CODE);
    }else{
      return new TemplateHookPoint("methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
    }
  }

  public String getCapitalizedAttributeNameWithS(ASTCDAttribute attribute) {
    return StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(attribute.getName()));
  }

  public String getAttributeType(ASTCDAttribute attribute) {
    return getTypeArgumentFromListType(attribute.getMCType());
  }
}
