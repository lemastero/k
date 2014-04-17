// Copyright (c) 2013-2014 K Team. All Rights Reserved.
package org.kframework.compile.transformers;

import org.kframework.kil.ASTNode;
import org.kframework.kil.Cast;
import org.kframework.kil.loader.Context;
import org.kframework.kil.visitors.CopyOnWriteTransformer;
import org.kframework.kil.visitors.exceptions.TransformerException;

public class RemoveSyntacticCasts extends CopyOnWriteTransformer {

    public RemoveSyntacticCasts(Context context) {
        super("Remove syntactic casts", context);
    }

    @Override
    public ASTNode visit(Cast node, Void _) throws TransformerException {
        // System.out.println("Remove: " + node.getFilename() + ":" + node.getLocation());
        // TODO (RaduM): remove only syntactic casts when variable type inference is updated
        //if (node.isSyntactic())
        return node.getContent().accept(this);
        //return super.transform(node);
    }
}
