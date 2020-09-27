package app.automs.sdk.domain.config.store;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

@Data
public class StorePageConfig {
    private Boolean storePage = true;
    private Boolean enforceAbsolutLinks = true;
    private PageConfigCharset charset = PageConfigCharset.UTF8;

    private Map<String, String> applyAbsolutLinksOnTargets;

    {
        applyAbsolutLinksOnTargets = unmodifiableMap(
                new HashMap<String, String>() {{
                    put("script", "src"); // javascript
                    put("link", "href");  // css
                    put("a", "href"); // links
                    put("img", "src"); // images
                }});
    }
}