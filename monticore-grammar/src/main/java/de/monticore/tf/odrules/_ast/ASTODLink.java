/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules._ast;


import de.monticore.umlstereotype._ast.ASTStereotype;
import javax.annotation.Nonnull;

import java.util.NoSuchElementException;

/**
 * AST node for links used in object-diagram transformation rules.
 */
public class ASTODLink extends ASTODLinkTOP {

  protected  ASTODLink () {
    // empty body
  }

  protected  ASTODLink (ASTStereotype stereotype,
                        String name,
                        java.util.List<de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName> leftReferenceNames,
                        String leftRole,
                        String rightRole,
                        ASTCardinality attributeCardinality,
                        java.util.List<de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName> rightReferenceNames,
                        boolean link)  {

    super();
    setStereotype(stereotype);
    setName(name);
    setLeftReferenceNameList(leftReferenceNames);
    setLeftRole(leftRole);
    setRightRole(rightRole);
    setAttributeCardinality(attributeCardinality);
    setRightReferenceNameList(rightReferenceNames);
    //super(stereotype, name, leftReferenceNames, leftRole, rightRole, rightReferenceNames, link);
  }

  private String sLeftRole;
  private String sRightRole;

  @Override
  public boolean isLink() {
    // ODLinks in ODRules are always compositions
    return link;
  }

  /**
   * Checks whether this link has a stereotype with the given name.
   *
   * @param name stereotype name to check
   * @return {@code true} if the stereotype is present
   */
  public boolean hasStereotype(@Nonnull String name) {
    if (isPresentStereotype()) {
      return getStereotype().contains(name.intern());
    }
    return false;
  }

  /**
   * Checks whether this link has a stereotype with the given name and value.
   *
   * @param name stereotype name to check
   * @param value expected stereotype value
   * @return {@code true} if the stereotype with the expected value is present
   */
  public boolean hasStereotype(@Nonnull String name, @Nonnull String value) {
    if (isPresentStereotype()) {
      return getStereotype().contains(name.intern(), value.intern());
    }
    return false;
  }

  /**
   * Returns the value of a stereotype.
   *
   * @param name stereotype name
   * @return stereotype value, or an empty string if no value exists
   */
  public @Nonnull String getStereotypeValue(@Nonnull String name) {
    if (isPresentStereotype()) {
      try {
        String value = getStereotype().getValue(name.intern());
        return value != null ? value : "";
      } catch (NoSuchElementException e) {
        return "";
      }
    }
    return "";
  }

  /**
   * Returns the explicit link name.
   *
   * @return link name, or an empty string if no name is present
   */
  public @Nonnull String printName() {
    if (isPresentName()) {
      return getName();
    }
    return "";
  }


  /**
   * Returns the left role of the according association of this link. Default is
   * the name of the association if leftRole is not set. In case the association
   * is unnamed the type-name of the left reference (in lower-case) is used
   * instead.
   *
   * @return left role, never {@code null}
   */
  public @Nonnull String printLeftRole() {
    if (sLeftRole == null) {
      if (isPresentLeftRole()) {
        sLeftRole = getLeftRole();
      }
      else if (isPresentName()) {
        sLeftRole = getName();
      }
      else {
          sLeftRole = "";
      }
    }
    return sLeftRole;
  }

  /**
   * Returns the right role of this link.
   * If no explicit right role is present, the link name is used.
   *
   * @return right role, never {@code null}
   */
  public @Nonnull String printRightRole() {
    if (sRightRole == null) {
      if (isPresentRightRole()) {
        sRightRole = getRightRole();
      }
      else if (isPresentName()) {
        sRightRole = getName();
      }
      else {

          sRightRole = "";
      }
    }
    return sRightRole;
  }

  /**
   * Checks whether the link cardinality allows multiple target values.
   *
   * @return {@code true} for {@code *} or {@code 1..*}
   */
  public boolean isAttributeIterated() {
    return getAttributeCardinality().isMany() || getAttributeCardinality().isOneToMany();
  }

  /**
   * Checks whether the link cardinality is optional.
   *
   * @return {@code true} for optional cardinality
   */
  public boolean isAttributeOptional() {
    return getAttributeCardinality().isOptional();
  }


}
