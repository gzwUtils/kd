package com.gzw.kd.common.generators;

import com.gzw.kd.common.enums.ResultCodeEnum;
import com.gzw.kd.common.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

@Component
@SuppressWarnings("all")
@Slf4j
public class RandomIdGenerator implements IdGenerator{


    @Override
    public String generate(Integer length) throws GlobalException {
        long currentTimeMillis = System.currentTimeMillis();
        String lastfieldOfHostName = null;
        try {
            lastfieldOfHostName = getLastfieldOfHostName();
        } catch (UnknownHostException e) {
            throw new GlobalException(ResultCodeEnum.ID_GENERATOR_ERROR);
        }
        length = length == null ? 8 : length;
        String randomAlphameric = generateRandomAlphameric(length);
        String id = String.format("%s%d%s", lastfieldOfHostName, currentTimeMillis, randomAlphameric);
        return id;
    }


    private String getLastfieldOfHostName() throws UnknownHostException {
        String substrOfHostName = null;
        String hostName = InetAddress.getLocalHost().getHostAddress();
        substrOfHostName = getLastSubstrSplittedByDot(hostName);
        return substrOfHostName;
    }

    private String getLastSubstrSplittedByDot(String hostName) {
        Assert.notNull(hostName,"hostName is null");
        String[] tokens = hostName.split("\\.");
        String substrOfHostName = tokens[tokens.length - 1];
        return substrOfHostName;
    }

    private String generateRandomAlphameric(int length){
        Assert.isTrue(length!=0,"length is not 0 ");
        char[] chars = new char[length];
        int count = 0;
        Random random = new Random();
        while (count < length){
            int maxAscii = 'z';
            int randomAscii = random.nextInt(maxAscii);
            boolean isDigit = randomAscii >= '0' && randomAscii <= '9';
            boolean isUppercase = randomAscii >= 'A' && randomAscii <= 'Z';
            boolean isLowercase = randomAscii >= 'a' && randomAscii <= 'z';
            if(isDigit || isLowercase || isUppercase){
                chars[count] = (char) (randomAscii);
                ++count;
            }
        }
        return new String(chars);
    }
}
