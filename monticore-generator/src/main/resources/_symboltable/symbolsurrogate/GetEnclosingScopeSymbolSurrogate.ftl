<#-- (c) https://github.com/MontiCore/monticore -->
if(delegate.isPresent()){
  return lazyLoadDelegate().getEnclosingScope();
}
return enclosingScope;