/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrulegeneration._ast;

import java.util.*;
import java.util.stream.Collectors;

public class ASTTransformationStructure extends ASTTransformationStructureTOP {
  
  protected HashMap<String, List<String>> foldingHash = new HashMap<>();
  
  protected ASTTransformationStructure() {
    super();
  }
  
  protected ASTTransformationStructure(String r__package, List<String> importss, String classname,
      ASTPattern pattern, ASTReplacement replacement, String constraintExpression,
      String doStatement, String undoStatement, List<String> assignmentss,
      List<ASTVariable> variables) {
    super();
    setPackage(r__package);
    setImportsList(importss);
    setClassname(classname);
    setPattern(pattern);
    setReplacement(replacement);
    setConstraintExpression(constraintExpression);
    setDoStatement(doStatement);
    setUndoStatement(undoStatement);
    setAssignmentsList(assignmentss);
    setVariableList(variables);
    //super(r__package, importss, classname, pattern, replacement, constraintExpression, doStatement, assignmentss, variables);
  }
  
  protected ASTTransformationStructure(HashMap<String, List<String>> foldingHash, String r__package,
      List<String> importss, String classname, ASTPattern pattern, ASTReplacement replacement,
      String constraintExpression, String doStatement, String undoStatement,
      List<String> assignmentss, List<ASTVariable> variables) {
    super();
    setPackage(r__package);
    setImportsList(importss);
    setClassname(classname);
    setPattern(pattern);
    setReplacement(replacement);
    setConstraintExpression(constraintExpression);
    setDoStatement(doStatement);
    setUndoStatement(undoStatement);
    setAssignmentsList(assignmentss);
    setVariableList(variables);
    //super(r__package, importss, classname, pattern, replacement, constraintExpression, doStatement, assignmentss, variables);
    setFoldingHash(foldingHash);
  }
  
  public HashMap<String, List<String>> getFoldingHash() {
    return this.foldingHash;
  }
  
  public void setFoldingHash(HashMap<String, List<String>> foldingHash) {
    this.foldingHash = foldingHash;
  }
  
  public List<String> getCompositionDependencyNames(ASTMatchingObject object) {
    return this.getPattern().getLinkConditionsList().stream().filter(
            l -> l.getLinktype().equals("composition") && l.getDependency().getContent()
                .equals(object.getObjectName())).map(ASTCondition::getObjectName)
        .collect(Collectors.toList());
  }
  
  public Map<String, String> getReplacementChangeMapping() {
    Map<String, String> replacementChangeMapping = new HashMap<>();
    this.getReplacement().getChangesList().stream().filter(ASTChange::isPresentValue)
        .filter(x -> x.getValue().charAt(0) == '_').forEach(change -> {
          String truncValueName = change.getValue().substring(1, change.getValue().lastIndexOf("_"));
          replacementChangeMapping.put(change.getObjectName(), truncValueName);
        });
    return replacementChangeMapping;
  }
  
  public Set<String> getAllInnerNonOptionalNames(List<ASTMatchingObject> allObjects,
      ASTMatchingObject matchObject) {
    Set<String> result = new HashSet<>();
    for (String innerLinkObjectName : matchObject.getInnerLinkObjectNamesList()) {
      ASTMatchingObject innerLinkObject = this.getPattern().getMatchingObjectsList().stream()
          .filter(f -> f.getObjectName().equals(innerLinkObjectName)).findFirst().get();
      if (!innerLinkObject.isOptObject()) {
        result.add(innerLinkObjectName);
      }
      result.addAll(getAllInnerNonOptionalNames(allObjects, innerLinkObject));
    }
    return result;
  }
  
  public String getJavaClassName() {
      if (this.isTop()) {
          return this.getClassname() + "TOP";
      }
    return this.getClassname();
  }
}
