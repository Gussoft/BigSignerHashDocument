package pe.bigprime;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.DocumentException;
import utils.*;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BigSignerHashMethodHandler implements RequestHandler<List<BigSignerHashRequest>, BigSignerHashResponse> {

    public static final String AWS_ACCESS_KEY = System.getenv("AWS_ACCESS_KEY");
    public static final String AWS_SECRET_KEY = System.getenv("AWS_SECRET_KEY");
    public static final String AWS_BUCKET_IN = System.getenv("AWS_BUCKET_IN");
    public static final String AWS_BUCKET_OUT = System.getenv("AWS_BUCKET_OUT");

    @Override
    public BigSignerHashResponse handleRequest(List<BigSignerHashRequest> event, Context context) {

        event.forEach(bigSignerHashRequest -> {
            //s3 el archivo se obtiene desde bigSignerHashRequest.path
            try {
                File fileForSign = S3Util.getFile(bigSignerHashRequest.getPath(),"-tempfile","pdf");
                SignMapper signMapper = new SignMapper();
                fillSignMapperObject(bigSignerHashRequest, signMapper);
                signMapper = SignatureUtil.preSign(signMapper, fileForSign);

                String base64Hash = new String(Base64.getEncoder().encode(signMapper.getHash()), StandardCharsets.UTF_8);
                //Preparar Hash para la firma -> go server
                ArrayList<HashJson> hashJsons = new ArrayList<>();
                hashJsons.add(new HashJson(new String(Base64.getEncoder().encode(signMapper.getSignHash()), StandardCharsets.UTF_8)));
                // firmar digitalmente hash
                Gson json = new Gson();
                String archivo = enviarHashApiBigSigner(json.toJson(hashJsons));
                //del texto cambiar <DATE> por la hora actual (new Date())

                byte[] codigo = Base64.getDecoder().decode(archivo);
                signMapper.setSignHash(codigo);

                //File tempFile = File.createTempFile(document.getDocumentName(),".pdf");
                ByteArrayOutputStream baos = SignatureUtil.postSign(signMapper);
                FileUtils.writeByteArrayToFile(fileForSign, baos.toByteArray());

                //Subir archivo a S3
                utils.S3Util.uploadFile(bigSignerHashRequest.getNombre(),fileForSign,false);

                //DynamoBD
                String salida = AWS_BUCKET_OUT + "/" +bigSignerHashRequest.getNombre();
                DynamoUtil dyno = new DynamoUtil();
                dyno.getDynamo(bigSignerHashRequest.getNombre(), salida, base64Hash, archivo);
                System.out.println("Finalizado");
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    private void fillSignMapperObject(BigSignerHashRequest bigSignerHashRequest, SignMapper signMapper) {
        signMapper.setVisX(String.valueOf(bigSignerHashRequest.getVisX()));
        signMapper.setVisY(String.valueOf(bigSignerHashRequest.getVisY()));
        signMapper.setVisP(String.valueOf(bigSignerHashRequest.getVisP()));
        signMapper.setVisI(String.valueOf(bigSignerHashRequest.getVisI()));
        signMapper.setVistexto(bigSignerHashRequest.getTexto());
        signMapper.setVisH(String.valueOf(bigSignerHashRequest.getVisH()));
        signMapper.setVisW(String.valueOf(bigSignerHashRequest.getVisW()));
    }

    public static String enviarHashApiBigSigner(String hash) throws IOException {
        try {
            URL url = new URL(System.getenv("BIGSIGNER_SIGN_HASH"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(hash.getBytes(StandardCharsets.UTF_8));
            os.flush();

            int statusCode = conn.getResponseCode();

            String responseFromServer = "";

            if (statusCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream()), StandardCharsets.UTF_8));

                String output;
                while ((output = br.readLine()) != null) {
                    responseFromServer = output;
                }
                conn.disconnect();

                responseFromServer = responseFromServer.replace("[","").replace("]","");
                JsonObject signhash = new JsonParser().parse(responseFromServer).getAsJsonObject();
                String signedHash = signhash.get("signedHash").getAsString();

                return signedHash;
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
