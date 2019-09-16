package com.enirvana.interview;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public interface SummaryEntry {

    @Parameter
    Double quantity();

    @Parameter
    Double price();
}
