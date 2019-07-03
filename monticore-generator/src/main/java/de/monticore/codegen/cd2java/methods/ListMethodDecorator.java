package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.exception.DecoratorErrorCode;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public abstract class ListMethodDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  protected String capitalizedAttributeNameWithS;

  protected String capitalizedAttributeNameWithOutS;

  protected String attributeType;

  public ListMethodDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    this.capitalizedAttributeNameWithS = getCapitalizedAttributeNameWithS(ast);
    this.capitalizedAttributeNameWithOutS = (capitalizedAttributeNameWithS.endsWith("s"))
        ? capitalizedAttributeNameWithS.substring(0, capitalizedAttributeNameWithS.length() - 1) :
        capitalizedAttributeNameWithS;
    this.attributeType = getAttributeType(ast);

    List<ASTCDMethod> methods = getMethodSignatures().stream()
        .map(getCDMethodFacade()::createMethodByDefinition)
        .collect(Collectors.toList());

    methods.forEach(m -> this.replaceTemplate(EMPTY_BODY, m, createListImplementation(m)));
    return methods;
  }

  protected abstract List<String> getMethodSignatures();

  protected String getTypeArgumentFromListType(ASTType type) {
    if (type instanceof ASTSimpleReferenceType && ((ASTSimpleReferenceType) type).getNameList().stream().anyMatch(s -> s.contains("List"))
        && ((ASTSimpleReferenceType) type).getTypeArguments().sizeTypeArguments() == 1) {
      //try to get inner TypeArgument for List
      ASTTypeArgument typeArgument = ((ASTSimpleReferenceType) type).getTypeArguments().getTypeArgument(0);
      return TypesPrinter.printTypeArgument(typeArgument);
    } else {
      //error if the List type was not correct
      String typeString = TypesPrinter.printType(type);
      throw new DecorateException(DecoratorErrorCode.EXPECTED_LIST_TYPE, typeString);
    }
  }

  private HookPoint createListImplementation(final ASTCDMethod method) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeNameWithOutS);
    int attributeIndex = method.getName().lastIndexOf(capitalizedAttributeNameWithOutS);
    String methodName = method.getName().substring(0, attributeIndex);
    String parameterCall = method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    String returnType = method.printReturnType();

    return new TemplateHookPoint("methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }

  public String getCapitalizedAttributeNameWithS(ASTCDAttribute attribute) {
    //todo find better util than the DecorationHelper
    return StringUtils.capitalize(DecorationHelper.getNativeAttributeName(attribute.getName()));
  }

  public String getAttributeType(ASTCDAttribute attribute) {
    return getTypeArgumentFromListType(attribute.getType());
  }
}
