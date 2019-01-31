${tc.signature("attribute")}
${attribute.printModifier()} ${attribute.printType()} ${attribute.getName()}<#if attribute.isPresentValue()> = ${attribute.printValue()}</#if>;