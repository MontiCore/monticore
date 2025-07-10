/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.manipul;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.mc2cd.AttributeCategory;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.AttributeCategory.determineCategory;

/**
 * Removes duplicate attributes that may result from rules having multiple nonterminals referencing
 * <p>
 * the same rule.
 */
final class RemoveRedundantAttributesManipulation implements UnaryOperator<ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDClass cdClass : cdCompilationUnit.getCDDefinition().getCDClassesList()) {
      cdClass.setCDAttributeList(removeRedundantAttributes(cdClass.getCDAttributeList()));
    }
    for (ASTCDInterface cdClass : cdCompilationUnit.getCDDefinition().getCDInterfacesList()) {
      cdClass.setCDAttributeList(removeRedundantAttributes(cdClass.getCDAttributeList()));
    }
    return cdCompilationUnit;
  }

  /**
   * @param cdAttributes the list of all the attributes in the class
   */
  List<ASTCDAttribute> removeRedundantAttributes(List<ASTCDAttribute> cdAttributes) {
    List<ASTCDAttribute> uniqueAttributes = new ArrayList<>();
    // In case multiple attributes (with the same name & type) are present:
    // The first attribute, which is created from the classprod/interface
    // itself, should be kept.
    // The 2nd attribute is, e.g., created by the InheritedAttributesTranslation,
    // and should be removed as redundant,
    // but only IFF the second attribute does not have more stereotypes
    // (in particular, the inherited one)
    // [a1=Attr[name], a2=Attr[name <<inherited>]] => a2
    // [a1=Attr[name], a2=Attr[name]] => a1
    // in addition, the attribute with the highest category is kept
    outer:
    for (ASTCDAttribute cdAttributeCand : cdAttributes) {
      for (int i = 0; i < uniqueAttributes.size(); i++) {
        ASTCDAttribute existingAttribute = uniqueAttributes.get(i);
        if (isColliding(cdAttributeCand, existingAttribute)) {
          if (isAttrPreferred(cdAttributeCand, existingAttribute)) {
            uniqueAttributes.set(i, cdAttributeCand);
          }
          continue outer;
        }
      }
      uniqueAttributes.add(cdAttributeCand);
    }
    return uniqueAttributes;
  }

  /**
   * Checks if the two attributes collide, i.e. share the same name & (original) type
   *
   * @return true if another attribute with the same variable name, the same original type exists
   */
  protected static boolean isColliding(ASTCDAttribute inspectedAttribute,
                                       ASTCDAttribute remainingAttribute) {
    String inspectedName = inspectedAttribute.getName();
    String inspectedType = getOriginalTypeName(inspectedAttribute);

    boolean sameName = inspectedName.equalsIgnoreCase(remainingAttribute.getName());

    boolean sameType = inspectedType.equals(getOriginalTypeName(remainingAttribute));

    return sameName && sameType;
  }

  /**
   * Checks if the candidate should replace the existing attribute:
   * - if it has a higher category, in case the same category is used:
   * - if the candidate has more stereotypes (in particular inherited)
   * @return true if the candAttr should be preferred over the existing one
   */
  protected static boolean isAttrPreferred(ASTCDAttribute candAttr,
                                           ASTCDAttribute existingAttr) {
    // First: check category
    AttributeCategory candCategory = determineCategory(candAttr);
    int categoryRelation = candCategory
            .compareTo(AttributeCategory.determineCategory(existingAttr));
    if (categoryRelation < 0) {
      return false;
    } else if (categoryRelation > 0) {
      return true;
    }
    // The same category: check stereo counts to keep inherited
    int candStereoCount = candAttr.getModifier().isPresentStereotype()
            ? candAttr.getModifier().getStereotype().getValuesList().size() : 0;
    int existingStereoCount = existingAttr.getModifier().isPresentStereotype()
            ? existingAttr.getModifier().getStereotype().getValuesList().size() : 0;
    return candStereoCount > existingStereoCount;
    // By default, the first attribute is kept
  }

  protected static String getOriginalTypeName(ASTCDAttribute cdAttribute) {
    AttributeCategory category = AttributeCategory.determineCategory(cdAttribute);
    if (category == AttributeCategory.GENERICLIST || category == AttributeCategory.OPTIONAL) {
      Optional<String> firstArgument = getFirstTypeArgument(cdAttribute);
      if (firstArgument.isPresent()) {
        return firstArgument.get();
      }
    }
    return TransformationHelper.typeToString(cdAttribute.getMCType());
  }

  protected static Optional<String> getFirstTypeArgument(ASTCDAttribute cdAttribute) {
    // the 'List' in 'List<String>'
    if (cdAttribute.getMCType() instanceof ASTMCGenericType) {
      List<ASTMCTypeArgument> argList = ((ASTMCGenericType) cdAttribute.getMCType()).getMCTypeArgumentList();
      if (!argList.isEmpty()) {
        String simpleTypeName = CD4CodeMill.prettyPrint(argList.get(0).getMCTypeOpt().get(), false);
        return Optional.of(simpleTypeName);
      }
    }
    return Optional.empty();
  }

}
