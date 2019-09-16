package com.enirvana.interview;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface Summary {

    List<SummaryEntry> buyOrders();

    List<SummaryEntry> sellOrders();

}
