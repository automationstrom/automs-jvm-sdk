package app.automs.sdk.helper;

import app.automs.sdk.domain.config.store.PageConfigCharset;

import static org.apache.commons.codec.binary.StringUtils.*;

public class CharsetHelper {

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
