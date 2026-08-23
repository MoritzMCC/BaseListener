package de.moritzmcc.parameterresolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParameterResolverRegistry {

    private static final List<ParameterResolver> resolvers = new ArrayList<>();

    private ParameterResolverRegistry() {
        /* Utility-Klasse, keine Instanzen */
    }

    public static List<ParameterResolver> getResolvers() {
        return Collections.unmodifiableList(resolvers);
    }

    public static void register(ParameterResolver resolver) {
        resolvers.add(resolver);
    }

    public static Builder builder() {
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
