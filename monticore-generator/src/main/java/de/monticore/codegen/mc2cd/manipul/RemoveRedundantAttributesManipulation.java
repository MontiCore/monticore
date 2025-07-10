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

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

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
    Iterator<ASTCDAttribute> iterator = cdAttributes.iterator();
    while (iterator.hasNext()) {
      ASTCDAttribute inspectedAttribute = iterator.next();
      List<ASTCDAttribute> remainingAttributes = cdAttributes
          .stream()
          .filter(attribute -> !attribute.equals(inspectedAttribute))
          .collect(Collectors.toList());
      boolean isRedundant = remainingAttributes
          .stream()
          .anyMatch(a -> isRedundant(inspectedAttribute, a));
      if (isRedundant) {
        iterator.remove();
      }
    }
    return cdAttributes;
  }

  /**
   * Checks if the remaining attributes contain an attribute that makes the inspected attribute
   * redundant.
   *
   * @return true if another attribute with the same variable name, the same original type and an
   * equal or higher category exists
   */
  protected static boolean isRedundant(ASTCDAttribute inspectedAttribute,
                                     ASTCDAttribute remainingAttribute) {
    String inspectedName = inspectedAttribute.getName();
    String inspectedType = getOriginalTypeName(inspectedAttribute);
    AttributeCategory inspectedCategory = determineCategory(inspectedAttribute);

    boolean sameName = inspectedName.equalsIgnoreCase(remainingAttribute.getName());

    boolean sameType = inspectedType.equals(getOriginalTypeName(remainingAttribute));

    boolean sameOrHigherCategory = inspectedCategory
        .compareTo(AttributeCategory.determineCategory(remainingAttribute)) < 1;

    if (sameName && sameType && sameOrHigherCategory) {
      // In case multiple attributes are present:
      // The first attribute, which is created from the classprod/interface
      // itself, should be kept.
      // The 2nd attribute is, e.g., created by the InheritedAttributesTranslation,
      // and should be removed as redundant,
      // but only IFF the second attribute does not have more stereotypes
      // (in particular, the inherited one)
      // [a1=Attr[name], a2=Attr[name <<inherited>]] => a2
      // [a1=Attr[name], a2=Attr[name]] => a1
      int inspectedStereoCount = inspectedAttribute.getModifier().isPresentStereotype()
              ? inspectedAttribute.getModifier().getStereotype().getValuesList().size() : 0;
      int remainingStereoCount = remainingAttribute.getModifier().isPresentStereotype()
              ? remainingAttribute.getModifier().getStereotype().getValuesList().size() : 0;

      return inspectedStereoCount < remainingStereoCount;
    }

    return false;
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
