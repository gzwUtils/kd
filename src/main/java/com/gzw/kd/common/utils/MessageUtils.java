package com.gzw.kd.common.utils;
import com.gzw.kd.config.LanguageLocalConfig;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author gzw
 * @description： MessageUtils
 * @since：2023/5/6 15:59
 */

@Slf4j
@Component
public class MessageUtils {


    private final LanguageLocalConfig resolver;

    private final MessageSource source;


    private static LanguageLocalConfig languageLocalConfig;

    private static MessageSource messageSource;

    public MessageUtils(LanguageLocalConfig resolver,MessageSource source) {
        this.resolver = resolver;
        this.source = source ;
    }

    @PostConstruct
    public void init() {
        setCustomLocaleResolver(resolver);
        setMessageSource(source);
        log.info("init locale message .........");
    }





    /**
     * 根据code获取国际化消息
     *
     * @param key code
     * @return msg
     */
    public static String getMessage(int key) {
        log.warn("locale {}",languageLocalConfig.getLocal().getLanguage());
        return messageSource.getMessage(String.valueOf(key), null, String.valueOf(key), languageLocalConfig.getLocal());
    }

    /**
     * 通过code和占位符获取国际化消息
     *
     * @param code         消息code
     * @param messageCodes 占位符
     * @return msg
     */
    public static String getMessage(String code, String... messageCodes) {
        Object[] objs = Arrays.stream(messageCodes).map(MessageUtils::getMessage).toArray();
        return messageSource.getMessage(code, objs, code, LocaleContextHolder.getLocale());
    }


    public static void setCustomLocaleResolver(LanguageLocalConfig resolver) {
        MessageUtils.languageLocalConfig = resolver;
    }

    public static void setMessageSource(MessageSource source) {
        MessageUtils.messageSource = source;
    }
}
