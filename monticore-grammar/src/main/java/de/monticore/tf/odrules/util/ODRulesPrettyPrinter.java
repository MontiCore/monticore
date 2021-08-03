/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.util;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesFullPrettyPrinter;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._visitor.UMLStereotypeVisitor2;
import de.se_rwth.commons.Names;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules._visitor.ODRulesHandler;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;
import de.monticore.tf.odrules._visitor.ODRulesVisitor2;

import java.util.Iterator;

/**
 * Created by KH.
 */
public class ODRulesPrettyPrinter  implements ODRulesVisitor2,
        ODRulesHandler, UMLStereotypeVisitor2,
        MCBasicTypesVisitor2 {

  private ODRulesTraverser traverser;

  @Override
  public ODRulesTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(ODRulesTraverser traverser) {
    this.traverser = traverser;
  }

  public String getPrintedAST() {
    return printer.toString();
  }

  StringBuilder printer = new StringBuilder();


  @Override
  public void visit(ASTODRule node) {
    printer.append("pattern ");
  }

  @Override
  public void traverse(ASTODRule node) {
    node.getLhs().accept(getTraverser());
    if (node.isPresentRhs()){
      printer.append("replacement ");
      node.getRhs().accept(getTraverser());
    }
    if (node.isPresentConstraint()){
      printer.append("where { \n");
      printer.append("  " + Util.printExpression(node.getConstraint()) + "\n");
      printer.append("}");
    }
    if(!node.getAssignmentList().isEmpty()){
      printer.append("assign { \n");
      for (ASTAssignment a : node.getAssignmentList()){
        a.accept(getTraverser());
      }
      printer.append("}");

    }
    if (node.isPresentDoBlock()){
        printer.append("do  " + Util.print(node.getDoBlock()) +"\n");
    }
    if(!node.getFoldingSetList().isEmpty()){
      printer.append("folding {\n");
      for(ASTFoldingSet fs : node.getFoldingSetList()){
        fs.accept(getTraverser());
      }
      printer.append("}\n");
    }
  }

  @Override
  public void endVisit(ASTODDefinition node) {
    printer.append("}\n");
  }

  @Override
  public void traverse(ASTODDefinition node) {
    // print stereotype
    // print object diagram name and parameters
    printer.append("objectdiagram " + node.getName() +  "{\n");
    // print body
    for(ASTODObject o: node.getODObjectList()) {
      o.accept(getTraverser());
    }
    for(ASTODLink l : node.getODLinkList()) {
      l.accept(getTraverser());
    }
  }

  @Override
  public void traverse(ASTODObject node) {
    /*// print completeness
    if (node.completenessIsPresent()) {
      node.getCompleteness().get().accept(this);
    }
    // print object modifier
    if (node.getModifier().isPresent())
      node.getModifier().get().accept(this);*/
    // print stereotype
    if (node.isPresentStereotype()) {
      node.getStereotype().accept(getTraverser());
    }
    // print object name and type
    if (node.isPresentName()) {
      printer.append("  " + node.getName());
    }
    if (node.isPresentType()) {
      MCSimpleGenericTypesFullPrettyPrinter prettyPrinter = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
      node.getType().accept(prettyPrinter.getTraverser());
      printer.append(":")
          .append(prettyPrinter.getPrinter().getContent());
    }
    // print object body
    if (node.getAttributesList().isEmpty() && node.getInnerLinksList().isEmpty() ) {
      printer.append(";\n\n");
    }
    else {
      printer.append("{\n");
      // print attributes
      if (!node.getAttributesList().isEmpty()) {
        for (ASTODAttribute a : node.getAttributesList()) {
          a.accept(getTraverser());
        }
      }
      // print inner links
      if (!node.getInnerLinksList().isEmpty()) {
        for (ASTODInnerLink a : node.getInnerLinksList()) {
          a.accept(getTraverser());
        }
      }
      printer.append("  }\n\n");
    }
    /*if (!node.getAttributes().isEmpty()) {
      printer.append("{\n");
      for (ASTODAttribute a : node.getAttributes()) {
        a.accept(this);
      }
      printer.append("  }\n\n");
    } else {
      printer.append(";\n\n");
    }*/
  }



  @Override
  public void traverse(ASTODAttribute node) {
    // print modifier
    /*if(node.getModifier().isPresent())
    node.getModifier().get().accept(this);*/
    // print type
    if (node.isPresentMCType()) {
      MCSimpleGenericTypesFullPrettyPrinter prettyPrinter = new MCSimpleGenericTypesFullPrettyPrinter(new IndentPrinter());
      node.getMCType().accept(prettyPrinter.getTraverser());
      printer.append(prettyPrinter.getPrinter().getContent());
      printer.append(" ");
    }
    // print name
    printer.append(node.getName());
    // print value
    if (node.isPresentSingleValue()) {
      printer.append(" = ");
      printer.append(node.printValue());
    } else if (node.isPresentList()) {
      printer.append(" = ");
      printer.append(node.printList());
    }

  }

  @Override
  public void visit(ASTODAttribute node) {
    printer.append("    ");
  }

  @Override
  public void endVisit(ASTODAttribute node) {
    printer.append(";\n");
  }

  @Override
  public void traverse(ASTODLink node) {
    // print stereotype
    if (node.isPresentStereotype()) {
      node.getStereotype().accept(getTraverser());
    }
    // print type of the link
    /*if (node.isLink()) {
      printer.append("link ");
    }
    else if (node.isComposition()) {
      printer.append("composition ");
    }*/
    // OD Rules are always compositions
    printer.append("composition ");
    // print name
    /*if (node.isDerived()) {
      printer.append("/");
    }*/
    if (node.isPresentName()) {
      printer.append(node.getName() + " ");
    }
    // print left modifier
    /*if(node.getLeftModifier().isPresent())
    node.getLeftModifier().get().accept(this);*/
    // print objects referenced on the left side of the link
    printQualifiedNameList(node.getLeftReferenceNameList().iterator(), ", ");
    printer.append(" ");
    // print left qualifier
    /*if (node.leftQualifierIsPresent()) {
      printer.append("[");
      node.getLeftQualifier().get().accept(this);
      printer.append("] ");
    }*/
    // print left role
    if (node.isPresentLeftRole()) {
      printer.append("(");
      printer.append(node.getLeftRole());
      printer.append(") ");
    }
    // print arrow
    printer.append(" -- ");
    // print right role
    if (node.isPresentRightRole()) {
      printer.append(" (");
      printer.append(node.getRightRole());
      printer.append(")");
    }
    // print right qualifier
    /*if (node.rightQualifierIsPresent()) {
      printer.append("[");
      node.getRightQualifier().get().accept(this);
      printer.append("] ");
    }*/
    // print objects referenced on the right side of the link
    printer.append(" ");
    printQualifiedNameList(node.getRightReferenceNameList().iterator(), ", ");
    printer.append(" ");
    // print right modifier
    /*if(node.getRightModifier().isPresent())
    node.getRightModifier().get().accept(this);*/
  }

  @Override
  public void visit(ASTODLink node) {
    printer.append("    ");
  }

  @Override
  public void endVisit(ASTODLink node) {
    printer.append(";\n\n");
  }



  @Override
  public void visit(ASTAssignment node) {
    printer.append("  " + node.getLhs());
    printer.append(" = ");
    printer.append(Util.printExpression(node.getRhs()));
    printer.append(";\n");
  }



  @Override
  public void visit(ASTFoldingSet node) {
    printer.append("  (");
    Iterator<String> objectNames = node.getObjectNamesList().iterator();
    while (objectNames.hasNext()) {
      printer.append(objectNames.next());
      if (objectNames.hasNext()) {
        printer.append(", ");
      }
    }
    printer.append(")\n");
  }


  @Override
  public void visit(ASTMCType node) {
    printer.append(node.printType(new MCBasicTypesFullPrettyPrinter(new IndentPrinter())));
  }

  /**
   * Prints the start of stereotypes
   *
   * @param a stereotype
   */
  @Override
  public void visit(ASTStereotype a) {
    printer.append("<<");
    String comma = "";
    for(ASTStereoValue v : a.getValuesList()){
      printer.append(comma).append(v.getName());
      comma = ",";
    }
  }

  /**
   * Prints the end of stereotypes
   *
   * @param a stereotype
   */
  @Override
  public void endVisit(ASTStereotype a) {
    printer.append(">>");
  }


  /**
   * Prints a list of ASTQualifiedNames in an ownVisit method
   *
   * @param iter iterator for the list of ASTQualifiedNames
   * @param seperator string for seperating the ASTQualifiedNames
   */
  protected void printQualifiedNameList(Iterator<ASTMCQualifiedName> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      printer.append(sep);
      printer.append(Names.getQualifiedName(iter.next().getPartsList())); // visit
      // item
      sep = seperator;
    }
  }
}
