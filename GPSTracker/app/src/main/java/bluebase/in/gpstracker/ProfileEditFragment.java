package bluebase.in.gpstracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileEditFragment extends Fragment {
    Context context;
    ProgressDialog progressDialog;
    JsonObject jsonObject;

    EditText name;
    EditText userName;
    EditText email;
    EditText empId;
    EditText mobileNo;
    EditText profile;

    String originalUserName;

    String urlGetProfile = CommonUtils.IP + "/GPSTracker/gps_tracker_android/getProfileDetails.php";
    String urlEditProfile = CommonUtils.IP + "/GPSTracker/gps_tracker_android/editProfileDetails.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.5);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        name = view.findViewById(R.id.name);
        userName = view.findViewById(R.id.userName);
        email = view.findViewById(R.id.email);
        empId = view.findViewById(R.id.empId);
        mobileNo = view.findViewById(R.id.mobileNo);
        profile = view.findViewById(R.id.profile);

        Button update = view.findViewById(R.id.update);

        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(!name.getText().toString().equals("")) {
                    if(!userName.getText().toString().equals("")) {
                        if(!email.getText().toString().equals("")) {
                            if(CommonUtils.emailValidator(email.getText().toString())) {
                                if (!empId.getText().toString().equals("")) {
                                    if (!mobileNo.getText().toString().equals("")) {
                                        if (!profile.getText().toString().equals("")) {
                                            progressDialog = new ProgressDialog(getContext());
                                            progressDialog.setCancelable(false);
                                            progressDialog.setMessage("Loading...");
                                            progressDialog.show();

                                            jsonObject = new JsonObject();
                                            jsonObject.addProperty("name", name.getText().toString());
                                            jsonObject.addProperty("userName", userName.getText().toString());
                                            jsonObject.addProperty("originalUserName", originalUserName);
                                            jsonObject.addProperty("email", email.getText().toString());
                                            jsonObject.addProperty("employeeId", empId.getText().toString());
                                            jsonObject.addProperty("mobileNo", mobileNo.getText().toString());
                                            jsonObject.addProperty("profile", profile.getText().toString());

                                            PostEditProfile postEditProfile = new PostEditProfile(context);
                                            postEditProfile.checkServerAvailability(2);
                                        } else {
                                            Toast.makeText(context, "Enter Profile", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Enter Mobile No.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Enter Employee Id", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(context, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(context, "Enter UserName", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        progressDialog = new ProgressDialog(getContext());
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
        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    name.setText(jsonObject.getString("name"));
                    originalUserName = jsonObject.getString("userName");
                    userName.setText(jsonObject.getString("userName"));
                    email.setText(jsonObject.getString("email"));
                    empId.setText(jsonObject.getString("empId"));
                    mobileNo.setText(jsonObject.getString("mobileNo"));
                    profile.setText(jsonObject.getString("profile"));
                }else{
                    Toast.makeText(context, "Data Fetch Error", Toast.LENGTH_SHORT).show();
                }

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private class PostEditProfile extends PostRequest{
        public PostEditProfile(Context context){
            super(context);
        }

        @Override
        public void serverAvailability(boolean isServerAvailable){
            if(isServerAvailable){
                super.postRequest(urlEditProfile, jsonObject);
            }else{
                Toast.makeText(context, "Connection to the server \nnot Available", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }

        @Override
        public void onFinish(JSONArray jsonArray){
            progressDialog.dismiss();

            try{
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);

                if(jsonObject.getBoolean("status")){
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    CommonUtils.userName = userName.getText().toString();
                    getActivity().getSupportFragmentManager().popBackStack();
                }else {
                    Toast.makeText(context, "Update unsuccessful", Toast.LENGTH_SHORT).show();
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}