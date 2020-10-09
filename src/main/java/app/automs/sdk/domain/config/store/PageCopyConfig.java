package app.automs.sdk.domain.config.store;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableMap;

@Data
@JsonInclude(NON_NULL)
public class PageCopyConfig {
    private Boolean enablePageCopy = true;
    private Boolean enableStructuredResponseCopy = true;

    private PageCopyConfigCharset usingCharset = PageCopyConfigCharset.UTF8;
    private Boolean usingAbsolutLinks = true;
    private Map<String, List<String>> replacingAbsolutLinksOnElements;

    {
        replacingAbsolutLinksOnElements = unmodifiableMap(
                new HashMap<String, List<String>>() {{
                    put("script", singletonList("src")); // javascript
                    put("link", singletonList("href"));  // css
                    put("a", singletonList("href")); // links
                    put("img", singletonList("src")); // images
                }});
    }
}
