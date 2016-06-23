package com.applozic.mobicomkit.api.attachment;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sunil on 21/6/16.
 */
public class MarkStream extends InputStream {
    private InputStream inputStream;

    private long offsetValue;
    private long resetValue;
    private long limit;

    private long defaultValue = -1;

    public MarkStream(InputStream inputStream) {
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        this.inputStream = inputStream;
    }


    @Override
    public void mark(int readLimit) {
        defaultValue = setPos(readLimit);
    }

    public long setPos(int readLimit) {
        long offsetLimit = offsetValue + readLimit;
        if (limit < offsetLimit) {
            setLimit(offsetLimit);
        }
        return offsetValue;
    }


    private void setLimit(long limit) {
        try {
            if (resetValue < offsetValue && offsetValue <= this.limit) {
                inputStream.reset();
                inputStream.mark((int) (limit - resetValue));
                skipBytes(resetValue, offsetValue);
            } else {
                resetValue = offsetValue;
                inputStream.mark((int) (limit - offsetValue));
            }
            this.limit = limit;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to mark: " + e);
        }
    }


    @Override
    public void reset() throws IOException {
        resetPos(defaultValue);
    }


    public void resetPos(long token) throws IOException {
        if (offsetValue > limit || token < resetValue) {
            throw new IOException("Cannot reset the pos ");
        }
        inputStream.reset();
        skipBytes(resetValue, token);
        offsetValue = token;
    }


    private void skipBytes(long current, long pos) throws IOException {
        while (current < pos) {
            long skipped = inputStream.skip(pos - current);
            current += skipped;
        }
    }

    @Override
    public int read() throws IOException {
        int result = inputStream.read();
        if (result != -1) {
            offsetValue++;
        }
        return result;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        int byteCount = inputStream.read(buffer);
        if (byteCount != -1) {
            offsetValue += byteCount;
        }
        return byteCount;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        int byteCount = inputStream.read(buffer, offset, length);
        if (byteCount != -1) {
            this.offsetValue += byteCount;
        }
        return byteCount;
    }

    @Override
    public long skip(long byteCount) throws IOException {
        long skipped = inputStream.skip(byteCount);
        offsetValue += skipped;
        return skipped;
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

}
