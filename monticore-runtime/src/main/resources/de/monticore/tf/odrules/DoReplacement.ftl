<#-- (c) https://github.com/MontiCore/monticore -->

public void doReplacement() {

  this.modelAccessor.notifyTransformationStart(getClass().getCanonicalName());

  for(Match m:allMatches){

    // assign values
<#list ast.getAssignmentsList() as assignment>
    ${assignment}
</#list>

    // create objects
${tc.include("de.monticore.tf.odrules.doreplacement.CreateObjects")}

    // update attributes
${tc.include("de.monticore.tf.odrules.doreplacement.ChangeAttributeValues")}

<#if ast.getDoStatement()?has_content>
    // execute do statements
    // TODO: How should we handle change notifications for DoStatements
${ast.getDoStatement()}
</#if>
    doReplacementExecuted = true;

    //do it only for the first match
    break;
  }

  this.modelAccessor.notifyTransformationEnd(getClass().getCanonicalName());
}
