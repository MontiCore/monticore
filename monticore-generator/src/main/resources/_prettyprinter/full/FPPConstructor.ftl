<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Constructor for a FullPrettyPrinter
-->
${tc.signature("grammarName", "grammarPackage", "superGrammars")}


${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:begin", ast)}
this.printer = printer;
this.traverser = ${grammarPackage}.${grammarName}Mill.traverser();

${grammarName}PrettyPrinter ${grammarName?uncap_first} = new ${grammarName}PrettyPrinter(printer, printComments);
traverser.set${grammarName}Handler(${grammarName?uncap_first});
traverser.add4${grammarName}(${grammarName?uncap_first});

// SuperGrammars
<#list superGrammars as supergrammar>
    <#assign ppClass>${"${supergrammar.getPackageName()}."?remove_beginning(".")}${supergrammar.getName()?lower_case}._prettyprint.${supergrammar.getName()}PrettyPrinter</#assign>
    ${ppClass} ${supergrammar.getName()?uncap_first} = new ${ppClass}(printer, printComments);
    traverser.set${supergrammar.getName()}Handler(${supergrammar.getName()?uncap_first});
    traverser.add4${supergrammar.getName()}(${supergrammar.getName()?uncap_first});
</#list>

${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:end", ast)}
