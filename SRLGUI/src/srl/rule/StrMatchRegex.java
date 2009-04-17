/* 
 * Copyright (c) 2008, National Institute of Informatics
 *
 * This file is part of SRL, and is free
 * software, licenced under the GNU Library General Public License,
 * Version 2, June 1991.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://www.fsf.org/licensing/licenses/info/GPLv2.html.
 */
package srl.rule;

import java.util.List;
import java.util.Stack;
import org.apache.lucene.analysis.Token;
import srl.corpus.SrlQuery;

/**
 *
 * @author john
 */
public class StrMatchRegex implements TypeExpr {

    final String expression;
    
    TypeExpr next;
            
    public StrMatchRegex(String expression) {
        this.expression = expression;
    }
    
    public void getQuery(SrlQuery query) {
        query.query.append("\" \"");
    }

    public TypeExpr matches(Token token, int no, Stack<MatchFork> stack, List<Token> lookBackStack) {
        if(token.termText().matches(expression)) {
            return next;
        } else {
            return null;
        }
    }

    public void setNext(TypeExpr te) {
        next = te;
    }

    public void skip(Token token) {
       
    }

    public void reset() {
       
    }

    @Override
    public String toString() {
        return "regex(\"" + expression + "\")";
    }

    public boolean canEnd() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StrMatchRegex) {
            return ((StrMatchRegex)obj).expression.equals(expression);
        }
        return false;
    }

    public TypeExpr copy() {
        return new StrMatchRegex(expression);
    }
}
