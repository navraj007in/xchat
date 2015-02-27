package com.kss.xchat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
public class RegisterActivity extends Activity {
public static String TAG="RegisterActivity";
RegisterTask mRegisterTask;
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
Button cmdSave;
Button cmdCancel;
String URL;
String Gender;
public static String FullName;
public static String DOB;
public static String Email;
APIResponse aResponse;
String profileBase64;
Bitmap profileBitmap;
@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
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
		
		imgProfile.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				selectImage();
				return false;
			}
		});

		rdbMale=(RadioButton)findViewById(R.id.rdbMale);
		rdbFemale=(RadioButton)findViewById(R.id.rdbFemale);

		cmdCancel=(Button)findViewById(R.id.cmdCancel);
		cmdCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
		cmdSave=(Button)findViewById(R.id.cmdRegister);
		cmdSave.setOnClickListener(new OnClickListener() {
			
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
				if(txtUserName.getText().toString().length()<6)
				{
				Toast.makeText(getApplicationContext(), "Nick Name can not be less than 6 characters", Toast.LENGTH_SHORT).show();	
				return ;
				}
				txtUserName.setText(txtUserName.getText().toString().toLowerCase());
				if(checkDate(txtDOB.getText().toString())){
				mRegisterTask = new RegisterTask();
				mRegisterTask.execute((Void) null);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Date format incorrect. Enter MM-dd-yyyy ", Toast.LENGTH_SHORT).show();
					
				}
			}
		});

		GetImageTask task = new GetImageTask();
        task.execute(new String[] { LoginActivity.profileImage.getUrl() });
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}
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
	public class RegisterTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog pd;
		@Override
        protected void onPreExecute(){
           super.onPreExecute();
       	pd=new ProgressDialog(RegisterActivity.this);

    	pd.setTitle("Registering New Account...");
    	pd.setMessage("Please wait....");
    	pd.setCancelable(false);
    	pd.setIndeterminate(true);
    	pd.show();
    	
    	Gender="";
    	if(rdbMale.isChecked()) Gender="M";
    	else Gender="F";
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String response="";//Utils.RestCall(URL);
			//Log.i(TAG,response);
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

					
					
					response=Utils.postData("RegisterWithPic", nameValuePairs);

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
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			// Log.i(TAG, response);

			// TODO: register the new account here.
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mRegisterTask = null;
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

					Intent intent=new Intent(RegisterActivity.this,TOSActivity.class);
					startActivity(intent);
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
			mRegisterTask = null;
		//	showProgress(false);
		}
	}
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}

	public void loadBitmap(int resId, ImageView imageView) {
		BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	    task.execute(resId);
	}
	private void selectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Choose your profile Pic!");
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

	class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(Integer... params) {
	        data = params[0];
	        return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

	private class GetImageTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
			}
			//map=Utils.getRoundedCornerBitmap(map, 128);
			return map;
		}

		// Sets the Bitmap returned by doInBackground
		@Override
		protected void onPostExecute(Bitmap result) {
			try{
			imgProfile.setImageBitmap(result);
			BitmapDrawable drawable = (BitmapDrawable) imgProfile.getDrawable();
			Bitmap bitmap = drawable.getBitmap();
			profileBase64=Utils.BitmapToBase64(bitmap);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
//			Log.i(TAG,"BAse 64 - "+profileBase64);

		}

		// Creates Bitmap from InputStream and returns it
		private Bitmap downloadImage(String url) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(url);
				bitmap = BitmapFactory.
						decodeStream(stream, null, bmOptions);
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return bitmap;
		}

		// Makes HttpURLConnection and returns InputStream
		private InputStream getHttpConnection(String urlString)
				throws IOException {
			InputStream stream = null;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return stream;
		}
	}
	@Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
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

        }

    }    

}
