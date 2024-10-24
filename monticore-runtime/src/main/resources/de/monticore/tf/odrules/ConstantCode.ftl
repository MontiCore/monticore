<#-- (c) https://github.com/MontiCore/monticore -->
<#assign deleteObjects = hierarchyHelper.getMandatoryDeleteObjects(ast.getReplacement().getDeleteObjectsList())>
<#assign changeObjects = hierarchyHelper.getMandatoryChangeObjects(ast.getReplacement().getChangesList())>
<#assign matchObjects = hierarchyHelper.getMandatoryMatchObjects(ast.getPattern().getMatchingObjectsList())>
<#assign optionalMatchObjects = hierarchyHelper.getOptionalMatchObjects(ast.getPattern().getLHSObjectsList())>

  private List<ASTNode> hostGraph;
  private GlobalExtensionManagement glex = new GlobalExtensionManagement();
  private List<Match> allMatches;
  private boolean doReplacementExecuted = false;
  <#-- for each object creates a _candidates, _candidates_temp nodelist and an _cand object-->

  // Matches
  <#list matchObjects as matchObject>
    ${tc.include("de.monticore.tf.odrules.constantcode.HandleMatchObject", matchObject)}
  </#list>
  <#list ast.getVariableList() as variable>
  // variables
  protected boolean ${variable.getName()}_is_fix = false;
  private ${variable.getType()} ${variable.getName()};
  </#list>
  private ModelTraversal <?> t = ModelTraversalFactory.getInstance().create((java.util.function.Supplier)${grammarName}Mill::inheritanceTraverser);
  <#list ast.getPattern().getAssocList() as association>
  private mc.ast.MCAssociation ${association.getName()};
  </#list>
  private Stack<String> searchPlan;
  // searchplans for optional structures
  <#list ast.getPattern().getLHSObjectsList() as lhsObject>
    <#if lhsObject.isOptObject() || lhsObject.getType()?ends_with("IOptional") || lhsObject.isListObject()>
  private Stack<String> searchPlan_${lhsObject.getObjectName()};
    </#if>
  </#list>

<#--  // Changes
  <#list changeObjects as changeObject>
    ${tc.include("de.monticore.tf.odrules.constantcode.HandleChangeObject", changeObject)}
  </#list>-->

<#--  // Deletes
  <#list deleteObjects as deleteObject>
    ${tc.include("de.monticore.tf.odrules.constantcode.HandleDeleteObject", deleteObject)}
  </#list>-->

  public void reportChange(ASTNode astNode, String attr, String from, String to) {
    reportTransformationObjectChange("${ast.getClassname()}", astNode, attr);
    reportTransformationOldValue("${ast.getClassname()}", from);
    reportTransformationNewValue("${ast.getClassname()}", to);
  }

  public void reportDeletion(ASTNode astNode){
    reportTransformationObjectDeletion("${ast.getClassname()}", astNode);
  }

  public void reportCreation(ASTNode astNode){
    reportTransformationObjectCreation("${ast.getClassname()}", astNode);
  }

  public void reportMatch(ASTNode astNode){
    reportTransformationObjectMatch("${ast.getClassname()}", astNode);
  }

  public List<Match> getMatches(){
    return allMatches;
  }

  public void doAll(){
    doPatternMatching();
    doReplacement();
  }
