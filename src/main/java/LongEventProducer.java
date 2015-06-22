/**
 * Created by ethan-liu on 15/6/20.
 */

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

public class LongEventProducer implements Runnable
{
    private final RingBuffer<LongEvent> ringBuffer;
    private String name;

    public LongEventProducer(String name, RingBuffer<LongEvent> ringBuffer)
    {
        this.ringBuffer = ringBuffer;
        this.name =name;
    }

    public void onData(ByteBuffer bb)
    {
        long sequence = ringBuffer.next();  // Grab the next sequence
        try
        {
            LongEvent event = ringBuffer.get(sequence); // Get the entry in the Disruptor
            // for the sequence
            event.set(bb.getLong(0),this.name);  // Fill with data
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }
    @Override
    public void run(){
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            this.onData(bb);
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
}