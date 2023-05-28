package com.app.appsinrek.downloader.core;

import com.app.appsinrek.downloader.domain.DownloadInfo;
import com.app.appsinrek.downloader.exception.DownloadException;



public interface DownloadResponse {

    void onStatusChanged(DownloadInfo downloadInfo);

    void handleException(DownloadInfo downloadInfo, DownloadException exception);
}
