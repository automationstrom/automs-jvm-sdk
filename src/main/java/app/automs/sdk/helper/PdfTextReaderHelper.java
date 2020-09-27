package app.automs.sdk.helper;

import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedInputStream;
import java.net.URL;

@Deprecated
public class PdfTextReaderHelper {

    public static String readPDFContent(String appUrl) throws Exception {
        val url = new URL(appUrl);
        val input = url.openStream();
        val fileToParse = new BufferedInputStream(input);
        PDDocument document = null;
        String output;

        try {
            document = PDDocument.load(fileToParse);
            output = new PDFTextStripper().getText(document);
        } finally {
            if (document != null) {
                document.close();
            }
            fileToParse.close();
        }
        return output;
    }

}
