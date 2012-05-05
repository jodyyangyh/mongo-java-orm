
package com.googlecode.mjorm.mql;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.TreeAdaptor;

import com.googlecode.mjorm.query.DaoQuery;

public class MqlCompiler {

	private static final TreeAdaptor ADAPTER = new CommonTreeAdaptor() {
		public Object create(Token payload) {
			return new CommonTree(payload);
		}
	};

	public List<DaoQuery> compile(String code)
		throws IOException,
		RecognitionException {
		return compile(new ByteArrayInputStream(code.getBytes()));
	}

	public List<DaoQuery> compile(InputStream ips)
		throws IOException,
		RecognitionException {

		// create the lexer and parser
		MqlLexer lexer 				= new MqlLexer(new ANTLRInputStream(ips));
		CommonTokenStream tokens 	= new CommonTokenStream(lexer);
		MqlParser parser 			= new MqlParser(tokens);

		// set adapter
		parser.setTreeAdaptor(ADAPTER);

		// parse
		MqlParser.start_return ret = parser.start();
		CommonTree tree = CommonTree.class.cast(ret.getTree());

		printTree(tree, 0);

		return null;
	}

	public void printTree(CommonTree t, int indent) {
		if ( t != null ) {
			StringBuffer sb = new StringBuffer(indent);
			for ( int i = 0; i < indent; i++ )
				sb = sb.append("   ");
			for ( int i = 0; i < t.getChildCount(); i++ ) {
				System.out.println(sb.toString() + t.getChild(i).toString());
				printTree((CommonTree)t.getChild(i), indent+1);
			}
		}
	}

}
