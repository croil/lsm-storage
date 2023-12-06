package org.lsm.db.entry;

public interface Entry<D> {
    D key();

    D value();
}
