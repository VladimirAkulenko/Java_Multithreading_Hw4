package com.company;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    private static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    private static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    private static final int SIZE_TEXT = 10_000;
    private static final int LENGTH_TEXT = 100_000;
    private static final String WORD_PATTERN = "abc";

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int i = 0; i < SIZE_TEXT; i++) {
                String text = generateText(WORD_PATTERN, LENGTH_TEXT);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

        Thread threadA = threadSearch(queueA, 'a');
        Thread threadB = threadSearch(queueB, 'b');
        Thread threadC = threadSearch(queueC, 'c');

        threadA.start();
        threadB.start();
        threadC.start();
        threadA.join();
        threadB.join();
        threadC.join();
    }

    private static long findLetter(BlockingQueue<String> queue, char letter) throws InterruptedException {
        long count;
        long maxLength = 0;
        String text;
        for (int i = 0; i < SIZE_TEXT; i++) {
            text = queue.take();
            count = text.chars().filter(ch -> ch == letter).count();
            if (count > maxLength) {
                maxLength = count;
            }
        }
        return maxLength;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread threadSearch(BlockingQueue<String> queue, char letter) {
        return new Thread(() -> {
            long maxLength;
            try {
                maxLength = findLetter(queue, letter);
            } catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getName() + " interrupted");
                maxLength = -1;
            }
            System.out.println("Максимальное количество символов " + letter + " в тексте " + maxLength);
        });
    }
}
