package Client;

import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private static String name = Reader.class.getName();
    private static Logger log = Logger.getLogger(name);
    private Client client;
    private LinkedBlockingQueue<ByteBuffer> buffers;
    private PackTool packer;
    /**
     * If KeepOpen is true, writer will not close the connection,
     * is was useful when there was a long connection,
     * or this request is for client.
     * The default value is false
     */
    boolean keepOpen = true;
    /**
     * isSending control the process of sending data
     */
    private boolean isSending = false;
    /**
     * sendTimes recorder how many times a msg already sanded
     */
    private int sendTimes = 0;

    Writer(Client client) {
        this.client = client;
        buffers = new LinkedBlockingQueue<>();
        packer = new PackTool(new byte[]{'G', 'r', 'a', 'm', 'b', 'l', 'e'});
    }

    /**
     * Write Data into buffer list,
     * data will be send as soon as possible
     *
     * @param data waite to be send
     */
    boolean Write(byte[] data) {
        boolean res = buffers.offer(packer.DataConstructor(data));
        if (!isSending && !buffers.isEmpty()) {
            continueSend();
        }
        return res;
    }

    /**
     * Send the data in buffer which written by Write()
     */
    private void continueSend() {
        isSending = true;
        //poll is a nonblocking method
        ByteBuffer buff = buffers.poll();
        sendTimes++;
        client.ch.write(buff, 10, TimeUnit.SECONDS, buff, this);
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if (result != -1) {
            if (buffer.hasRemaining()) {
                sendTimes++;
                if (sendTimes < 4) {
                    client.ch.write(buffer, 10, TimeUnit.SECONDS, buffer, this);
                } else {//Already send too many times
                    log.info(String.format("client %s Timeout and closed", client.Name));
                    client.Close();
                }
            } else {
                sendTimes = 0;
                isSending = false;
                if (!buffers.isEmpty()) {
                    continueSend();
                } else if (!keepOpen) {
                    client.Close();
                }
            }
        } else {
            log.info("client closed");
            client.Close();
        }

    }

    @Override
    public void failed(Throwable e, ByteBuffer buffer) {
        if (e instanceof java.nio.channels.InterruptedByTimeoutException) {
            client.Close();
            log.info(String.format("client %s Timeout and closed", client.Name));
        } else {
            log.info("client closed");
            client.Close();
            e.printStackTrace();
        }
    }
}
