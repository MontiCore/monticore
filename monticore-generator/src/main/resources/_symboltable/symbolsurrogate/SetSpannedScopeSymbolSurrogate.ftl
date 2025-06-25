<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("scopeName")}
if(delegate.isPresent()){
  lazyLoadDelegate().setSpannedScope((${scopeName}) scope);
}
