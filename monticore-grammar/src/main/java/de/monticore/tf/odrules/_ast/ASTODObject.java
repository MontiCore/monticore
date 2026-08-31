/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules._ast;

import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.Names;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * AST node for an object diagram object including cached renderings for name and type.
 */
public class ASTODObject extends ASTODObjectTOP {
  
  protected ASTODObject() {
    // empty body
  }
  
  protected ASTODObject(ASTStereotype stereotype, String name,
      de.monticore.types.mcbasictypes._ast.ASTMCType type, List<ASTODAttribute> attributes,
      List<ASTODInnerLink> innerLinks) {
    super();
    setStereotype(stereotype);
    setName(name);
    setType(type);
    setAttributesList(attributes);
    setInnerLinksList(innerLinks);
    //super(stereotype, name, type, attributes, innerLinks);
  }
  
  private String sName;
  private String sType;
  private String sQualifiedType;
  private ArrayList<ASTODLink> linkList;
  
  @Override
  public @Nonnull ASTODObject deepClone() {
    return super.deepClone();
  }
  
  /**
   * Checks whether this object has the given stereotype.
   *
   * @param name stereotype name to check
   * @return {@code true} if the stereotype exists on this object
   */
  public boolean hasStereotype(@Nonnull String name) {
    if (isPresentStereotype()) {
      return getStereotype().contains(name.intern());
    }
    return false;
  }
  
  /**
   * Checks whether this object has a stereotype with the given value.
   *
   * @param name stereotype name to check
   * @param value expected stereotype value
   * @return {@code true} if the stereotype exists with the given value
   */
  public boolean hasStereotype(@Nonnull String name, @Nonnull String value) {
    if (isPresentStereotype()) {
      return getStereotype().contains(name.intern(), value.intern());
    }
    return false;
  }
  
  /**
   * Resolves a stereotype value by name.
   *
   * @param name stereotype name
   * @return the configured value, or an empty string if no value exists
   */
  public @Nonnull String getStereotypeValue(@Nonnull String name) {
    if (isPresentStereotype()) {
      String sv = getStereotype().getValue(name.intern());
      if (sv != null) {
        return sv;
      }
    }
    return "";
  }
  
  /**
   * Returns the printable object name.
   * If no explicit name is present, it is derived from the simple type name.
   *
   * @return printable name, never {@code null}
   */
  public @Nonnull String printName() {
    // lazy calculation from ast
    if (sName == null) {
      if (name.isPresent()) {
        sName = name.get().intern();
      }
      else {
        // use type-name instead
        sName = Names.getSimpleName(printType());
        // remove generic-part
        if (sName.contains("<")) {
          sName = sName.substring(0, sName.indexOf("<"));
        }
        sName = sName.toLowerCase().intern();
      }
    }
    return sName;
  }
  
  /**
   * Returns the printable type without redundant empty generic brackets ({@code <>}).
   *
   * @return printable type, or an empty string if no type is present
   */
  public @Nonnull String printType() {
    if (sType == null) {
      if (type.isPresent()) {
        // lazy calculation from ast
        sType = MCSimpleGenericTypesMill.prettyPrint(type.get(), false).intern();
        if (sType.endsWith("<>")) {
          sType = sType.substring(0, sType.length() - 2);
        }
      }
      else {
        sType = "";
      }
    }
    return sType;
  }
  
  /**
   * Returns the cached qualified type representation.
   *
   * @return qualified type if present; otherwise {@code null}
   */
  public @Nullable String printQualifiedType() {
    if (sQualifiedType == null) {
      if (type.isPresent()) {
        sQualifiedType = printType();
      }
    }
    return sQualifiedType;
  }
  
  /**
   * Returns all ODObjects, including inner links
   * and deeper hierarchies.
   *
   * @return a list of all nested {@link ASTODObject} instances; never {@code null}
   */
  public @Nonnull List<ASTODObject> getAllODObjects() {
    List<ASTODObject> allObjects = new ArrayList<>();
    
    for (ASTODInnerLink link : getInnerLinksList()) {
      ASTODObject innerObject = link.getODObject();
      allObjects.add(innerObject);
      allObjects.addAll(innerObject.getAllODObjects());
    }
    
    return allObjects;
  }
}
