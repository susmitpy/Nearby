package e.raju.nearby;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView status;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mdcref, near;
    private String Location;
    private GeoPoint loc;
    private String nm;
    int flag1;
    Task<QuerySnapshot> a;
    String b;
    String lat1;
    String long1;
    String latCurr, longCurr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        status = (TextView) findViewById(R.id.info3);
        toggle();
    }

    private void toggle() {
        Switch sw = (Switch) findViewById(R.id.sw);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            int flag = -1;

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flag = flag * -1;
                if (flag == 1) {
                    get_loc();
                    Exists();
                } else if (flag == -1) {
                    off();
                }
            }
        });


    }

    private void main() {
        status.append("Upload");
        status.append("\n" + lat1);
        status.append("\n" + long1);
        Location = "Location";
        Double lat2, long2;
        lat2 = Double.parseDouble(latCurr);
        long2 = Double.parseDouble(longCurr);
        loc = new GeoPoint(lat2, long2);
        Map<String, Object> usermap = new HashMap<String, Object>();
        usermap.put(Location, loc);
        mdcref = db.document("Users/" + b);
        mdcref.update(usermap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                status.append("\nSuccess");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                status.append("\nFailed");
            }
        });

    }

    private void off() {
        status.setText("OFF");

    }

    private void get_doc_id() {

        db.collection("Users")
                .whereEqualTo("Name", nm)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                b = document.getId();
                                get_near();
                                break;
                            }
                        }
                    }
                });


    }

    private void get_near() {
        near = db.document("Users/" + b);
        near.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String IAmNearTo = documentSnapshot.getString("Near");
                String[] nears = IAmNearTo.split(",");
                int l = nears.length;
                status.setText("\n");
                for (int i = 0; i <= l; i++) status.append(nears[i] + "\n");
                check();
            }
        });
    }

    private void check() {
        while (true) {
            SystemClock.sleep(5000);
            if (lat1 != null) {
                main();
                break;
            }
        }
    }

    public void get_loc() {
        SingleShotLocationProvider.requestSingleUpdate(MainActivity.this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                         latCurr = String.valueOf(location.latitude);
                         longCurr = String.valueOf(location.longitude);
                         finish();
                    }
                });
       }



    public void Exists() {
        CollectionReference citiesRef = db.collection("Users");
        nm = ((EditText) findViewById(R.id.nm)).getText().toString().toUpperCase();
        // Create a query against the collection.
        Query query = citiesRef.whereEqualTo("Name", nm);

        query.addSnapshotListener((documentSnapshots, e) -> {

            QuerySnapshot ds = documentSnapshots;
                if (ds == null) {
                    status.append("User Not in Database");
                } else {
                    get_doc_id();
                }

        });
    }
}

