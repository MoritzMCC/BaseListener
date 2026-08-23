package de.moritzmcc.parameterresolver;

import de.moritzmcc.parameterresolver.impl.PlayerResolver;
import de.moritzmcc.parameterresolver.impl.PlayersNearbyResolver;

import java.lang.reflect.Parameter;

public class ParameterManager {

    public static void register() {
        ParameterResolverRegistry.builder()
                .addResolver(new PlayerResolver())
                .addResolver(new PlayersNearbyResolver())
                .build();
    }

    public static ParameterResolver resolve(Parameter parameter) {
        for (ParameterResolver r : ParameterResolverRegistry.getResolvers()) {
            if (r.canResolve(parameter)) {
                return r;
            }
        }
        return null;
    }
}
