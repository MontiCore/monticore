/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;

/**
 * This class provides Layout functionality for the templates. It is typically
 * provided under name "layouter" in the templates. Enables it:
 * op.setValue("layouter", new util.Layouter()); and is set once at the
 * beginning of the main template.
 * 
 * @author rumpe
 */
public class Layouter {
  
  static final String START_TAG = "(";
  static final String END_TAG = ")";

	/**
	 * Performs a right padding (= fills up with spaces) If s is too long, it
	 * will not be shortened (no data missing)
	 * 
	 * @param o
	 *            Object to be printed (String, Integer, etc.)
	 * @param l
	 * @return
	 */
	public static String padright(Object o, int l) {
		String s = o.toString();
		return String.format("%" + l + "s", s);
	}

	/**
	 * Performs a left padding (= fills up with spaces on right) If s is too
	 * long, it will not be shortened (no data missing)
	 * 
	 * @param o
	 *            Object to be printed (String, Integer, etc.)
	 * @param l
	 * @return
	 */
	public static String padleft(Object o, int l) {
		String s = o.toString();
		return String.format("%-" + l + "s", s);
	}

	/**
	 * Formats the source position of an ASTnode
	 * 
	 * @param a
	 */
	public static String sourcePos(SourcePosition sp) {
		if (sp != null) {
			return String.format(START_TAG + "%d,%d" + END_TAG, sp.getLine(), sp.getColumn());
		} else {
			return "";
		}
	}

	/**
	 * Provides the Name of the Nonterminal of the AST (no qualifier, no "AST"
	 * at the beginning)
	 * 
	 * @param ast
	 * @return Nonterminalname
	 */
	public static String nodeName(ASTNode ast) {
		return className(ast).substring(3);
	}

	/**
	 * Provides the Name of the Nonterminal of the AST (no qualifier, no "AST"
	 * at the beginning)
	 * 
	 * @param ast
	 * @return Nonterminalname
	 */
	public static String unqualName(String s) {
		String[] c = s.split("\\.");
		String node = "Unknown";
		if (c.length >= 1) {
			node = c[c.length - 1];
		}
		return node;
	}

	/**
	 * Provides the Name of the Java File (no qualifier, last 2 compartments
	 * including file + extension)
	 * 
	 * @param ast
	 * @return Nonterminalname
	 */
	public static String unqual2Name(String s) {
		String[] c = s.split("\\.");
		String node = "Unknown!E534";
		if (c.length >= 2) {
			node = c[c.length - 2] + "." + c[c.length - 1];
		} else if (c.length >= 1) {
			node = c[c.length - 1];
		}
		return node;
	}

	/**
	 * unqualified class name
	 * 
	 * @param value
	 * @return String
	 */
	public static String className(Object value) {
		return unqualName(value.getClass().getName());
	}

	/**
	 * derives a useful (compact) value for any object with special treatment
	 * for String, Integers, Boolean. We have a length cut at 78 characters,
	 * then we use dots + [length] at endto describe incompleteness
	 * 
	 * @param value
	 * @return String
	 */
	public static String valueStr(Object value) {
		String out;
		if (value == null) {
			out = "null";
		} else {
			String clazzn = className(value);
			// Sonderbehandlung mancher Typen
			if (clazzn.equals("String")) {
				out = "\"" + value.toString() + "\"";
			} else if (clazzn.equals("Integer") || clazzn.equals("Boolean")) {
				out = value.toString();
			} else {
				out = "(" + clazzn + ")" + value.toString();
			}
		}
		int l = out.length();
		final int maxLength = 90;
		if (l > maxLength) {
			out = out.substring(0, maxLength - 7 - 5) + "..."
					+ out.substring(l - 5) + "[" + l + "]";
		}
		return out;
	}

	/**
	 * Provides the name of the Nonterminal of the AST (no qualifier, no "AST"
	 * at the beginning)
	 * 
	 * @param ast
	 * @return Nonterminalname
	 */
	public static String unqualNamePadleft(String s, int l) {
		return padleft(unqualName(s), l);
	}

	public static String getSpaceString(int length) {
		if (length < 0) {
			return " ";
		}
		StringBuilder b = new StringBuilder("");
		for (int i = 0; i < length; i++) {
			b.append(" ");
		}
		return b.toString();
	}

}
