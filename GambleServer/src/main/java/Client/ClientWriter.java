package Client;

import PackTool.PackTool;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientWriter implements CompletionHandler<Integer, ByteBuffer> {
    private Client client;
    LinkedBlockingQueue<ByteBuffer> buffers;
    private ByteBuffer buffer;
    private PackTool packer;
    /**
     * If KeepOpen is true, writer will not close the connection,
     * is was useful when there was a long connection,
     * or this request is for client.
     * The default value is false
     */
    public boolean keepOpen = true;
    /**
     * isSending control the process of sending data
     */
    private boolean isSending = false;
    /**
     * sendTimes recorder how many times a msg already sanded
     */
    private int sendTimes = 0;

    public ClientWriter(Client client) {
        this.client = client;
        buffer = ByteBuffer.allocate(2048);
        packer = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
    }

    /**
     * Write Data into buffer list,
     * data will be send as soon as possible
     * @param data waite to be send
     */
    public boolean Write(byte[] data) {
        boolean res =  buffers.offer(ByteBuffer.wrap(data));
        if(!isSending && !buffers.isEmpty()){
            ContinueSend();
        }
        return res;
    }

    /**
     * Send the data in buffer which written by Write()
     */
    public void ContinueSend(){
        isSending = true;
        client.ch.write(buffer, buffer, this);
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if(result!=-1) {
            if (buffer.hasRemaining()){
                client.ch.write(buffer, buffer, this);
            } else {
                isSending = false;
                if(!buffers.isEmpty()){
                    ContinueSend();
                } else if(!keepOpen){
                    client.Close();
                }
            }
        } else {
            client.Close();
        }

    }

    @Override
    public void failed(Throwable e, ByteBuffer buffer) {
        client.Close();
        e.printStackTrace();
    }
}
