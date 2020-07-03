<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symbolTablePrinterName", "delegates", "generatedErrorCode")}
if (this.realThis != realThis) {
  if (!(realThis instanceof ${symbolTablePrinterName})) {
    Log.error("0xA7117${generatedErrorCode} realThis of ${symbolTablePrinterName} must be ${symbolTablePrinterName} itself.");
  }
  this.realThis = (${symbolTablePrinterName}) realThis;
<#list delegates as delegate>
  this.${inheritedVisitor}.setRealThis(realThis);
</#list>

}