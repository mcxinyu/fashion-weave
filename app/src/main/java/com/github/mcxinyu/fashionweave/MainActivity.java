package com.github.mcxinyu.fashionweave;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mcxinyu.weavelib.SimpleWeaveView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.simple_weave_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SimpleWeaveView) v).setLineDegrees(((SimpleWeaveView) v).getLineDegrees() + 15);
            }
        });
    }
}
