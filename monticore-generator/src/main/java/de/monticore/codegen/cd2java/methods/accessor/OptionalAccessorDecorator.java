package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.MCCollectionTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class OptionalAccessorDecorator extends AbstractDecorator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String GET = "get%s";

  protected static final String GET_OPT = "get%sOpt";

  protected static final String IS_PRESENT = "isPresent%s";

  protected String naiveAttributeName;

  public OptionalAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    //todo find better util than the DecorationHelper
    this.naiveAttributeName = getNaiveAttributeName(ast);
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod getOpt = createGetOptMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod();
    return new ArrayList<>(Arrays.asList(get, getOpt, isPresent));
  }

  protected String getNaiveAttributeName(ASTCDAttribute ast) {
    return StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName()));
  }

  protected ASTCDMethod createGetMethod(final ASTCDAttribute ast) {
    String name = String.format(GET, naiveAttributeName);
    ASTMCType type = MCCollectionTypesHelper.getReferenceTypeFromOptional(ast.getMCType().deepClone()).getMCTypeOpt().get();
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, returnType, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.Get", ast, naiveAttributeName));
    return method;
  }

  protected ASTCDMethod createGetOptMethod(final ASTCDAttribute ast) {
    String name = String.format(GET_OPT, naiveAttributeName);
    ASTMCType type = ast.getMCType().deepClone();
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, returnType, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }

  private ASTCDMethod createIsPresentMethod() {
    String name = String.format(IS_PRESENT, naiveAttributeName);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(getCDTypeFacade().createBooleanType()).build();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, returnType, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.opt.IsPresent", naiveAttributeName));
    return method;
  }
}
