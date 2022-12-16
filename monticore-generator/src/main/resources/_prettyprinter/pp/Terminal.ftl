<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("terminal", "hasNoSpace", "nonAlphabeticNoSpace")}
<#--
  Print a terminal (or constant)
  In case of the noSpace directive no space-suffix is appended
  Special default handling for semicolons and curtly brackets included.
-->
<#if terminal == ";">
<#if hasNoSpace>
    getPrinter().print("${terminal}");
<#elseif nonAlphabeticNoSpace>
    getPrinter().stripTrailing();
    getPrinter().println("${terminal}");
<#else>
    getPrinter().println("${terminal} ");
</#if>
<#elseif terminal == "{">
<#if hasNoSpace>
    getPrinter().print("${terminal}");
<#else>
    getPrinter().println("${terminal} ");
    getPrinter().indent();
</#if>
<#elseif terminal == "}">
<#if hasNoSpace>
    getPrinter().print("${terminal}");
<#else>
    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("${terminal} ");
</#if>
<#else >
<#if hasNoSpace>
    getPrinter().print("${terminal}");
<#elseif nonAlphabeticNoSpace>
    getPrinter().stripTrailing();
    getPrinter().print("${terminal}");
<#else>
    getPrinter().print("${terminal} ");
</#if>
</#if>
