package ru.peppers;

import java.io.StringReader;
import java.security.KeyStore;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

final public class PhpData {
    static boolean withDebug = true;
    static String sessionid = "";
    static String newURL = "https://www.abs-taxi.ru/fcgi-bin/office/cman.fcgi";

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    static public void errorFromServer(Context context, Node errorNode) {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.error_title))
                .setMessage(errorNode.getTextContent())
                .setNeutralButton(context.getString(R.string.close), null).show();
    }

    static public Document postData(Activity activity, List<NameValuePair> nameValuePairs, String url) {
        if (isNetworkAvailable(activity)) {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = getNewHttpClient();
            HttpPost httppost = new HttpPost(url);
            // http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
            // http://10.0.2.2/api
            try {
                // Add your data
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                if (sessionid != "" && url == newURL)
                    httppost.setHeader("cookie", "cmansid=" + sessionid);
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc;
                if (withDebug) {
                    String str = EntityUtils.toString(response.getEntity());
                    Log.d("My_tag", str);
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(str));
                    // response.getEntity().getContent()
                    doc = builder.parse(is);
                } else {
                    doc = builder.parse(response.getEntity().getContent());

                }

                return doc;

            } catch (Exception e) {
                errorHandler(activity);
            }
        } else {
            new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.error_title))
                    .setMessage(activity.getString(R.string.no_internet))
                    .setNeutralButton(activity.getString(R.string.close), null).show();
        }
        Log.d("My_tag", "no connection");
        return null;
    }

    private static void errorHandler(Context context) {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.error_title))
                .setMessage(context.getString(R.string.error_message))
                .setNeutralButton(context.getString(R.string.close), null).show();
    }

    static public Document postData(Activity activity, List<NameValuePair> nameValuePairs) {

        return postData(activity, nameValuePairs,
                "http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");

    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
