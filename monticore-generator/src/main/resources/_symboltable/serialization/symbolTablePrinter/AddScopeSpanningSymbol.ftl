<#-- (c) https://github.com/MontiCore/monticore -->
  if (null != spanningSymbol && spanningSymbol.isPresent()) {
    printer.beginObject(de.monticore.symboltable.serialization.JsonConstants.SCOPE_SPANNING_SYMBOL);
    printer.member(de.monticore.symboltable.serialization.JsonConstants.KIND, spanningSymbol.get().getClass().getName());
    printer.member(de.monticore.symboltable.serialization.JsonConstants.NAME, spanningSymbol.get().getName());
    printer.endObject();
  }