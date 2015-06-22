/**
 * Created by ethan-liu on 15/6/20.
 */

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LongEventMain
{
    private static Logger log = Log.get();

    public static void handleEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        System.out.println(event.name + " : " +event.value);
    }

    public static void handleOtherEvent(LongEvent event, long sequence, boolean endOfBatch)
    {
        System.out.println("Event: " + event.name + " : " +event.value);
    }

    public static void translate(LongEvent event, long sequence, ByteBuffer buffer, String name)
    {
        event.set(buffer.getLong(0), name);
    }

//    static void startServer() throws IOException {
//        int port = 8899;
//        //定义一个ServerSocket监听在端口8899上
//        ServerSocket server = new ServerSocket(port);
//        //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
//        Socket socket = server.accept();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        StringBuilder sb = new StringBuilder();
//        String temp;
//        int index;
//        while ((temp=reader.readLine()) != null) {
//            System.out.println(temp);
//            if ((index = temp.indexOf("eof")) != -1) {//遇到eof时就结束接收
//                sb.append(temp.substring(0, index));
//                break;
//            }
//            sb.append(temp);
//        }
//        Log.debug(log, "read null or eof");
//        reader.close();
//        socket.close();
//        server.close();
//    }

    public static void main(String[] args) throws Exception
    {
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(LongEvent::new, bufferSize, executor);

        // Connect the handler
        disruptor.handleEventsWith(LongEventMain::handleEvent, LongEventMain::handleOtherEvent);

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        //LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            ringBuffer.publishEvent(LongEventMain::translate, bb,"p1");
            Thread.sleep(1000);
        }
    }
}