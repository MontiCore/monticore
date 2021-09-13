<#-- (c) https://github.com/MontiCore/monticore -->

public void doReplacement() {

  for(Match m:allMatches){

    // assign values
<#list ast.getAssignmentsList() as assignment>
    ${assignment}
</#list>

    // create objects
${tc.include("de.monticore.tf.odrules.doreplacement.CreateObjects")}

    // update attributes
${tc.include("de.monticore.tf.odrules.doreplacement.ChangeAttributeValues")}

    // execute do statements
${ast.getDoStatement()}

    Reporting.flush(hostGraph.get(0));

    doReplacementExecuted = true;

    //do it only for the first match
    break;
  }
}
