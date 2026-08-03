<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
${tc.signature("blockData", "grammarName", "astPackage")}

<#-- OPTIONAL: ( ... )? -->
<#if blockData.getIteration() == 1>
  <#list blockData.getAltDataList() as alt>
    <#if alt_index == 0>if<#else>else if</#if> (${alt.getExpressionConj()}) {
      ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
    }
  </#list>

<#-- STAR: ( ... )* -->
<#elseif blockData.getIteration() == 2>
  <#list blockData.getAltDataList() as alt>
    while (${alt.getExpressionConj()}) {
      ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
    }
  </#list>

<#-- PLUS: ( ... )+ -->
<#elseif blockData.getIteration() == 3>
  <#list blockData.getAltDataList() as alt>
    do {
      ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
    } while (${alt.getExpressionConj()});
  </#list>

<#-- DEFAULT: ( ... ) -->
<#else>
  <#list blockData.getAltDataList() as alt>
    <#if alt_index == 0>if<#else>else if</#if> (${alt.getExpressionConj()}) {
      ${includeArgs("_prettyprinter.pp.FormattingAlt", alt, grammarName, astPackage)}
    }
  </#list>
</#if>