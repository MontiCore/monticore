<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="altData" type="de.monticore.codegen.prettyprint.data.FormattingAltData" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
${tc.signature("altData", "grammarName", "astPackage")}

<#list altData.getComponentList() as comp>
  <#if comp.getType().name() == "T">
    <#-- Terminals -->
    <#if comp.isList()>
      for (String ${comp.getNameToUse()?uncap_first} : node.get${comp.getNameToUse()?cap_first}List()) {
        getPrinter().emit(${comp.getNameToUse()?uncap_first}, "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
      }
    <#elseif comp.isOpt()>
      if (node.isPresent${comp.getNameToUse()?cap_first}()) {
        getPrinter().emit(node.get${comp.getNameToUse()?cap_first}(), "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
      }
    <#else>
      getPrinter().emit("${comp.getName()?j_string}", "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
    </#if>

  <#elseif comp.getType().name() == "NT">
    <#-- Standard NonTerminals -->
    <#if comp.isStringType()>
      <#if comp.isList()>
        for (String ${comp.getNameToUse()?uncap_first} : node.get${comp.getNameToUse()?cap_first}List()) {
          getPrinter().emit(${comp.getNameToUse()?uncap_first}, "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
        }
      <#elseif comp.isOpt()>
        if (node.isPresent${comp.getNameToUse()?cap_first}()) {
          getPrinter().emit(node.get${comp.getNameToUse()?cap_first}(), "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
        }
      <#else>
        getPrinter().emit(node.get${comp.getNameToUse()?cap_first}(), "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
      </#if>
    <#else>
      <#if comp.isList()>
        node.get${comp.getNameToUse()?cap_first}List().forEach(element -> element.accept(getTraverser()));
      <#elseif comp.isOpt()>
        if (node.isPresent${comp.getNameToUse()?cap_first}()) {
          node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
        }
      <#else>
        node.get${comp.getNameToUse()?cap_first}().accept(getTraverser());
      </#if>
    </#if>

  <#elseif comp.getType().name() == "NT_AST_DEF">
    <#-- NonTerminal with ASTRule reducing from List to Def -->
    <#if comp.isStringType()>
      getPrinter().emit(node.get${comp.getNameToUse()?cap_first}(0), "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
    <#else>
      node.get${comp.getNameToUse()?cap_first}(0).accept(getTraverser());
    </#if>

  <#elseif comp.getType().name() == "NT_ITERATED">
    <#-- Shared List Iterators (e.g., Expression ("," Expression)* ) -->
    <#if comp.isStringType()>
      getPrinter().emit(iter_${comp.getNameToUse()?uncap_first}.next(), "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
    <#else>
      iter_${comp.getNameToUse()?uncap_first}.next().accept(getTraverser());
    </#if>

  <#elseif comp.getType().name() == "BLOCK">
    <#-- Nested Blocks -->
    ${includeArgs("_prettyprinter.pp.FormattingBlock", comp.getBlockData(), grammarName, astPackage)}

  <#elseif comp.getType().name() == "CG">
    <#-- Constant Groups -->
    <#if comp.getConstants()?size == 1>
      getPrinter().emit("${comp.getConstants()?first.getValue()?j_string}", "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
    <#else>
      <#list comp.getConstants() as const>
        <#if const_index == 0>if<#else>else if</#if> (node.${comp.getNameToUse()}() == ${astPackage}.ASTConstants${grammarName?cap_first}.${const.getKey()?upper_case}) {
          getPrinter().emit("${const.getValue()?j_string}", "${comp.getPlaceholder()}", "${comp.getNameOrIndex()}");
        }
      </#list>
    </#if>
  </#if>
</#list>