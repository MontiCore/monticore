${tc.signature("delegateClassNames","jsonPrinterFullName")}
this.printer = new ${jsonPrinterFullName}();
<#list delegateClassNames as delegate>
${delegate?uncap_first}Delegate = new ${delegate}();
</#list>
