package om.metamorph.abrarnd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import android.support.v7.app.AppCompatActivity;

import om.metamorph.abrarnd.model.User;


public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";

    private TextView usernameTextView;
    private TextView ownerTextView;
    private TextView emailTextView;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ClientConfiguration clientConfiguration = new ClientConfiguration();

        String poolId = "eu-central-1_Wuyb5USN9";
        String clientId = "1csu04tegvmj4pbqucvqiiml5v";

        // Create a CognitoUserPool object to refer to your user pool
        String clientSecret = "1qa4a23ph9lef7f0torhkso6ib02pid15a6s6kd9vtvioeegm5im";
        CognitoUserPool userPool = new CognitoUserPool(SecondActivity.this, poolId, clientId, clientSecret,
                clientConfiguration, Regions.EU_CENTRAL_1);
        final CognitoUser user = userPool.getCurrentUser();
        Log.i(TAG,"yellow" );

        //Credentials
        final CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "eu-central-1:b9aca23c-0edb-486d-a620-d4caa2b97d10",
                Regions.EU_CENTRAL_1
        );

        //credentialsProvider.refresh();
        Runnable runnable = new Runnable() {
            public void run() {
                credentialsProvider.refresh();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        //DynamoDbStuff
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        Button button = (Button) findViewById(R.id.btn_get_data);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //retrieve user details
                Log.i(TAG,user.getUserId());
                Runnable runnable = new Runnable() {
                    public void run() {
                        User userDetails = mapper.load(User.class, user.getUserId());
                        usernameTextView = (TextView) findViewById(R.id.tv_username);
                        usernameTextView.setText(userDetails.getUsername());

                        ownerTextView = (TextView) findViewById(R.id.tv_is_owner);
                        emailTextView = (TextView) findViewById(R.id.tv_email);
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();
            }
        });

        logoutButton = (Button) findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.signOut();
            }
        });
    }
}
