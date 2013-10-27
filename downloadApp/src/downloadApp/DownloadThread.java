package downloadApp;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadThread extends Thread {
    private DownloadInfo info;
    private RandomAccessFile raf;
    private boolean isStop = false;
    private boolean isFinish = false;
    private static int BUFFER_SIZE = 8 * 1024;
    public DownloadThread(DownloadInfo info) {
        this.info = info;
    }
    public synchronized boolean isFinish() {
        return isFinish;
    }
    public synchronized void setStop(boolean isStop) {
        this.isStop = isStop;
    }
    public synchronized boolean isStop() {
        return isStop;
    }
    @Override
    public void run() {
        while (!isStop() && !isFinish()) {
            try {
                URL url = new URL(info.getUrl());
                HttpURLConnection con = (HttpURLConnection) url
                        .openConnection();
                String prop = "bytes=" + info.getPos() + "-";
                System.out.println(prop);
                System.out.println(info.getFileSize());
                con.setRequestProperty("RANGE", prop);
                InputStream is = con.getInputStream();
                raf = new RandomAccessFile(info.getFileName(), "rw");
                raf.seek(info.getPos());
                byte[] buffer = new byte[BUFFER_SIZE];
                int b = -1;
                while ((b = is.read(buffer)) != -1 && !isStop()) {
                    raf.write(buffer, 0, b);
                    info.setPos(info.getPos() + b);
                }
                if (!isStop()) {
                    isFinish = true;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    raf.close();
                } catch (IOException e) {
                }
            }
        }
    }
}