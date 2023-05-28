package com.app.appsinrek.downloader.callback;

import com.app.appsinrek.downloader.exception.DownloadException;


public interface DownloadListener {

    void onStart();

    void onWaited();

    void onPaused();

    void onDownloading(long progress, long size);

    void onRemoved();

    void onDownloadSuccess();

    void onDownloadFailed(DownloadException e);
}
