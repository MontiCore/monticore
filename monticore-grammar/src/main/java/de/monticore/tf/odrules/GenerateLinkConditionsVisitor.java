/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.ASTDependency;
import de.monticore.tf.odrulegeneration._ast.ASTLinkCondition;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._visitor.ODRulesVisitor2;
import de.monticore.tf.odrules.util.Util;

import java.util.ArrayList;
import java.util.List;

public class GenerateLinkConditionsVisitor implements
        ODRulesVisitor2 {

  private List<ASTLinkCondition> links = new ArrayList<>();
  private ASTODDefinition def;


  @Override
  public void visit(ASTODDefinition node){
    def = node;
  }

  public List<ASTLinkCondition> getLinkConditions() {
    return links;
  }

  @Override
  public void visit(ASTODLink node) {
    String leftObjectName = Names.constructQualifiedName(node.getLeftReferenceName(0).getPartsList());
    String rightObjectName = Names.constructQualifiedName(node.getRightReferenceName(0).getPartsList());
    ASTLinkCondition linkCondition = ODRuleGenerationMill.linkConditionBuilder().uncheckedBuild();
    linkCondition.setObjectName(leftObjectName);
    ASTLinkCondition secondLinkCondition = ODRuleGenerationMill.linkConditionBuilder().uncheckedBuild();
    secondLinkCondition.setObjectName(rightObjectName);
    // it's always a composition in the new grammar
    //if (node.isComposition()) {
      linkCondition.setLinktype("composition");
      secondLinkCondition.setLinktype("composition");
      // if it is a composition the left side is the parent of the right
      if (node.isPresentRightRole()) {

        if (node.isAttributeIterated()) {
          secondLinkCondition.setConditionString("return new ArrayList<ASTNode>(" + leftObjectName + "_cand.get" + Util.makeSingular(node.getRightRole()) + "List());");
        } else if(node.isAttributeOptional()) {
          secondLinkCondition.setConditionString("ArrayList<ASTNode> list = new ArrayList<ASTNode>();\n" +
              "if (" + leftObjectName + "_cand.isPresent" + StringTransformations.capitalize(node.getRightRole()) + "()) {\n" +
              " list.add(" + leftObjectName + "_cand.get" + StringTransformations.capitalize(node.getRightRole()) + "());\n" +
              "}\n" +
              " return list;");

        } else {
          secondLinkCondition.setConditionString("ArrayList<ASTNode> list = new ArrayList<ASTNode>();\n" +
                  "if (" + leftObjectName + "_cand.get" + StringTransformations.capitalize(node.getRightRole()) + "() != null) {\n" +
                  " list.add(" + leftObjectName + "_cand.get" + StringTransformations.capitalize(node.getRightRole()) + "());\n" +
                  "}\n" +
                  " return list;");
        }
        ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
        dependency.setContent(leftObjectName);
        secondLinkCondition.setDependency(dependency);

      }
    /*} else {
      if (!node.getName().get().isEmpty()) {
        linkCondition.setLinktype("link");
        secondLinkCondition.setLinktype("link");
        linkCondition.setConditionString(StringTransformations.uncapitalize(node.getName().get()) + ".getLeftFromRight(" + rightObjectName + "_cand" + ")");
        linkCondition.setDependency(ODRuleGenerationNodeFactory.createASTDependency(rightObjectName));
        secondLinkCondition.setConditionString(StringTransformations.uncapitalize(node.getName().get()) + ".getRightFromLeft(" + leftObjectName + "_cand" + ")");
        secondLinkCondition.setDependency(ODRuleGenerationNodeFactory.createASTDependency(leftObjectName));
      }
    }*/
    if (secondLinkCondition.getConditionString() != null && !secondLinkCondition.getConditionString().isEmpty()) {
      links.add(secondLinkCondition);
    }

    if (linkCondition.getConditionString() != null && !linkCondition.getConditionString().isEmpty()) {
      links.add(linkCondition);
    }
  }

}
