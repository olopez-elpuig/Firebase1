package com.example.firebase1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firebase1.databinding.FragmentChatBinding;
import com.example.firebase1.databinding.FragmentSigninBinding;
import com.example.firebase1.databinding.ViewholderChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private FirebaseFirestore mDb;
    private ArrayList<Mensaje> mensajes = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = FirebaseFirestore.getInstance();

        binding.enviar.setOnClickListener(v ->{
            String mensaje = binding.mensaje.getText().toString();
            String autor = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String fecha = LocalDateTime.now().toString();


            mDb.collection("mensajes")
                    .add(new Mensaje(mensaje, autor, fecha));
        });

        mDb.collection("mensajes").addSnapshotListener((value, error) -> {
            mensajes = new ArrayList<>();

            for (QueryDocumentSnapshot d: value){
                mensajes.add(new Mensaje(d.getString("mensaje"), d.getString("autor"), d.getString("fecha")));
            }
        });
        ChatAdapter chatAdapter = new ChatAdapter();
        binding.recyclerView.setAdapter(chatAdapter);
    }

    class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder>{

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChatViewHolder(ViewholderChatBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

            Mensaje mensaje = mensajes.get(position);

            holder.binding.autor.setText(mensaje.autor);
            holder.binding.mensaje.setText(mensaje.mensaje);
            holder.binding.fecha.setText(mensaje.fecha);
        }

        @Override
        public int getItemCount() {
            return mensajes.size();
        }
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder{

        ViewholderChatBinding binding;

        public ChatViewHolder(@NonNull ViewholderChatBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}