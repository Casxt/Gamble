import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private Request req;
    private ByteBuffer buffer;
    private PackTool packer;
    public Writer(Request req) {
        this.req = req;
        buffer = ByteBuffer.allocate(2048);
        packer = new PackTool(new byte[] {'G','r','a','m','b','l','e'});
    }

    public void Write(byte[] data) {
        buffer.put(data);
    }

    /**
     * WriteOnce the Data to buffer and send
     * only have one chance, don't call it more than once!
     * @param data
     */
    public void WriteOnce(byte[] data) {
        //assert buffer == null;
        buffer.put(data);
        Send();
    }

    public void Send(){
        req.ch.write(buffer, buffer, this);
    }



    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if(result!=-1) {
            if (buffer.hasRemaining()){
                req.ch.write(buffer, buffer, this);
            } else {
                // TODO: The Connection Should be reused or Closed?
                req.Close();
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
