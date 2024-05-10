package soapy.serialport;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    private static final String TAG = "SerialPort";
    public static final String DEFAULT_SU_PATH = "/system/bin/su";
    private static String sSuPath = "/system/bin/su";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    public static void setSuPath(String suPath) {
        if (suPath != null) {
            sSuPath = suPath;
        }
    }

    public static String getSuPath() {
        return sSuPath;
    }

    private SerialPort(File device, int baudrate, int dataBits, int parity, int stopBits, int flags) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                Process su = Runtime.getRuntime().exec(sSuPath);
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\nexit\n";
                su.getOutputStream().write(cmd.getBytes());
                if (su.waitFor() != 0 || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception var9) {
                var9.printStackTrace();
                throw new SecurityException();
            }
        }

        this.mFd = this.open(device.getAbsolutePath(), baudrate, dataBits, parity, stopBits, flags);
        if (this.mFd == null) {
            Log.e("SerialPort", "native open returns null");
            throw new IOException();
        } else {
            this.mFileInputStream = new FileInputStream(this.mFd);
            this.mFileOutputStream = new FileOutputStream(this.mFd);
        }
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    private native FileDescriptor open(String var1, int var2, int var3, int var4, int var5, int var6);

    public native void close();

    public static Builder newBuilder(File device, int baudrate) {
        return new Builder(device, baudrate);
    }

    public static Builder newBuilder(String devicePath, int baudrate) {
        return new Builder(devicePath, baudrate);
    }

    static {
        System.loadLibrary("serial_port");
    }

    public static final class Builder {
        private File device;
        private int baudrate;
        private int dataBits;
        private int parity;
        private int stopBits;
        private int flags;

        private Builder(File device, int baudrate) {
            this.dataBits = 8;
            this.parity = 0;
            this.stopBits = 1;
            this.flags = 0;
            this.device = device;
            this.baudrate = baudrate;
        }

        private Builder(String devicePath, int baudrate) {
            this(new File(devicePath), baudrate);
        }

        public Builder dataBits(int dataBits) {
            this.dataBits = dataBits;
            return this;
        }

        public Builder parity(int parity) {
            this.parity = parity;
            return this;
        }

        public Builder stopBits(int stopBits) {
            this.stopBits = stopBits;
            return this;
        }

        public Builder flags(int flags) {
            this.flags = flags;
            return this;
        }

        public SerialPort build() throws SecurityException, IOException {
            return new SerialPort(this.device, this.baudrate, this.dataBits, this.parity, this.stopBits, this.flags);
        }
    }
}
