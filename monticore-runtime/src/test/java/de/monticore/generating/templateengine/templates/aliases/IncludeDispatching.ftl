<#-- (c) https://github.com/MontiCore/monticore -->
<#-- old include macro, renamed  -->
<#function includeOld templ>
    <#return tc.include(templ)>
</#function>
String argument
${includeOld("Plain")}
${include("Plain")}

List argument
${includeOld(["Plain", "Plain"])}
${include(["Plain", "Plain"])}