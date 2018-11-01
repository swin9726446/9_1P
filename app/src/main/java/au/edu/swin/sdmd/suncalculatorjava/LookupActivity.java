package au.edu.swin.sdmd.suncalculatorjava;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.util.Locale;

import au.edu.swin.sdmd.suncalculatorjava.place.*;

/**
 * Created by 9726446 on 1/11/18 @ LB1-MAC-024
 */

public class LookupActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lookup);
        initaliseUI();
    }

    private void initaliseUI() {
        final TextView tvOffsetValue = findViewById(R.id.tvOffsetValue);
        SeekBar sbOffset = findViewById(R.id.sbOffset);

        tvOffsetValue.setText(setOffsetText(sbOffset.getProgress()));
        sbOffset.setOnSeekBarChangeListener((new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //restores to GMT+0.
                tvOffsetValue.setText(setOffsetText(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        }));
    }

    private String setOffsetText(int offset){
        return String.format(Locale.ENGLISH,"GMT%+d:00",offset-12);
    }


    private String validate(CharSequence s){
        try{
            Double d = Double.parseDouble(s.toString());
            if (d > 180) return "90";
            if (d < -180) return "-90";
        }
        catch (NumberFormatException nfe){
            Log.e("normalise", String.format(
                    "\"%s\" couldn't be parsed as double.", s.toString())
            );
        }
        return s.toString();
    }

    private String validate(EditText et){
        et.setText(validate(et.getText().toString()));
        return et.getText().toString();
    }

    /**
     * Creates a file if needed. If not just returns.
     */
    public void saveToFile(View view){
        String filename = "CustomPlaces";
//        Place p = new Place(
//                "Emerald",
//                "-37.9332297",
//                "145.439215",
//                "GMT+10:00"
//        );

        Place p = new Place(
                ((EditText)findViewById(R.id.etName)).getText().toString(),
                validate((EditText)findViewById(R.id.etLat)),
                validate((EditText)findViewById(R.id.etLong)),
                ((TextView)findViewById(R.id.tvOffsetValue)).getText().toString()
        );

        FileOutputStream outputStream;
        try{
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(String.format(Locale.ENGLISH,
                    "%s,%s,%s,%s\n",
                    p.getName(), p.getLatitude(), p.getLongitude(), p.getLocale()
            ).getBytes());
            outputStream.close();
        }
        catch (Exception e){
            Log.e("Create File", String.format("Couldn't save data.\n%s", e.toString()));
        }
        setResult(Activity.RESULT_OK);
        finish();
    }
}
