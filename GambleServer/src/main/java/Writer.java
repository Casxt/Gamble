import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private Request req;
    private ByteBuffer buffer;
    private PackTool packer;
    /**
     * If KeepOpen is true, writer will not close the connection,
     * is was useful when there was a long connection,
     * or this request is for client.
     * The default value is false
     */
    public boolean keepOpen = false;

    public Writer(Request req) {
        this.req = req;
        buffer = ByteBuffer.allocate(2048);
        packer = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
    }

    /**
     * Write Data into buffer, can be call more than once.
     * this method work with Send(),
     * And the Write-Send can not use with WriteOnce
     * @param data waite to be send
     */
    public void Write(byte[] data) {
        buffer.put(data);
    }

    /**
     * Send the data in buffer which written by Write()
     */
    public void Send(){
        buffer.flip();
        packer.DataConstructor(buffer);
        req.ch.write(buffer, buffer, this);
    }


    /**
     * WriteOnce the Data to buffer and send and close
     * Only send the data that given, the data write by the Write() will not be send.
     * faster than Write-Send
     * @param data is the only data need to send
     */
    public void WriteOnce(byte[] data) {
        ByteBuffer buffer = packer.DataConstructor(data);
        req.ch.write(buffer, buffer, this);
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if(result!=-1) {
            if (buffer.hasRemaining()){
                req.ch.write(buffer, buffer, this);
            } else {
                // TODO: The Connection Should be reused or Closed?
                if(!keepOpen){
                    req.Close();
                }
            }
        } else {
            req.Close();
        }

    }

    @Override
    public void failed(Throwable e, ByteBuffer buffer) {
        req.Close();
        e.printStackTrace();
    }
}
