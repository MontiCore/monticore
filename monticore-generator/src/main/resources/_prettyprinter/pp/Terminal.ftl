<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "terminal")}
<#--
  Print a terminal (or constant)
  Special default handling for semicolons and curtly brackets included.
-->
<#if terminal == ";">
    getPrinter().println("${terminal} ");
<#elseif terminal == "{">
    getPrinter().println("${terminal} ");
    getPrinter().indent();
<#elseif terminal == "}">
    getPrinter().unindent();
    getPrinter().println();
    getPrinter().println("${terminal} ");
<#else >
    getPrinter().print("${terminal} ");
</#if>
