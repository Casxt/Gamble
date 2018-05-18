import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private Request req;
    private ByteBuffer buffer;

    public Writer(Request req) {
        this.req = req;
        buffer = null;
    }

    /**
     * Write the Data to buffer
     * only have one chance, don't call it more than once!
     * @param data
     */
    public void Response(byte[] data) {
        assert buffer == null;
        buffer = DataConstructor(data);
        req.ch.write(buffer, buffer, this);
    }

    /**
     * DataConstructor will Constructor data struct
     * @param data is data wait to be pack
     * @return the buffer wait to be send
     */
    private ByteBuffer DataConstructor(byte[] data){
        byte[] head = {'G','r','a','m','b','l','e'};
        CRC32 crc32 = new CRC32();
        crc32.update(data);

        ByteBuffer b = ByteBuffer.allocate((head.length + 4 + data.length + 8));
        b.put(head);
        b.putInt(data.length);
        b.put(data);
        b.putLong(crc32.getValue());
        b.flip();
        return b;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        if(result!=-1) {
            if (buffer.hasRemaining()){
                req.ch.write(buffer, buffer, this);
            } else {
                // TODO: The Connection Could be reused
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
