package app.automs.sdk.helper;

import app.automs.sdk.domain.config.store.PageConfigCharset;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static org.apache.commons.codec.binary.StringUtils.*;

public class HtmlHelper {
    public static String appendAbsolutPath(String html, @NotNull String exitPointUrl) {
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


    private static void parseAbsolutAt(Elements selectLinks, String href) {
        for (Element e : selectLinks) {
            e.attr(href, e.absUrl(href));
        }
    }

    public static byte[] getStringBytesEncodedAs(String rawString, PageConfigCharset configCharset) {
        byte[] bytes = null;

        switch (configCharset) {
            case UTF8:
                bytes = getBytesUtf8(rawString);
                break;
            case UTF16:
                bytes = getBytesUtf16(rawString);
                break;
            case LATIN:
                bytes = getBytesIso8859_1(rawString);
                break;
        }
        return bytes;
    }
}
