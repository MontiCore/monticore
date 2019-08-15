<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("delegatorVisitorName", "simpleVisitorName", "inheritedVisitorSimpleNames")}
if (this.realThis != realThis) {
  if (!(realThis instanceof ${delegatorVisitorName})) {
  Log.error("0xA7111x046 realThis of ${delegatorVisitorName} must be ${delegatorVisitorName} itself.");
  }
  this.realThis = (${delegatorVisitorName}) realThis;
  // register the known delegates to the realThis (and therby also set their new realThis)
  if (this.${simpleVisitorName?uncap_first}.isPresent()) {
    this.set${simpleVisitorName}(${simpleVisitorName?uncap_first}.get());
  }
<#list inheritedVisitorSimpleNames as inheritedVisitor>
  if (this.${inheritedVisitor?uncap_first}.isPresent()) {
    this.set${inheritedVisitor}(${inheritedVisitor?uncap_first}.get());
  }
</#list>

}