package com.github.bmsantos.core.cola.filters;

import com.github.bmsantos.core.cola.formatter.TagStatementDetails;

public interface Filter {

    boolean filtrate(final TagStatementDetails statement);

}
