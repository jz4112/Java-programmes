package downloadApp;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private long pos;
    private long fileSize;
    private String fileName;
    public DownloadInfo(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        this.fileSize = fecthFileSize();
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public long getPos() {
        return pos;
    }
    public void setPos(long pos) {
        this.pos = pos;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    @Override
    public String toString() {
        return "DownloadInfo [url=" + url + ", pos=" + pos + ", fileSize="
                + fileSize + ", fileName=" + fileName + "]";
    }
   
    private int fecthFileSize() {
        int fileSize = -1;
        try {
            URL url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int stateCode = conn.getResponseCode();
            if (stateCode == HttpURLConnection.HTTP_OK) {
                fileSize = conn.getContentLength();
                return fileSize;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileSize;
    }
}