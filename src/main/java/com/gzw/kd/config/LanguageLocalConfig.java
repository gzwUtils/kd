package com.gzw.kd.config;

import com.gzw.kd.common.Constants;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

/**
 * @author gzw
 * @description： Language Local
 * @since：2023/5/6 15:55
 */
@Configuration
public class LanguageLocalConfig implements LocaleResolver {


    @Resource
    private HttpServletRequest request;

    public Locale getLocal() {
        return resolveLocale(request);
    }

    /**
     *
     * @param request language
     * @return locale
     */
    @NotNull
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale locale = Locale.CHINA;
        String language = request.getHeader("Accept-Language");
        if (StringUtils.isNotBlank(language)) {
            String[] splitLanguage = language.split(Constants.STRING_UNDERLINE);
               if(splitLanguage.length > 1){
                   locale = new Locale(splitLanguage[0], splitLanguage[1]);
               }

        }
        return locale;
    }

    @Override
    public void setLocale(@NotNull HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }

    @Bean
    public LocaleResolver localeResolver() {
        return new LanguageLocalConfig();
    }
}
