<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  traverse initialization for a FullPrettyPrinter
  This method is overrideable
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="grammarSymbol" type="de.monticore.grammar.grammar._symboltable.MCGrammarSymbol" -->
<#-- @ftlvariable name="superGrammars" type="java.util.List<String>" -->
<#-- @ftlvariable name="replacedKeywordGrammars" type="java.util.Map<String, java.util.Map<de.monticore.grammar.grammar._symboltable.ProdSymbol, java.util.Map<String, java.util.Collection<String>>>>" -->
${tc.signature("grammarSymbol", "superGrammars", "replacedKeywordGrammars")}

<#assign grammarName=grammarSymbol.getName()>

${grammarName}PrettyPrinter ${grammarName?uncap_first} = new ${grammarName}PrettyPrinter(getPrinter(), printComments);
<#if replacedKeywordGrammars[grammarSymbol.getFullName()]??>
    // Note: Keywords were replaced in this grammar, but this has already been respected in the various handle methods
</#if>
getTraverser().set${grammarName}Handler(${grammarName?uncap_first});
getTraverser().add4${grammarName}(${grammarName?uncap_first});

// SuperGrammars
<#list superGrammars as supergrammar>
    <#assign ppClass>${"${supergrammar.getPackageName()}."?remove_beginning(".")}${supergrammar.getName()?lower_case}._prettyprint.${supergrammar.getName()}PrettyPrinter</#assign>
    ${ppClass} ${supergrammar.getName()?uncap_first} = new ${ppClass}(getPrinter(), printComments)
    <#if replacedKeywordGrammars[supergrammar.getFullName()]??>
        ${includeArgs("FPPReplaceKeywords", supergrammar, replacedKeywordGrammars[supergrammar.getFullName()])}
    <#else>
        ;
    </#if>
    getTraverser().set${supergrammar.getName()}Handler(${supergrammar.getName()?uncap_first});
    getTraverser().add4${supergrammar.getName()}(${supergrammar.getName()?uncap_first});
</#list>

${glex.defineHookPoint(tc,"<Statement>*FPPTraverserInit:end", ast)}
