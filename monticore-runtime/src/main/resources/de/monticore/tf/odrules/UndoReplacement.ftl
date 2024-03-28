<#-- (c) https://github.com/MontiCore/monticore -->

public void undoReplacement() {

  if(!doReplacementExecuted) {
    Log.warn("undoReplacement was called before doReplacement was executed. This may lead to unspecified behaviour.");
  }

  for(Match m:allMatches){

    // assign values
<#list ast.getAssignmentsList() as assignment>
    ${assignment}
</#list>

    // undo update attributes
${tc.include("de.monticore.tf.odrules.undoreplacement.ChangeAttributeValues")}

    // execute do statements
${ast.getUndoStatement()}

    Reporting.flush(hostGraph.get(0));

    doReplacementExecuted = false;

    //undo only for the first match
    break;
  }
}
