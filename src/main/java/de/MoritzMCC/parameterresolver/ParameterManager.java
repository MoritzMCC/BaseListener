package de.MoritzMCC.parameterresolver;

import de.MoritzMCC.parameterresolver.impl.PlayerResolver;

import java.util.HashMap;
import java.util.Map;

public class ParameterManager {

    private static final Map<Class<?>, ParameterResolver> cache = new HashMap<>();

    public static void register(){
        ParameterResolverRegistry.builder()
                .addResolver(new PlayerResolver())
                .build();
    }

    public static ParameterResolver resolve(Class<?> type) {
        ParameterResolver resolver = cache.get(type);
        if (resolver != null) {
            return resolver;
        }
        for (ParameterResolver r : ParameterResolverRegistry.getResolvers()) {
            if (r.canResolve(type)) {
                cache.put(type, r);
                return r;
            }

        }
        cache.put(type, null);
        return null;
    }
}
