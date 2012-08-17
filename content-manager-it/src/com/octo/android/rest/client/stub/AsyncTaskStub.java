package com.octo.android.rest.client.stub;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskStub< Params, Progress, Result > extends AsyncTask< Params, Progress, Result > {
    protected boolean isLoadDataFromNetworkCalled = false;

    private ReentrantLock lock = new ReentrantLock();
    private Condition requestFinishedCondition = lock.newCondition();

    @Override
    protected Result doInBackground( Params... params ) {
        Log.d( "ed", "edd" );

        lock.lock();
        try {
            isLoadDataFromNetworkCalled = true;
            requestFinishedCondition.signal();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public boolean isLoadDataFromNetworkCalled() {
        lock.lock();
        try {
            requestFinishedCondition.signal();
        } finally {
            lock.unlock();
        }
        return isLoadDataFromNetworkCalled;
    }

    public void await( long millisecond ) throws InterruptedException {
        lock.lock();
        try {
            requestFinishedCondition.await( millisecond, TimeUnit.MILLISECONDS );
        } finally {
            lock.unlock();
        }
    }
}