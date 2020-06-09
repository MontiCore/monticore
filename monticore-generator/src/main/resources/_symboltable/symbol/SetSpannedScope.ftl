<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("isInherited")}
<#if isInherited>
  super.setSpannedScope(spannedScope);
</#if>
  this.spannedScope = spannedScope;
  this.spannedScope.setSpanningSymbol(this);