/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MandatoryAccessorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  private static final String GET = "get%s";

  private static final String IS = "is%s";

  public MandatoryAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }


  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createGetter(ast)));
  }

  private ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String getterPrefix;
    if (getCDTypeFacade().isBooleanType(ast.getMCType())) {
      getterPrefix = IS;
    } else {
      getterPrefix = GET;
    }
    //todo find better util than the DecorationHelper
    String name = String.format(getterPrefix, StringUtils.capitalize(DecorationHelper.getNativeAttributeName(ast.getName())));
    ASTMCType type = ast.getMCType().deepClone();
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(type).build();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC, returnType, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }
}
