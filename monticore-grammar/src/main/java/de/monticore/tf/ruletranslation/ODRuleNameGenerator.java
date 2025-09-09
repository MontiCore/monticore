/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.ruletranslation;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.StringTransformations;
import de.monticore.tf.ast.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create names for the objects in generated OD rules.
 *
 */
public class ODRuleNameGenerator {

  private Map<ITFObject, String> generatedNames = new HashMap<>();
  private Map<Class<? extends ASTNode>, Integer> numberOfElements = new HashMap<>();

  public String getNameForElement(ITFObject element, Map<ASTNode,ASTNode> parents) {
    ASTNode parent = parents.get(element);
    if(parent instanceof IList || parent instanceof IOptional){
      return getNameForElement(element);
    }
    // return the name chosen by the user if available
    if (parent instanceof ITFObject && !(parent instanceof IPattern)
        && ((ITFObject) parents.get(element)).isPresentSchemaVarName()) {
      return ((ITFObject) parents.get(element)).getSchemaVarName();
    }

    return getNameForElement(element);
  }

  public String getNameForElement(ITFObject element, ITFObject parent) {

    // return the name chosen by the user if available
    if (parent.isPresentSchemaVarName()) {
      return  parent.getSchemaVarName();
    }

    return getNameForElement(element);
  }

  public String getNameForElement(ITFObject element) {
    // use the schema variable name, if present
    if (element.isPresentSchemaVarName() && !element.getSchemaVarName().equals("$_")) {
      // we cannot use schema variable names for optional objects, as they do not represent
      // any real model object. they just group other objects together. the schema variable name
      // will be used as a name for the actual object within the optional object
      if(! (element instanceof IOptional)) {
        return element.getSchemaVarName();
      }
    }
    // for negation
    // we can safely use the LHS because this is no replacement
    if (element instanceof INegation) {
      element = (ITFObject) element.getTFElement();
    }
    if (!generatedNames.containsKey(element)) {
      int nextNumber;
      Class<? extends ASTNode> elementType;
      // optional nodes always have the type de.monticore.tf.ast.IOptional
      // regardless of the element they contain
      if (element instanceof IOptional) {
        elementType = IOptional.class;
      }
      else if (element instanceof IList) {
        elementType = IList.class;
      }
      else {
        elementType = element._getTFElementType();
      }
      if (numberOfElements.containsKey(elementType)) {
        nextNumber = numberOfElements.get(elementType) + 1;
        numberOfElements.put(elementType, nextNumber);
      } else {
        nextNumber = 1;
        numberOfElements.put(elementType, 1);
      }
      // remove AST prefix, convert first letter to lower case and append number
      // for complete object name
      String prefix;
      if (element instanceof IOptional || element instanceof IList) {
        // remove the 'I' from 'IOptional' or 'IList'
        prefix = StringTransformations.uncapitalize(elementType.getSimpleName().substring(1));
      }
      else {
        prefix = StringTransformations.uncapitalize(elementType.getSimpleName().substring(3));
      }
      String name = StringTransformations.uncapitalize(prefix + "_" + Integer.toString(nextNumber));
      generatedNames.put(element, name);
    }
    return generatedNames.get(element);
  }

}
