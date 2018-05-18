import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;

public class Writer implements CompletionHandler<Integer, ByteBuffer> {
    private Request req;
    private ByteBuffer buffer;

    public Writer(Request req) {
        this.req = req;
    }

    public void Write(byte[] data) {
        buffer = DataConstructor(data);
        if(!isSending){
            continueSending();
        }
    }

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

    private void continueSending(){
        //Starting a new sending progress
            req.ch.write(buffer, buffer, this);
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
