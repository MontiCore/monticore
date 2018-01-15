/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * getRealThis() project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * getRealThis() library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with getRealThis() project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.prettyprint;

import de.monticore.mcbasictypes1._ast.ASTBooleanType;
import de.monticore.mcbasictypes1._ast.ASTByteType;
import de.monticore.mcbasictypes1._ast.ASTCharType;
import de.monticore.mcbasictypes1._ast.ASTDoubleType;
import de.monticore.mcbasictypes1._ast.ASTFloatType;
import de.monticore.mcbasictypes1._ast.ASTImportStatement;
import de.monticore.mcbasictypes1._ast.ASTIntType;
import de.monticore.mcbasictypes1._ast.ASTLongType;
import de.monticore.mcbasictypes1._ast.ASTMCBasicTypes1Node;
import de.monticore.mcbasictypes1._ast.ASTNameAsReferenceType;
import de.monticore.mcbasictypes1._ast.ASTQualifiedName;
import de.monticore.mcbasictypes1._ast.ASTShortType;
import de.monticore.mcbasictypes1._visitor.MCBasicTypes1Visitor;

public class MCBasicTypes1PrettyPrinter implements MCBasicTypes1Visitor {
	private IndentPrinter printer = null;

	public MCBasicTypes1PrettyPrinter(IndentPrinter printer) {
		this.printer = printer;
	}

	@Override
	public void handle(ASTQualifiedName node) {
		int count = 1;
		for (String s : node.getParts()) {
			getPrinter().print(s);
			if (node.getParts().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}
	}

	@Override
	public void handle(ASTImportStatement node) {
		int count = 1;
		getPrinter().print("import ");
		for (String s : node.getQualifiedName().getParts()) {
			getPrinter().print(s);
			if (node.getQualifiedName().getParts().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}
		if (node.isStar()) {
			getPrinter().print(".*");
		}
		getPrinter().print(";");
	}

	@Override
	public void handle(ASTBooleanType node) {
		getPrinter().print("boolean");
	}

	@Override
	public void handle(ASTByteType node) {
		getPrinter().print("byte");
	}

	@Override
	public void handle(ASTCharType node) {
		getPrinter().print("char");
	}

	@Override
	public void handle(ASTShortType node) {
		getPrinter().print("short");
	}

	@Override
	public void handle(ASTIntType node) {
		getPrinter().print("int");
	}

	@Override
	public void handle(ASTFloatType node) {
		getPrinter().print("float");
	}

	@Override
	public void handle(ASTLongType node) {
		getPrinter().print("long");
	}

	@Override
	public void handle(ASTDoubleType node) {
		getPrinter().print("double");
	}

	@Override
	public void handle(ASTNameAsReferenceType node) {
		int count = 1;
		for (String s : node.getNames()) {
			getPrinter().print(s);
			if (node.getNames().size() > count) {
				getPrinter().print(".");
			}
			count++;
		}

	}

	public IndentPrinter getPrinter() {
		return this.printer;
	}

	public String prettyprint(ASTMCBasicTypes1Node node) {
		getPrinter().clearBuffer();
		node.accept(getRealThis());
		return getPrinter().getContent();
	}

}
