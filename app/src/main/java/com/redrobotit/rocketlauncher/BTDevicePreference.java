package com.redrobotit.rocketlauncher;

import android.bluetooth.*;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.Set;

public class BTDevicePreference extends ListPreference {

    public BTDevicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();
        CharSequence[] entries = new CharSequence[pairedDevices.size()];
        CharSequence[] entryValues = new CharSequence[pairedDevices.size()];
        int i = 0;
        for (BluetoothDevice dev : pairedDevices) {
            entries[i] = dev.getName();
            if (entries[i] == null) entries[i] = "unknown";
            entryValues[i] = dev.getAddress();
            i++;
        }
        setEntries(entries);
        setEntryValues(entryValues);
    }

    public BTDevicePreference(Context context) {
        this(context, null);
    }

}
