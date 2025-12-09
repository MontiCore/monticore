<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
if(checkLazyLoadDelegate()) {
  lazyLoadDelegate().setSpannedScope((${scopeName}) scope);
}
