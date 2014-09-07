package com.openxc.enabler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.openxc.VehicleManager;
import com.openxc.measurements.AcceleratorPedalPosition;
import com.openxc.measurements.BrakePedalStatus;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.SteeringWheelAngle;
import com.openxc.measurements.TurnSignalRightStatus;
import com.openxc.measurements.TurnSignalLeftStatus;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.remote.VehicleServiceException;



public class DrowsyDetector extends Activity {

    private static String TAG = "VehicleDashboard";

    private VehicleManager mVehicleManager;
    private boolean mIsBound;
    private final Handler mHandler = new Handler();

    private TextView mSteeringWheelAngleView;
    private TextView mVehicleBrakeStatusView;
    private TextView mAcceleratorPedalPositionView;
    private TextView mTurnSignalRightView;
    private TextView mTurnSignalLeftView;
    
    private SteeringWheelAngle prevAngle = null;
    private boolean hasJerked = false;
    private boolean isDrowsy = false;
    private boolean jerkNeg;
    private boolean triedToWake = false;
    protected static boolean isPlaying = false;
    private boolean stopMusic = false;
    //private SongsManager songs = new SongsManager(getApplicationContext());

    
    public class MusicThread extends Thread {
    	
    	
    	
    	public void run() {
    		ArrayList<HashMap<String,String>> songList = getPlayList(getApplicationContext());
    		Random rand = new Random();
    		int randomSelection = rand.nextInt(songList.size()-1);
    		String songPath = songList.get(randomSelection).get("songPath");
			MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(songPath));
    		//MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bad);
			mediaPlayer.start();
			isPlaying = true;
			System.out.println("RUNNINGGGG");
			while (mediaPlayer.isPlaying() && !stopMusic){}
			stopMusic = false;
			mediaPlayer.pause();
			mediaPlayer.stop();
			mediaPlayer.release();
			isPlaying = false;
			System.out.println("STOPPED RUNNING");
    	}
    	
    }
    
    public ArrayList<HashMap<String, String>> getPlayList(Context c) {
		ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	    /*use content provider to get beginning of database query that queries for all audio by display name, path
	    and mimtype which i dont use but got it incase you want to scan for mp3 files only you can compare with RFC  mimetype for mp3's
	    */
	    final Cursor mCursor = c.getContentResolver().query(
	            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
	            new String[] { MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.MIME_TYPE }, null, null,
	            "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

	    String songs_name = "";
	    String mAudioPath = "";

	    /* run through all the columns we got back and save the data we need into the arraylist for our listview*/
	    if (mCursor.moveToFirst()) {
	        do {

	        String file_type = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));


	            songs_name = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
	            mAudioPath = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
	            HashMap<String, String> song = new HashMap<String, String>();

	            song.put("songTitle", songs_name);
	            song.put("songPath", mAudioPath);

	            songsList.add(song);

	        } while (mCursor.moveToNext());
	    }
	    return songsList;
	}
    
    public void playTone(Context context) throws IOException {
    	Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, soundUri);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        
        if(audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } 
    }
    
    boolean turnSigOn = false;
    TurnSignalRightStatus.Listener mTurnSignalRightListener = 
    		new TurnSignalRightStatus.Listener() {
    	public void receive(Measurement measurement) {
    		final TurnSignalRightStatus status = (TurnSignalRightStatus) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mTurnSignalRightView.setText(status.toString());
    				if(status.toString().equals("on")) turnSigOn = true;
    			}
    		});
    	}
    };
    
    TurnSignalLeftStatus.Listener mTurnSignalLeftListener = 
    		new TurnSignalLeftStatus.Listener() {
    	public void receive(Measurement measurement) {
    		final TurnSignalLeftStatus status = (TurnSignalLeftStatus) measurement;
    		mHandler.post(new Runnable() {
    			public void run() {
    				mTurnSignalLeftView.setText(status.toString());
    				if(status.toString().equals("on")) turnSigOn = true;
    			}
    		});
    	}
    };
    
    BrakePedalStatus.Listener mBrakePedalStatus =
            new BrakePedalStatus.Listener() {
        public void receive(Measurement measurement) {
            final BrakePedalStatus status = (BrakePedalStatus) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mVehicleBrakeStatusView.setText(status.toString());
                }
            });
        }
    };

    AcceleratorPedalPosition.Listener mAcceleratorPedalPosition =
            new AcceleratorPedalPosition.Listener() {
        public void receive(Measurement measurement) {
            final AcceleratorPedalPosition status =
                (AcceleratorPedalPosition) measurement;
            mHandler.post(new Runnable() {
                public void run() {
                    mAcceleratorPedalPositionView.setText(status.toString());
                }
            });
        }
    };


    SteeringWheelAngle.Listener mSteeringWheelListener =
            new SteeringWheelAngle.Listener() {
        public void receive(Measurement measurement) {
            final SteeringWheelAngle angle = (SteeringWheelAngle) measurement;
            final float diff;
            
            if(prevAngle == null){
            	prevAngle = angle;
            	diff = 0;
            } else {
            	float prevValue = Float.parseFloat(prevAngle.toString().substring(0,prevAngle.toString().length()-1));
            	float currentValue = Float.parseFloat(angle.toString().substring(0,angle.toString().length()-1));
            	diff = currentValue - prevValue;
            	//System.out.println("Current Val:" + currentValue);
            	//System.out.println("Prev Val:" + prevValue);
            	float diffAbs = Math.abs(diff);
            	//System.out.println("absolute difference: " + diffAbs);
            	boolean jerked = diffAbs >= 15 && diffAbs <= 70;
            	//System.out.println("jerked?: " + jerked);
            	//System.out.println("hasJerked?: " + hasJerked);            	
            	//System.out.println("diff<0 != jerkNeg?" + (diff < 0 != jerkNeg));
            	
            	if(jerked && !hasJerked) {
            		jerkNeg = diff < 0; 
            		hasJerked = true;
            	} else if (hasJerked && jerked && (diff < 0 != jerkNeg)){
            		System.out.println("DROWSY ALERT");
            		isDrowsy = true;
            	} else {
            		System.out.println("no jerk.");
            		hasJerked = false;
            	} // end if elseif else

            	prevAngle = angle;
            } // end if else
            
            

	            mHandler.post(new Runnable() {
	                public void run() {
	                	if(!turnSigOn)
	                	{
	                	Thread playMusic= new Thread (new MusicThread());
                            if(isDrowsy && !isPlaying && !triedToWake) {	
		                    	
                            	System.out.println("TIMETOPLAY");
		                    	
									isDrowsy = false;
									triedToWake = true;
									playMusic.start();

		                    	
                            } else if (isDrowsy && triedToWake) {
                            	
                            	stopMusic = true;
                            	Toast.makeText(getApplicationContext(), "STOP DRIVING PULL OVER SLEEP PLEASE", Toast.LENGTH_SHORT).show();
                            	for (int i=0; i < 3; i++) {
		                    		try {
										playTone(getApplicationContext());
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
                            	}// end for
                            	
                            	triedToWake = false;
                            	isDrowsy = false;
                            	
                            } // end if/if else
	                    mSteeringWheelAngleView.setText(angle.toString() + " w/ difference of: " + diff);
	                	}    
	                	turnSigOn = false;
	                } // end mHandler run
	            
	            }); // end mHandler post
            
        } // end mSteeringWheelListener receive
    }; // end SteeringWheelAngle Listener 
    
    
   private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder)service
                    ).getService();

            try {
                mVehicleManager.addListener(SteeringWheelAngle.class,
                        mSteeringWheelListener);
                mVehicleManager.addListener(TurnSignalRightStatus.class,
                		mTurnSignalRightListener);
                mVehicleManager.addListener(TurnSignalLeftStatus.class,
                		mTurnSignalLeftListener);
          //      mVehicleManager.addListener(Longitude.class,
          //      		mLongAccelListener);
            } catch(VehicleServiceException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            } catch(UnrecognizedMeasurementTypeException e) {
                Log.w(TAG, "Couldn't add listeners for measurements", e);
            }
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.w(TAG, "VehicleService disconnected unexpectedly");
            mVehicleManager = null;
            mIsBound = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_dashboard);
        Log.i(TAG, "Vehicle dashboard created");


        mSteeringWheelAngleView = (TextView) findViewById(
                R.id.steering_wheel_angle);
        mTurnSignalRightView = (TextView) findViewById(
        		R.id.turn_signal_right);
        mTurnSignalLeftView = (TextView) findViewById(
        		R.id.turn_signal_left);
      //  mLongAccelView = (TextView) findViewById(
      //          R.id.android_long_Accel); 
    }

    @Override
    public void onResume() {
        super.onResume();
        bindService(new Intent(this, VehicleManager.class),
                mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mIsBound) {
            Log.i(TAG, "Unbinding from vehicle service");
            unbindService(mConnection);
            mIsBound = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
}
