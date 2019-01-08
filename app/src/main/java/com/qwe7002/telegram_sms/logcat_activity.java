package com.qwe7002.telegram_sms;

import android.content.Context;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class logcat_activity extends AppCompatActivity {

    Context context;
    file_observer observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logcat);
        TextView logcat = findViewById(R.id.logcat_view);
        this.setTitle(R.string.logcat);
        context = getApplicationContext();
        logcat.setText(public_func.read_log_file(context));
        observer = new file_observer(context, logcat);

    }
    @Override
    public void onPause() {
        super.onPause();
        observer.stopWatching();
    }

    @Override
    public void onResume() {
        super.onResume();
        observer.startWatching();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logcat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        public_func.write_log_file(context, "");
        return true;
    }

    class file_observer extends FileObserver {
        private Context mContext;
        private TextView mLogcat;

        file_observer(Context context, TextView logcat) {
            super(context.getFilesDir().getAbsolutePath());
            mContext = context;
            mLogcat = logcat;
        }

        @Override
        public void onEvent(int event, String path) {
            if (event == FileObserver.MODIFY) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mLogcat.setText(public_func.read_log_file(mContext));
                    }
                });
            }

        }
    }

}


