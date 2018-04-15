package com.assignment.nanit.nanitassignment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Calendar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.assignment.nanit.nanitassignment.ImageHandler.getPickImageChooserIntent;


public class DetailsActivity extends AppCompatActivity {

	private static final String TAG = "DetailsActivity";
	static final String PREF_NAME_KEY = "PREF_NAME_KEY";
	static final String PREF_BIRTHDAY_KEY = "PREF_BIRTHDAY_KEY";
	static final String EXTRA_NAME = "EXTRA_NAME";
	static final String EXTRA_BIRTHDAY = "EXTRA_BIRTHDAY";
	static final String EXTRA_PICTURE_FILE_PATH = "EXTRA_PICTURE_FILE_PATH";
	static final int CAMERA_REQUEST_CODE = 1000;
	static final int BIRTHDAY_REQUEST_CODE = 1001;

	@BindView(R.id.name_edittext)
	EditText name;

	@BindView(R.id.birthday_edittext)
	EditText birthday;

	@BindView(R.id.picture)
	ImageView picture;

	@BindView(R.id.show_birthday_screen_btn)
	Button showBirthdayScreenBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		ButterKnife.bind(this);
		setUi();

		// TODO: 4/5/2018 check for code correctness
	}

	@OnClick(R.id.picture_btn)
	public void onPictureButtonClick() {
		Log.d(TAG, "onPictureButtonClick: ");
		startActivityForResult(getPickImageChooserIntent(this), CAMERA_REQUEST_CODE);
	}

	@OnClick(R.id.show_birthday_screen_btn)
	public void onShowBirthdayScreenButtonClick() {
		Log.d(TAG, "onShowBirthdayScreenButtonClick: ");
		Intent intent = new Intent(this, BirthdayActivity.class);
		intent.putExtra(EXTRA_NAME, name.getText().toString());
		intent.putExtra(EXTRA_BIRTHDAY, birthday.getText().toString());
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		String pictureFilePath = sharedPref.getString(ImageHandler.PREF_PICTURE_KEY, null);
		if (pictureFilePath != null) {
			intent.putExtra(EXTRA_PICTURE_FILE_PATH, pictureFilePath);
		}
		startActivityForResult(intent, BIRTHDAY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == CAMERA_REQUEST_CODE) {
				ImageHandler.handleImage(this, data, picture, false);
			} else if (requestCode == BIRTHDAY_REQUEST_CODE) {
				SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
				setPicture(sharedPref);
			}
		}
	}

	private void setUi() {
		SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
		final String nameFromPref = sharedPref.getString(PREF_NAME_KEY, null);
		name.setText(nameFromPref);
		final String birthdayFromPref = sharedPref.getString(PREF_BIRTHDAY_KEY, null);
		birthday.setText(birthdayFromPref);
		birthday.setText(birthdayFromPref);
		setNameTextChangesHandler();
		setBirthdayOnClickHandler();
		setPicture(sharedPref);
		setShowBirthdayScreenButton();

	}

	private void setPicture(SharedPreferences sharedPref) {
		String pictureFilePath = sharedPref.getString(ImageHandler.PREF_PICTURE_KEY, null);
		ImageHandler.setPictureFromStorageIfExist(this, picture, pictureFilePath);
	}

	private void setNameTextChangesHandler() {
		// Both not working for all cases
		//name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
		//name.setOnEditorActionListener(new EditText.OnEditorActionListener()

		name.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				// I can also validate the text if we want to.
				// I can do it more sophisticated and performance wise if i will wait some X amount of time and only
				// if the user didn't enter another letter then save it.
				SharedPreferenceHandler.saveToSharedPreferences(DetailsActivity.this,
						PREF_NAME_KEY, s.toString());
				setShowBirthdayScreenButton();
			}
		});
	}

	private void setShowBirthdayScreenButton() {
		// Can make it a one line code but this is more readable.
		if (!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(birthday.getText())) {
			showBirthdayScreenBtn.setEnabled(true);
		} else {
			showBirthdayScreenBtn.setEnabled(false);
		}
	}

	private void setBirthdayOnClickHandler() {
		birthday.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePickerDialog.OnDateSetListener datePickerListener = listenToDatePicker();
				Calendar date = Calendar.getInstance();
				DatePickerDialog datePickerDialog = new DatePickerDialog(DetailsActivity.this, datePickerListener,
						date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
				datePickerDialog.show();
			}

			@NonNull
			private DatePickerDialog.OnDateSetListener listenToDatePicker() {
				return new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

						int month = monthOfYear + 1;
						String dateString = dayOfMonth + "/" + month + "/" + year;
						birthday.setText(dateString);
						SharedPreferenceHandler.saveToSharedPreferences(DetailsActivity.this,
								PREF_BIRTHDAY_KEY, dateString);
						setShowBirthdayScreenButton();
					}
				};
			}
		});
	}

}
