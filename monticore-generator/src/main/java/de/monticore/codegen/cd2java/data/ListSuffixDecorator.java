/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.data;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;

import java.util.ArrayList;
import java.util.List;

public class ListSuffixDecorator extends AbstractTransformer<ASTCDCompilationUnit> {
  /*
  adds an 's' behind the attribute name of list type attributes
  e.g. List<String> name; -> List<String> names;
   */

  public static final String LIST_SUFFIX_S = "s";

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit originalInput, ASTCDCompilationUnit changedInput) {
    for (ASTCDInterface astcdInterface : changedInput.getCDDefinition().getCDInterfaceList()) {
      addSToListAttributes(astcdInterface.getCDAttributeList());
      astcdInterface.setCDAttributeList(getAttributesUniqueAgain(astcdInterface.getCDAttributeList()));
    }
    for (ASTCDClass astcdClass : changedInput.getCDDefinition().getCDClassList()) {
      addSToListAttributes(astcdClass.getCDAttributeList());
      astcdClass.setCDAttributeList(getAttributesUniqueAgain(astcdClass.getCDAttributeList()));
    }
    for (ASTCDEnum astcdEnum : changedInput.getCDDefinition().getCDEnumList()) {
      addSToListAttributes(astcdEnum.getCDAttributeList());
      astcdEnum.setCDAttributeList(getAttributesUniqueAgain(astcdEnum.getCDAttributeList()));
    }
    return originalInput;
  }

  protected String getAttributeNameWithListSuffix(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.getName() + LIST_SUFFIX_S;
  }

  protected void addSToListAttributes(List<ASTCDAttribute> attributeList) {
    for (ASTCDAttribute astcdAttribute : attributeList) {
      if (getDecorationHelper().isListType(astcdAttribute.printType()) &&
          hasDerivedAttributeName(astcdAttribute)) {
        astcdAttribute.setName(getAttributeNameWithListSuffix(astcdAttribute));
      }
    }
  }

  /**
   * because of the s addition it is possible that now two attributes with the same name exist
   * to avoid double names for attributes, make them unique again here
   * @param attributeList
   * @return list of attribute with unique names
   */
  protected List<ASTCDAttribute> getAttributesUniqueAgain(List<ASTCDAttribute> attributeList) {
    List<ASTCDAttribute> uniqueAttributes = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : attributeList) {
      if(uniqueAttributes.stream().noneMatch(attr-> astcdAttribute.getName().equals(attr.getName()))){
        uniqueAttributes.add(astcdAttribute);
      }
    }
    return uniqueAttributes;
  }

  protected boolean hasDerivedAttributeName(ASTCDAttribute astcdAttribute) {
    return astcdAttribute.isPresentModifier() && astcdAttribute.getModifier().isPresentStereotype()
        && astcdAttribute.getModifier().getStereotype().sizeValues() > 0 &&
        astcdAttribute.getModifier().getStereotype().getValueList()
            .stream()
            .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
  }
}
