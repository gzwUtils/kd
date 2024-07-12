package com.gzw.kd.learn.spi.load;

import com.gzw.kd.common.exception.Asserts;
import com.gzw.kd.learn.spi.StandardRemoteServiceInterface;
import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceLoader;

/**
 * @author gzw
 * @description： 服务加载
 * @since：2024/1/17 23:44
 */
@SuppressWarnings({"unused"})
public class SpiServiceLoaderHelper {


    public static StandardRemoteServiceInterface serviceInterface(){
        // 先从缓存中加载
        Object serviceCache = DependServiceRegistryHelper.getDependObject(StandardRemoteServiceInterface.class);
        if (serviceCache != null) {
            return (StandardRemoteServiceInterface) serviceCache;
        }
        // spi 方式加载
        StandardRemoteServiceInterface serviceInterface = loadSpiImpl(StandardRemoteServiceInterface.class);
        // 防止注入的bean为空 提前进行判断 以免业务执行出现问题
        boolean isExist = !Objects.isNull(serviceInterface);
        if (!isExist) {
            Asserts.fail( "StandardRemoteServiceInterface load impl failed,please check spi service");
        }
        // 添加进统一的依赖管理
        DependServiceRegistryHelper.registry(StandardRemoteServiceInterface.class, serviceInterface);
        return serviceInterface;
    }


    /**
     * 以spi的方式加载实现类
     * @param cls class
     * @param <P>  服务实现
     * @return spi 服务实现
     */
    private static <P> P loadSpiImpl(Class<P> cls) {
        ServiceLoader<P> spiLoader = ServiceLoader.load(cls);
        Iterator<P> iaIterator = spiLoader.iterator();
        if (iaIterator.hasNext()) {
            return iaIterator.next();
        }
        return null;
    }
}
