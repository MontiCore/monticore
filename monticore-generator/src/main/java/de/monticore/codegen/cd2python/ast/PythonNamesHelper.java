/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.cd2python.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PythonNamesHelper {
    public static final String PREFIX_WHEN_WORD_IS_RESERVED = "r__";

    public static String pythonAttribute(String in) {
        if (in != null && in.length() > 0) {
            String out = (in.substring(0, 1).toLowerCase() + in.substring(1)).intern();
            return getNonReservedName(out);
        } else {
            return in;
        }
    }

    public PythonNamesHelper() {
    }

    public static String getNonReservedName(String name) {
        return PythonReservedWordReplacer.getNonReservedName(name);
    }

    private static class PythonReservedWordReplacer {
        private static Set<String> goodNames = null;

        private PythonReservedWordReplacer() {
        }

        public static String getNonReservedName(String name) {
            if (goodNames == null) {
                goodNames = new HashSet();
                goodNames.addAll(Arrays.asList("and", "del", "from","not","while","as",
                        "elif","global","or","with","assert","else","if","pass","yield","break",
                        "except","import","print","class","exec","in","raise","continue",
                        "finally","is","return","def","for","lambda","try"));
            }

            if (name == null) {
                return null;
            } else {
                return goodNames.contains(name) ? (PREFIX_WHEN_WORD_IS_RESERVED + name).intern() : name.intern();
            }
        }
    }
}
