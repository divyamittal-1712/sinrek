package com.app.appsinrek.downloader.db;

import com.app.appsinrek.downloader.domain.DownloadInfo;
import com.app.appsinrek.downloader.domain.DownloadThreadInfo;

import java.util.List;


public interface DownloadDBController {

    List<DownloadInfo> findAllDownloading();

    List<DownloadInfo> findAllDownloaded();

    DownloadInfo findDownloadedInfoById(String id);

    void pauseAllDownloading();

    void createOrUpdate(DownloadInfo downloadInfo);

    void createOrUpdate(DownloadThreadInfo downloadThreadInfo);

    void delete(DownloadInfo downloadInfo);

    void delete(DownloadThreadInfo download);
}
