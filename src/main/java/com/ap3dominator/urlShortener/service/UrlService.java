package com.ap3dominator.urlShortener.service;

import com.ap3dominator.urlShortener.model.Url;
import com.ap3dominator.urlShortener.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService {
    public Url generateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public void deleteShortLink(Url url);
}
