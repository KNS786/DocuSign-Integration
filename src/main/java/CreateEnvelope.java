import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.*;
import com.migcomponents.migbase64.Base64;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateEnvelope {

    public static String UserName="navani@beezlabs.com";
    public static String IntegrationKey="d74f3284-c6af-4c1a-ad7f-866d7cd41131";
    public static String UserId="76c8d55e-63fd-4c55-9397-e31f5195328b";
    public static String AccountId="e2e18ae4-c799-4b91-9b06-837f47d1f36b";
    public static String BaseUrl="https://demo.docusign.net";
    public static String AccessToken;

    public byte[] GetFileBase64Format() {
        byte[] fileBytes=null;
     //  String pathToDocument="/new_document.pdf";
        String pathToDocument="/FY21 Annual Regional Representation Letter (5).doc";
        try {
            String currentDir = System.getProperty("user.dir");
            Path path = Paths.get(currentDir + pathToDocument);
            fileBytes = Files.readAllBytes(path);

        } catch (IOException exp) {
            System.out.println(exp);
        }
        return fileBytes;
    }

    //Private rsa key
    public  byte[] rsaPrivateKey(){
        byte[] fileBytes=null;
        String PrivateKeyFilePath="/src/certs/private.pem";
        try{
            String CurrentDir=System.getProperty("user.dir");
            Path path=Paths.get(CurrentDir+PrivateKeyFilePath);
            fileBytes=Files.readAllBytes(path);

        }catch(IOException exp){
            System.out.println(exp);
        }
        return fileBytes;

    }


    //generate  JWT Token respect with user given credentials
    public  void JWTLogin(){
        System.out.println("JWT Generating");
        ApiClient apiclient=new ApiClient(BaseUrl);
        try {
            List<String> scopes = new ArrayList<>();
            scopes.add(OAuth.Scope_SIGNATURE);
            scopes.add(OAuth.Scope_IMPERSONATION);
            OAuth.OAuthToken oAuthToken = apiclient.requestJWTUserToken(IntegrationKey, UserId, scopes, rsaPrivateKey(), 3600);
            apiclient.setAccessToken(oAuthToken.getAccessToken(),oAuthToken.getExpiresIn());
            OAuth.UserInfo userInfo=apiclient.getUserInfo(oAuthToken.getAccessToken());
            System.out.println("Private key : "+rsaPrivateKey().toString());
            System.out.println("Access Token : "+oAuthToken.getAccessToken().toString());
            System.out.println("UserInfo : "+userInfo);
            apiclient.setBasePath(userInfo.getAccounts().get(0).getBaseUri()+"/restapi");
            Configuration.setDefaultApiClient(apiclient);

        }catch (IOException | ApiException e) {
            e.printStackTrace();
        }

    }

    //Create Envelope
    public void RequestSignEnvelope(){
        System.out.println("Request a signature ");
        EnvelopeDefinition envDef=new EnvelopeDefinition();
        envDef.setEmailSubject("Test Document pdf ");
        envDef.setEmailBlurb("Hello, Please sign my Java SDK Envelope.");


        //Add a Document to the Envelope
        Document doc=new Document();
        String base64Doc=Base64.encodeToString(GetFileBase64Format(),false);
        System.out.println("Base64Doc : "+base64Doc);
        doc.setDocumentBase64(base64Doc);
        doc.setName("new_document");
        doc.setDocumentId("12345");
        doc.setFileExtension("doc");


        List<Document> docs=new ArrayList<>();
        docs.add(doc);

        //Add a recipient to sign the document
        Signer signer=new Signer();
        signer.setEmail("navaninavani306@gmail.com");
        signer.setName("Test Document docx");
        signer.setRecipientId("1");
        signer.setRoutingOrder("1");

        Signer signer2=new Signer();
        signer2.setEmail("navaninavani786@gmail.com");
        signer2.setName("Test Document pdf ");
        signer2.setRecipientId("3");
        signer2.setRoutingOrder("3");

        Signer Signer1=new Signer();
         Signer1.setEmail("navani@beezlabs.com");
         Signer1.setName("Test Document docx");
         Signer1.setRecipientId("2");
         Signer1.setRoutingOrder("2");

        //Create a signhere tab somewhare on the document for signer to sign
        SignHere signHere=new SignHere();
      /*  signHere.setDocumentId("12345");
        signHere.setPageNumber("1");
        signHere.setRecipientId("1");
        signHere.setXPosition("100");
        signHere.setYPosition("100");
        signHere.setScaleValue("0.5");*/

        List<SignHere> signHereTabs = new ArrayList<>();
        signHereTabs.add(signHere);
        Tabs tabs = new Tabs();
        tabs.setSignHereTabs(signHereTabs);
        signer.setTabs(tabs);

        envDef.setRecipients(new Recipients());
        envDef.getRecipients().setSigners(new ArrayList<>());
        envDef.getRecipients().getSigners().add(signer);
       envDef.getRecipients().getSigners().add(Signer1);
        envDef.getRecipients().getSigners().add(signer2);
        envDef.setDocuments(docs);
        // envDef.documentBase64();

        envDef.setStatus("sent");
        try{
            EnvelopesApi envelopesApi=new EnvelopesApi();
            EnvelopeSummary envelopeSummary=envelopesApi.createEnvelope(AccountId,envDef);
            System.out.println("EnvelopeSummery : "+envelopeSummary);
        }catch(Exception exp){
            System.out.println("Exception : "+exp);
        }


    }
    public static void main(String[] args){
        CreateEnvelope Envelope=new CreateEnvelope();
        byte[] GetFile=Envelope.GetFileBase64Format();
        byte[] GetPrivateKey=Envelope.rsaPrivateKey();

        if(GetFile.length > 0  && GetPrivateKey.length > 0 ) {
            Envelope.JWTLogin();
            Envelope.RequestSignEnvelope();
        }
        else{
            System.out.println("Please give Private key File and Document to send");

        }

    }


}

