package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java.factories.DecorationHelper;

public class ListSuffixDecorator extends AbstractTransformer<ASTCDCompilationUnit> {

  protected static final String LIST_SUFFIX_S = "s";

  @Override
  public ASTCDCompilationUnit decorate( final ASTCDCompilationUnit originalInput, ASTCDCompilationUnit changedInput) {
    for (ASTCDInterface astcdInterface : originalInput.getCDDefinition().getCDInterfaceList()) {
      for (ASTCDAttribute astcdAttribute : astcdInterface.getCDAttributeList()) {
        if (DecorationHelper.isListType(astcdAttribute.printType())) {
          astcdAttribute.setName(getAttributeNameWithListSuffix(astcdAttribute));
        }
      }
    }
    for (ASTCDClass astcdClass : originalInput.getCDDefinition().getCDClassList()) {
      for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
        if (DecorationHelper.isListType(astcdAttribute.printType())) {
          astcdAttribute.setName(getAttributeNameWithListSuffix(astcdAttribute));
        }
      }
    }
    for (ASTCDEnum astcdEnum : originalInput.getCDDefinition().getCDEnumList()) {
      for (ASTCDAttribute astcdAttribute : astcdEnum.getCDAttributeList()) {
        if (DecorationHelper.isListType(astcdAttribute.printType())) {
          astcdAttribute.setName(getAttributeNameWithListSuffix(astcdAttribute));
        }
      }
    }
    return originalInput;
  }

  protected String getAttributeNameWithListSuffix(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getName() + LIST_SUFFIX_S;
  }
}
