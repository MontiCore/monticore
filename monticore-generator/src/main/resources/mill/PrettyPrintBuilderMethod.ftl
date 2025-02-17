<#-- (c) https://github.com/MontiCore/monticore -->
/*
* Static getter for the pretty printer that delegates to the non static implementation.
* Only two pretty printer objects are created and reused.
* @param printComments Whether comments should be printed
* @return the pretty printer instance
*/
${tc.signature("astNodeName")}
  if (mill${astNodeName}.get() == null) {
    mill${astNodeName}.set(getMill());
    mill${astNodeName}.get().fullPrettyPrinter = null; // reset cached
  }
  return mill${astNodeName}.get()._prettyPrint(node, printComments);
