// Copyright (c) 2012-2014 K Team. All Rights Reserved.
package org.kframework.compile.transformers;

import org.kframework.kil.*;
import org.kframework.kil.visitors.CopyOnWriteTransformer;
import org.kframework.kil.visitors.exceptions.TransformerException;

/**
 * Initially created by: Traian Florin Serbanuta
 * Date: 11/1/12
 * Time: 7:59 AM
 */
public class ResolveRewrite extends CopyOnWriteTransformer {

    public ResolveRewrite(org.kframework.kil.loader.Context context) {
        super("Pushing local rewrites to top", context);
    }

    @Override
    public ASTNode visit(Rule node, Void _) throws TransformerException {
        Term body = node.getBody();
        if (body instanceof Rewrite) return node;
        node = node.shallowCopy();
        Term left = (Term) body.accept(new OneSideTransformer(LRHS.LEFT, context));
        Term right = (Term) body.accept(new OneSideTransformer(LRHS.RIGHT, context));
        Rewrite rewrite = new Rewrite(left, right, context);
        node.setBody(rewrite);
        return node;
    }

    @Override
    public ASTNode visit(Syntax node, Void _) throws TransformerException {
        return node;
    }

    @Override
    public ASTNode visit(Configuration node, Void _) throws TransformerException {
        return node;
    }

    @Override
    public ASTNode visit(org.kframework.kil.Context node, Void _) throws TransformerException {
        return node;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public enum LRHS {
        LEFT,
        RIGHT,
    }

    public class OneSideTransformer extends CopyOnWriteTransformer {
        private LRHS lrhs;

        public OneSideTransformer(LRHS lrhs, org.kframework.kil.loader.Context context) {
            super("Retrieving the " + lrhs + "side of the term", context);
            this.lrhs = lrhs;
        }

        @Override
        public ASTNode visit(Rewrite node, Void _) throws TransformerException {
            switch (lrhs) {
                case LEFT:
                    return node.getLeft();
                case RIGHT:
                    return node.getRight();
            }
            return null;
        }
    }
}
