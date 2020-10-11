package app.automs.sdk.domain.config.headless;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static java.util.Collections.unmodifiableList;
import static java.util.UUID.randomUUID;

@Data
@JsonInclude(NON_EMPTY)
public class ChromeDriverOptionsConfig {
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> defaultOptions;
    private List<String> sessionOptions;
    private String httpProxy = "none";
    private String customUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36";
    private Integer elementSearchTimeout = 5;
    private String trackingId = randomUUID().toString();
    private Boolean quitSession = false;

    //    private Integer pageLoadTimeout = 20;
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
                add("--kiosk-printing");
                add("--no-default-browser-check");
                add("--no-crash-upload");

//                add("--user-agent");
//                add("--disable-extensions");

//                Don't enforce the same-origin policy. (Used by people testing their sites.)
//                add("--disable-web-security");
//                Disables the use of a zygote process for forking child processes.
//                add("--no-zygote");

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
