package com.android.youtubelist.network;

import com.android.youtubelist.model.Playlist;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ApiServiceImpl implements ApiService {
    private static ApiServiceImpl instance;

    private ApiService apiService;

    public static ApiServiceImpl getInstance() {
        if (instance == null) {
            instance = new ApiServiceImpl();
        }
        return instance;
    }

    private ApiServiceImpl() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    @NotNull
    @Override
    public Single<Playlist> getPlayList() {
        return apiService
            .getPlayList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
