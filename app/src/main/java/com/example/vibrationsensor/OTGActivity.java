package com.example.vibrationsensor;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class OTGActivity extends AppCompatActivity {

    /*
    * Notifications from UsbService will be received here.
    */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private TextView display;
    private EditText editText;
    private MyHandler mHandler;

    //Physicaloid mPhysicaloid;
    Button btnOTAOpen;
    Button btnOTAClose;
    TextView moreinfo;
    UsbDevice usbDevice;
    UsbManager usbManager;

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otg);

        display = (TextView) findViewById(R.id.textView1);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        String latitude = intent.getStringExtra(LocationStartService.EXTRA_LATITUDE);
                        String longitude = intent.getStringExtra(LocationStartService.EXTRA_LONGITUDE);

                        String sub_latitude = latitude.substring(0,4);
                        String sub_longitude = longitude.substring(0,4);

                        if (latitude != null && longitude != null) {
                            display.setText( "\n Latitude : " + latitude + "\n Longitude: " + longitude + "\n");
                            //String data = latitude + "|" + longitude + "\n";
                            String data = latitude + "\n";
                            //String data = latitude;


                            if (usbService != null) { // if UsbService was correctly binded, Send data
                                //display.append(data);
                                Toast t = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
                                t.show();
                                usbService.write(data.getBytes());

                            }
                        }
                    }
                }, new IntentFilter(LocationStartService.ACTION_LOCATION_BROADCAST)
        );

        moreinfo = (TextView) findViewById(R.id.moreinfo);

        btnOTAOpen = (Button) findViewById(R.id.open_otg);
        btnOTAOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast toast1 = Toast.makeText(getApplicationContext(),"This is a message displayed in a Toast", Toast.LENGTH_SHORT);
                //toast1.show();

                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                int i = 0;
                while(deviceIterator.hasNext()){

                    i++;
                    UsbDevice device = deviceIterator.next();
                    //log("--------");

                    //log("device id : " + device.getDeviceId());
                    //log("name : " + device.getDeviceName());
                    //log("class : " + device.getDeviceClass());
                    //log("subclass : " + device.getDeviceSubclass());
                    //log("vendorId : " + device.getVendorId());
                    // log("version : " + device.getVersion() );
                    //log("serial number : " + device.getSerialNumber() );
                    //log("interface count : " + device.getInterfaceCount());
                    //log("device protocol  : " + device.getDeviceProtocol());
                    //log("--------");
                    //Toast toast1 = Toast.makeText(getApplicationContext(),"Device :" + device.getDeviceName(), Toast.LENGTH_SHORT);
                    //toast1.show();
                    moreinfo.setText("Device Id :" + device.getDeviceId() +
                            "\n Device name :" + device.getDeviceName() +
                            "\n Vendor Id :" + device.getVendorId() +
                            "\n serial number :" + device.getSerialNumber());

                }




            }
        });

        btnOTAClose = (Button) findViewById(R.id.close_otg);
        btnOTAClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast toast1 = Toast.makeText(getApplicationContext(),"This is a message displayed in a Toast", Toast.LENGTH_SHORT);
                //toast1.show();

                UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);

                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                int i = 0;
                while(deviceIterator.hasNext()){

                    i++;
                    UsbDevice device = deviceIterator.next();

                    moreinfo.setText("Device Id :" + device.getDeviceId() +
                            "\n Device name :" + device.getDeviceName() +
                            "\n Vendor Id :" + device.getVendorId() +
                            "\n serial number :" + device.getSerialNumber());

                }




            }
        });

        mHandler = new MyHandler(this);


        editText = (EditText) findViewById(R.id.editText1);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("")) {
                    String data = editText.getText().toString();
                    if (usbService != null) { // if UsbService was correctly binded, Send data
                        display.append(data);
                        usbService.write(data.getBytes());
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<OTGActivity> mActivity;

        public MyHandler(OTGActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    mActivity.get().display.append(data);
                    break;
            }
        }
    }
}
