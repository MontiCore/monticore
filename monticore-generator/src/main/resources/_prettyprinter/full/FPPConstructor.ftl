<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Constructor for a FullPrettyPrinter
-->
${tc.signature("grammarName", "grammarPackage")}


${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:begin", ast)}
this.printer = printer;
this.traverser = ${grammarPackage}.${grammarName}Mill.traverser();

this.initializeTraverser(printComments);

${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:end", ast)}
