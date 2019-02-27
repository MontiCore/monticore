${tc.signature("attributeName")}
if (this.${attributeName}.isPresent()) {
    return this.${attributeName}().get();
}
Log.error("0xA7003 ${attributeName} can't return a value. It is empty.");
// Normally this statement is not reachable
throw new IllegalStateException();