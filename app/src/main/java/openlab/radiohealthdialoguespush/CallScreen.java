package openlab.radiohealthdialoguespush;

/**
 * Created by nkk27 on 02/06/15.
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton;
import android.widget.Toast;


public class  CallScreen extends Activity {
    private static final String TAG = CallScreen.class.getCanonicalName();

    private final NgnEngine mEngine;
    private TextView mTvInfo;
    private TextView mTvRemote;
    private TextView displaytext;

    private Button mBtHangUp;

    private NgnAVSession mSession;
    private BroadcastReceiver mSipBroadCastRecv;
    private BroadcastReceiver br;
    private String phonenumber;
    //public static String url =  "http://192.168.21.49:8080/TestServlet/Test1";
    //public static String url =  "http://192.168.1.102:8080/TestServlet/Test1";
    public static String url_getopinionusersconnectedcount = "http://172.31.27.18/g "; /// "http://ec2-52-24-148-218.us-west-2.compute.amazonaws.com/getopinionusers.php";
    //public static String url_usercount =  "http://ec2-52-24-148-218.us-west-2.compute.amazonaws.com/getconnectedcount.php";
    public static String  url_updatemute = "http://172.31.27.18/updatemute.php";// "http://ec2-52-24-148-218.us-west-2.compute.amazonaws.com/updatemute.php";



    public static String BROADCAST_ACTION = "SHOWBROADCAST";


    private ListView listvw;
    private TextView textview;
    private MainAdapter mAdapter;
    private Timer timer,timer1 ;

    static ArrayList<MainListViewItem>  userlist = new   ArrayList<MainListViewItem>();
    private HashMap<String,String> opiniousers = new HashMap<String,String>();

	/*static final MainListViewItem[] userList = new MainListViewItem[]{
		new MainListViewItem("1000"),
		new MainListViewItem("1001"),
		new MainListViewItem("1002"),
		//new MainListViewItem("200006395544399062", "200006395544399062"),
	};*/



    public CallScreen(){
        super();
        mEngine = NgnEngine.getInstance();

    }

   /* BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("abc", "Inside On Receiver");
            Toast.makeText(getApplicationContext(), "received broadcast",Toast.LENGTH_SHORT).show();

            String param = intent.getStringExtra("data");
            Log.d("abc", "param"+param);
            updateListenerInfo(param);
            // do something

        }
    };*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        Log.d("abc", "onCreate");

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            mSession = NgnAVSession.getSession(extras.getLong(Main.EXTRAT_SIP_SESSION_ID));

            //	phonenumber = extras.getString("phonenumber");


        }

        // if(phonenumber.equals("5000"))
        //	Log.d("abc","dialed number is 5000");

        if(mSession.getRemotePartyDisplayName().equals("5000"))
        {
            Log.d("abc","remote party  number is 5000");


        }




       /* final Handler handler1 = new Handler();
        timer1 = new Timer();
        TimerTask task1 = new TimerTask() {


             @Override
             public void run() {
            	 Log.d("abc","timer task");
               handler1.post(new Runnable() {
                  public void run() {
                	  new getusercount().execute(url_usercount);
                     Log.d("abc","Heyyyyyy getting count value");
                  }
                });
              }
        };
        timer1.schedule(task1, 0, 1000);*/







      /*  final Handler handler = new Handler();
        timer = new Timer();
        TimerTask task = new TimerTask() {


            @Override
            public void run() {
                Log.d("abc","timer task");
                handler.post(new Runnable() {
                    public void run() {
                        new show().execute(url_getopinionusersconnectedcount);
                        Log.d("abc","Heyyyyyy");
                    }
                });
            }
        };
        timer.schedule(task, 0, 10000);*/


        // new show().execute(url);


        if(mSession == null){
            Log.e(TAG, "Null session");
            finish();
            return;
        }
        mSession.incRef();
        mSession.setContext(this);

        // listen for audio/video session state
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleSipEvent(intent);
              //  Toast.makeText(getApplicationContext(), "mSipBroadCast",Toast.LENGTH_SHORT).show();
            }
        };

         br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try
                {
                Log.d("abc", "Inside On Receiver");
                Toast.makeText(getApplicationContext(), "received broadcast",Toast.LENGTH_SHORT).show();

                String param = intent.getStringExtra("data");
                Log.d("abc", "param" + param);
                updateListenerInfo(param);
                // do something
                }
                catch(Exception e){
                    Log.d("abc","Exception in onReceive of receiver :"+e.getMessage() );

                }

            }

        };

        Log.d("abc", "onregistering receiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(br, filter);
        //Log.d("abc", "Going to start a service");
        //startService(new Intent(CallScreen.this, TCPService.class));

        Log.d("abc", "receiver registered");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
        registerReceiver(mSipBroadCastRecv, intentFilter);


        mTvInfo = (TextView)findViewById(R.id.call_screen_textView_info);
        mTvRemote = (TextView)findViewById(R.id.callscreen_textView_remote);
        displaytext = (TextView)findViewById(R.id.textView_count);
        mBtHangUp = (Button)findViewById(R.id.callscreen_button_hangup);

        mBtHangUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // unregisterReceiver(br);
               // stopService(new Intent(CallScreen.this, TCPService.class)) ;
                if(mSession != null){
                    mSession.hangUpCall();
                    finish();
                }
            }
        });

        mTvRemote.setText(mSession.getRemotePartyDisplayName());
        mTvInfo.setText(getStateDesc(mSession.getState()));


        mAdapter = new MainAdapter(this);

        listvw = (ListView) findViewById(R.id.main_listView);

        listvw.setAdapter(mAdapter);


        listvw.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        Log.d("abc", "onCreate End");
    }





    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume()");
        if(mSession != null){
            final InviteState callState = mSession.getState();
            mTvInfo.setText(getStateDesc(callState));
            if(callState == InviteState.TERMINATING || callState == InviteState.TERMINATED){
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy()");
        if(mSipBroadCastRecv != null){
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }

        if(br != null){
            unregisterReceiver(br);
            br = null;
        }

        if(mSession != null){
            mSession.setContext(null);
            mSession.decRef();
        }
    //    timer.cancel();
     //   timer.purge();
      //  stopService(new Intent(CallScreen.this, TCPService.class)) ;
      //  unregisterReceiver(br);
        Log.d("abc","calling onDestroy parent");
        stopService(new Intent(CallScreen.this, TCPService.class)) ;
        super.onDestroy();
    }

    private String getStateDesc(InviteState state){
        switch(state){
            case NONE:
            default:
                return "Unknown";
            case INCOMING:
                return "Incoming";
            case INPROGRESS:
                return "Inprogress";
            case REMOTE_RINGING:
                return "Ringing";
            case EARLY_MEDIA:
                return "Early media";
            case INCALL:
                return "In Call";
            case TERMINATING:
                return "Terminating";
            case TERMINATED:
                return "termibated";
        }
    }

    private void handleSipEvent(Intent intent){
        if(mSession == null){
            Log.e(TAG, "Invalid session object");
            return;
        }
        final String action = intent.getAction();
        if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)){
            NgnInviteEventArgs args = intent.getParcelableExtra(NgnInviteEventArgs.EXTRA_EMBEDDED);
            if(args == null){
                Log.e(TAG, "Invalid event args");
                return;
            }
            if(args.getSessionId() != mSession.getId()){
                return;
            }

            final InviteState callState = mSession.getState();
            mTvInfo.setText(getStateDesc(callState));
            switch(callState){
                case REMOTE_RINGING:
                    mEngine.getSoundService().startRingBackTone();
                    break;
                case INCOMING:
                    mEngine.getSoundService().startRingTone();
                    break;
                case EARLY_MEDIA:
                case INCALL:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    mSession.setSpeakerphoneOn(false);
                    break;
                case TERMINATING:
                case TERMINATED:
                    mEngine.getSoundService().stopRingTone();
                    mEngine.getSoundService().stopRingBackTone();
                    finish();
                    break;
                default:
                    break;
            }
        }
    }


    static class MainListViewItem{
        private final String userID;
        private final String UUID;


        MainListViewItem(String id, String uuid ){
            userID = id;
            UUID = uuid;
        }

        public String getUserID() {
            return userID;
        }

        public String getUUID() {
            return UUID;
        }



    }

    static class MainAdapter extends BaseAdapter{
        final LayoutInflater mInflater;
        final CallScreen mMain;
        MainAdapter(CallScreen main){
            super();
            mMain = main;
            mInflater = LayoutInflater.from(main);
        }

        void refresh(){
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return userlist.size();
        }

        @Override
        public Object getItem(int position) {
            return userlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final MainListViewItem item = (MainListViewItem)getItem(position);
            if(view == null){
                view = mInflater.inflate(R.layout.activity_show_host_screen, null);
            }


            ((TextView)view.findViewById(R.id.main_userTextiew)).setText(item.getUserID());
            final ToggleButton btn_listener = ((ToggleButton)view.findViewById(R.id.button_listener));

            btn_listener.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // The toggle is enabled
                        String userdetails[]= {url_updatemute,item.getUUID(),"unmute"};
                        Log.d("abc","calling update mute status thread to unmute");
                        mMain.new updatemute().execute(userdetails);

                    } else {
                        // The toggle is disabled
                        String userdetails[]= {url_updatemute,item.getUUID(),"mute"};
                        mMain.new updatemute().execute(userdetails);
                        Log.d("abc","calling update mute status thread to Mute");
                    }
                }

            });

        return view;
    }



}





           /* ((TextView)view.findViewById(R.id.main_userTextiew)).setText(item.getUserID());
            final ToggleButton btn_mute = ((ToggleButton)view.findViewById(R.id.button_mute));
            btn_mute.setText("mute");

            btn_mute.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    if(btn_mute.isChecked())
                    {
                        btn_mute.setText("unmute");
                        String userdetails[]= {url_updatemute,item.getUUID(),"unmute"};
                        Log.d("abc","calling update mute status thread to unmute");
                        mMain.new updatemute().execute(userdetails);

                    }
                    else
                    {	String userdetails[]= {url_updatemute,item.getUUID(),"mute"};
                        mMain.new updatemute().execute(userdetails);
                        Log.d("abc","calling update mute status thread to Mute");
                        btn_mute.setText("mute");

                    }
                } */



           /* ((TextView)view.findViewById(R.id.main_userTextiew)).setText(item.getUserID());
            final Button btn_mute = ((Button)view.findViewById(R.id.button_mute));
            //btn_mute.setText("unmute"); btn_mute.setText("mute");

            btn_mute.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    // Perform action on click

                    if(btn_mute.getText() == "unmute")
                    {
                        btn_mute.setText("mute");
                        String userdetails[]= {url_updatemute,item.getUUID(),"unmute"};
                        Log.d("abc","calling update mute status thread to unmute");
                        mMain.new updatemute().execute(userdetails);

                    }
                    else
                    {   btn_mute.setText("unmute");
                        String userdetails[]= {url_updatemute,item.getUUID(),"mute"};
                        mMain.new updatemute().execute(userdetails);
                        Log.d("abc","calling update mute status thread to Mute");

                    }
                } */




    private class show extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            String output = null;
            System.out.println(" show doinbackground");
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }


        private String getOutputFromUrl(String url) {
            Log.d("abc", url);
            String output = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine st = httpResponse.getStatusLine();
                if(st.getStatusCode()== HttpStatus.SC_OK)
                {
                    Log.d("abc", "HttpStatus.SC_OK");
                    HttpEntity httpEntity = httpResponse.getEntity();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    httpEntity.writeTo(out);
                    out.close();

                    output = out.toString();
                    System.out.println(output);
                    Log.d("abc", output);
                }else{
                    Log.d("abc","HttpStatus Not oK");
                    output = "error";
                    System.out.println(output);
                    Log.d("abc", output);

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;


        }

        protected void onPostExecute(String result) {

            String[] connectedusers = result.split(",");
            //<String> opinionusers = new ArrayList<String>(result.split(","));

            String count = connectedusers[0];
            displaytext.setText(count);

            for(int j=1;j<connectedusers.length;j++)
                Log.d("abc","opinon user list "+connectedusers[j]);

            for(int j=0;j<userlist.size();j++)
                Log.d("abc","userlist before"+ userlist.get(j).getUserID());

            Log.d("abc","userlist size " +userlist.size());
            // for(int k=0;k<userlist.size();k++)
            // {
            // Log.d("abc","k = "+k);
            userlist.clear();

            //}

            for(int n=0;n<userlist.size();n++)
                Log.d("abc","userlist after delete "+ userlist.get(n).getUserID());


            for(int i=1;i<connectedusers.length-1;i=i+2)
            {

                Log.d("abc","Adding item "+connectedusers[i]+","+connectedusers[i+1] );

                userlist.add(new MainListViewItem(connectedusers[i], connectedusers[i+1]));


            }
            // Log.d("abc","list size is"+userlist.size()+", user list is "+userlist.get((userlist.size())-1).getUserID()+","+userlist.get(1).getUserID());

            for(int j=0;j<userlist.size();j++)
                Log.d("abc","userlist new updated list  "+ userlist.get(j).getUserID()+userlist.get(j).getUUID());
            //   mAdapter.getData().addAll(objects);

            mAdapter.notifyDataSetChanged();
            //	    displaytext.setText(result);

        }



    }

    /*
    private class getusercount extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            String output = null;
            System.out.println(" geusercount doinbackground");
            for (String url : urls) {
                output = getOutputFromUrl(url);
            }
            return output;
        }


        private String getOutputFromUrl(String url) {

            String output = null;
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                StatusLine st = httpResponse.getStatusLine();
                if(st.getStatusCode()== HttpStatus.SC_OK)
                {
                    Log.d("abc", "HttpStatus.SC_OK");
                    HttpEntity httpEntity = httpResponse.getEntity();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    httpEntity.writeTo(out);
                    out.close();

                    output = out.toString();
                    System.out.println(output);
                    Log.d("abc", output);
                }else{
                    Log.d("abc","HttpStatus Not oK");
                    output = "error";
                    System.out.println(output);
                    Log.d("abc", output);

                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return output;


        }

        protected void onPostExecute(String result) {

            displaytext.setText(result);

        }



    } */


    private class updatemute extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {

            String output = null;
            String url = urls[0];
            String mutedata = urls[2];
            String uuid = urls[1];
            System.out.println("update mute doinbackground");
            Log.d("updatemute", "mutedata is " + mutedata + "uuid is" + uuid);


            //for (String url : urls) {
            output = getOutputFromUrl(url, mutedata, uuid);
            //}

            Log.d("updatemute", "inside update mute status function doin background");
            return output;
        }


        private String getOutputFromUrl(String url, String mutedata, String uuid) {

            String output = null;
            try {

					/*	List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);

						nameValuePair.add(new BasicNameValuePair("status",mutedata));
						nameValuePair.add(new BasicNameValuePair("uuid",uuid));*/


                HttpClient httpClient = new DefaultHttpClient();

                String urledit = url + "?" + "status=" + mutedata + "&" + "uuid=" + uuid;

                HttpPost httpPost = new HttpPost(urledit);



				/*	try {
						httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
						Log.d("updatemute", "url for mute update "+httpPost.getURI().toURL().toString());
						Log.d("updatemute","http post entity content is "+httpPost.getEntity().getContent().toString());
						Log.d("updatemute","http post entity is "+httpPost.getEntity().toString());


					} catch (UnsupportedEncodingException e) {
						// writing error to Log
						e.printStackTrace();
					}*/


                // Making HTTP Request
                try {
                    HttpResponse response = httpClient.execute(httpPost);
                    StatusLine st = response.getStatusLine();
                    int x = st.getStatusCode();
                    Log.d("updatemute", st.toString() + ":" + Integer.toString(x));

                    if (st.getStatusCode() == HttpStatus.SC_OK) {
                        // writing response to log
                        Log.d("abc", "HttpStatus OK");
                        HttpEntity entity = response.getEntity();
                        final String responseText = EntityUtils
                                .toString(entity);
                        output = responseText;
                        Log.d("updatemute", "inside update mute thread response test is " + responseText);
                    } else {

                        Log.d("updatemute", "UpdateMute Status Http not ok");
                        output = "Wrong Input";

                    }

                } catch (ClientProtocolException e) {
                    // writing exception to log
                    e.printStackTrace();
                } catch (IOException e) {
                    // writing exception to log
                    e.printStackTrace();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return output;


        }

        protected void onPostExecute(String result) {


        }

    }
        public void updateListenerInfo(String result) {

            String[] connectedusers = result.split(",");
            //<String> opinionusers = new ArrayList<String>(result.split(","));
           // if(connectedusers[0].equalsIgnoreCase("0"))
             //   displaytext.setText(connectedusers[0]);
            //if (connectedusers.length > 2) {
                String count = connectedusers[0];
                displaytext.setText(count);

                for (int j = 1; j < connectedusers.length; j++)
                    Log.d("abc", "opinon user list " + connectedusers[j]);

                for (int j = 0; j < userlist.size(); j++)
                    Log.d("abc", "userlist before" + userlist.get(j).getUserID());

                Log.d("abc", "userlist size " + userlist.size());
                // for(int k=0;k<userlist.size();k++)
                // {
                // Log.d("abc","k = "+k);
                userlist.clear();

                //}

                for (int n = 0; n < userlist.size(); n++)
                    Log.d("abc", "userlist after delete " + userlist.get(n).getUserID());


                for (int i = 1; i < connectedusers.length - 1; i = i + 2) {

                    Log.d("abc", "Adding item " + connectedusers[i] + "," + connectedusers[i + 1]);

                    userlist.add(new MainListViewItem(connectedusers[i], connectedusers[i + 1]));


                }
                // Log.d("abc","list size is"+userlist.size()+", user list is "+userlist.get((userlist.size())-1).getUserID()+","+userlist.get(1).getUserID());

                for (int j = 0; j < userlist.size(); j++)
                    Log.d("abc", "userlist new updated list  " + userlist.get(j).getUserID() + userlist.get(j).getUUID());
                //   mAdapter.getData().addAll(objects);

                mAdapter.notifyDataSetChanged();
                //	    displaytext.setText(result);

            }


       // }

}