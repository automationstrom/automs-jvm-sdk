package app.automs.sdk.helper;

import app.automs.sdk.domain.config.store.PageCopyConfig;
import app.automs.sdk.domain.config.store.PageCopyConfigCharset;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.commons.codec.binary.StringUtils.*;

public class HtmlHelper {
    public static String prepareHtmlFile(String html, @NotNull String exitPointUrl, PageCopyConfig config) {
        Document doc = Jsoup.parse(html, exitPointUrl);

        config.getReplacingAbsolutLinksOnElements()
                .forEach((element, attributes) ->
                        attributes.forEach(attribute -> parseAbsolutAt(doc.select(element), attribute))
                );

        injectJs(doc,
                "function addScript(url) {\n" +
                        "    var script = document.createElement('script');\n" +
                        "    script.type = 'application/javascript';\n" +
                        "    script.src = url;\n" +
                        "    document.head.appendChild(script);\n" +
                        "}");

        switch (config.getUsingCharset()) {
            case UTF8:
                doc.charset(UTF_8.newDecoder().charset());
                break;
            case UTF16:
                doc.charset(UTF_16.newDecoder().charset());
                break;
            case LATIN:
                doc.charset(ISO_8859_1.newDecoder().charset());
                break;
        }
        return doc.html();
    }

    /***
     *  js injector function (for pdf client generation)
     *  usage:
     *     https://github.com/eKoopmans/html2pdf.js
     *     addScript('https://raw.githack.com/eKoopmans/html2pdf/master/dist/html2pdf.bundle.js');
     *     then html2pdf(document.body)
     * @param doc jsoup html target document
     */
    @SuppressWarnings("SameParameterValue")
    private static void injectJs(Document doc, String jsFunction) {
        doc.head()
                .appendElement("script")
                .attr("type", "text/javascript")
                .appendChild(new DataNode(jsFunction));
    }

    private static void parseAbsolutAt(Elements selectLinks, String href) {
        for (Element e : selectLinks) {
            e.attr(href, e.absUrl(href));
        }
    }

    public static byte[] getStringBytesEncodedAs(String rawString, PageCopyConfigCharset configCharset) {
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
