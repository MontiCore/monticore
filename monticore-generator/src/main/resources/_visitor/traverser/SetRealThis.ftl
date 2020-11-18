<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("traverserType", "traverserName", "inheritedTraversersSimpleNames", "generatedErrorCode")}
if (this.realThis != realThis) {
  if (!(realThis instanceof ${traverserType})) {
  Log.error("0xA7111${generatedErrorCode} realThis of ${traverserType} must be ${traverserType} itself.");
  }
  this.realThis = (${traverserType}) realThis;
  // register the known delegates to the realThis (and therby also set their new realThis)
  if (this.${traverserName?uncap_first}.isPresent()) {
    this.set${traverserName}(${traverserName?uncap_first}.get());
  }
<#list inheritedTraversersSimpleNames as inheritedVisitor>
  if (this.${inheritedVisitor?uncap_first}.isPresent()) {
    this.set${inheritedVisitor}(${inheritedVisitor?uncap_first}.get());
  }
</#list>

}