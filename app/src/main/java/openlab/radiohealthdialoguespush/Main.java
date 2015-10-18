package openlab.radiohealthdialoguespush;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;
import org.doubango.tinyWRAP.tmedia_qos_strength_t;
import org.doubango.tinyWRAP.tmedia_qos_stype_t;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;



public class Main extends Activity {
    private ListView mListView;
    private TextView mTvLog;
    private MainAdapter mAdapter;
    private BroadcastReceiver mSipBroadCastRecv;
    private final NgnEngine mEngine;
    private final INgnConfigurationService mConfigurationService;
    private final INgnSipService mSipService;

    //information for registering to the server
    private final static String SIP_DOMAIN = "172.31.27.18"; //"ec2-52-24-148-218.us-west-2.compute.amazonaws.com";
    private final static String SIP_USERNAME = "1000";
    private final static String SIP_PASSWORD = "Radio_1234@IIITD";
    private final static String SIP_SERVER_HOST =  "172.31.27.18"; //"ec2-52-24-148-218.us-west-2.compute.amazonaws.com"; "172.31.27.18";
    private final static int SIP_SERVER_PORT = 5060;
    public static final String DEFAULT_QOS_PRECOND_BANDWIDTH = "Low";
    public static final String DEFAULT_QOS_PRECOND_STRENGTH = tmedia_qos_strength_t.tmedia_qos_strength_none.toString();
    public static final String DEFAULT_QOS_PRECOND_TYPE = tmedia_qos_stype_t.tmedia_qos_stype_none.toString();
    public static final String DEFAULT_QOS_REFRESHER = "none";
    public static final String EXTRAT_SIP_SESSION_ID = "SipSession";


    static final MainListViewItem[] sMainListViewItems = new MainListViewItem[]{
            new MainListViewItem("001", "001"),
            new MainListViewItem("8555", "8555"),
            new MainListViewItem("5000", "5000"),
            //new MainListViewItem("200006395544399062", "200006395544399062"),
    };

    public Main(){
        mEngine = NgnEngine.getInstance();
        mConfigurationService = mEngine.getConfigurationService();
        mSipService = mEngine.getSipService();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mTvLog = (TextView)findViewById(R.id.main_textView_log);

        mAdapter = new MainAdapter(this);
        mListView = (ListView) findViewById(R.id.main_listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });

        mTvLog.setText("onCreate()");

        // Listen for registration events
        mSipBroadCastRecv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();

                // Registration Event
                if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
                    NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                    if(args == null){
                        mTvLog.setText("Invalid event args");
                        return;
                    }
                    switch(args.getEventType()){
                        case REGISTRATION_NOK:
                            mTvLog.setText("Failed to register :(");
                            break;
                        case UNREGISTRATION_OK:
                            mTvLog.setText("You are now unregistered :)");
                            break;
                        case REGISTRATION_OK:
                            mTvLog.setText("You are now registered :)");
                            break;
                        case REGISTRATION_INPROGRESS:
                            mTvLog.setText("Trying to register...");
                            break;
                        case UNREGISTRATION_INPROGRESS:
                            mTvLog.setText("Trying to unregister...");
                            break;
                        case UNREGISTRATION_NOK:
                            mTvLog.setText("Failed to unregister :(");
                            break;
                    }
                    mAdapter.refresh();
                }
            }
        };
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
        registerReceiver(mSipBroadCastRecv, intentFilter);
       // startService(new Intent(Main.this, TCPService.class));
    }


    @Override
    protected void onDestroy() {
        // Stops the engine
        if(mEngine.isStarted()){
            mEngine.stop();
        }
        // release the listener
        if (mSipBroadCastRecv != null) {
            unregisterReceiver(mSipBroadCastRecv);
            mSipBroadCastRecv = null;
        }
        //stopService(new Intent(Main.this, TCPService.class)) ;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Starts the engine
        if(!mEngine.isStarted()){
            if(mEngine.start()){
                mTvLog.setText("Engine started :)");
            }
            else{
                mTvLog.setText("Failed to start the engine :(");
            }

        }
        // Register
        if(mEngine.isStarted()){
            if(!mSipService.isRegistered()){
                // Set credentials
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, SIP_USERNAME);
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", SIP_USERNAME, SIP_DOMAIN));
                mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, SIP_PASSWORD);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, SIP_SERVER_HOST);
                mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, SIP_SERVER_PORT);
                mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, SIP_DOMAIN);
                mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G,true);

                //Boolean status = mConfigurationService.getBoolean(NgnConfigurationEntry.NETWORK_USE_3G, true);
                mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_WIFI,NgnConfigurationEntry.DEFAULT_NETWORK_USE_WIFI);
                mConfigurationService.putString(NgnConfigurationEntry.QOS_PRECOND_BANDWIDTH_LEVEL,DEFAULT_QOS_PRECOND_BANDWIDTH);
                mConfigurationService.putString(NgnConfigurationEntry.QOS_PRECOND_STRENGTH,DEFAULT_QOS_PRECOND_STRENGTH);
                mConfigurationService.putString(NgnConfigurationEntry.QOS_PRECOND_TYPE,DEFAULT_QOS_PRECOND_TYPE);
                mConfigurationService.putString(NgnConfigurationEntry.QOS_REFRESHER,DEFAULT_QOS_REFRESHER);

                //	mConfigurationService.putInt(NgnConfigurationEntry.QOS_SIP_CALLS_TIMEOUT, 7200);
                //	mConfigurationService.putBoolean(NgnConfigurationEntry.GENERAL_SEND_DEVICE_INFO, false);
                //	mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT,10000);
                //int timeout = mConfigurationService.getInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT,10000);

                boolean useWifi = NgnEngine.getInstance().getConfigurationService().getBoolean(NgnConfigurationEntry.NETWORK_USE_3G,
                        NgnConfigurationEntry.DEFAULT_NETWORK_USE_3G);
                boolean useWifi1 = NgnEngine.getInstance().getConfigurationService().getBoolean(NgnConfigurationEntry.NETWORK_USE_WIFI,
                        NgnConfigurationEntry.DEFAULT_NETWORK_USE_WIFI);

                Log.d("abc","default network 3g enabled is "+useWifi);
                Log.d("abc","default network WIFI enabled is "+useWifi1);
                //	Log.d("abc","default network timeout= "+timeout);

                // VERY IMPORTANT: Commit changes
                mConfigurationService.commit();
                // register (log in)
                mSipService.register(this);
            }
        }
    }


    boolean makeVoiceCall(String phoneNumber){

       // Log.d("abc", "Going to start a service");

        final String validUri = NgnUriUtils.makeValidSipUri(String.format("sip:%s@%s", phoneNumber, SIP_DOMAIN));
        if(validUri == null){
            mTvLog.setText("failed to normalize sip uri '" + phoneNumber + "'");
            return false;
        }
        NgnAVSession avSession = NgnAVSession.createOutgoingSession(mSipService.getSipStack(), NgnMediaType.Audio);
     //   startService(new Intent(Main.this, TCPService.class));
        Intent i = new Intent();
        i.setClass(this, CallScreen.class);
        i.putExtra(EXTRAT_SIP_SESSION_ID, avSession.getId());

        startActivity(i);



        return avSession.makeCall(validUri);
    }

    //
    //	MainListViewItem
    //
    static class MainListViewItem{
        private final String mDescription;
        private final String mPhoneNumber;

        MainListViewItem(String description, String phoneNumer){
            mDescription = description;
            mPhoneNumber = phoneNumer;
        }

        String getDescription(){
            return mDescription;
        }

        String getPhoneNumber(){
            return mPhoneNumber;
        }
    }

    //
    //
    //
    class MainAdapter extends BaseAdapter{
        final LayoutInflater mInflater;
        final Main mMain;
        MainAdapter(Main main){
            super();
            mMain = main;
            mInflater = LayoutInflater.from(main);
        }

        void refresh(){
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return sMainListViewItems.length;
        }

        @Override
        public Object getItem(int position) {
            return sMainListViewItems[position];
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
                view = mInflater.inflate(R.layout.main_item, null);
            }
            ((TextView)view.findViewById(R.id.main_item_textView1_description)).setText(item.getDescription());
            final Button button = (Button)view.findViewById(R.id.main_item_button_call);
            button.setEnabled(mMain.mSipService.isRegistered());
            button.setTag(item.getPhoneNumber());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startService(new Intent(Main.this, TCPService.class));
                    mMain.makeVoiceCall(v.getTag().toString());


                }
            });
            return view;
        }
    }



}