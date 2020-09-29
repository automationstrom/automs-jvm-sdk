package app.automs.sdk.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.unmodifiableList;

@Data
@JsonInclude(NON_EMPTY)
public class ChromeDriverOptionsConfig {
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> defaultOptions;
    private List<String> sessionOptions;

    {
        sessionOptions = ImmutableList.of();
        defaultOptions = unmodifiableList(new ArrayList<String>() {
            {
                // https://peter.sh/experiments/chromium-command-line-switches/#load-extension
                // more details

                // core
                add("--headless");
                add("--disable-dev-shm-usage");
                add("--no-sandbox");

                // testing
                add("--window-size=1366,768");
                add("--disable-gpu");
                add("--disable-notifications");
//                add("--no-zygote");
//                add("--disable-extensions");

                //recommended
                add("--disable-background-timer-throttling");
                add("--disable-backgrounding-occluded-windows");
                add("--disable-breakpad");
                add("--disable-component-extensions-with-background-pages");
                add("--disable-extensions");
                add("--disable-features=TranslateUI,BlinkGenPropertyTrees");
                add("--disable-ipc-flooding-protection");
                add("--disable-renderer-backgrounding");
                add("--enable-features=NetworkService,NetworkServiceInProcess");
                add("--force-color-profile=srgb");
                add("--hide-scrollbars");
                add("--metrics-recording-only");
                add("--mute-audio");
                add("--ignore-certificate-errors");
                add("--ignore-ssl-errors");
            }
        });
    }
}
