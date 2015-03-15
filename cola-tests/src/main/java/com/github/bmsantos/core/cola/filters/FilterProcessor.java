package com.github.bmsantos.core.cola.filters;

import java.util.Iterator;
import java.util.List;

import com.github.bmsantos.core.cola.formatter.FeatureDetails;

public class FilterProcessor {

    private final List<FeatureDetails> features;

    public FilterProcessor(final List<FeatureDetails> features) {
        this.features = features;
    }

    public List<FeatureDetails> using(final List<Filter> filters) {
        final Iterator<FeatureDetails> it = features.iterator();
        while (it.hasNext()) {
            final FeatureDetails feature = it.next();
            for (final Filter filter : filters) {
                if (filter.filtrate(feature)) {
                    it.remove();
                    break;
                }
            }
        }
        return features;
    }

    public static FilterProcessor filtrate(final List<FeatureDetails> features) {
        return new FilterProcessor(features);
    }
}