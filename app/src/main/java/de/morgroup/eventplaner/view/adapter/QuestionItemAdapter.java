package de.morgroup.eventplaner.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.morgroup.eventplaner.R;

public class QuestionItemAdapter extends RecyclerView.Adapter {

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference votingDB;

    private ListenerRegistration listenerRegistration;

    private Context context;
    private List options;
    private ArrayList<ArrayList<String>> votes;

    private static boolean flag = true;

    class OptionsViewHolder extends RecyclerView.ViewHolder {

        SeekBar seekBar;
        TextView option, percent;

        public OptionsViewHolder(View view) {
            super(view);
            seekBar = view.findViewById(R.id.seekbar);
            option = view.findViewById(R.id.option);
            percent = view.findViewById(R.id.percent);
        }


        @RequiresApi(api = Build.VERSION_CODES.N)
        void bindData(int position) {
            option.setText(options.get(position).toString());
            listenerRegistration = votingDB.collection("votes")
                    .addSnapshotListener((documents, e) -> {
                        double total = 0;
                        int count = 0;
                        for (QueryDocumentSnapshot document : documents) {
                            List<String> usersTotal = (List<String>) document.get("users");
                            total += usersTotal.size();
                            if (document.getId().equals(String.valueOf(position))) {
                                List<String> users = (List<String>) document.get("users");
                                count = users.size();
                            }
                        }
                        System.out.println("TOTAL:" + total);
                        System.out.println("COUNT:" + count);
                        double per = (count / total) * 100;
                        System.out.println("PERCENT:" + per);
                        if (total != 0) {
                            percent.setText(String.format("%.0f%%", per));
                        }
                        seekBar.setProgress((int) Math.round(per));
                    });

            seekBar.setOnTouchListener((v, event) -> true);

            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: Dokument name zu UID und im Dokument auswahl abspeichern (PROBLEM: Zwei Auswahlmöglichkeiten)
                    if (flag) {
                        flag = false;
                        votingDB.collection("votes").whereArrayContains("users", firebaseUser.getUid())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                if (!document.getId().equals(position)) {
                                                    // delete old vote
                                                    DocumentReference docRef = votingDB.collection("votes").document(document.getId());
                                                    Map<String, Object> updates = new HashMap<>();
                                                    updates.put("users", FieldValue.arrayRemove(firebaseUser.getUid()));
                                                    docRef.update(updates);
                                                }
                                            }
                                            // set new vote
                                            DocumentReference ref = votingDB.collection("votes").document(String.valueOf(position));
                                            Map<String, Object> update = new HashMap<>();
                                            update.put("users", FieldValue.arrayUnion(firebaseUser.getUid()));
                                            ref.set(update, SetOptions.merge());
                                        }
                                        flag = true;
                                    }
                                });
                    }
                }
            });

        }
    }

    public QuestionItemAdapter(Context context, List options, DocumentReference votingDB) {
        this.context = context;
        this.options = options;
        this.votingDB = votingDB;
    }

    @NotNull
    @Override
    public OptionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OptionsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.option_list_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OptionsViewHolder) {
            ((OptionsViewHolder) holder).bindData(position);
        }
    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}
