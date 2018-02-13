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
    User user;

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

        this.usernameTV = (TextView) view.findViewById(R.id.usernameTextView);
        this.passbox = (TextView) view.findViewById(R.id.password);
        this.emailbox = (TextView) view.findViewById(R.id.email);
        this.profilePic = view.findViewById(R.id.homeProfilePic);
        Bundle bundle = this.getArguments();
        getActivity().setTitle("Home");
      // if (bundle != null) {
            User user = bundle.getParcelable("UserInfo");
            setTextBoxes(user.getName(), user.getEmail(), user.getPassword());
       // }
        if(user.getProfilePic() != null && !user.getProfilePic().isEmpty()){
            profilePic.setImageURI(Uri.parse(user.getProfilePic()));
        }
    }

    public void setTextBoxes(String name, String email, String password){
        this.usernameTV.setText(name);
        this.emailbox.setText(email);
        this.passbox.setText(password);
    }
}
