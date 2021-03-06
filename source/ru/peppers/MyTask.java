package ru.peppers;

import java.util.List;

import org.apache.http.NameValuePair;
import org.w3c.dom.Document;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class MyTask extends AsyncTask<List<NameValuePair>, Void, Document> {

    private ProgressDialog progress;
    private BalanceActivity activity;
    private AsyncTaskCompleteListener<Document> callback;

    public MyTask(BalanceActivity activity, ProgressDialog progress,
            AsyncTaskCompleteListener<Document> callback) {
        this.progress = progress;
        this.activity = activity;
        this.callback = callback;
    }

    public void onPreExecute() {
        if(!progress.isShowing())
            progress.show();
    }

    @Override
    protected Document doInBackground(List<NameValuePair>... nameValuePairs) {
        if (PhpData.isNetworkAvailable(activity,true))
            return PhpData.postData(activity, nameValuePairs[0], PhpData.newURL,true);
        else
            return null;
    }

    public void onPostExecute(Document result) {
        if(progress.isShowing())
        progress.dismiss();
        callback.onTaskComplete(result);
    }

}
