package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public abstract class ListMethodDecorator implements Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  protected final GlobalExtensionManagement glex;

  protected final CDMethodFactory cdMethodFactory;

  protected String capitalizedAttributeName;

  protected String attributeType;

  public ListMethodDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    this.capitalizedAttributeName = StringUtils.capitalize(ast.getName());
    this.attributeType = getTypeArgumentFromListType(ast.getType());

    List<ASTCDMethod> methods = getMethodSignatures().stream()
        .map(cdMethodFactory::createMethodByDefinition)
        .collect(Collectors.toList());

    methods.forEach(m -> this.glex.replaceTemplate(EMPTY_BODY, m, createListImplementation(m, capitalizedAttributeName)));

    return methods;
  }

  protected abstract List<String> getMethodSignatures();

  private String getTypeArgumentFromListType(ASTType type) {
    String typeString = TypesPrinter.printType(type);
    int lastListIndex = typeString.lastIndexOf("List<")+5;
    return typeString.substring(lastListIndex, typeString.length() - 1);
  }

  private HookPoint createListImplementation(final ASTCDMethod method, final String capitalizedAttributeName) {
    String attributeName = StringUtils.uncapitalize(capitalizedAttributeName);
    String methodName = method.getName().substring(0, method.getName().length() - capitalizedAttributeName.length());
    String parameterCall = method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    String returnType = method.printReturnType();

    return new TemplateHookPoint("methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }
}
