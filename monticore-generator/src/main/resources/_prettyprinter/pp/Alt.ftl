<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "altData", "grammarName", "astPackage")}
<#--
  Build the pretty print behaviour for an Alt
  Differentiate between the various types of RuleComponent
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="altData" type="de.monticore.codegen.prettyprint.data.AltData" -->
<#-- @ftlvariable name="grammarName" type="String" -->
<#-- @ftlvariable name="astPackage" type="String" -->

<@alt glex altData grammarName astPackage/>
<#macro alt glex altData grammarName astPackage>

<#list altData.getComponentList() as comp>
    <#if comp.getType().name() == "T">  <#-- Terminal -->
        <#if comp.isList()> <#-- Terminal with usagename: */+ -->
            node.get${comp.getNameToUse()?cap_first}List().forEach(n->{
            getPrinter().print(n ${comp.isHasNoSpace()?then("", " + \" \"")});
            });
        <#elseif comp.isOpt()>  <#-- Terminal with usagename: ? -->
            if (node.isPresent${comp.getNameToUse()?cap_first}()) {
            getPrinter().print(node.get${comp.getNameToUse()?cap_first}() ${comp.isHasNoSpace()?then("", " + \" \"")});
            }
        <#else >
            ${includeArgs("Terminal", comp.getName(), comp.isHasNoSpace(), comp.isTerminalNoSpace(comp.getName()))}
        </#if>
    <#elseif comp.getType().name() == "NT">  <#-- NonTerminal -->
        <#if comp.isList()>
            <#if comp.isStringType() >
                node.get${comp.getNameToUse()?cap_first}List().forEach(n->{
                getPrinter().print(${includeArgs("Tokens", comp, "n")} ${comp.isHasNoSpace()?then("", " + \" \"")});
                });
            <#else>
                node.get${comp.getNameToUse()?cap_first}List().forEach(n->n.accept(getTraverser()));
            </#if>
        <#elseif comp.isOpt()>
            if (node.isPresent${comp.getNameToUse()?cap_first}()) {
            <#if comp.isStringType() >
                getPrinter().print(${includeArgs("Tokens", comp, "node.get${comp.getNameToUse()?cap_first}()")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
            <#else>
                node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
            </#if>
            }
        <#else >
            <#if comp.isStringType() >
                getPrinter().print(${includeArgs("Tokens", comp, "node.get${comp.getNameToUse()?cap_first}()")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
            <#else>
                node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
            </#if>
        </#if>
    <#elseif comp.getType().name() == "NT_AST_DEF">  <#-- NonTerminal with ASTRule reducing from List to Def -->
        <#if comp.isStringType() >
            getPrinter().print(${includeArgs("Tokens", comp, "node.get${comp.getNameToUse()?cap_first}(0)")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
        <#else>
            node.get${comp.getNameToUse()?cap_first}(0).accept(getTraverser());
        </#if>
    <#elseif comp.getType().name() == "NT_ITERATED">  <#-- NonTerminal with Iterator usage* -->
        <#if comp.isList()>   <#-- e.g. Name Name* -->
            while(iter_${comp.getNameToUse()?uncap_first}.hasNext()) {
            <#if comp.isStringType() >
                getPrinter().print(${includeArgs("Tokens", comp, "iter_${comp.getNameToUse()?uncap_first}.next()")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
            <#else>
                iter_${comp.getNameToUse()?uncap_first}.next().accept(getTraverser());
            </#if>
            }
        <#elseif comp.isOpt()>
            if(iter_${comp.getNameToUse()?uncap_first}.hasNext()) {
            <#if comp.isStringType() >
                getPrinter().print(${includeArgs("Tokens", comp, "iter_${comp.getNameToUse()?uncap_first}.next()")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
            <#else>
                iter_${comp.getNameToUse()?uncap_first}.next().accept(getTraverser());
            </#if>
            }
        <#else >
            <#if comp.isStringType() >
                getPrinter().print(${includeArgs("Tokens", comp, "iter_${comp.getNameToUse()?uncap_first}.next()")}  ${comp.isHasNoSpace()?then("", " + \" \"")});
            <#else>
                iter_${comp.getNameToUse()?uncap_first}.next().accept(getTraverser());
            </#if>
        </#if>
    <#elseif comp.getType().name() == "BLOCK">  <#-- Block -->
        ${includeArgs("Block", ast, comp.getBlockData(), grammarName, astPackage)}
    <#elseif comp.getType().name() == "CG">  <#-- Constant Group -->
        <#if comp.getConstants()?size == 1>
        <#-- No if-guard required, as it is already present in the outer Alt-condition-->
            ${includeArgs("Terminal", comp.getConstants()?first.getValue(), comp.isHasNoSpace(), comp.isTerminalNoSpace(comp.getConstants()?first.getValue()))}
        <#else>
            <#list comp.getConstants() as const>
                if (node.${comp.getNameToUse()}() == ${astPackage}.ASTConstants${grammarName?cap_first}.${const.getKey()?upper_case}) {
                ${includeArgs("Terminal", const.getValue(), comp.isHasNoSpace(), comp.isTerminalNoSpace(const.getValue()))}
                }
                <#sep> else
            </#list>
        </#if>

    <#else >
        ${error("Unknown RuleComponent Type " + comp.getType())}
    </#if>
</#list>
</#macro>