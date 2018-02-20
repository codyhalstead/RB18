package com.rentbud.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cody.rentbud.R;
import com.rentbud.activities.MainActivity;
import com.rentbud.model.User;

/**
 * Created by Cody on 1/5/2018.
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    ImageView profilePic;
    TextView usernameTV, emailbox, passbox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_initial, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        super.onCreate(savedInstanceState);
        this.usernameTV = view.findViewById(R.id.usernameTextView);
        this.passbox = view.findViewById(R.id.password);
        this.emailbox = view.findViewById(R.id.email);
        this.profilePic = view.findViewById(R.id.homeProfilePic);
        getActivity().setTitle("Home");
        setTextBoxes(MainActivity.user.getName(), MainActivity.user.getEmail(), MainActivity.user.getPassword());
        //If user has a profile pic, set that pic
        if (MainActivity.user.getProfilePic() != null && !MainActivity.user.getProfilePic().isEmpty()) {
            profilePic.setImageURI(Uri.parse(MainActivity.user.getProfilePic()));
        }
    }

    public void setTextBoxes(String name, String email, String password) {
        //Sets user info to text boxes, TEMPORARY
        this.usernameTV.setText(name);
        this.emailbox.setText(email);
        this.passbox.setText(password);
    }
}
