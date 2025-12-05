package ru.nbelov.farmdisplay;

import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import ru.nbelov.farmdisplay.dbapi.GetAssetSyncAPI;

public class CustomWebViewClient extends WebViewClient {

    //get mime type by url
    public String getMimeType(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            if (extension.equals("js")) {
                return "text/javascript";
            } else if (extension.equals("css")) {
                return "text/css";
            } else if (extension.equals("html")) {
                return "text/html";
            } else if (extension.equals("woff")) {
                return "application/font-woff";
            } else if (extension.equals("woff2")) {
                return "application/font-woff2";
            } else if (extension.equals("ttf")) {
                return "application/x-font-ttf";
            } else if (extension.equals("eot")) {
                return "application/vnd.ms-fontobject";
            } else if (extension.equals("svg")) {
                return "image/svg+xml";
            } else if (extension.equals("jpg")) {
                return "image/jpeg";
            } else if (extension.equals("png")) {
                return "image/png";
            } else if (extension.equals("gif")) {
                return "image/gif";
            } else {
                return "text/plain";
            }
        }
        Log.i("ERROR", "Unexpected mime type" + url);
        return "text/plain";
    }
/*
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().getPath();
        if (url == null) {
            return super.shouldInterceptRequest(view, request);
        }
        String assetName = url.replace("/", "");
        Log.d("WebViewAsset", "Запрос ресурса: " + url + ", Имя ассета: " + assetName);

        byte[] data = GetAssetSyncAPI.getAsset(url.replace("/", ""));
        // Проверяем, что вернул GetAssetSyncAPI
        if (data == null) {
            Log.e("WebViewAsset", "РЕСУРС НЕ НАЙДЕН: " + assetName + ". GetAssetSyncAPI вернул null.");
            // Возвращаем ошибку, чтобы в инструментах разработчика было видно 404
            return new WebResourceResponse(null, null, null);
        } else {
            Log.d("WebViewAsset", "Ресурс " + assetName + " найден. Размер: " + data.length + " байт.");
        }
        if (data.length == 0) {
            Log.e("WebViewAsset", "ОШИБКА: Ресурс " + assetName + " найден, но он ПУСТОЙ (0 байт). Проверьте GetAssetSyncAPI.");
            return new WebResourceResponse(null, null, null); // Отдаем 404 Not Found
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        if (data == null) {
            return new WebResourceResponse("application/json", "UTF-8", new ByteArrayInputStream("OK".getBytes(StandardCharsets.UTF_8)));
        }
        Log.i("IMAGE return", getMimeType(url));
        return new WebResourceResponse(getMimeType(url), "UTF-8", byteArrayInputStream);
    }*/

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().getPath();
        if (url == null) {
            return super.shouldInterceptRequest(view, request);
        }

        String assetName = url.replace("/", "");
        Log.d("WebViewAsset", "Запрос ресурса: " + url + ", Имя ассета: " + assetName);

        // Используем обновлённый GetAssetSyncAPI
        byte[] data = GetAssetSyncAPI.getAsset(assetName);

        if (data == null) {
            Log.e("WebViewAsset", "РЕСУРС НЕ НАЙДЕН: " + assetName);
            return new WebResourceResponse("text/plain", "UTF-8", 404, "Not Found",
                    null, new ByteArrayInputStream("Resource not found".getBytes(StandardCharsets.UTF_8)));
        }

        if (data.length == 0) {
            Log.e("WebViewAsset", "Ресурс " + assetName + " пустой (0 байт)");
            return new WebResourceResponse("text/plain", "UTF-8", 204, "No Content",
                    null, new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        }

        Log.d("WebViewAsset", "Ресурс " + assetName + " загружен. Размер: " + data.length + " байт");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

        return new WebResourceResponse(getMimeType(url), "UTF-8", byteArrayInputStream);
    }
}