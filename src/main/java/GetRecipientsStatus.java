import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.Recipients;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GetRecipientsStatus{
    public static String UserName="navani@beezlabs.com";
    public static String IntegrationKey="d74f3284-c6af-4c1a-ad7f-866d7cd41131";
    public static String UserId="76c8d55e-63fd-4c55-9397-e31f5195328b";
    public static String AccountId="e2e18ae4-c799-4b91-9b06-837f47d1f36b";
    public static String BaseUrl="https://demo.docusign.net";
    public static String BasicEnvelopId="ddf77733-d422-4f41-bfbf-f285355cd6e9";

    //Private rsa key
    public  byte[] rsaPrivateKey(){
        byte[] fileBytes=null;
        String PrivateKeyFilePath="/src/certs/private.pem";
        try{
            String CurrentDir=System.getProperty("user.dir");
            Path path= Paths.get(CurrentDir+PrivateKeyFilePath);
            fileBytes= Files.readAllBytes(path);

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

    public void TrackUserSignedStatus() throws ApiException {

        EnvelopesApi ListOfUsers=new EnvelopesApi();
        Recipients GetRecipients=new Recipients();
        GetRecipients=ListOfUsers.listRecipients(AccountId,BasicEnvelopId);
        System.out.println(GetRecipients);

     }

    public static void main(String[] args){
        GetRecipientsStatus ListOfRecipients=new GetRecipientsStatus();
        ListOfRecipients.JWTLogin();
        try {
            ListOfRecipients.TrackUserSignedStatus();
        }catch(Exception e){
            System.out.println(e.toString());
        }


    }



}