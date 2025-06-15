package example;

import example.model.Message;
import example.storage.InventoryService;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrentTest {
    @Test
    void testConcurrentOperations() throws InterruptedException {
        InventoryService service = new InventoryService();
        int threads = 100;
        int operations = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads * operations);


        service.process(new Message("ADD_ITEM", "test_item", 0, 0.0, "test_category"));

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operations; j++) {
                    service.process(new Message("ACCEPT", "test_item", 1, 0.0, null));
                    latch.countDown();
                }
            });
        }

        latch.await();
        String result = service.process(new Message("GET_QUANTITY", "test_item", 0, 0.0, null));
        assertTrue(result.contains("Quantity: " + (threads * operations)));
    }
}