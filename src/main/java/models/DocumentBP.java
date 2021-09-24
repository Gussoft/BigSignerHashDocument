package models;

public class DocumentBP {

    public DocumentBP() {
    }

    public DocumentBP(String documentName, String pathUnsigned, Boolean statusSign, String hashDocument) {
        this.documentName = documentName;
        this.pathUnsigned = pathUnsigned;
        this.statusSign = statusSign;
        this.hashDocument = hashDocument;
    }

    private Long id;
    private String documentName;
    private String pathUnsigned;
    private String pathSigned;
    private Boolean statusSign;
    private String hashDocument;

    public String getPathSigned() {
        return pathSigned;
    }

    public void setPathSigned(String pathSigned) {
        this.pathSigned = pathSigned;
    }

    public Long getId() {
        return id;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getPathUnsigned() {
        return pathUnsigned;
    }

    public Boolean getStatusSign() {
        return statusSign;
    }

    public String getHashDocument() {
        return hashDocument;
    }

}
