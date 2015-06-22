/**
 * Created by ethan-liu on 15/6/20.
 */
import com.lmax.disruptor.EventHandler;

public class LongEventHandler implements EventHandler<LongEvent>
{
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        System.out.println("Event: " + event.name + " : " + event.value);
    }
}