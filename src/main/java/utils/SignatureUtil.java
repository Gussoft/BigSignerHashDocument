package utils;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.security.*;
import com.typesafe.config.ConfigFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class SignatureUtil {

    public static SignMapper preSign2(SignMapper signMapper, File file) throws CertificateException, IOException, DocumentException {
        File certificadoPublico = getPublicCert();//new File("C:\\Users\\LENOVO\\Downloads\\signtest.cer");//
        String x509Path = certificadoPublico.getPath();//System.getenv("bigsigner.certificate.path");//

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate[] chain = new Certificate[1];
        chain[0] = factory.generateCertificate(new FileInputStream(x509Path));

        PdfReader reader = new PdfReader(file.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0');

        PDDocument pdDocument = PDDocument.load(file);
        int visualSignaturePage = pdDocument.getNumberOfPages();
        int providedVisSigPage = signMapper.getVisP();
        if (providedVisSigPage > 0 && providedVisSigPage <= visualSignaturePage) {
            visualSignaturePage = providedVisSigPage;
        }
        PDRectangle visualSignaturePageDimension = getPageDimension(pdDocument, visualSignaturePage);

// we create the signature appearance
        PdfSignatureAppearance sap = stamper.getSignatureAppearance();

//        sap.setReason("Test");
        sap.setLocation("BigSigner Server");
        sap.setVisibleSignature(
                new Rectangle(signMapper.getVisX(),
                        visualSignaturePageDimension.getHeight() - signMapper.getVisY() - signMapper.getVisH(),
                        signMapper.getVisX() + signMapper.getVisW(),
                        visualSignaturePageDimension.getHeight() - signMapper.getVisY()),
                visualSignaturePage, "sig");
        //sap.setSignatureGraphic(Image.getInstance(signMapper.getVisI()));
        //sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
        sap.setCertificate(chain[0]);

        // we create the signature infrastructure
        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
//        dic.setReason(sap.getReason());
        dic.setLocation(sap.getLocation());
        dic.setContact(sap.getContact());
        dic.setDate(new PdfDate(sap.getSignDate()));
        sap.setCryptoDictionary(dic);
        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, new Integer(8192 * 2 + 2));
        sap.setLayer2Text(SignatureUtil.processTextTemplate(System.getenv("BIGSIGNER_TEMPLANTE"), sap.getSignDate()));

        sap.preClose(exc);
        ExternalDigest externalDigest = new ExternalDigest() {
            public MessageDigest getMessageDigest(String hashAlgorithm)
                    throws GeneralSecurityException {
                return DigestAlgorithms.getMessageDigest(hashAlgorithm, null);
            }
        };

        try {
            PdfPKCS7 sgn = new PdfPKCS7(null, chain, "SHA256", null, externalDigest, false);

            InputStream data = sap.getRangeStream();
            byte hash[] = DigestAlgorithms.digest(data, externalDigest.getMessageDigest("SHA256"));

            // we get OCSP and CRL for the cert
            OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
            OcspClient ocspClient = new OcspClientBouncyCastle(ocspVerifier);
            byte[] ocsp = null;
            if (chain.length >= 2 && ocspClient != null) {
                ocsp = ocspClient.getEncoded((X509Certificate) chain[0], (X509Certificate) chain[1], null);
            }
            Collection<byte[]> crlBytes = null;
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, null, null, MakeSignature.CryptoStandard.CMS);
            certificadoPublico.delete();

            signMapper.setAppearance(sap);
            signMapper.setHash(hash);
            signMapper.setSignHash(sh);
            signMapper.setOcsp(ocsp);
            signMapper.setSgn(sgn);
            signMapper.setBaos(baos);
            return signMapper;

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SignMapper preSign(File file) throws CertificateException, IOException, DocumentException {
        File certificadoPublico = getPublicCert();
        String x509Path = certificadoPublico.getAbsolutePath();//ConfigFactory.load().getString("bigsigner.certificate.path");

        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        Certificate[] chain = new Certificate[1];
        chain[0] = factory.generateCertificate(new FileInputStream(x509Path));


        PdfReader reader = new PdfReader(file.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = PdfStamper.createSignature(reader, baos, '\0');

        SignMapper signMapper = new SignMapper();
        PDDocument pdDocument = PDDocument.load(file);
        int visualSignaturePage = pdDocument.getNumberOfPages();
        int providedVisSigPage = signMapper.getVisP();
        if (providedVisSigPage > 0 && providedVisSigPage <= visualSignaturePage) {
            visualSignaturePage = providedVisSigPage;
        }
        PDRectangle visualSignaturePageDimension = getPageDimension(pdDocument, visualSignaturePage);

// we create the signature appearance
        PdfSignatureAppearance sap = stamper.getSignatureAppearance();

//        sap.setReason("Test");
        sap.setLocation("BigSigner Server");
        sap.setVisibleSignature(
                new Rectangle(signMapper.getVisX(),
                        visualSignaturePageDimension.getHeight() - signMapper.getVisY() - signMapper.getVisH(),
                        signMapper.getVisX() + signMapper.getVisW(),
                        visualSignaturePageDimension.getHeight() - signMapper.getVisY()),
                visualSignaturePage, "sig");
        //sap.setSignatureGraphic(Image.getInstance(signMapper.getVisI()));
        //sap.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
        sap.setCertificate(chain[0]);

        // we create the signature infrastructure
        PdfSignature dic = new PdfSignature(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
//        dic.setReason(sap.getReason());
        dic.setLocation(sap.getLocation());
        dic.setContact(sap.getContact());
        dic.setDate(new PdfDate(sap.getSignDate()));
        sap.setCryptoDictionary(dic);
        HashMap<PdfName, Integer> exc = new HashMap<PdfName, Integer>();
        exc.put(PdfName.CONTENTS, new Integer(8192 * 2 + 2));
        sap.setLayer2Text(SignatureUtil.processTextTemplate(System.getenv("BIGSIGNER_TEMPLANTE"), sap.getSignDate()));
        sap.preClose(exc);
        ExternalDigest externalDigest = new ExternalDigest() {
            public MessageDigest getMessageDigest(String hashAlgorithm)
                    throws GeneralSecurityException {
                return DigestAlgorithms.getMessageDigest(hashAlgorithm, null);
            }
        };

        try {
            PdfPKCS7 sgn = new PdfPKCS7(null, chain, "SHA256", null, externalDigest, false);

            InputStream data = sap.getRangeStream();
            byte[] hash = DigestAlgorithms.digest(data, externalDigest.getMessageDigest("SHA256"));

            // we get OCSP and CRL for the cert
            OCSPVerifier ocspVerifier = new OCSPVerifier(null, null);
            OcspClient ocspClient = new OcspClientBouncyCastle(ocspVerifier);
            byte[] ocsp = null;
            if (chain.length >= 2 && ocspClient != null) {
                ocsp = ocspClient.getEncoded((X509Certificate) chain[0], (X509Certificate) chain[1], null);
            }
            Collection<byte[]> crlBytes = null;
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, null, null, MakeSignature.CryptoStandard.CMS);

            signMapper.setAppearance(sap);
            signMapper.setHash(hash);
            signMapper.setSignHash(sh);
            signMapper.setOcsp(ocsp);
            signMapper.setSgn(sgn);
            signMapper.setBaos(baos);
            return signMapper;

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteArrayOutputStream postSign(SignMapper signMapper) {
        PdfPKCS7 sgn = signMapper.getSgn();
        PdfSignatureAppearance appearance = signMapper.getAppearance();
        sgn.setExternalDigest(signMapper.getSignHash(), null, "RSA");

        Collection<byte[]> crlBytes = null;
        TSAClientBouncyCastle tsaClient = new TSAClientBouncyCastle("http://timestamp.gdca.com.cn/tsa", null, null);
        byte[] encodedSig = sgn.getEncodedPKCS7(signMapper.getHash(), tsaClient, signMapper.getOcsp(), crlBytes, MakeSignature.CryptoStandard.CMS);
        byte[] paddedSig = new byte[8192];
        System.arraycopy(encodedSig, 0, paddedSig, 0, encodedSig.length);
        PdfDictionary dic2 = new PdfDictionary();
        dic2.put(PdfName.CONTENTS, new PdfString(paddedSig).setHexWriting(true));

        try {
            appearance.close(dic2);
        } catch (DocumentException | IOException e) {
            System.out.println(e.getMessage());
        }
        return signMapper.getBaos();
    }

    private static PDRectangle getPageDimension(PDDocument document, int pageIndex) {
        List<?> pages = document.getDocumentCatalog().getAllPages();
        PDPage lastPage = (PDPage) pages.get(pageIndex - 1); // Cause 'pageIndex' is 1 based.
        return lastPage.findMediaBox();
    }

    public static String processTextTemplate(String textTemplate, Calendar date) {
        String processedTextTemplate = textTemplate;
//        processedTextTemplate = processedTextTemplate.replaceAll("<SIGNER>", signerCertificate);
        processedTextTemplate = processedTextTemplate.replaceAll("<DATE>", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(date.getTime()));
        processedTextTemplate = processedTextTemplate.replaceAll("<BR>", System.lineSeparator());
        //endregion
        return processedTextTemplate;
    }

    public static String getCNFromDN(String dn) throws InvalidNameException {
        LdapName ln = new LdapName(dn);

        for(Rdn rdn : ln.getRdns()) {
            if(rdn.getType().equalsIgnoreCase("CN")) {
                return rdn.getValue().toString();
            }
        }
        return null;
    }
    public static File getPublicCert(){
        try {
            String certificadoPublico = System.getenv("RUTA_CERTIFICADO_PUBLICO");
            URL url = new URL(certificadoPublico);
            URLConnection urlCon = url.openConnection();
            System.out.println(urlCon.getContentType());
            InputStream is = urlCon.getInputStream();
            File tempFile = File.createTempFile("public-cert", ".cer");
            FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
            byte[] array = new byte[1000]; // buffer temporal de lectura.
            int leido = is.read(array);
            while (leido > 0) {
                fos.write(array, 0, leido);
                leido = is.read(array);
            }
            is.close();
            fos.close();
            return  tempFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
