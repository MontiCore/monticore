<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  set the printComments flag
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="superGrammars" type="java.util.List<de.monticore.grammar.grammar._symboltable.MCGrammarSymbol>" -->

${tc.signature("grammarName", "superGrammars")}


((${grammarName}PrettyPrinter)getTraverser().get${grammarName}Handler().get()).setPrintComments(printComments);

// SuperGrammars
<#list superGrammars as supergrammar>
    <#assign ppClass>${"${supergrammar.getPackageName()}."?remove_beginning(".")}${supergrammar.getName()?lower_case}._prettyprint.${supergrammar.getName()}PrettyPrinter</#assign>
    ((${ppClass})getTraverser().get${supergrammar.getName()}Handler().get()).setPrintComments(printComments);
</#list>

${glex.defineHookPoint(tc,"<Statement>*FPPSetComments:end", ast)}
