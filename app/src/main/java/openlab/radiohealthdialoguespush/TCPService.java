package openlab.radiohealthdialoguespush;

        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.PrintStream;
        import java.net.InetAddress;
        import java.net.InetSocketAddress;
        import java.net.Socket;
        import java.net.UnknownHostException;

        import android.app.Service;
        import android.content.Intent;
        import android.os.Binder;
        import android.os.IBinder;
        import android.os.Messenger;
        import android.support.v4.content.LocalBroadcastManager;
        import android.util.Log;
        import android.widget.Toast;

        import static java.lang.Thread.sleep;

public class TCPService extends Service {
    Socket socket;
    PrintStream os;
    String message;
    private Messenger messageHandler;

    private final IBinder myBinder = new LocalBinder();

    public TCPService() {
    }

    public class LocalBinder extends Binder {
        public TCPService getService() {
            System.out.println("I am in Localbinder ");
            Log.d("abc","I am in Localbinder ");
            return TCPService.this;

        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("I am in Ibinder onBind method");
        Log.d("abc","I am in Ibinder onBind method");
        // return myBinder;
        return null;
    }

    public void IsBoundable(){
        Toast.makeText(this,"I bind like butter", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    //  public void onStart(Intent intent, int startId) {
    // TODO Auto-generated method stub
    public int onStartCommand(Intent intent, int flags, int startId) {
        // super.onStart(intent, startId);
        Log.d("abc","Service Started");
        Log.d("abc", "Received start id " + startId + ": " + intent);
        Toast.makeText(this, "Service Started TCPService", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();
       return START_STICKY;
    }

    class connectSocket implements Runnable {

        @Override
        public void run() {
            boolean running = true;
            Log.d("abc", "inside connectSocket Thread");
            //  }
            //while (running)
            //{
                try {
                    String msg;                    InetAddress serverAddr = InetAddress.getByName("172.31.27.18");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(serverAddr, 4444));

                    // socket.connect(new InetSocketAddress(serverAddr, 4444), 5000);

                    Log.d("abc", "socket variable is " + socket);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "US-ASCII"));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "US-ASCII"));
                    out.write("clientapp");
                    out.write("\n");
                    out.flush();
                    //sleep(1000);
                    while (true) {
                        //sleep(1000);
                        try {
                     /*   //Log.d("abc","going to readline");
                        char ch;
                        int i;
                        StringBuffer buffer = new StringBuffer();
                        while (true) {
                            Log.d("abc", "inside read characer loop");
                            i = in.read();

                            if (i == ',' && buffer.length() < 1) {
                                Log.d("abc", "read returned ,");
                                break;

                            }
                            if (i == -1) {
                                Log.d("abc", "read returned -1");

                            }
                            if (i == 'z') {
                                Log.d("abc", "new line found");
                                //buffer.append('\0');
                                msg = buffer.toString();
                                message = msg;
                                Log.d("abc", "you got reply ---" + message);
                                sendBroadcast();
                                break;
                            }
                            ch = (char) i;
                            buffer.append(ch);

                            Log.d("abc", "Do something with " + ch);

                        }*/
                            if(in.ready() == true) {
                                msg = in.readLine();
                                Log.d("abc", "msg =" + msg);
                                if (msg.equalsIgnoreCase("END")) {
                                    break;
                                } else if (msg.length() != 0) {
                                    message = msg;
                                    Log.d("abc", "you got reply ---" + message);
                                    sendBroadcast();
                                } else {
                                    Log.d("abc", "nothing read from server, msg = " + msg);
                                    break;
                                }
                            }

                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                            Log.d("abc", "IOException by readLine " + e1.getMessage());
                            break;
                        }

                    }
                    Log.d("abc", "after while loop" + "message read " + message);
                    //msg = in.readLine();

                    //  UserList ob = new UserList();
                    // ob.setUserlist(message);

                    in.close();
                    out.close();
                    socket.close();

                    // out.close();
                } catch (UnknownHostException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Log.d("abc", "UnknownHostException " + e1.getMessage());
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Log.d("abc", "IOException " + e1.getMessage());


                    // }
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
           // }

        }
    }

    public void sendLocalBroadcast(Intent intent){
        Log.d("abc","sendLocationBroadcast"+message);
        intent.putExtra("message", message);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void sendBroadcast() {
        Intent broadcast = new Intent();
        broadcast.setAction("SHOWBROADCAST");
        broadcast.putExtra("data", message);
        broadcast.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcast);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {

            socket.close();
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }

}
