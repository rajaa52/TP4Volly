package ma.rajaa.projetws.beans;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Map<String, String> mHeaders;
    private final Map<String, String> mParams;
    private final Map<String, DataPart> mByteData;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mHeaders = null;
        this.mParams = null;
        this.mByteData = null;
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    @Override
    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + System.currentTimeMillis();
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);

        try {
            inputStream = new ByteArrayInputStream(mByteData.get("image").getData());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(String name, byte[] data) {
            fileName = name;
            content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
