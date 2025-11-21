  if(!(obj instanceof de.monticore.symboltable.ISymbol)) {
    return false;
  }
  de.monticore.symboltable.ISymbol s1 = getThis();
  de.monticore.symboltable.ISymbol s2 = ((de.monticore.symboltable.ISymbol) obj).getThis();

  return s1 == s2;
