package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Utils {

    public static String fileToBase64(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
