package org.example;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.RBucket;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.codec.JsonJacksonCodec;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    private final RedissonClient redissonClient;

    public Main() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(0);
        redissonClient = Redisson.create(config);

        RMap<String, String> flightData = redissonClient.getMap("flight:FA634");
        flightData.put("airline", "Airline X");
        flightData.put("departure", "2025-03-01 10:00");
        flightData.put("destination", "New York");

        RMap<String, String> seatData = redissonClient.getMap("seat:FA634:A34_S012C");
        seatData.put("bookedBy", "None");
    }

    public boolean bookSeat(String flightId, String seatId) {
        String lockKey = "lock:seat:" + flightId + ":" + seatId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() + ": Booking seat " + seatId + " on flight " + flightId);

                    RMap<String, String> flightData = redissonClient.getMap("flight:" + flightId);

                    System.out.println("Airline: " + flightData.get("airline"));
                    System.out.println("Destination: " + flightData.get("destination"));
                    System.out.println("Departure: " + flightData.get("departure"));

                    RMap<String, String> seatData = redissonClient.getMap("seat:" + flightId + ":" + seatId);
                    if ("None".equals(seatData.get("bookedBy"))) {
                        seatData.put("bookedBy", "Thanh Truong");
                    }
                    else {
                        System.out.println(Thread.currentThread().getName() + ": Seat " + seatId + " is already booked.");
                        return false;
                    }

                    Thread.sleep(3000);
                    System.out.println(Thread.currentThread().getName() + ": Booking confirmed!");
                    return true;
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + ": Seat " + seatId + " is already being booked. Try again later.");
                return false;
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public static void main(String[] args) {
        Main bookingService = new Main();

        Runnable task = () -> bookingService.bookSeat("FA634", "A34_S012C");

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();
    }
}