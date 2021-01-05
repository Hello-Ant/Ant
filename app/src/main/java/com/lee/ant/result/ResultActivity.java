/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.lee.ant.result;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lee.ant.R;

public class ResultActivity extends AppCompatActivity {
    private TextView tvTip;
    private ActivityResultLauncher<String> launcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_act_result);
        initView();
        launcher = registerForActivityResult(new MyActivityResultContract(), new ActivityResultCallback<String>() {
            @Override
            public void onActivityResult(String result) {
                tvTip.setText("回传数据：" + result);
            }
        });
    }

    private void initView() {
        tvTip = findViewById(R.id.tv_tip);
        findViewById(R.id.btn_navigation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("Hello World.");
            }
        });
    }

    static class MyActivityResultContract extends ActivityResultContract<String, String> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, String input) {
            Log.e("Ant", "createIntent:context: " + context + " input: " + input);
            Intent intent = new Intent(context, ResultTargetActivity.class);
            intent.putExtra("name", input);
            return intent;
        }

        @Override
        public String parseResult(int resultCode, @Nullable Intent intent) {
            Log.e("Ant", "parseResult:resultCode " + resultCode + " intent: " + intent);
            String data = intent == null ? null : intent.getStringExtra("result");
            if (resultCode == RESULT_OK && data != null) {
                return data;
            }
            return null;
        }
    }
}
