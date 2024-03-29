/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class MandatoryAccessorDecorator extends AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> {

  protected static final String GET = "get%s";

  protected static final String IS = "is%s";

  public MandatoryAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }


  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(Arrays.asList(createGetter(ast)));
  }

  protected ASTCDMethod createGetter(final ASTCDAttribute ast) {
    String getterPrefix;
    if (getMCTypeFacade().isBooleanType(ast.getMCType())) {
      getterPrefix = IS;
    } else {
      getterPrefix = GET;
    }
    String name = String.format(getterPrefix, StringUtils.capitalize(getDecorationHelper().getNativeAttributeName(ast.getName())));
    ASTMCType type = ast.getMCType().deepClone();
    ASTCDMethod method = this.getCDMethodFacade().createMethod(PUBLIC.build(), type, name);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("methods.Get", ast));
    return method;
  }
}
