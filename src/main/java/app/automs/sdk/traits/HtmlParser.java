package app.automs.sdk.traits;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface HtmlParser {

    default String appendAbsolutPath(String html, @NotNull String exitPointUrl) {

        Document doc = Jsoup.parse(html, exitPointUrl);

        val selectJs = doc.select("script");
        parseAbsolutAt(selectJs, "src");

        val selectCss = doc.select("link");
        parseAbsolutAt(selectCss, "href");

        val selectLinks = doc.select("a");
        parseAbsolutAt(selectLinks, "href");

        val selectImg = doc.select("img");
        parseAbsolutAt(selectImg, "src");

        return doc.toString();
    }


    default void parseAbsolutAt(Elements selectLinks, String href) {
        for (Element e : selectLinks) {
            e.attr(href, e.absUrl(href));
        }
    }


}
