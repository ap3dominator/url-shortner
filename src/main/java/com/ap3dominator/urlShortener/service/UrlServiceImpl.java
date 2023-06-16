package com.ap3dominator.urlShortener.service;

import com.ap3dominator.urlShortener.model.Url;
import com.ap3dominator.urlShortener.model.UrlDto;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class UrlServiceImpl implements UrlService{
    public static final String HASH_KEY = "SHORT_URL";
    @Autowired
    private RedisTemplate<String,Url> redisTemplate;

    @Override
    public Url generateShortLink(UrlDto urlDto) {
        if(StringUtils.isNotEmpty(urlDto.getUrl())){
            String encodedUrl = encodeUrl(urlDto.getUrl());
            Url urlToPersist = new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(
                        urlDto.getExpirationDate() ,
                        urlToPersist.getCreationDate()
                    ));
            Url urlToRet = persistShortLink(urlToPersist);

            if(urlToRet != null)
                return urlToRet;

        }
        return null;
    }

    private LocalDateTime getExpirationDate(
                String expirationDate,
                LocalDateTime creationDate) {
         if(StringUtils.isBlank(expirationDate)){
             return creationDate.plusSeconds(60);  //by default 1 minutes expiration
         }
        return LocalDateTime.parse(expirationDate);
    }

    private String encodeUrl(String url) {
        String encodedUrl = "";
        LocalDateTime time = LocalDateTime.now();
        encodedUrl = Hashing.murmur3_32_fixed()
                        .hashString(url.concat(time.toString()) , StandardCharsets.UTF_8)
                        .toString();
        return encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {
        redisTemplate.opsForHash().put(HASH_KEY , url.getShortLink(), url);
        return url;
    }

    @Override
    public Url getEncodedUrl(String url) {
        return (Url) redisTemplate.opsForHash().get(HASH_KEY, url);
    }

    @Override
    public void deleteShortLink(Url url) {
        redisTemplate.opsForHash().delete(HASH_KEY , url.getShortLink());
    }
}
