package com.enirvana.interview;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

@Immutable
@Style(stagedBuilder = true)
public interface Order {

    String userId();

    Double quantity();

    Double price();

    OrderType type();
}
