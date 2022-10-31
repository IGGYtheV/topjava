package ru.javawebinar.topjava;

import org.junit.AssumptionViolatedException;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;

import java.util.concurrent.TimeUnit;

public class MyJUnitStopWatch extends Stopwatch{

    public static long totalMicros = 0;
    public static int finishedTests =0;
    public static int succeeded =0;

    private static void logInfo(Description description, String status, long nanos) {
        String testName = description.getMethodName();
        long micros = TimeUnit.NANOSECONDS.toMicros(nanos);
        totalMicros += micros;
        System.out.printf("Test %s %s, spent %d microseconds%n",
                testName, status, micros);
    }

    @Override
    protected void succeeded(long nanos, Description description) {
        succeeded++;
        logInfo(description, "succeeded", nanos);
    }

    @Override
    protected void failed(long nanos, Throwable e, Description description) {
        logInfo(description, "failed", nanos);
    }

    @Override
    protected void skipped(long nanos, AssumptionViolatedException e, Description description) {
        logInfo(description, "skipped", nanos);
    }

    @Override
    protected void finished(long nanos, Description description) {
        finishedTests++;
        logInfo(description, "finished", nanos);
    }
}