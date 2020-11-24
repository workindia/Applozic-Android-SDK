package com.applozic.mobicommons.task;

/**
 * implement this interface to create a background thread running task
 * that class will be passed to a background task executor
 *
 * purpose of this interface is to implement loose dependency injection
 *
 * @param <Result> the data type of result
 * @param <Progress> the data type of progress variable
 */
public abstract class BaseAsyncTask<Progress, Result> {
    public BaseAsyncTask() { }

    protected void onPreExecute() { } //call this in UI thread before call()
    protected Result doInBackground() { return null; } //call this in background thread
    protected void onPostExecute(Result result) { } //call this in UI thread after call()
    protected void onProgress(Progress progress) { } //call this in the UI thread; make your own implementation for it
    protected void publishProgress(Progress progress) { } //add implementation to call onProgress in UI thread
    public abstract void execute(); //the code for execution of all the above listed functions
    protected void onCancelled() { } //this code will be run if task is cancelled
}
