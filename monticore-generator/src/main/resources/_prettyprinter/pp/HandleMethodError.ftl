<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Handle method for pretty printing, where automatic printer generation fails
-->
${tc.signature("errorMessage", "astName", "node", "blockData")}

// TODO: Implement me
/*
 * ${blockData?replace("*/","* - /", "g")}
 *
 * ${grammarPrinter.prettyprint(node)?replace("*/","* - /", "g")}
*/
Log.error("0xA1067 ${errorMessage}");
throw new IllegalStateException("${errorMessage}");
