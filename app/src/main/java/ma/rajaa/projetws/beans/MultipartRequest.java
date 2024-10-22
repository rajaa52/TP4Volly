package ma.rajaa.projetws.beans;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    private final Response.Listener<String> listener;
    private final Map<String, String> params;
    private final Map<String, DataPart> byteData;

    public MultipartRequest(int method, String url, Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
        this.params = new HashMap<>();
        this.byteData = new HashMap<>();
    }

    @Override
    protected Map<String, String> getParams() {
        return params;
    }


    protected Map<String, DataPart> getByteData() {
        return byteData;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String responseString = null;
        try {
            responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    public void addParam(String key, String value) {
        params.put(key, value);
    }

    public void addFile(String key, DataPart dataPart) {
        byteData.put(key, dataPart);
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;

        public DataPart(String fileName, byte[] content) {
            this.fileName = fileName;
            this.content = content;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }
    }
}