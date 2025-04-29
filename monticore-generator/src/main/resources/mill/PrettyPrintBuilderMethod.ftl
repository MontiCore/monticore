<#-- (c) https://github.com/MontiCore/monticore -->
/*
* Static getter for the pretty printer that delegates to the non-static implementation.
* Only one pretty printer object is created and reused.
* This method is not protected against side effects.
* @param printComments Whether comments should be printed
* @return the pretty printer instance
*/
${tc.signature()}
  return getMill()._prettyPrint(node, printComments);
