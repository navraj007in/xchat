package com.kss.xchat;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfileActivity extends RootActivity {
	String TAG="EditProfileActivity";
	ImageView imgProfile;
	EditText txtUserName;
	EditText txtName;
	EditText txtDOB;
	EditText txtCity;
	EditText txtZip;
	EditText txtState;
	EditText txtCountry;
	RadioButton rdbMale;
	RadioButton rdbFemale;
	Button cmdEdit;
	Button cmdCancel;
	String URL;
	String Gender;
	public static String FullName;
	public static String DOB;
	public static String Email;
	APIResponse aResponse;
	String profileBase64;
	Bitmap profileBitmap;
	UpdateProfileTask mRegisterTask;
	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private AlertDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);
		initView();
	}

	
	private void initView()
	{
		imgProfile=(ImageView)findViewById(R.id.imgProfile);
		txtName=(EditText)findViewById(R.id.txtName);
		txtUserName=(EditText)findViewById(R.id.txtNickName);
		txtDOB=(EditText)findViewById(R.id.txtDOB);
		txtCity=(EditText)findViewById(R.id.txtCity);
		txtZip=(EditText)findViewById(R.id.txtZip);
		txtState=(EditText)findViewById(R.id.txtState);
		txtCountry=(EditText)findViewById(R.id.txtCountry);
		txtDOB.setText(DOB);
		txtName.setText(FullName);
		Gender=Utils.ReadPreference(getApplicationContext(), "Gender");
		txtName.setText(Utils.ReadPreference(getApplicationContext(), "Name"));
		txtUserName.setText(Utils.ReadPreference(getApplicationContext(), "NickName"));
		txtDOB.setText(Utils.ReadPreference(getApplicationContext(), "DOB"));
		txtCity.setText(Utils.ReadPreference(getApplicationContext(), "City"));
		txtState.setText(Utils.ReadPreference(getApplicationContext(), "State"));
		txtCountry.setText(Utils.ReadPreference(getApplicationContext(), "Country"));
		profileBase64=Utils.ReadPreference(getApplicationContext(), "ProfileImage");
		captureImageInitialization();
		if(profileBase64!=null){
			byte[] decodedString = Base64.decode(profileBase64, Base64.DEFAULT);
			Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			Drawable d = new BitmapDrawable(getResources(),decodedByte);
			getActionBar().setIcon(d);
			imgProfile.setImageDrawable(d);
			}
		imgProfile.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				//selectImage();
				dialog.show();
				return false;
			}
		});

		rdbMale=(RadioButton)findViewById(R.id.rdbMale);
		rdbFemale=(RadioButton)findViewById(R.id.rdbFemale);
		try{
		if(Gender.equalsIgnoreCase("Male")) rdbMale.setSelected(true);
		else
			rdbFemale.setSelected(true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		cmdCancel=(Button)findViewById(R.id.cmdCancel);
		cmdCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		cmdEdit=(Button)findViewById(R.id.cmdRegister);
		cmdEdit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(txtUserName.getText().toString().length()==0)
				{
				Toast.makeText(getApplicationContext(), "Nick Name can not be empty", Toast.LENGTH_SHORT).show();	
				return ;
				}
				if(txtDOB.getText().toString().length()==0)
				{
					Toast.makeText(getApplicationContext(), "DOB can not be empty", Toast.LENGTH_SHORT).show();	
					return ;
				
				}
				
				if(checkDate(txtDOB.getText().toString())){
				mRegisterTask = new UpdateProfileTask();
				mRegisterTask.execute((Void) null);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Date format incorrect. Enter MM-dd-yyyy ", Toast.LENGTH_SHORT).show();
				}
				

			}
		});

/*		GetImageTask task = new GetImageTask();
        task.execute(new String[] { LoginActivity.profileImage.getUrl() });
*/	}
	public boolean checkDate(String date)
	{
		SimpleDateFormat fromServer = new SimpleDateFormat("MM-dd-yyyy");
		SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd");

		
		try {

		     displayFormat.format(fromServer.parse(date));

		   // Log.i("Newsfeeddate", reformattedStr+"-"+reformattedTimeStr);
		} catch (ParseException e) {
		    e.printStackTrace();
			return false;
		}
		return true;
	}
	private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }

        });
        builder.show();
    }
	@Override
	public void onMPResume() {
		// TODO Auto-generated method stub
		
	}
	public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
	    Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(mutableBitmap);
	    drawable.setBounds(0, 0, widthPixels, heightPixels);
	    drawable.draw(canvas);

	    return mutableBitmap;
	}
	public class UpdateProfileTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
           try{
              	Drawable d=imgProfile.getDrawable();
               	profileBase64=Utils.BitmapToBase64(convertToBitmap(d, 512, 512));
                   pd=new ProgressDialog(EditProfileActivity.this);

            	pd.setTitle("Updating Profile...");
            	pd.setMessage("Please wait....");
            	pd.setCancelable(false);
            	pd.setIndeterminate(true);
            	pd.show();
            	
            	Gender="";
            	if(rdbMale.isChecked()) Gender="M";
            	else Gender="F";
        	   
           }
           catch(Exception e)
           {
        	e.printStackTrace();   
           }
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String response="";//Utils.RestCall(URL);
//			Log.i(TAG,response);
			 ObjectMapper mapper = new ObjectMapper();

				try {
					List<NameValuePair> nameValuePairs=new ArrayList<NameValuePair>(17);
					nameValuePairs.add(new BasicNameValuePair("AppID", "abc123"));
					nameValuePairs.add(new BasicNameValuePair("Name", txtName.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("NickName",txtUserName.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("ZipCode",txtZip.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("Gender",Gender));
					nameValuePairs.add(new BasicNameValuePair("Category","none"));
					nameValuePairs.add(new BasicNameValuePair("BusinessName","None"));
					nameValuePairs.add(new BasicNameValuePair("Email",Utils.ReadPreference(getApplicationContext(), "Email")));
					nameValuePairs.add(new BasicNameValuePair("password","123"));
					nameValuePairs.add(new BasicNameValuePair("DOB",txtDOB.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("City",txtCity.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("State",txtState.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("Country",txtCountry.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("DeviceID",Utils.ReadPreference(getApplicationContext(), 
							xChatApplication.PROPERTY_REG_ID)));
					nameValuePairs.add(new BasicNameValuePair("IMEI","123"));
					nameValuePairs.add(new BasicNameValuePair("ApiKey","123"));
					nameValuePairs.add(new BasicNameValuePair("Pic",profileBase64));
					nameValuePairs.add(new BasicNameValuePair("AccessToken",Utils.ReadPreference(getApplicationContext(), "AccessToken")));
					nameValuePairs.add(new BasicNameValuePair("Status",Utils.ReadPreference(getApplicationContext(), "keyStatus")));

//					Log.i(TAG, "Token - "+Utils.ReadPreference(getApplicationContext(), "AccessToken"));
	//				Log.i(TAG, "Email - "+Utils.ReadPreference(getApplicationContext(), "Email"));
		//			Log.i(TAG, "Status - "+Utils.ReadPreference(getApplicationContext(), "keyStatus"));
			//		Log.i(TAG, profileBase64);
					
					
					response=Utils.postData("updateProfile", nameValuePairs);

					mapper = new ObjectMapper();

					aResponse= mapper.readValue(response, 
							new TypeReference<APIResponse>() {
					});
					

				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(Exception e)
				{
					e.printStackTrace();
				}
//			 Log.i(TAG, response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			try{
				if(pd!=null) pd.dismiss();
				if(aResponse.HttpResponse.equalsIgnoreCase("SUCCESS"))
				{
					Utils.WritePreference(getApplicationContext(), "AccessToken", aResponse.Token);
					Utils.WritePreference(getApplicationContext(), "NickName", txtUserName.getText().toString());
					Utils.WritePreference(getApplicationContext(), "Email", Email);
					Utils.WritePreference(getApplicationContext(), "Name", txtName.getText().toString());
					Utils.WritePreference(getApplicationContext(), "Gender", Gender);
					Utils.WritePreference(getApplicationContext(), "DOB", txtDOB.getText().toString());
					Utils.WritePreference(getApplicationContext(), "City", txtCity.getText().toString());
					Utils.WritePreference(getApplicationContext(), "State", txtState.getText().toString());
					Utils.WritePreference(getApplicationContext(), "Country", txtCountry.getText().toString());
					Utils.WritePreference(getApplicationContext(), "isLogged", "YES");
					Utils.WritePreference(getApplicationContext(), "ProfileImage", profileBase64);
					
					finish();
				}
				else
				{
					Toast.makeText(getApplicationContext(), aResponse.HttpResponse, Toast.LENGTH_SHORT).show();
				}
				
			}
			catch(Exception e)
			{
				Toast.makeText(getApplicationContext(), "An Error Happened during registeration.(Try checking the date(MM-dd-yyyy)?)", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}

		}

		@Override
		protected void onCancelled() {

		//	showProgress(false);
		}
	}

	@Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

     switch (requestCode) {
     case PICK_FROM_CAMERA:
            /**
             * After taking a picture, do the crop
             */
            doCrop();

            break;

     case PICK_FROM_FILE:
            /**
             * After selecting image from files, save the selected path
             */
            mImageCaptureUri = data.getData();

            doCrop();

            break;

     case CROP_FROM_CAMERA:
            Bundle extras = data.getExtras();
            /**
             * After cropping the image, get the bitmap of the cropped image and
             * display it on imageview.
             */
            if (extras != null) {
                  Bitmap photo = extras.getParcelable("data");

                  imgProfile.setImageBitmap(photo);
            }

            File f = new File(mImageCaptureUri.getPath());
            /**
             * Delete the temporary image
             */
            if (f.exists())
                  f.delete();

            break;

     }
        /*if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions); 
                    imgProfile.setImageBitmap(bitmap);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

            	Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath+"");

                imgProfile.setImageBitmap(thumbnail);

            }
            else if(requestCode==3)
            {
            	Bundle extras = data.getExtras();
                *//**
                 * After cropping the image, get the bitmap of the cropped image and
                 * display it on imageview.
                 *//*
                if (extras != null) {
                      Bitmap photo = extras.getParcelable("data");

                      imgProfile.setImageBitmap(photo);
                }

                File f = new File(mImageCaptureUri.getPath());
                *//**
                 * Delete the temporary image
                 *//*
                if (f.exists())
                      f.delete();

                
            }

        }*/

    }
	public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
               super(context, R.layout.crop_selector, options);

               mOptions = options;

               mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
               if (convertView == null)
                     convertView = mInflater.inflate(R.layout.crop_selector, null);

               CropOption item = mOptions.get(position);

               if (item != null) {
                     ((ImageView) convertView.findViewById(R.id.iv_icon))
                                   .setImageDrawable(item.icon);
                     ((TextView) convertView.findViewById(R.id.tv_name))
                                   .setText(item.title);

                     return convertView;
               }

               return null;
        }
 }

 public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
 }
	private void captureImageInitialization() {
        /**
         * a selector dialog to display two image source options, from camera
         * ‘Take from camera’ and from existing files ‘Select from gallery’
         */
        final String[] items = new String[] { "Take from camera",
                     "Select from gallery" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                     android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int item) { // pick from
                                                                                                               // camera
                     if (item == 0) {
                            /**
                             * To take a photo from camera, pass intent action
                             * ‘MediaStore.ACTION_IMAGE_CAPTURE‘ to open the camera app.
                             */
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                            /**
                             * Also specify the Uri to save the image on specified path
                             * and file name. Note that this Uri variable also used by
                             * gallery app to hold the selected image path.
                             */
                            mImageCaptureUri = Uri.fromFile(new File(Environment
                                          .getExternalStorageDirectory(), "tmp_avatar_"
                                          + String.valueOf(System.currentTimeMillis())
                                          + ".jpg"));

                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                                          mImageCaptureUri);

                            try {
                                   intent.putExtra("return-data", true);

                                   startActivityForResult(intent, PICK_FROM_CAMERA);
                            } catch (ActivityNotFoundException e) {
                                   e.printStackTrace();
                            }
                     } else {
                            // pick from file
                            /**
                             * To select an image from existing files, use
                             * Intent.createChooser to open image chooser. Android will
                             * automatically display a list of supported applications,
                             * such as image gallery or file manager.
                             */
                            Intent intent = new Intent();

                            intent.setType("image/*");
                             intent.setAction(Intent.ACTION_GET_CONTENT);

                            startActivityForResult(Intent.createChooser(intent,
                                          "Complete action using"), PICK_FROM_FILE);
                     }
               }
        });

        dialog = builder.create();
 }
	 private void doCrop() {
         final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
         /**
          * Open image crop app by starting an intent
          * ‘com.android.camera.action.CROP‘.
          */
         Intent intent = new Intent("com.android.camera.action.CROP");
         intent.setType("image/*");

         /**
          * Check if there is image cropper app installed.
          */
         List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                      intent, 0);

         int size = list.size();

         /**
          * If there is no image cropper app, display warning message
          */
         if (size == 0) {

                Toast.makeText(this, "Can not find image crop app",
                             Toast.LENGTH_SHORT).show();

                return;
         } else {
                /**
                 * Specify the image path, crop dimension and scale
                 */
                intent.setData(mImageCaptureUri);

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                /**
                 * There is posibility when more than one image cropper app exist,
                 * so we have to check for it first. If there is only one app, open
                 * then app.
                 */

                if (size == 1) {
                      Intent i = new Intent(intent);
                      ResolveInfo res = list.get(0);

                      i.setComponent(new ComponentName(res.activityInfo.packageName,
                                    res.activityInfo.name));

                      startActivityForResult(i, CROP_FROM_CAMERA);
                } else {
                      /**
                       * If there are several app exist, create a custom chooser to
                       * let user selects the app.
                       */
                      for (ResolveInfo res : list) {
                             final CropOption co = new CropOption();

                             co.title = getPackageManager().getApplicationLabel(
                                           res.activityInfo.applicationInfo);
                             co.icon = getPackageManager().getApplicationIcon(
                                           res.activityInfo.applicationInfo);
                             co.appIntent = new Intent(intent);

                             co.appIntent
                                           .setComponent(new ComponentName(
                                                         res.activityInfo.packageName,
                                                         res.activityInfo.name));

                             cropOptions.add(co);
                      }

                      CropOptionAdapter adapter = new CropOptionAdapter(
                                    getApplicationContext(), cropOptions);

                      AlertDialog.Builder builder = new AlertDialog.Builder(this);
                      builder.setTitle("Choose Crop App");
                      builder.setAdapter(adapter,
                                    new DialogInterface.OnClickListener() {
                                           public void onClick(DialogInterface dialog, int item) {
                                                  startActivityForResult(
                                                                cropOptions.get(item).appIntent,
                                                                CROP_FROM_CAMERA);
                                           }
                                    });

                      builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                             @Override
                             public void onCancel(DialogInterface dialog) {

                                    if (mImageCaptureUri != null) {
                                           getContentResolver().delete(mImageCaptureUri, null,
                                                         null);
                                           mImageCaptureUri = null;
                                    }
                             }
                      });

                      AlertDialog alert = builder.create();

                      alert.show();
                }
         }
  }
}
