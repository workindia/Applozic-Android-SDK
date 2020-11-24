package com.applozic.mobicommons.task;

import android.os.Binder;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExecutorAsyncTask<Progress, Result> extends BaseAsyncTask<Progress, Result> {
    private final @NonNull ExecutorService executor = Executors.newCachedThreadPool();
    private final @NonNull Handler handler = new Handler(Looper.getMainLooper());
    Future<?> future;

    private AtomicBoolean cancelled = new AtomicBoolean(false);
    private Status status = Status.PENDING;

    public Status getStatus() {
        return status;
    }

    public boolean isCancelled() {
        return cancelled.get();
    }

    @Override
    public void execute() {
        if (status != Status.PENDING) {
            switch (status) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        doInBackground();
        status = Status.RUNNING;
        executeTask();
    }

    private void executeTask() {
        future = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final Result result = doInBackground();
                    Binder.flushPendingCommands();
                    postResult(result);
                } catch (Throwable t) {
                    cancelled.set(true);
                } finally {
                    status = Status.FINISHED;
                }
            }
        });
    }

    private void postResult(final Result result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(!isCancelled()) {
                    onPostExecute(result);
                } else {
                    onCancelled();
                }
            }
        });
    }

    @Override
    protected void publishProgress(final Progress progress) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onProgress(progress);
            }
        });
    }

    public void cancel(boolean mayInterruptIfRunning) {
        cancelled.set(true);
        future.cancel(mayInterruptIfRunning);
    }

    public enum Status {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that {@link BaseAsyncTask#onPostExecute(Object)} has finished.
         */
        FINISHED,
    }
}
