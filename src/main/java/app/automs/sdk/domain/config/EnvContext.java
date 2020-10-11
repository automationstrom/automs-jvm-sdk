package app.automs.sdk.domain.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnvContext {
    private String context;

    public Boolean isDev() {
        return context.equalsIgnoreCase("local") || context.equalsIgnoreCase("compose");
    }

    public Boolean isLocal() {
        return context.equalsIgnoreCase("local");
    }

    public Boolean isCompose() {
        return context.equalsIgnoreCase("compose");
    }

    public Boolean isCloud() {
        return context.equalsIgnoreCase("cloud");
    }

}
