package pe.bigprime;

import com.itextpdf.text.DocumentException;
import models.DocumentBP;
import utils.HashJson;
import utils.SignMapper;
import utils.SignatureUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;

public class pruebas {
    public static void main(String[] args) throws CertificateException, DocumentException, IOException {
        pruebas p = new pruebas();
        System.out.println("Welcome to the Jungle!");
        String hash = p.getHashDocument(new File("C:\\Users\\LENOVO\\Desktop\\rekognition-dg.pdf"));
        System.out.println("hash = " + hash);
    }
    private String getHashDocument(File file) throws CertificateException, DocumentException, IOException {
        SignMapper signMapper = SignatureUtil.preSign(file);
        String base64Hash = new String(Base64.getEncoder().encode(signMapper.getHash()), StandardCharsets.UTF_8);
        DocumentBP document = new DocumentBP(file.getName(), file.getAbsolutePath(), false, base64Hash);

        //preparar hashes para la firma digital de lado servidor
        ArrayList<HashJson> hashJsons = new ArrayList<>();
        hashJsons.add(new HashJson( new String(Base64.getEncoder().encode(signMapper.getSignHash()), StandardCharsets.UTF_8)));
        assert signMapper != null;
        return hashJsons.get(0).getHash();
    }

}
