package app.automs.sdk.helper;

import lombok.val;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScreenshotHelper {
    public static byte[] takeFullPageScreenShotAsByte(WebDriver webDriver) throws IOException {
        val fpScreenshot =
                new AShot()
                        .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                        .takeScreenshot(webDriver);

        val originalImage = fpScreenshot.getImage();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(originalImage, "png", outputStream);
            outputStream.flush();
            return outputStream.toByteArray();
        }
    }

}
