package app.automs.sdk.domain.config;

import lombok.Data;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

@Data
public class ChromeDriverOptionsConfig {
    Map<String, List<String>> configProfiles;

    {
        val defaultProfileOptions = new ArrayList<String>() {
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
            }
        };

        configProfiles = unmodifiableMap(
                new HashMap<String, List<String>>() {{
                    put("default", defaultProfileOptions); //
                }});
    }
}
