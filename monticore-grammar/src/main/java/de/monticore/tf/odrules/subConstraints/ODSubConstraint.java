/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules.subConstraints;

import de.monticore.tf.odrulegeneration._ast.ASTMatchingObject;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Objects of type ODSubConstraint represent modules of constraints that can be checked independently.
 */
public class ODSubConstraint {

    public Set<ASTMatchingObject> dependVars;
    public String constrExpr;

    //Constraints with an Optional in an OR-Expression require special handling in templates
    public boolean optionalInOrPresent = false;

    public boolean isOptionalInOrPresent() {
        return optionalInOrPresent;
    }

    public ODSubConstraint(Set<ASTMatchingObject> dependVars, String constrExpr){
        this.dependVars = dependVars;
        this.constrExpr = constrExpr;
    }

    public Set<ASTMatchingObject> getDependVars(){
        return dependVars;
    }

    public boolean isDependendOn(ASTMatchingObject object){
      return dependVars.stream().anyMatch(astMatchingObject -> astMatchingObject.getObjectName()
          .equals(object.getObjectName()));
    }

    public String getConstrExpr(){
        return constrExpr;
    }

    public String getConstrExprFor(String dependencyName){
        return constrExpr.replace(dependencyName+"_cand","cand");
    }

    public ODSubConstraint() {
        dependVars = new LinkedHashSet<>();
    }
}
