package Client;

import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private static Logger log = Logger.getLogger(Reader.class.getName());
    /**
     * If KeepOpen is true, writer will not close the connection,
     * is was useful when there was a long connection,
     * or this request is for client.
     * The default value is false
     */
    boolean keepOpen = true;
    private Client client;
    private LinkedBlockingQueue<ByteBuffer> buffers;
    private PackTool packer;
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
        boolean res = buffers.offer(packer.Construct(data));
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
                    log.info(String.format("client %s too slow and closed", client.Name));
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
            log.info(String.format("client %s Timeout Interrupted and closed", client.Name));
        } else if (e instanceof java.io.IOException){
            //java.io.IOException: 远程主机强迫关闭了一个现有的连接。
            log.info(String.format("client %s remote closed", client.Name));
        } else {
            e.printStackTrace();
        }
        client.Close();
    }
}
