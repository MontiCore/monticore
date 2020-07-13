<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolTablePrinterName")}
  ${symbolTablePrinterName} symbolTablePrinter;
  if(this.printer!=null){
    symbolTablePrinter = new ${symbolTablePrinterName}(printer);
  }else{
    symbolTablePrinter = new ${symbolTablePrinterName}();
  }
  return symbolTablePrinter;