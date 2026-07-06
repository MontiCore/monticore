<#-- @ftlvariable name="node" type="de.monticore.ast.ASTNode" -->
<#-- @ftlvariable name="altData" type="de.monticore.codegen.prettyprint.data.AltData" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
<#-- @ftlvariable name="helper" type="de.monticore.codegen.prettyprint.FormattingPrettyPrinterGenerationVisitor.FormattingHelper" -->
${tc.signature("node", "altData", "grammarName", "astPackage", "helper")}

<#list altData.getComponentList() as comp>
    <#if comp.getType().name() == "T">
        <#assign antlrName = helper.getRuleName(comp.getName())>
        <#if comp.isList()>
            node.get${comp.getNameToUse()?cap_first}List().forEach(n -> {
                getPrinter().emit(n, "${antlrName}", "${helper.next()}");
            });
        <#elseif comp.isOpt()>
            if (node.isPresent${comp.getNameToUse()?cap_first}()) {
                getPrinter().emit(node.get${comp.getNameToUse()?cap_first}(), "${antlrName}", "${helper.next()}");
            }
        <#else>
            getPrinter().emit("${comp.getName()}", "${antlrName}", "${helper.next()}");
        </#if>

    <#elseif comp.getType().name() == "NT" || comp.getType().name() == "NT_ITERATED">
        <#-- AUTOMATIC LIST/OPTIONAL HANDLING -->
        <#if comp.isList()>
            node.get${comp.getNameToUse()?cap_first}List().forEach(n -> n.accept(getTraverser()));
        <#elseif comp.isOpt()>
            if (node.isPresent${comp.getNameToUse()?cap_first}()) {
                node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
            }
        <#else>
            node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
        </#if>

    <#elseif comp.getType().name() == "BLOCK">
        ${includeArgs("FormattingBlock", node, comp.getBlockData(), grammarName, astPackage, helper)}

    <#elseif comp.getType().name() == "CG">
        <#list comp.getConstants() as const>
            <#if const?is_first>if<#else>else if</#if> (node.${comp.getNameToUse()}() == ${astPackage}.ASTConstants${grammarName?cap_first}.${const.getKey()?upper_case}) {
                getPrinter().emit("${const.getValue()}", "${helper.getRuleName(const.getValue())}", "${helper.next()}");
            }
        </#list>
    </#if>
</#list>