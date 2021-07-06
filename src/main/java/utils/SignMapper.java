package utils;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import java.io.ByteArrayOutputStream;

public class SignMapper {

    private byte[] hash;
    private byte[] signHash;
    private byte[] ocsp;
    private PdfPKCS7 sgn;
    private PdfSignatureAppearance appearance;
    private ByteArrayOutputStream baos;
    private float visX;
    private float visY;
    private String vistexto;
    private float visH;
    private float visW;
    private String visI;
    private int visP;
    private String pathIn;


    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    public void setBaos(ByteArrayOutputStream baos) {
        this.baos = baos;
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getOcsp() {
        return ocsp;
    }

    public void setOcsp(byte[] ocsp) {
        this.ocsp = ocsp;
    }

    public PdfPKCS7 getSgn() {
        return sgn;
    }

    public void setSgn(PdfPKCS7 sgn) {
        this.sgn = sgn;
    }

    public PdfSignatureAppearance getAppearance() {
        return appearance;
    }

    public void setAppearance(PdfSignatureAppearance appearance) {
        this.appearance = appearance;
    }

    public byte[] getSignHash() {
        return signHash;
    }

    public void setSignHash(byte[] signHash) {
        this.signHash = signHash;
    }

    public float getVisX() {
        return visX;
    }

    public void setVisX(String visX) {
        this.visX = Float.parseFloat(visX);
    }

    public float getVisY() {
        return visY;
    }

    public void setVisY(String visY) {
        this.visY = Float.parseFloat(visY);
    }

    public float getVisH() {
        return visH;
    }

    public void setVisH(String visH) {
        this.visH = Float.parseFloat(visH);
    }

    public float getVisW() {
        return visW;
    }

    public void setVisW(String visW) {
        this.visW = Float.parseFloat(visW);
    }

    public String getVisI() {
        return visI;
    }

    public void setVisI(String visI) {
        this.visI = visI;
    }

    public int getVisP() {
        return visP;
    }

    public void setVisP(String visP) {
        this.visP = Integer.parseInt(visP);
    }

    public String getPathIn() {
        return pathIn;
    }

    public void setPathIn(String pathIn) {
        this.pathIn = pathIn;
    }

    public String getVistexto() {
        return vistexto;
    }

    public void setVistexto(String vistexto) {
        this.vistexto = vistexto;
    }

}
