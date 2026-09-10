/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods;

import java.util.function.Supplier;

public class __internal__Supplier<T> implements Supplier<T> {
    Supplier<T> supplier;

    public __internal__Supplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return supplier.get();
    }
}
