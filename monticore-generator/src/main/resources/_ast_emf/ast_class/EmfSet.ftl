${tc.signature("packageName", "className", "attribute")}

<#assign attributeName = attribute.getName()>
${attribute.printType()} old${attributeName?cap_first} = this.${attributeName};
this.${attributeName} = ${attributeName};

if (eNotificationRequired()) {
  eNotify(new ENotificationImpl(this, Notification.SET, ${packageName}.${className}_${attributeName?cap_first}, old${attributeName?cap_first}, ${attributeName}));
}