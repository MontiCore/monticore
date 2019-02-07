${tc.signature("attribute")}
if (this.${attribute.getName()}.isPresent()) {
    return this.${attribute.getName()}().get();
}
Log.error("0xA7003 ${attribute.getName()} can't return a value. It is empty.");
// Normally this statement is not reachable
throw new IllegalStateException();