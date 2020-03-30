package td.pokladna2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirestoreDatabase {

    private static FirestoreDatabase instance;

    public static FirebaseFirestore FIRESTORE;

    public FirestoreDatabase() {

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();

        FIRESTORE = FirebaseFirestore.getInstance();
        FIRESTORE.setFirestoreSettings(settings);

    }

    public static FirestoreDatabase getInstance(){

        if (instance == null){
            instance = new FirestoreDatabase();
        }

        return instance;
    }

}
