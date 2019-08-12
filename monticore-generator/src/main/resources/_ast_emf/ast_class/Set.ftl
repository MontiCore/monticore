${tc.signature("packageName", "className", "attribute")}
<#assign attributeName = attribute.getName()>
<#assign genHelper = glex.getGlobalVar("astHelper")>

<#if genHelper.isListAstNode(attribute)>
  if (${attributeName}.isEmpty() && this.${attributeName}.isEmpty()) {
    return;
  }
</#if>

${attribute.printType()} old${attributeName?cap_first} = this.${attributeName};
this.${attributeName} = ${attributeName};

if (eNotificationRequired()) {
  eNotify(new org.eclipse.emf.ecore.impl.ENotificationImpl(this, org.eclipse.emf.common.notify.Notification.SET,
    ${packageName}.${className}_${attributeName?cap_first}, old${attributeName?cap_first}, ${attributeName}));
}
