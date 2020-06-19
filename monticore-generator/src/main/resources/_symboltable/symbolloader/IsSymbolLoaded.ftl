<#-- (c) https://github.com/MontiCore/monticore -->
  if (!delegate.isPresent()) {
    lazyLoadDelegate();
  }
  return delegate.isPresent();