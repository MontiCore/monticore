package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.factories.DecorationHelper;

import java.util.List;

public class ListSuffixDecorator extends AbstractTransformer<ASTCDCompilationUnit> {
  /*
  adds an 's' behind the attribute name of list type attributes
  e.g. List<String> name; -> List<String> names;
   */

  protected static final String LIST_SUFFIX_S = "s";

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalInput, ASTCDCompilationUnit changedInput) {
    for (ASTCDInterface astcdInterface : changedInput.getCDDefinition().getCDInterfaceList()) {
      addSToAttributes(astcdInterface.getCDAttributeList());
    }
    for (ASTCDClass astcdClass : changedInput.getCDDefinition().getCDClassList()) {
      addSToAttributes(astcdClass.getCDAttributeList());
    }
    for (ASTCDEnum astcdEnum : changedInput.getCDDefinition().getCDEnumList()) {
      addSToAttributes(astcdEnum.getCDAttributeList());
    }
    return originalInput;
  }

  protected String getAttributeNameWithListSuffix(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getName() + LIST_SUFFIX_S;
  }

  protected void addSToAttributes(List<ASTCDAttribute> attributeList) {
    for (ASTCDAttribute astcdAttribute : attributeList) {
      if (DecorationHelper.isListType(astcdAttribute.printType())) {
        astcdAttribute.setName(getAttributeNameWithListSuffix(astcdAttribute));
      }
    }
  }
}
