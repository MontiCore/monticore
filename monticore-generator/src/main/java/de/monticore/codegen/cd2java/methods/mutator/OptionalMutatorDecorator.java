/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class OptionalMutatorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String SET = "set%s";

  protected static final String SET_OPT = "set%sOpt";

  protected static final String SET_ABSENT = "set%sAbsent";

  protected String naiveAttributeName;

  public OptionalMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    //todo find better util than the DecorationHelper
    List<ASTCDMethod> methodList = new ArrayList<>();
    naiveAttributeName = StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName()));
    methodList.add(createSetMethod(ast));
    methodList.add(createSetAbsentMethod(ast));
    return methodList;
  }

  protected ASTCDMethod createSetMethod(final ASTCDAttribute ast) {
    String name = String.format(SET, naiveAttributeName);
    ASTMCType parameterType = MCCollectionTypesHelper.getReferenceTypeFromOptional(ast.getMCType()).getMCTypeOpt().get().deepClone();
    ASTCDParameter parameter = this.getCDParameterFacade().createParameter(parameterType, ast.getName());
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name, parameter);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Set", ast, naiveAttributeName));
    return method;
  }

  protected ASTCDMethod createSetAbsentMethod(final ASTCDAttribute ast) {
    String name = String.format(SET_ABSENT, naiveAttributeName);
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.SetAbsent", ast));
    return method;
  }
}
