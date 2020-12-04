package bluebase.in.gpstracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {
    Context context;
    ProgressDialog progressDialog;
    JsonObject jsonObject;

    TextView name;
    TextView userName;
    TextView email;
    TextView empId;
    TextView mobileNo;
    TextView profile;

    String urlGetProfile = CommonUtils.IP + "/GPSTracker/gps_tracker_android/getProfileDetails.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.5);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        name = view.findViewById(R.id.nameValue);
        userName = view.findViewById(R.id.userNameValue);
        email = view.findViewById(R.id.emailValue);
        empId = view.findViewById(R.id.empIdValue);
        mobileNo = view.findViewById(R.id.mobileNoValue);
        profile = view.findViewById(R.id.profileValue);

        ImageView editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .addToBackStack("profileEditFragment")
                        .replace(R.id.fragment_container, new ProfileEditFragment(), "profileEditFragment")
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        jsonObject = new JsonObject();
        jsonObject.addProperty("userName", CommonUtils.userName);

        PostProfile postProfile = new PostProfile(context);
        postProfile.checkServerAvailability(2);
    }

    private class PostProfile extends PostRequest{
        public PostProfile(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlGetProfile, jsonObject);
            }else {
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray) {
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    name.setText(jsonObject.getString("name"));
                    userName.setText(jsonObject.getString("userName"));
                    email.setText(jsonObject.getString("email"));
                    empId.setText(jsonObject.getString("empId"));
                    mobileNo.setText(jsonObject.getString("mobileNo"));
                    profile.setText(jsonObject.getString("profile"));
                }else{
                    Toast.makeText(context, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
