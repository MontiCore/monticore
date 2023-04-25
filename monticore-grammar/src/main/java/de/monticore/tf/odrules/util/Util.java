/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;


import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;
import de.monticore.tf.odrules.HierarchyHelper;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules._symboltable.ODDefinitionSymbol;
import de.monticore.tf.odrules._symboltable.ODObjectSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by
 *
 */
public class Util {
  private static final int PREFIX_LENGHT = 3;

  protected Util(){
    // no instances
  }

  /**
   * Resolves an ODObject by it's name.
   * @param node the parent ODDefinition
   * @param attrName the object's name
   * @return the ASTODObject
   */
  public static ASTODObject getODObject(ASTODDefinition node, String attrName) {
    // first, try to resolve the object directly with the parent ODDefinition
    ODDefinitionSymbol symb = node.getSymbol();
    Optional<ODObjectSymbol> resultSymbol = getFirstSymbol(symb.getSpannedScope().resolveODObjectDownMany(attrName));

    if(resultSymbol.isPresent() && resultSymbol.get().isPresentAstNode()) {
      return (ASTODObject) resultSymbol.get().getAstNode();
    }
    else {
      // the object is not a direct child of the ODDefinition.
      // it could still be within an inner link of some other ODObject, so check those, too
      for (ASTODObject parent : node.getODObjectList()) {
        ASTODObject obj = Util.getODObject(parent, attrName);
        if (obj != null) {
          return obj;
        }
      }

      // if we reach this then the ODObject really is not in the object diagram
      return null;
    }
  }

  /**
   * Resolves an ODObject by it's name.
   * @param node the parent ODObject
   * @param attrName the object's name
   * @return the ASTODObject
   */
  public static ASTODObject getODObject(ASTODObject node, String attrName) {
    // first, try to resolve the object directly with the parent object
    ODObjectSymbol symb = node.getSymbol();
    Optional<ODObjectSymbol> resultSymbol = getFirstSymbol(symb.getSpannedScope().resolveODObjectDownMany(attrName));

    if (resultSymbol.isPresent() && resultSymbol.get().isPresentAstNode()) {
      return resultSymbol.get().getAstNode();
    }
    else {
      // the object is not a direct child of the parent object.
      // it could still be within an inner link of some other inner link, so check those too
      for (ASTODInnerLink link : node.getInnerLinksList()) {
        ASTODObject obj = Util.getODObject(link.getODObject(), attrName);
        if (obj != null) {
          return obj;
        }
      }

      // if we reach this then the ODObject really is not a child of the parent object
      return null;
    }
  }

  /**
   * Returns the first symbol of the given collection.
   * @param symbols the symbol collection
   * @return optional of the first symbol or empty optional if collection is null or empty
   */
  private static Optional<ODObjectSymbol> getFirstSymbol(Collection<ODObjectSymbol> symbols) {
    if (symbols == null || symbols.size() == 0) {
      return Optional.empty();
    }
    else {
      for (ODObjectSymbol symb : symbols) {
        return Optional.of(symb);
      }
      return Optional.empty();
    }
  }

  /**
   * Returns all ODObjects in the given node. This includes objects in inner links
   * of other objects.
   *
   * @param node the node
   * @return all ASTODObjects
   */
  public static List<ASTODObject> getAllODObjects(ASTODDefinition node) {
    List<ASTODObject> result = new ArrayList<>();

    for (ASTODObject obj : node.getODObjectList()) {
      result.add(obj);
      result.addAll(obj.getAllODObjects());
    }

    return result;
  }

  public static String printImportDeclaration(ASTMCImportStatement importDeclaration){
    return MCBasicTypesMill.prettyPrint(importDeclaration, true);
  }

  public static String printExpression(ASTExpression astExpression) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    return p.prettyprint(astExpression);
  }

  public static String printExpression(ASTExpression astExpression, List<ASTMatchingObject> objects, HierarchyHelper helper) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter(),objects, helper);
    return p.prettyprint(astExpression);
  }

  public static String printListValue(ASTArrayInit initializer) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    return p.prettyprint(initializer);
  }

  public static String printFirstListValue(ASTArrayInit astArrayInit) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    String value = p.prettyprint(astArrayInit.getVariableInit(0));
    if(value.startsWith("\"$") && value.endsWith("\"")){
      value = value.substring(1, value.length()-1);
    }
    return value;
  }

  public static String print(ASTMCJavaBlock doblock) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    return p.prettyprint(doblock);
  }

  public static String print(ASTMCJavaBlock doblock, List<ASTMatchingObject> objects, HierarchyHelper helper) {
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(new IndentPrinter(),objects, helper);
    return p.prettyprint(doblock);
  }

  public static String getRightRoleType(ASTODLink node, ASTODDefinition rhs) {
    String attrName = Names.constructQualifiedName(node.getRightReferenceName(0).getPartsList());
    ASTODObject obj = getODObject(rhs, attrName);
    return obj != null ? obj.printType() : null;
  }

  public static String getLeftRoleType(ASTODLink node, ASTODDefinition lhs) {
    String attrName = Names.constructQualifiedName(node.getLeftReferenceName(0).getPartsList());
    ASTODObject obj = getODObject(lhs, attrName);
    return obj != null ? obj.printType() : null;
  }

  public static boolean isBuiltInType(ASTODAttribute attribute) {
    String typename = attribute.printType();
    if("boolean".equals(typename)
            || "int".equals(typename)
            || "float".equals(typename)
            || "double".equals(typename)
            || "char".equals(typename)
            || "long".equals(typename)
            || "byte".equals(typename)
            || "short".equals(typename)
            || "String".equals(typename)){
      return true;

    }
    return false;
  }

  @Deprecated
  public static boolean isIterated(ASTODLink node, ASTODDefinition def, MCGrammarSymbol grammarSymbol) {
    ASTODObject sourceObject = getODObject(def,
        Names.getSimpleName(node.getLeftReferenceName(0).getPartsList()));
    return isIterated(node, sourceObject, grammarSymbol);
  }
  @Deprecated
  public static boolean isIterated(ASTODLink node, ASTODObject leftObject, MCGrammarSymbol grammarSymbol) {
    if(grammarSymbol ==null){
      Log.warn("Grammar not specified. Cannot determine whether model elements are allowed multiple times within the grammar.");
      return false;
    } else if (leftObject.hasStereotype("list")){
      return true;
    } else {
      Optional<RuleComponentSymbol> attribute = getMCAttributeSymbol(node.getRightRole(), leftObject,
          grammarSymbol);

      return attribute.isPresent() && attribute.get().isIsList();
    }
  }
  @Deprecated
  public static boolean isOptional(ASTODLink node, ASTODDefinition def, MCGrammarSymbol grammarSymbol) {
    ASTODObject sourceObject = getODObject(def,
        Names.getSimpleName(node.getLeftReferenceName(0).getPartsList()));
    return isOptional(node, sourceObject, grammarSymbol);
  }

  @Deprecated
  public static boolean isOptional(ASTODLink node, ASTODObject astodObject,
      MCGrammarSymbol grammarSymbol) {
    if(grammarSymbol == null){
      Log.warn("Grammar not specified. Cannot determine whether model elements are optional within the grammar.");
      return false;
    } else if(astodObject.hasStereotype("list")){
      return false;
    }
    Optional<RuleComponentSymbol> attribute = getMCAttributeSymbol(node.getRightRole(),astodObject, grammarSymbol);

    return attribute.isPresent() && attribute.get().isIsOptional() && !attribute.get().isIsList();
  }
  @Deprecated
  public static Optional<RuleComponentSymbol> getMCAttributeSymbol (String attributeName, ASTODObject leftObject, MCGrammarSymbol grammarSymbol){
    ASTMCType sourceType = leftObject.getType();
    String javaType = Names.getSimpleName(Util.printType(sourceType));
    // remove AST prefix
    String grammarType = javaType.substring(PREFIX_LENGHT);


    ProdSymbol prod = grammarSymbol.getProdWithInherited(grammarType).get();
    Optional<RuleComponentSymbol> attribute = getProdComponent(prod, attributeName);

    // This may be required depending on whether there is a usage name for this
    // attribute
    if (!attribute.isPresent()) {
      attribute = getProdComponent(prod, StringTransformations.capitalize(attributeName));
    }
    if (!attribute.isPresent()) {
      attribute = getProdComponent(prod, attributeName.concat("s"));
    }
    if (!attribute.isPresent() && attributeName.endsWith("s")) {
      attributeName = attributeName.substring(0, attributeName.length() - 1);
      attribute = getProdComponent(prod, attributeName);
    }
    if (!attribute.isPresent() && attributeName.endsWith("e")) {
      attributeName = attributeName.substring(0, attributeName.length() - 1);
      attribute = getProdComponent(prod, attributeName);
    }
    return  attribute;
  }
  @Deprecated
  private static Optional<RuleComponentSymbol> getProdComponent(ProdSymbol prod, String attributeName) {
    return prod.getProdComponents().stream().filter(c -> c.getName().equals(attributeName)).findFirst();
  }
  @Deprecated
  public static boolean isIterated(ASTODObject object, ASTODAttribute attribute, MCGrammarSymbol grammarSymbol) {
    if(grammarSymbol == null){
      Log.warn("Grammar not specified. Cannot determine whether model elements are optional within the grammar.");
      return false;
    }
    Optional<RuleComponentSymbol> attributeSymbol = getMCAttributeSymbol(attribute.getName(),object, grammarSymbol);

    if(attribute.printType().equals("boolean")) {
        return false;
    }

    return attributeSymbol.isPresent() && attributeSymbol.get().isIsList();
  }
  @Deprecated
  public static boolean isOptional(ASTODObject object, ASTODAttribute attribute, MCGrammarSymbol grammarSymbol) {
    if("boolean".equals(attribute.printType())){
      return false;
    }
    if(grammarSymbol == null){
      Log.warn("Grammar not specified. Cannot determine whether model elements are optional within the grammar.");
      return false;
    }
    Optional<RuleComponentSymbol> attributeSymbol = getMCAttributeSymbol(attribute.getName(),object, grammarSymbol);
    return attributeSymbol.isPresent() && attributeSymbol.get().isIsOptional() && !attributeSymbol.get().isIsList();

  }
  @Deprecated
  public static boolean isOptional(Optional<ASTODObject> target, String attrname,
      MCGrammarSymbol grammarSymbol) {
    if(target.isPresent()){
      Optional<RuleComponentSymbol> attributeSymbol = getMCAttributeSymbol(attrname,target.get(), grammarSymbol);
      return attributeSymbol.isPresent() && attributeSymbol.get().isIsOptional()&& !attributeSymbol.get().isIsList();
    }
    return false;
  }

  public static String makeSingular(String name) {
    if(name.endsWith("ses"))
      name = name.substring(0, name.length() - 2);
    else if(name.endsWith("ss") && !name.endsWith("class"))
      name = name.substring(0, name.length() - 2);
    else if(name.endsWith("s") && !name.endsWith("class"))
      name = name.substring(0, name.length() - 1);
    return StringTransformations.capitalize(name);
  }

  public static String printType(ASTMCType type) {
    return MCSimpleGenericTypesMill.prettyPrint(type, false).replace("<>", "");
  }
}
