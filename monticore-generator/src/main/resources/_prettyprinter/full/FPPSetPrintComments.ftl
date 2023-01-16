<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  set the printComments flag
-->
${tc.signature("grammarName", "superGrammars")}


((${grammarName}PrettyPrinter)getTraverser().get${grammarName}Handler().get()).setPrintComments(printComments);

// SuperGrammars
<#list superGrammars as supergrammar>
    <#assign ppClass>${"${supergrammar.getPackageName()}."?remove_beginning(".")}${supergrammar.getName()?lower_case}._prettyprint.${supergrammar.getName()}PrettyPrinter</#assign>
    ((${ppClass})getTraverser().get${supergrammar.getName()}Handler().get()).setPrintComments(printComments);
</#list>

${glex.defineHookPoint(tc,"<Statement>*FPPSetComments:end", ast)}
