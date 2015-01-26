package com.example.week3;

import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;



public class Fragment4 extends Fragment {
	private View view;
	private Facebook facebook;
	private String APP_ID = "870973169590719";
	private Button postImageBtn;
	private Button updateStatusBtn;

	private TextView userName;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static String message = "Sample status posted from android app";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment4, container, false);

		uiHelper = new UiLifecycleHelper(getActivity(), statusCallback);
		uiHelper.onCreate(savedInstanceState);
		
		
		userName = (TextView) view.findViewById(R.id.user_name);
		LoginButton loginButton = (LoginButton) view
				.findViewById(R.id.authButton);
		loginButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
			@Override
			public void onUserInfoFetched(GraphUser user) {
				if (user != null) {
					userName.setText("Hello, " + user.getName());
				} else {
					userName.setText("You are not logged");
				}
			}
		});
		loginButton.setReadPermissions("user_friends");
		loginButton.setFragment(this);
		
		postImageBtn = (Button) view.findViewById(R.id.post_image);
		postImageBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				postImage();
			}
		});
		updateStatusBtn = (Button) view.findViewById(R.id.update_status);
		updateStatusBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		buttonsEnabled(false);
		return view;
	}
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			if (state.isOpened()) {
				buttonsEnabled(true);
				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};

	public void buttonsEnabled(boolean isEnabled) {
		postImageBtn.setEnabled(isEnabled);
		updateStatusBtn.setEnabled(isEnabled);
	}
	public void postImage() {
		if (checkPermissions()) {
			Bitmap img = BitmapFactory.decodeResource(getResources(),
					R.drawable.ic_launcher);
			Request uploadRequest = Request.newUploadPhotoRequest(
					Session.getActiveSession(), img, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Toast.makeText(getActivity(),
									"Photo uploaded successfully",
									Toast.LENGTH_LONG).show();
						}
					});
			uploadRequest.executeAsync();
		} else {
			requestPermissions();
		}
	}

	public void postStatusMessage() {
		if (checkPermissions()) {
			Request request = Request.newStatusUpdateRequest(
					Session.getActiveSession(), message,
					new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							if (response.getError() == null)
								Toast.makeText(getActivity(),
										"Status updated successfully",
										Toast.LENGTH_LONG).show();
						}
					});
			request.executeAsync();
		} else {
			requestPermissions();
		}
	}

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}
	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null)
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(
					this, PERMISSIONS));
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		buttonsEnabled(Session.getActiveSession().isOpened());
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}

}