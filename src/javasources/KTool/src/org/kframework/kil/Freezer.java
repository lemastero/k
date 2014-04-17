// Copyright (c) 2012-2014 K Team. All Rights Reserved.
package org.kframework.kil;

import org.kframework.kil.visitors.Visitor;

/** A frozen term. Contains a {@link FreezerHole}. */
public class Freezer extends Term {

    private Term term;

    public Freezer(Freezer f) {
        super(f);
        term = f.term;
    }

    public Freezer(Term t) {
        super("K");
        term = t;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }


    @Override
    public Freezer shallowCopy() {
        return new Freezer(this);
    }

    @Override
    public String toString() {
        return "#freezer " + term.toString() + "(.KList)";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Freezer)) return false;
        Freezer f = (Freezer)o;
        return term.equals(f.term);
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Bracket)
            return contains(((Bracket)o).getContent());
        if (o instanceof Cast)
            return contains(((Cast)o).getContent());
        if (!(o instanceof Freezer)) return false;
        Freezer f = (Freezer)o;
        return term.contains(f.term);
    }


    @Override
    public int hashCode() {
        return term.hashCode();
    }
    
    @Override
    public <P, R, E extends Throwable> R accept(Visitor<P, R, E> visitor, P p) throws E {
        return visitor.visit(this, p);
    }
}
