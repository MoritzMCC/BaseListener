package de.MoritzMCC.parameterresolver;


import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterResolverRegistry {
    private ParameterResolverRegistry() {
        /* This utility class should not be instantiated */
    }

    @Getter
    private static final List<ParameterResolver> resolvers = new ArrayList<>();

    public static void register(ParameterResolver resolver) {
        resolvers.add(resolver);
    }



    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private final List<ParameterResolver> registry = new ArrayList<>();

        public Builder addResolver(ParameterResolver resolver) {
            registry.add(resolver);
            return this;
        }
        public void build() {
            resolvers.addAll(registry);
        }
    }

}
