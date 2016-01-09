package com.example.crackgg.dictnote;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by crackgg on 1/8/16.
 */
public class WebFragment extends Fragment {

    View view;
    WebView webView;
    WebSettings webSettings;
    ProgressDialog dialog;
    RadioButton b1, b2, b3, b4;
    LayoutInflater inflaterGlobal;
    MyDatabase db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.web_layout, container, false);

        inflaterGlobal = inflater;
        initialization();

        b1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
//                    webView.loadUrl("https://m.baidu.com");
                    webView.loadUrl("https://www.google.com");
            }
        });

        b2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    webView.loadUrl("http://m.youdao.com");
            }
        });

        b3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    webView.loadUrl("https://zh.m.wikipedia.org#");
//                    webView.loadUrl("http://wapbaike.baidu.com/");
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()).setTitle("请输入网址");
                View view = inflaterGlobal.inflate(R.layout.choose_list3, null);
                dialog.setView(view);
                final EditText editText = (EditText) view.findViewById(R.id.editText);
                editText.setHint("例：www.google.com");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        webView.loadUrl("http://" + editText.getText().toString());
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });

        return view;
    }

    public void initialization() {
        webView = (WebView) view.findViewById(R.id.webView);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        b1 = (RadioButton) view.findViewById(R.id.b1);
        b2 = (RadioButton) view.findViewById(R.id.b2);
        b3 = (RadioButton) view.findViewById(R.id.b3);
        b4 = (RadioButton) view.findViewById(R.id.b4);

        db = new MyDatabase(getActivity(), "itemDB.db3", null, 1);

        webView.requestFocus();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }


        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    closeDialog();
                } else {
                    openDialog(newProgress);
                }

                super.onProgressChanged(view, newProgress);
            }
        });

        FloatingActionButton fab1 = (FloatingActionButton) getActivity().findViewById(R.id.fab1);
        FloatingActionButton fab2 = (FloatingActionButton) getActivity().findViewById(R.id.fab2);

        fab1.hide();
        fab2.show();

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LinearLayout linearLayout = (LinearLayout) inflaterGlobal.inflate(R.layout.dialog_layout, null);
                final EditText index = (EditText) linearLayout.findViewById(R.id.edit_index);
                final EditText note = (EditText) linearLayout.findViewById(R.id.edit_note);

                dialog.setView(linearLayout);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!addListItems(new ListViewFragment().getItemID(), index.getText().toString(), note.getText().toString()))
                            return;
                    }
                });
                dialog.setNegativeButton("取消", null);
                dialog.create().show();
            }
        });

    }

    public boolean canGoback() {
        try {
            return webView.canGoBack();
        } catch (Exception e) {
            return false;
        }
    }

    public void goBack() {
        webView.goBack();
    }

    public boolean addListItems(String itemID, String index, String note) {
        if (index.equals("") || index.equals(" ")) {
            Toast.makeText(getActivity(), "INDEX不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (note.equals("") || note.equals(" ")) {
            Toast.makeText(getActivity(), "批注不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        String [] data = {itemID, index, note, index.substring(0, 1), note.substring(0, 1)};
        db.getReadableDatabase().execSQL("insert into form values(?, ?, ?, ?, ?)", data);
        return true;
    }

    private void openDialog(int newProgress) {
        if (dialog == null) {
            dialog = new ProgressDialog(inflaterGlobal.getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setTitle("Loading...");
            dialog.setProgress(newProgress);
            dialog.show();
        } else {
            dialog.setProgress(newProgress);
        }
    }

    private void closeDialog() {
        if ( dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            }
        } else {
            dialog = null;
        }
    }
}
