package com.app.appsinrek.downloader.callback;

import com.app.appsinrek.downloader.db.DownloadDBController;
import com.app.appsinrek.downloader.domain.DownloadInfo;

import java.util.List;


public interface DownloadManager {

    void download(DownloadInfo downloadInfo);

    void pause(DownloadInfo downloadInfo);

    void resume(DownloadInfo downloadInfo);

    void remove(DownloadInfo downloadInfo);

    void destroy();

    DownloadInfo getDownloadById(String id);

    List<DownloadInfo> findAllDownloading();

    List<DownloadInfo> findAllDownloaded();

    DownloadDBController getDownloadDBController();

    void resumeAll();

    void pauseAll();

    void onDownloadFailed(DownloadInfo downloadInfo);
}
